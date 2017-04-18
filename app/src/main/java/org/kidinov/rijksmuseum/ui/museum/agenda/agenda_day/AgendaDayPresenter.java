package org.kidinov.rijksmuseum.ui.museum.agenda.agenda_day;

import org.kidinov.rijksmuseum.data.DataManager;
import org.kidinov.rijksmuseum.data.remote.RetrofitException;
import org.kidinov.rijksmuseum.ui.base.BasePresenter;
import org.kidinov.rijksmuseum.util.RxEventBus;
import org.kidinov.rijksmuseum.util.RxUtil;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class AgendaDayPresenter extends BasePresenter<AgendaDayView> {

    private final DataManager dataManager;
    private final RxEventBus eventBus;
    private CompositeSubscription subscriptions;

    @Inject AgendaDayPresenter(DataManager dataManager, RxEventBus eventBus) {
        this.dataManager = dataManager;
        this.eventBus = eventBus;
    }

    @Override
    public void attachView(AgendaDayView mvpView) {
        super.attachView(mvpView);

        subscriptions = new CompositeSubscription();
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(subscriptions);
    }

    /**
     * Loads data for given date. Should show progress view if there is no data available.
     * <p>
     * Should repeat remote call in case if first was unsuccessful due internet connection, but device came back online.
     * <p>
     * Should hide refresh sign in case of error or normal execution
     * <p>
     * Should show error screen if there is no data and error happened
     *
     * @param date        date of agenda needed to be shown
     * @param forceRemote true of remote call must be done, false otherwise
     */
    void loadAgendaForDate(String date, boolean forceRemote) {
        if (dataManager.getSavedAgendaByDate(date) == null) {
            getMvpView().showProgress();
        }

        subscriptions.add(dataManager.getAgendaForDate(date, forceRemote)
                .doOnError(e -> handleAgendaFetchingError(date, e))
                .retryWhen(errorsNotif -> RxUtil.retryOnBackOnlinePolice(errorsNotif, eventBus))
                .subscribe(x -> getMvpView().hideRefreshSign(), e -> Timber.e(e, "")));
    }

    /**
     * Subscribe on all agenda changes saved on DB. Show that changes in View. Show error screen if error happened
     *
     * @param date date of agenda needed to be shown
     */
    void subscribeOnAgenda(String date) {
        subscriptions.add(dataManager.getSavedAgendaByDateAsObservable(date)
                .subscribe(agenda -> getMvpView().showAgenda(agenda),
                        e -> {
                            Timber.e(e, "Can't get agenda");
                            getMvpView().showError();
                        }));
    }

    private void handleAgendaFetchingError(String date, Throwable e) {
        getMvpView().hideRefreshSign();
        if (e instanceof RetrofitException) {
            if (((RetrofitException) e).getKind() == RetrofitException.Kind.NETWORK) {
                getMvpView().showNoNetworkNotification();

                if (dataManager.getSavedAgendaByDate(date) == null) {
                    getMvpView().showError();
                }
            } else {
                throw new RuntimeException(e);
            }
        } else {
            getMvpView().showError();
        }
    }
}

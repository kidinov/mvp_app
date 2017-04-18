package org.kidinov.rijksmuseum.ui.museum.collection;

import org.kidinov.rijksmuseum.data.DataManager;
import org.kidinov.rijksmuseum.data.model.collection.Collection;
import org.kidinov.rijksmuseum.data.remote.RetrofitException;
import org.kidinov.rijksmuseum.injection.annotation.ConfigPersistent;
import org.kidinov.rijksmuseum.ui.base.BasePresenter;
import org.kidinov.rijksmuseum.util.C;
import org.kidinov.rijksmuseum.util.RxEventBus;
import org.kidinov.rijksmuseum.util.RxUtil;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;


@ConfigPersistent
public class CollectionPresenter extends BasePresenter<CollectionView> {
    private final RxEventBus eventBus;
    private DataManager dataManager;
    private CompositeSubscription compositeSubscription;

    private boolean loadingMore;

    @Inject CollectionPresenter(DataManager dataManager, RxEventBus eventBus) {
        this.dataManager = dataManager;
        this.eventBus = eventBus;
    }

    @Override
    public void attachView(CollectionView mvpView) {
        super.attachView(mvpView);

        compositeSubscription = new CompositeSubscription();

        getMvpView().setTitle(C.COLLECTION_MAKER);
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(compositeSubscription);
    }

    /**
     * Subscribe on all collection changes saved on DB. Show that changes in View. Show error screen if error happened
     */
    void subscribeToCollection() {
        compositeSubscription.add(dataManager.getSavedCollectionAsObservable()
                .subscribe(collection -> getMvpView().showCollectionItems(collection.getArtObjects()),
                        e -> {
                            Timber.e(e, "There was an error loading collection from db");
                            getMvpView().showError();
                        }));
    }

    /**
     * Load next page of data.
     * <p>
     * Shouldn't start loading in case if first in progress.
     * <p>
     * Shouldn't start loading in case if all data fetched.
     * <p>
     * Should notidy view when hide and show progress of pagination
     * <p>
     * Should repeat remote call in case if first was unsuccessful due internet connection, but device came back online.
     */
    void loadMore() {
        if (loadingMore) {
            return;
        }

        Collection savedCollection = dataManager.getSavedCollection();
        if (savedCollection != null && savedCollection.getArtObjects().size() == savedCollection.getCount()) {
            Timber.d("End of collection");
            return;
        }

        loadingMore = true;
        getMvpView().showProgressOfPagination();

        int page = savedCollection == null ? 0 : savedCollection.getArtObjects().size() / C.COLLECTION_PAGE_SIZE + 1;

        compositeSubscription.add(dataManager.getMoreCollectionFromServer(page)
                .doOnError(this::handleLoadMoreError)
                .retryWhen(errorsNotif -> RxUtil.retryOnBackOnlinePolice(errorsNotif, eventBus))
                .subscribe(x -> {
                    loadingMoreFinished();
                    Timber.d("load more - items fetched");
                }, e -> Timber.e(e, "")));
    }

    private void handleLoadMoreError(Throwable e) {
        Timber.e(e, "can't get more");
        loadingMoreFinished();
        if (e instanceof RetrofitException) {
            if (((RetrofitException) e).getKind() == RetrofitException.Kind.NETWORK) {
                getMvpView().showNoNetworkNotification();
            } else {
                throw new RuntimeException(e);
            }
        } else {
            getMvpView().showError();
        }
    }

    /**
     * Loads data.
     * <p>
     * Should show progress view if there is no data available.
     * <p>
     * Should repeat remote call in case if first was unsuccessful due internet connection, but device came back online.
     * <p>
     * Should hide refresh sign in case of error or normal execution
     * <p>
     * Should show error screen if there is no data and error happened
     * <p>
     * Should clear list if fetching successful in order to show proper data
     *
     * @param forceRemote true of remote call must be done, false otherwise
     */
    void reloadItems(boolean forceRemote) {
        loadingMore = false;

        if (dataManager.getSavedCollection() == null) {
            getMvpView().showProgress();
        }
        compositeSubscription.add(dataManager.getCollection(1, forceRemote)
                .doOnError(this::handleReloadItemsError)
                .retryWhen(errorsNotif -> RxUtil.retryOnBackOnlinePolice(errorsNotif, eventBus))
                .subscribe(x -> {
                    Timber.d("reloadItems - items fetched");
                    getMvpView().hideRefreshSign();
                    getMvpView().clearList();
                }, e -> Timber.e(e, "")));
    }

    private void loadingMoreFinished() {
        loadingMore = false;
        getMvpView().stopPaginationLoading();
    }

    private void handleReloadItemsError(Throwable e) {
        Timber.e(e, "can't get collection");
        getMvpView().hideRefreshSign();
        if (e instanceof RetrofitException) {
            if (((RetrofitException) e).getKind() == RetrofitException.Kind.NETWORK) {
                getMvpView().showNoNetworkNotification();

                if (dataManager.getSavedCollection() == null) {
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

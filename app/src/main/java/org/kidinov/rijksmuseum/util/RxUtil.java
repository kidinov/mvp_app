package org.kidinov.rijksmuseum.util;

import org.kidinov.rijksmuseum.data.remote.RetrofitException;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RxUtil {

    /**
     * Unsubscribe subscription from observable if subscribed
     *
     * @param subscription
     */
    public static void unsubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    /**
     * Simplyfy process of running network request on IO thread and getting result in UI thread. Need to use via
     * {@link rx.Observable#compose}
     */
    public static <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Use to retry failed request when device came back
     *
     * @param errorsNotif Observable which throw Throwable which was thrown by original Observable
     * @param eventBus    Event queue which will be notifyed about when network available
     * @return Observable with error if no network error and observable with next element otherwise
     */
    public static Observable retryOnBackOnlinePolice(Observable<? extends Throwable> errorsNotif, RxEventBus eventBus) {
        return errorsNotif.flatMap(e -> {
            if (e instanceof RetrofitException
                    && ((RetrofitException) e).getKind() == RetrofitException.Kind.NETWORK) {
                return eventBus.filteredObservable(BusEvents.NetworkEnabled.class)
                        .distinctUntilChanged();
            }
            return Observable.error(e);
        }).observeOn(AndroidSchedulers.mainThread());
    }
}

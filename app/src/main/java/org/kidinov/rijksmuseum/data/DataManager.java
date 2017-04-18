package org.kidinov.rijksmuseum.data;

import android.support.annotation.NonNull;

import org.kidinov.rijksmuseum.data.local.DatabaseHelper;
import org.kidinov.rijksmuseum.data.local.PreferencesHelper;
import org.kidinov.rijksmuseum.data.model.ObjectWithLoadingTime;
import org.kidinov.rijksmuseum.data.model.agenda.Agenda;
import org.kidinov.rijksmuseum.data.model.collection.Collection;
import org.kidinov.rijksmuseum.data.remote.RijksMuseumService;
import org.kidinov.rijksmuseum.util.C;
import org.kidinov.rijksmuseum.util.DateUtil;
import org.kidinov.rijksmuseum.util.RxUtil;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Class performs work with data - remote or local
 */
@Singleton
public class DataManager {
    // 5 mins
    private static final int MAX_CACHE_TTL = 5 * 60 * 1000;

    private final RijksMuseumService rijksMuseumService;
    private final DatabaseHelper databaseHelper;

    @Inject
    public DataManager(RijksMuseumService rijksMuseumService, PreferencesHelper preferencesHelper,
                       DatabaseHelper databaseHelper) {
        this.rijksMuseumService = rijksMuseumService;
        this.databaseHelper = databaseHelper;
    }

    /**
     * @param date date of agenda
     * @return Observable which will emit agendas objects when it was changed in DB
     */
    public Observable<Agenda> getSavedAgendaByDateAsObservable(@NonNull String date) {
        return databaseHelper.getSavedAgendaByDateAsObservable(date)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * @param date @date of agenda
     * @return Agenda saved in DB or Null
     */
    public Agenda getSavedAgendaByDate(@NonNull String date) {
        return databaseHelper.getSavedAgendaByDate(date);
    }

    /**
     * If not forced remote call, check if there is fresh data in DB and return it, otherwise perform remote call.
     * Saves data got remotely in DB
     *
     * @param date        Date of required agenda
     * @param forceRemote true of remote call must be done, false otherwise
     * @return Observable which emits relevance Agenda object
     */
    public Observable<Agenda> getAgendaForDate(@NonNull String date, boolean forceRemote) {
        return Observable.just(databaseHelper.getSavedAgendaByDate(date))
                .flatMap(agenda -> {
                    if (forceRemote || agenda == null || cacheObjectExpired(agenda)) {
                        return rijksMuseumService.getAgenda(date)
                                .compose(RxUtil.applySchedulers())
                                .flatMap(agenda1 -> databaseHelper.saveAgendaAndDeleteOld(agenda1, date));
                    } else {
                        return Observable.just(agenda);
                    }
                });
    }

    /**
     * @return Observable which will emit collections objects when it was changed in DB
     */
    public Observable<Collection> getSavedCollectionAsObservable() {
        return databaseHelper.getSavedCollectionItemsAsObservable()
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * @return Collection saved in DB or Null
     */
    public Collection getSavedCollection() {
        return databaseHelper.getSavedCollection();
    }

    /**
     * If not forced remote call, check if there is fresh data in DB and return it, otherwise perform remote call.
     * Saves data got remotely in DB
     *
     * @param page        page of date which need to be fetched
     * @param forceRemote true of remote call must be done, false otherwise
     * @return Observable which emits relevance Collection object
     */
    public Observable<Collection> getCollection(int page, boolean forceRemote) {
        return Observable.just(getSavedCollection())
                .flatMap(collection -> {
                    if (forceRemote || collection == null || cacheObjectExpired(collection)) {
                        return doGetCollectionFromServer(page, true);
                    } else {
                        return Observable.just(collection);
                    }
                });
    }

    /**
     * Get more data from server. Doesn't clear data in db, just append
     *
     * @param page page of date which need to be fetched
     * @return Observable which emits relevance Collection object
     */
    public Observable<Collection> getMoreCollectionFromServer(int page) {
        return doGetCollectionFromServer(page, false);
    }

    private Observable<Collection> doGetCollectionFromServer(int page, boolean removeOldOnSuccess) {
        return rijksMuseumService.getCollectionByMaker(C.COLLECTION_MAKER, page,
                C.COLLECTION_PAGE_SIZE, C.COLLECTION_ORDER, true)
                .compose(RxUtil.applySchedulers())
                .flatMap(collection -> {
                    if (removeOldOnSuccess) {
                        databaseHelper.deleteCollection();
                    }
                    return databaseHelper.saveCollectionOrAppendArtObjects(collection);
                });
    }

    private boolean cacheObjectExpired(@NonNull ObjectWithLoadingTime objectWithLoadingTime) {
        return DateUtil.getDateDiff(objectWithLoadingTime.getLoadingTime(), new Date(), TimeUnit.MILLISECONDS) >
                MAX_CACHE_TTL;
    }

}

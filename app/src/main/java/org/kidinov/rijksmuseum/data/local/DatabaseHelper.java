package org.kidinov.rijksmuseum.data.local;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.kidinov.rijksmuseum.data.model.agenda.Agenda;
import org.kidinov.rijksmuseum.data.model.collection.ArtObject;
import org.kidinov.rijksmuseum.data.model.collection.Collection;
import org.kidinov.rijksmuseum.util.C;
import org.kidinov.rijksmuseum.util.DateUtil;

import java.util.Date;

import javax.inject.Singleton;

import io.realm.Realm;
import rx.Observable;
import timber.log.Timber;

/**
 * Class that implements work with DB. Realm in current case.
 */

@Singleton
public class DatabaseHelper {
    private final Realm realm;

    public DatabaseHelper(Realm realm) {
        this.realm = realm;
    }

    /**
     * Saves agenda to db and remove old (less then current date) saved agendas
     *
     * @param agenda to save
     * @param date   date of agenda. Uses as PK for that object
     * @return Observable which emits saved agenda and then completes
     */
    public Observable<Agenda> saveAgendaAndDeleteOld(@NonNull final Agenda agenda, @NonNull final String date) {
        return deleteOldSavedAgendas()
                .flatMap(x -> Observable.create(subscriber -> {
                            realm.executeTransaction(realm -> {
                                agenda.setAgendaDate(DateUtil.parseDate(date, C.AGENDA_API_DATE_FORMAT));
                                agenda.setAgendaDateString(date);
                                agenda.setLoadingTime(new Date());
                                Agenda savedAgenda = realm.copyToRealmOrUpdate(agenda);
                                subscriber.onNext(savedAgenda);
                                subscriber.onCompleted();
                                Timber.d("agenda saved - %s", savedAgenda);
                            });
                        }
                ));
    }

    private Observable<Agenda> deleteOldSavedAgendas() {
        return Observable.create(subscriber -> {
            realm.executeTransaction(realm ->
                    realm.where(Agenda.class).lessThan("agendaDate", DateUtil.getDateNDaysDiff(-1))
                            .findAll()
                            .deleteAllFromRealm());
            subscriber.onNext(null);
            subscriber.onCompleted();
        });
    }

    /**
     * @param date date of agenda
     * @return Agenda saved in DB or Null
     */
    public @Nullable Agenda getSavedAgendaByDate(@NonNull String date) {
        return realm.where(Agenda.class).equalTo("agendaDateString", date).findFirst();
    }

    /**
     * @param date date of agenda
     * @return Observable which will emit agendas objects when it was changed in DB
     */
    public Observable<Agenda> getSavedAgendaByDateAsObservable(@NonNull String date) {
        return realm.where(Agenda.class)
                .equalTo("agendaDateString", date)
                .findAll()
                .<Agenda>asObservable()
                .flatMapIterable(x -> x)
                .filter(agenda -> agenda != null);
    }

    /**
     * @param newCollection collection to save or to append
     * @return saved collection
     */
    public Observable<Collection> saveCollectionOrAppendArtObjects(@NonNull final Collection newCollection) {
        return Observable.create(subscriber -> {
            realm.executeTransaction(realm -> {
                Collection saved = getSavedCollection();
                newCollection.setLoadingTime(new Date());
                newCollection.setMaker(C.COLLECTION_MAKER);
                if (saved != null) {
                    saved.setLoadingTime(new Date());
                    saved.getArtObjects().addAll(realm.copyToRealmOrUpdate(newCollection.getArtObjects()));

                    int i = 0;
                    for (ArtObject artObject : saved.getArtObjects()) {
                        artObject.setFetchOrderNumber(i++);
                    }
                    subscriber.onNext(realm.copyToRealmOrUpdate(saved));
                } else {
                    int i = 0;
                    for (ArtObject artObject : newCollection.getArtObjects()) {
                        artObject.setFetchOrderNumber(i++);
                    }
                    subscriber.onNext(realm.copyToRealmOrUpdate(newCollection));
                }
                subscriber.onCompleted();
                Timber.d("collection saved - %s", newCollection);
            });
        });
    }

    /**
     * Delete collection from db
     */
    public void deleteCollection() {
        Timber.d("deleteCollection");
        realm.executeTransaction(realm -> {
            realm.where(Collection.class)
                    .findAll()
                    .deleteAllFromRealm();
        });
    }

    /**
     * @return Observable which will emit collections objects when it was changed in DB
     */
    public Observable<Collection> getSavedCollectionItemsAsObservable() {
        return realm.where(Collection.class)
                .equalTo("maker", C.COLLECTION_MAKER)
                .findAll()
                .<Agenda>asObservable()
                .flatMapIterable(x -> x)
                .filter(collection -> collection != null);
    }

    /**
     * @return Collection saved in DB or Null
     */
    public @Nullable Collection getSavedCollection() {
        return realm.where(Collection.class).findFirst();
    }
}

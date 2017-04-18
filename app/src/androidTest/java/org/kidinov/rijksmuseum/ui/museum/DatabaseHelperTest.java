package org.kidinov.rijksmuseum.ui.museum;

import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.kidinov.rijksmuseum.data.local.DatabaseHelper;
import org.kidinov.rijksmuseum.data.model.agenda.Agenda;
import org.kidinov.rijksmuseum.data.model.collection.ArtObject;
import org.kidinov.rijksmuseum.data.model.collection.Collection;
import org.kidinov.rijksmuseum.test.common.TestDataFactory;
import org.kidinov.rijksmuseum.util.DateUtil;
import org.kidinov.rijksmuseum.util.ListUtil;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rx.functions.Action0;
import rx.observers.TestSubscriber;
import rx.subscriptions.CompositeSubscription;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Test runs on Dalvik. Testing class - {@link DatabaseHelper}
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {
    private DatabaseHelper databaseHelper;
    private Realm defaultInstance;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private CompositeSubscription compositeSubscription;

    @Before
    public void setup() throws IOException {
        File tempFolder = testFolder.newFolder("realmdata");
        runOnUi(() -> {
            RealmConfiguration config =
                    new RealmConfiguration.Builder(InstrumentationRegistry.getTargetContext(), tempFolder)
                            .build();
            defaultInstance = Realm.getInstance(config);
            databaseHelper = new DatabaseHelper(defaultInstance);

            compositeSubscription = new CompositeSubscription();
        });
    }

    @After
    public void release() {
        runOnUi(() -> {
            defaultInstance.close();
            compositeSubscription.unsubscribe();
        });
    }

    @Test
    public void saveAgendaSavesProperData() {
        String date = "10-10-10";
        Agenda agenda = TestDataFactory.makeAgenda(date, 10, "");

        runOnUi(() -> {
            TestSubscriber<Agenda> testObserver = new TestSubscriber<>();
            databaseHelper.saveAgendaAndDeleteOld(agenda, date)
                    .subscribe(testObserver);

            Agenda savedAgenda = getSavedAgendaByDate(date);

            testObserver.assertNoErrors();

            assertTrue(ListUtil.compareListItems(agenda.getOptions(), savedAgenda.getOptions()));
            assertEquals(agenda.getAgendaDateString(), savedAgenda.getAgendaDateString());
        });
    }

    @Test
    public void saveAgendaDeleteOldAgendas() {
        String dateOldAgenda = "10-10-10";
        Agenda oldAgenda = TestDataFactory.makeAgenda(dateOldAgenda, 10, "");
        oldAgenda.setLoadingTime(DateUtil.getDateNDaysDiff(-5));
        String dateNewAgenda = "11-10-10";
        Agenda newAgenda = TestDataFactory.makeAgenda(dateNewAgenda, 10, "");
        oldAgenda.setLoadingTime(new Date());

        runOnUi(() -> {
            TestSubscriber<Agenda> testObserver = new TestSubscriber<>();
            databaseHelper.saveAgendaAndDeleteOld(oldAgenda, dateOldAgenda)
                    .subscribe(testObserver);
            testObserver.assertNoErrors();
            databaseHelper.saveAgendaAndDeleteOld(newAgenda, dateNewAgenda)
                    .subscribe(testObserver);
            testObserver.assertNoErrors();

            assertNull(getSavedAgendaByDate(dateOldAgenda));
            assertEquals(newAgenda, getSavedAgendaByDate(dateNewAgenda));
        });
    }

    @Test
    public void getSavedAgendaByDateAsObservableEmitAgendaWhenItSavedToDb() {
        String date = "10-10-10";
        Agenda agenda = TestDataFactory.makeAgenda(date, 10, "");

        runOnUi(() -> {
            saveAgenda(agenda);
            TestSubscriber<Agenda> testObserver = new TestSubscriber<>();
            databaseHelper.getSavedAgendaByDateAsObservable(date)
                    .subscribe(testObserver);
            testObserver.assertNoErrors();

            assertEquals(agenda, testObserver.getOnNextEvents().get(0));
        });
    }

    @Test
    public void saveCollectionSavesProperDate() {
        Collection collection = TestDataFactory.makeCollection(10, "");

        runOnUi(() -> {
            TestSubscriber<Collection> testObserver = new TestSubscriber<>();
            databaseHelper.saveCollectionOrAppendArtObjects(collection)
                    .subscribe(testObserver);

            Collection savedCollection = getSavedCollection();

            testObserver.assertNoErrors();

            assertTrue(ListUtil.compareListItems(collection.getArtObjects(), savedCollection.getArtObjects()));
            assertEquals(collection, savedCollection);
        });
    }

    @Test
    public void saveCollectionSecondCollectionAppendArtObjectsToFirstAndSetFetchNumbers() {
        Collection collection1 = TestDataFactory.makeCollection(10, "");
        Collection collection2 = TestDataFactory.makeCollection(10, "");

        runOnUi(() -> {
            TestSubscriber<Collection> testObserver = new TestSubscriber<>();
            databaseHelper.saveCollectionOrAppendArtObjects(collection1)
                    .subscribe(testObserver);
            testObserver.assertNoErrors();
            databaseHelper.saveCollectionOrAppendArtObjects(collection2)
                    .subscribe(testObserver);
            testObserver.assertNoErrors();

            Collection savedCollection = getSavedCollection();

            collection1.getArtObjects().addAll(collection2.getArtObjects());
            assertTrue(ListUtil.compareListItems(collection1.getArtObjects(), savedCollection.getArtObjects()));
            Integer i = 0;
            for (ArtObject artObject : savedCollection.getArtObjects()) {
                assertEquals(artObject.getFetchOrderNumber(), i);
                i++;
            }
        });
    }

    @Test
    public void getSavedCollectionItemsAsObservableEmitCollectionWhenItSavedToDb() {
        Collection collection = TestDataFactory.makeCollection(10, "");

        runOnUi(() -> {
            saveCollection(collection);
            TestSubscriber<Collection> testObserver = new TestSubscriber<>();
            databaseHelper.getSavedCollectionItemsAsObservable()
                    .subscribe(testObserver);
            testObserver.assertNoErrors();

            assertEquals(collection, testObserver.getOnNextEvents().get(0));
        });
    }


    @Nullable
    private Agenda getSavedAgendaByDate(String date) {
        return defaultInstance.where(Agenda.class).equalTo("agendaDateString", date).findFirst();
    }

    @Nullable
    private Collection getSavedCollection() {
        return defaultInstance.where(Collection.class).findFirst();
    }

    private void saveAgenda(Agenda agenda) {
        defaultInstance.executeTransaction(realm -> realm.copyToRealmOrUpdate(agenda));
    }

    private void saveCollection(Collection collection) {
        defaultInstance.executeTransaction(realm -> realm.copyToRealmOrUpdate(collection));
    }

    private void runOnUi(Action0 action) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(action::call);
    }

}
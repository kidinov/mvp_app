package org.kidinov.rijksmuseum;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kidinov.rijksmuseum.data.DataManager;
import org.kidinov.rijksmuseum.data.local.DatabaseHelper;
import org.kidinov.rijksmuseum.data.local.PreferencesHelper;
import org.kidinov.rijksmuseum.data.model.agenda.Agenda;
import org.kidinov.rijksmuseum.data.model.collection.Collection;
import org.kidinov.rijksmuseum.data.remote.RijksMuseumService;
import org.kidinov.rijksmuseum.test.common.TestDataFactory;
import org.kidinov.rijksmuseum.util.C;
import org.kidinov.rijksmuseum.util.DateUtil;
import org.kidinov.rijksmuseum.util.ListUtil;
import org.kidinov.rijksmuseum.util.RxSchedulersOverrideRule;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import rx.Observable;
import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JVM test of {@link DataManager}
 * class with mocked dependencies
 */
@RunWith(MockitoJUnitRunner.class)
public class DataManagerTest {
    private static final String AGENDA_DATE = "10-10-10";

    @Mock
    DatabaseHelper mockDatabaseHelper;
    @Mock
    PreferencesHelper mockPreferencesHelper;
    @Mock
    RijksMuseumService mockRijksMuseumService;
    private DataManager dataManager;

    @Rule
    public RxSchedulersOverrideRule rxSchedulersOverrideRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() {
        dataManager = new DataManager(mockRijksMuseumService, mockPreferencesHelper, mockDatabaseHelper);
    }

    @Test
    public void getSavedAgendaByDateReturnSavedAgenda() {
        Agenda agenda = TestDataFactory.makeAgenda(AGENDA_DATE, 20, "");
        stubServiceAndDbHelper(agenda);

        assertTrue((agenda.getAgendaDateString()
                .equals(dataManager.getSavedAgendaByDate(AGENDA_DATE).getAgendaDateString())));
    }

    @Test
    public void getSavedAgendaByDateAsObservableEmitSavedAgenda() {
        Agenda agenda = TestDataFactory.makeAgenda(AGENDA_DATE, 20, "");
        stubServiceAndDbHelper(agenda);

        TestSubscriber<Agenda> result = new TestSubscriber<>();
        dataManager.getSavedAgendaByDateAsObservable(AGENDA_DATE)
                .subscribe(result);
        result.assertNoErrors();
        Agenda nextEvent = result.getOnNextEvents().get(0);
        assertTrue(ListUtil.compareListItems(agenda.getOptions(), nextEvent.getOptions()));
    }

    @Test
    public void getAgendaForDateCallsApiAndDbHelper() {
        Agenda agenda = TestDataFactory.makeAgenda(AGENDA_DATE, 20, "");
        stubServiceAndDbHelper(agenda);

        TestSubscriber<Agenda> testObserver = new TestSubscriber<>();
        dataManager.getAgendaForDate(AGENDA_DATE, true).subscribe(testObserver);
        testObserver.assertNoErrors();
        verify(mockRijksMuseumService).getAgenda(AGENDA_DATE);
        verify(mockDatabaseHelper).getSavedAgendaByDate(AGENDA_DATE);
        verify(mockDatabaseHelper).saveAgendaAndDeleteOld(agenda, AGENDA_DATE);
    }

    @Test
    public void getAgendaForDateNotCallsApiIfFreshDateInDb() {
        Agenda agenda = TestDataFactory.makeAgenda(AGENDA_DATE, 20, "");
        agenda.setLoadingTime(new Date());
        stubServiceAndDbHelper(agenda);

        TestSubscriber<Agenda> testObserver = new TestSubscriber<>();
        dataManager.getAgendaForDate(AGENDA_DATE, false).subscribe(testObserver);
        testObserver.assertNoErrors();
        verify(mockRijksMuseumService, never()).getAgenda(AGENDA_DATE);
        verify(mockDatabaseHelper).getSavedAgendaByDate(AGENDA_DATE);
        verify(mockDatabaseHelper, never()).saveAgendaAndDeleteOld(agenda, AGENDA_DATE);
    }

    @Test
    public void getAgendaForDateCallsApiIfOldDateInDb() {
        Agenda agenda = TestDataFactory.makeAgenda(AGENDA_DATE, 20, "");
        agenda.setLoadingTime(DateUtil.getDateNDaysDiff(-1));
        stubServiceAndDbHelper(agenda);

        TestSubscriber<Agenda> testObserver = new TestSubscriber<>();
        dataManager.getAgendaForDate(AGENDA_DATE, false).subscribe(testObserver);
        testObserver.assertNoErrors();
        verify(mockRijksMuseumService).getAgenda(AGENDA_DATE);
        verify(mockDatabaseHelper).getSavedAgendaByDate(AGENDA_DATE);
        verify(mockDatabaseHelper).saveAgendaAndDeleteOld(agenda, AGENDA_DATE);
    }

    @Test
    public void getAgendaForDateNotCallsSavedInDbIfApiFails() {
        when(mockRijksMuseumService.getAgenda(AGENDA_DATE))
                .thenReturn(Observable.error(new RuntimeException()));

        TestSubscriber<Agenda> testObserver = new TestSubscriber<>();
        dataManager.getAgendaForDate(AGENDA_DATE, false).subscribe(testObserver);
        testObserver.assertError(RuntimeException.class);
        verify(mockRijksMuseumService).getAgenda(AGENDA_DATE);
        verify(mockDatabaseHelper, never()).saveAgendaAndDeleteOld(any(Agenda.class), anyString());
    }

    @Test
    public void getCollectionFromServerSaveProperDateInDb() {
        Collection collection = TestDataFactory.makeCollection(20, "");
        stubServiceAndDbHelper(collection, 1);

        TestSubscriber<Collection> result = new TestSubscriber<>();
        dataManager.getCollection(1, true).subscribe(result);
        result.assertNoErrors();
        Collection nextEvent = result.getOnNextEvents().get(0);
        assertTrue(ListUtil.compareListItems(collection.getArtObjects(), nextEvent.getArtObjects()));
    }

    @Test
    public void getCollectionFromServerCallDbHelperAndApi() {
        Collection collection = TestDataFactory.makeCollection(20, "");
        stubServiceAndDbHelper(collection, 1);

        TestSubscriber<Collection> result = new TestSubscriber<>();
        dataManager.getCollection(1, true).subscribe(result);
        result.assertNoErrors();
        verify(mockRijksMuseumService).getCollectionByMaker(C.COLLECTION_MAKER, 1, C.COLLECTION_PAGE_SIZE,
                C.COLLECTION_ORDER, true);
        verify(mockDatabaseHelper).getSavedCollection();
        verify(mockDatabaseHelper).saveCollectionOrAppendArtObjects(collection);
    }

    @Test
    public void getCollectionFromServerNotCallApiIfFreshData() {
        Collection collection = TestDataFactory.makeCollection(20, "");
        collection.setLoadingTime(new Date());
        stubServiceAndDbHelper(collection, 1);

        TestSubscriber<Collection> result = new TestSubscriber<>();
        dataManager.getCollection(1, false).subscribe(result);
        result.assertNoErrors();
        verify(mockRijksMuseumService, never()).getCollectionByMaker(C.COLLECTION_MAKER, 1, C.COLLECTION_PAGE_SIZE,
                C.COLLECTION_ORDER, true);
        verify(mockDatabaseHelper, never()).saveCollectionOrAppendArtObjects(collection);
    }

    @Test
    public void getCollectionFromServerNotCallDbIfApiFailed() {
        Collection collection = TestDataFactory.makeCollection(20, "");

        when(mockRijksMuseumService.getCollectionByMaker(C.COLLECTION_MAKER, 1, C.COLLECTION_PAGE_SIZE,
                C.COLLECTION_ORDER, true))
                .thenReturn(Observable.error(new RuntimeException()));

        TestSubscriber<Collection> result = new TestSubscriber<>();
        dataManager.getCollection(1, false).subscribe(result);
        result.assertError(RuntimeException.class);
        verify(mockDatabaseHelper, never()).saveCollectionOrAppendArtObjects(collection);
    }

    @Test
    public void getMoreCollectionFromServerNotClearDataButSaveData() {
        Collection collection = TestDataFactory.makeCollection(20, "");
        stubServiceAndDbHelper(collection, 2);

        TestSubscriber<Collection> result = new TestSubscriber<>();
        dataManager.getMoreCollectionFromServer(2).subscribe(result);
        result.assertNoErrors();
        verify(mockDatabaseHelper, never()).deleteCollection();
        verify(mockDatabaseHelper).saveCollectionOrAppendArtObjects(collection);
    }

    @Test
    public void getMoreCollectionFromServerEmitErrorIfApiFailed() {
        Collection collection = TestDataFactory.makeCollection(20, "");
        when(mockRijksMuseumService.getCollectionByMaker(C.COLLECTION_MAKER, 1, C.COLLECTION_PAGE_SIZE,
                C.COLLECTION_ORDER, true))
                .thenReturn(Observable.error(new RuntimeException()));

        TestSubscriber<Collection> result = new TestSubscriber<>();
        dataManager.getMoreCollectionFromServer(1).subscribe(result);
        result.assertError(RuntimeException.class);
        verify(mockDatabaseHelper, never()).deleteCollection();
        verify(mockDatabaseHelper, never()).saveCollectionOrAppendArtObjects(collection);
    }

    private void stubServiceAndDbHelper(Agenda agenda) {
        when(mockRijksMuseumService.getAgenda(AGENDA_DATE))
                .thenReturn(Observable.just(agenda));
        when(mockDatabaseHelper.saveAgendaAndDeleteOld(agenda, AGENDA_DATE))
                .thenReturn(Observable.just(agenda));
        when(mockDatabaseHelper.getSavedAgendaByDate(AGENDA_DATE))
                .thenReturn(agenda);
        when(mockDatabaseHelper.getSavedAgendaByDateAsObservable(AGENDA_DATE))
                .thenReturn(Observable.just(agenda));
    }

    private void stubServiceAndDbHelper(Collection collection, int page) {
        when(mockRijksMuseumService.getCollectionByMaker(C.COLLECTION_MAKER, page, C.COLLECTION_PAGE_SIZE,
                C.COLLECTION_ORDER, true))
                .thenReturn(Observable.just(collection));
        when(mockDatabaseHelper.saveCollectionOrAppendArtObjects(collection))
                .thenReturn(Observable.just(collection));
        when(mockDatabaseHelper.getSavedCollection())
                .thenReturn(collection);
        when(mockDatabaseHelper.getSavedCollectionItemsAsObservable())
                .thenReturn(Observable.just(collection));
    }

}

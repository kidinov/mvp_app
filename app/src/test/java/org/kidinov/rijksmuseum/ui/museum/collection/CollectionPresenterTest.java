package org.kidinov.rijksmuseum.ui.museum.collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kidinov.rijksmuseum.data.DataManager;
import org.kidinov.rijksmuseum.data.model.collection.ArtObject;
import org.kidinov.rijksmuseum.data.model.collection.Collection;
import org.kidinov.rijksmuseum.data.remote.RetrofitException;
import org.kidinov.rijksmuseum.test.common.TestDataFactory;
import org.kidinov.rijksmuseum.util.RxEventBus;
import org.kidinov.rijksmuseum.util.RxSchedulersOverrideRule;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JVM test of {@link CollectionPresenter} class with mocked dependencies
 */
@RunWith(MockitoJUnitRunner.class)
public class CollectionPresenterTest {

    @Mock
    CollectionView collectionInstaView;
    @Mock
    DataManager mockDataManager;
    @Mock
    RxEventBus mockEventBus;

    private CollectionPresenter presenter;

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() {
        presenter = new CollectionPresenter(mockDataManager, mockEventBus);
        presenter.attachView(collectionInstaView);
    }

    @After
    public void tearDown() {
        presenter.detachView();
    }

    @Test
    public void reloadItemsAndShowProgressAndClearList() {
        Collection collection = TestDataFactory.makeCollection(10, "");
        when(mockDataManager.getCollection(1, true))
                .thenReturn(Observable.just(collection));

        presenter.reloadItems(true);
        verify(collectionInstaView).showProgress();
        verify(collectionInstaView).clearList();
        verify(collectionInstaView, never()).showError();
    }

    @Test
    public void reloadItemsAndNotShowProgressSignOfThereIsItemsInDb() {
        Collection collection = TestDataFactory.makeCollection(10, "");
        when(mockDataManager.getCollection(anyInt(), anyBoolean()))
                .thenReturn(Observable.just(collection));
        when(mockDataManager.getSavedCollection())
                .thenReturn(collection);

        presenter.reloadItems(false);
        verify(collectionInstaView, never()).showProgress();
        verify(collectionInstaView, never()).showError();
    }

    @Test
    public void reloadItemsAndShowErrorIfErrorHappened() {
        when(mockDataManager.getCollection(anyInt(), anyBoolean()))
                .thenReturn(Observable.error(new RuntimeException()));

        presenter.reloadItems(false);
        verify(collectionInstaView).hideRefreshSign();
        verify(collectionInstaView, never()).showNoNetworkNotification();
        verify(collectionInstaView).showProgress();
        verify(collectionInstaView).showError();
    }

    @Test
    public void reloadItemsAndShowNetworkErrorIfNetworkErrorHappened() {
        when(mockDataManager.getCollection(anyInt(), anyBoolean()))
                .thenReturn(Observable.error(TestDataFactory.makeRetrofitException(RetrofitException.Kind.NETWORK)));

        presenter.reloadItems(false);
        verify(collectionInstaView).hideRefreshSign();
        verify(collectionInstaView).showNoNetworkNotification();
        verify(collectionInstaView).showProgress();
        verify(collectionInstaView).showError();
    }

    @Test
    public void reloadItemsAndShowNetworkErrorIfNetworkErrorHappenedAndThereIsDataInDb() {
        Collection collection = TestDataFactory.makeCollection(10, "");
        when(mockDataManager.getSavedCollection())
                .thenReturn(collection);
        when(mockDataManager.getCollection(anyInt(), anyBoolean()))
                .thenReturn(Observable.error(TestDataFactory.makeRetrofitException(RetrofitException.Kind.NETWORK)));

        presenter.reloadItems(false);
        verify(collectionInstaView).hideRefreshSign();
        verify(collectionInstaView, never()).showError();
        verify(collectionInstaView).showNoNetworkNotification();
        verify(collectionInstaView, never()).showProgress();
    }

    @Test
    public void loadMoreShowProgressItem() {
        Collection collection = TestDataFactory.makeCollection(10, "");
        when(mockDataManager.getMoreCollectionFromServer(anyInt()))
                .thenReturn(Observable.just(collection));

        presenter.loadMore();
        verify(collectionInstaView, never()).showError();
        verify(collectionInstaView).showProgressOfPagination();
        verify(collectionInstaView).stopPaginationLoading();
    }

    @Test
    public void loadMoreDoesntDoRemoteCallIfEndOfList() {
        Collection collection = TestDataFactory.makeCollection(10, "");
        collection.setCount(10);
        when(mockDataManager.getMoreCollectionFromServer(anyInt()))
                .thenReturn(Observable.empty());
        when(mockDataManager.getSavedCollection())
                .thenReturn(collection);

        presenter.loadMore();
        verify(mockDataManager, never()).getMoreCollectionFromServer(anyInt());
    }

    @Test
    public void loadMoreCallShowNetworkNotificationIfNetworkErrorHappened() {
        when(mockDataManager.getMoreCollectionFromServer(anyInt()))
                .thenReturn(Observable.error(TestDataFactory.makeRetrofitException(RetrofitException.Kind.NETWORK)));

        presenter.loadMore();
        verify(collectionInstaView).showNoNetworkNotification();
        verify(collectionInstaView, never()).showCollectionItems(anyListOf(ArtObject.class));
        verify(collectionInstaView, never()).showError();
    }

    @Test
    public void loadMoreCallShowErrorIfErrorHappened() {
        when(mockDataManager.getMoreCollectionFromServer(anyInt()))
                .thenReturn(Observable.error(new RuntimeException()));

        presenter.loadMore();
        verify(collectionInstaView, never()).showNoNetworkNotification();
        verify(collectionInstaView, never()).showCollectionItems(anyListOf(ArtObject.class));
        verify(collectionInstaView).showError();
    }

    @Test
    public void subscribeToCollectionObservableReturnsCollection() {
        Collection collection = TestDataFactory.makeCollection(10, "");
        when(mockDataManager.getSavedCollectionAsObservable())
                .thenReturn(Observable.just(collection));

        presenter.subscribeToCollection();
        verify(collectionInstaView).showCollectionItems(collection.getArtObjects());
        verify(collectionInstaView, never()).showError();
    }

    @Test
    public void subscribeToObservableCallsNothingIfThereIsNoItems() {
        when(mockDataManager.getSavedCollectionAsObservable())
                .thenReturn(Observable.empty());

        presenter.subscribeToCollection();
        verify(collectionInstaView, never()).showProgress();
        verify(collectionInstaView, never()).showCollectionItems(anyListOf(ArtObject.class));
        verify(collectionInstaView, never()).showError();
    }

    @Test
    public void subscribeToObservableShowErrorIfErrorHappened() {
        when(mockDataManager.getSavedCollectionAsObservable())
                .thenReturn(Observable.error(new RuntimeException()));

        presenter.subscribeToCollection();
        verify(collectionInstaView, never()).showProgress();
        verify(collectionInstaView, never()).showCollectionItems(anyListOf(ArtObject.class));
        verify(collectionInstaView).showError();
    }

}
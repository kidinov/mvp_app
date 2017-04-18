package org.kidinov.rijksmuseum.ui.museum.collection;


import org.kidinov.rijksmuseum.data.model.collection.ArtObject;
import org.kidinov.rijksmuseum.ui.base.MvpView;

import java.util.List;

interface CollectionView extends MvpView {
    /**
     * Show full screen progress
     */
    void showProgress();

    /**
     * Show progress of pagination
     */
    void showProgressOfPagination();

    /**
     * Show data
     *
     * @param items - items to show
     */
    void showCollectionItems(List<ArtObject> items);

    /**
     * Show full screen error
     */
    void showError();

    /**
     * Hide part screen progress
     */
    void hideRefreshSign();

    /**
     * Clear data from screen
     */
    void clearList();

    /**
     * Hode progress of pagination
     */
    void stopPaginationLoading();

    /**
     * Show no network notification
     */
    void showNoNetworkNotification();

    /**
     * Set title of view
     *
     * @param title - title of view
     */
    void setTitle(String title);
}

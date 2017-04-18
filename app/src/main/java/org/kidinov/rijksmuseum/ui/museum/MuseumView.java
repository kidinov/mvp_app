package org.kidinov.rijksmuseum.ui.museum;

import org.kidinov.rijksmuseum.ui.base.MvpView;


public interface MuseumView extends MvpView {
    void showAgenda();

    void showCollection();

    void closeDrawer();
}

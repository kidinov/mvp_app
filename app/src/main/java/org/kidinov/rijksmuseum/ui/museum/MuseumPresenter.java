package org.kidinov.rijksmuseum.ui.museum;

import org.kidinov.rijksmuseum.injection.annotation.ConfigPersistent;
import org.kidinov.rijksmuseum.ui.base.BasePresenter;

import javax.inject.Inject;


@ConfigPersistent
public class MuseumPresenter extends BasePresenter<MuseumView> {
    @Inject MuseumPresenter() {
    }

    @Override
    public void attachView(MuseumView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    void agendaClicked() {
        getMvpView().showAgenda();
        getMvpView().closeDrawer();
    }

    void collectionClicked() {
        getMvpView().showCollection();
        getMvpView().closeDrawer();
    }
}

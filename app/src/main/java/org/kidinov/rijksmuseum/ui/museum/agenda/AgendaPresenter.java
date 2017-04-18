package org.kidinov.rijksmuseum.ui.museum.agenda;

import org.kidinov.rijksmuseum.ui.base.BasePresenter;
import org.kidinov.rijksmuseum.util.C;
import org.kidinov.rijksmuseum.util.DateUtil;

import javax.inject.Inject;


public class AgendaPresenter extends BasePresenter<AgendaView> {

    @Inject AgendaPresenter() {
    }

    @Override
    public void attachView(AgendaView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    /**
     * Need to be called when new {@link android.support.v4.view.ViewPager} page became selected
     *
     * @param position position of page
     */
    void pageSelected(int position) {
        getMvpView().setTitle(DateUtil.getDateNDaysDiffAndFormat(position, C.AGENDA_API_DATE_FORMAT));
    }
}

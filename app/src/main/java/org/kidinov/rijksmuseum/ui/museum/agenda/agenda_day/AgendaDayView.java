package org.kidinov.rijksmuseum.ui.museum.agenda.agenda_day;

import org.kidinov.rijksmuseum.data.model.agenda.Agenda;
import org.kidinov.rijksmuseum.ui.base.MvpView;


public interface AgendaDayView extends MvpView {
    /**
     * Show full screen progress
     */
    void showProgress();

    /**
     * Show agenda
     *
     * @param agenda - agenda to show
     */
    void showAgenda(Agenda agenda);

    /**
     * Show full screen error
     */
    void showError();

    /**
     * Show no network notification
     */
    void showNoNetworkNotification();

    /**
     * Hide part screen progress
     */
    void hideRefreshSign();
}

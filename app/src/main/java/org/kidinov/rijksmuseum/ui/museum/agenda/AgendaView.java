package org.kidinov.rijksmuseum.ui.museum.agenda;

import org.kidinov.rijksmuseum.ui.base.MvpView;


public interface AgendaView extends MvpView {

    /**
     * Set title of view
     *
     * @param title - text which should be used as a Title
     */
    void setTitle(String title);
}

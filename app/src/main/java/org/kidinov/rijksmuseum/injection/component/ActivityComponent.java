package org.kidinov.rijksmuseum.injection.component;

import org.kidinov.rijksmuseum.injection.annotation.PerActivity;
import org.kidinov.rijksmuseum.injection.module.ActivityModule;
import org.kidinov.rijksmuseum.ui.museum.MuseumActivity;
import org.kidinov.rijksmuseum.ui.museum.agenda.AgendaFragment;
import org.kidinov.rijksmuseum.ui.museum.agenda.agenda_day.AgendaDayFragment;
import org.kidinov.rijksmuseum.ui.museum.collection.CollectionFragment;

import dagger.Subcomponent;

/**
 * This component inject dependencies to all Activities and Fragments across the application
 */
@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MuseumActivity o);

    void inject(AgendaFragment o);

    void inject(AgendaDayFragment o);

    void inject(CollectionFragment o);
}

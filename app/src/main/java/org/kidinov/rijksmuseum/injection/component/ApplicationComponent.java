package org.kidinov.rijksmuseum.injection.component;

import android.app.Application;
import android.content.Context;

import org.kidinov.rijksmuseum.data.DataManager;
import org.kidinov.rijksmuseum.injection.annotation.ApplicationContext;
import org.kidinov.rijksmuseum.injection.module.ApplicationModule;
import org.kidinov.rijksmuseum.injection.module.NetworkModule;
import org.kidinov.rijksmuseum.util.RxEventBus;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, NetworkModule.class})
public interface ApplicationComponent {

    @ApplicationContext
    Context context();

    Application application();

    DataManager dataManager();

    RxEventBus eventBus();

}

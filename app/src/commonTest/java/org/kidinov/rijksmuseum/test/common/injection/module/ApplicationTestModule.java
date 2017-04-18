package org.kidinov.rijksmuseum.test.common.injection.module;

import android.app.Application;
import android.content.Context;

import org.kidinov.rijksmuseum.data.DataManager;
import org.kidinov.rijksmuseum.data.remote.RijksMuseumService;
import org.kidinov.rijksmuseum.injection.annotation.ApplicationContext;
import org.kidinov.rijksmuseum.util.RxEventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

/**
 * Provides application-level dependencies for an app running on a testing environment
 * This allows injecting mocks if necessary.
 */
@Module
public class ApplicationTestModule {

    private final Application application;

    public ApplicationTestModule(Application application) {
        this.application = application;
    }

    @Provides
    Application provideApplication() {
        return application;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return application;
    }

    /*************
     * MOCKS
     *************/

    @Provides
    @Singleton
    DataManager provideDataManager() {
        return mock(DataManager.class);
    }

    @Provides
    @Singleton
    RijksMuseumService provideInstaService() {
        return mock(RijksMuseumService.class);
    }
    
    @Provides
    @Singleton
    RxEventBus provideEventBus() {
        return mock(RxEventBus.class);
    }

}

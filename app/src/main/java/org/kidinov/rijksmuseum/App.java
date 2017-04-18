package org.kidinov.rijksmuseum;

import android.app.Application;
import android.content.Context;

import org.kidinov.rijksmuseum.injection.component.ApplicationComponent;
import org.kidinov.rijksmuseum.injection.component.DaggerApplicationComponent;
import org.kidinov.rijksmuseum.injection.module.ApplicationModule;

import timber.log.Timber;

public class App extends Application {
    private ApplicationComponent applicationComponent;

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public ApplicationComponent getComponent() {
        if (applicationComponent == null) {
            applicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }
        return applicationComponent;
    }

    /**
     * Used when it's need to replace the component with a test specific one
     */
    public void setComponent(ApplicationComponent applicationComponent) {
        this.applicationComponent = applicationComponent;
    }
}

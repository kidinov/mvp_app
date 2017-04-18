package org.kidinov.rijksmuseum.injection.module;

import android.app.Activity;
import android.content.Context;

import org.kidinov.rijksmuseum.injection.annotation.ActivityContext;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    private Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides
    Activity provideActivity() {
        return activity;
    }

    @Provides
    @ActivityContext
    Context providesContext() {
        return activity;
    }
}

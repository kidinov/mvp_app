package org.kidinov.rijksmuseum.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import org.kidinov.rijksmuseum.injection.annotation.ApplicationContext;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Preferences helper class which uses for saving some short living, unstructured data
 */
@Singleton
public class PreferencesHelper {
    private static final String PREF_FILE_NAME = "rijksmuseum_pref_file";

    private final SharedPreferences pref;

    @Inject PreferencesHelper(@ApplicationContext Context context) {
        pref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

}

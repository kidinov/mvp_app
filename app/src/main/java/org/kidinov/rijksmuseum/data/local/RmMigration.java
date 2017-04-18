package org.kidinov.rijksmuseum.data.local;

import javax.inject.Singleton;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import timber.log.Timber;

/**
 * Migration of Realm DB from version to another
 */
@Singleton
public class RmMigration implements RealmMigration {

    @Override
    public void migrate(final DynamicRealm realm, long oldVersion, long newVersion) {
        Timber.i("migration from %d to %d", oldVersion, newVersion);
    }
}
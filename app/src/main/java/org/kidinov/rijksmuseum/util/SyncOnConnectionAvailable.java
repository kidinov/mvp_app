package org.kidinov.rijksmuseum.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import org.kidinov.rijksmuseum.App;

import timber.log.Timber;

/**
 * {@link BroadcastReceiver} which catch @{link ConnectivityManager#CONNECTIVITY_ACTION} intent and post
 * {@link org.kidinov.rijksmuseum.util.BusEvents.AuthenticationError} in {@link RxEventBus}
 */
public class SyncOnConnectionAvailable extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        RxEventBus eventBus = App.get(context).getComponent().eventBus();

        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)
                && NetworkUtil.isNetworkConnected(context)) {
            Timber.i("Connection is now available");
            eventBus.post(new BusEvents.NetworkEnabled());
        }
    }
}

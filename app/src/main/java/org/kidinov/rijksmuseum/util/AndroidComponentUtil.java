package org.kidinov.rijksmuseum.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Set of handy android related methods
 */
public final class AndroidComponentUtil {

    /**
     * Enable/Disable component, e.g. {@link android.content.BroadcastReceiver}
     */
    public static void toggleComponent(Context context, Class componentClass, boolean enable) {
        ComponentName componentName = new ComponentName(context, componentClass);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(componentName,
                enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

}

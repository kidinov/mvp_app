package org.kidinov.rijksmuseum.util;


import android.support.annotation.NonNull;

import java.util.List;

/**
 * Set of handy list related methods
 */
public class ListUtil {
    public static <T> boolean compareListItems(@NonNull List<T> list1, @NonNull List<T> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }

        boolean equal;
        for (T t : list1) {
            equal = false;
            for (T t1 : list2) {
                if (t.equals(t1)) {
                    equal = true;
                    break;
                }
            }
            if (!equal) {
                return false;
            }
        }
        return true;
    }
}

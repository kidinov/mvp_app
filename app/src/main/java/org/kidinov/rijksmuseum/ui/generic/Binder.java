package org.kidinov.rijksmuseum.ui.generic;


import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Class which makes it easier to have several types of objects inside
 * {@link android.support.v7.widget.RecyclerView.Adapter}
 *
 * @param <T> type of object which will be binded to view item
 */
public abstract class Binder<T> extends RecyclerView.ViewHolder {
    public Binder(View itemView) {
        super(itemView);
    }

    public abstract void bind(T chatMessage);
}
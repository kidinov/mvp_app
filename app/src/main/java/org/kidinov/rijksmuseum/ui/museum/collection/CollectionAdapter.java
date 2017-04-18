package org.kidinov.rijksmuseum.ui.museum.collection;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.kidinov.rijksmuseum.R;
import org.kidinov.rijksmuseum.data.model.collection.ArtObject;
import org.kidinov.rijksmuseum.data.model.collection.WebImage;
import org.kidinov.rijksmuseum.ui.generic.Binder;

import java.util.Collection;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


public class CollectionAdapter extends RecyclerView.Adapter<Binder<ArtObject>> {
    private static final String PROGRESS_ITEM_ID = "-1";

    private static final int NORMAL_ITEM = 0;
    private static final int PROGRESS = 1;

    private SortedList<ArtObject> sortedList;

    @Inject CollectionAdapter() {
        sortedList = new SortedList<>(ArtObject.class, new SortedListAdapterCallback<ArtObject>(this) {

            @Override
            public int compare(ArtObject o1, ArtObject o2) {
                if (o1.getId().equals(PROGRESS_ITEM_ID) && o2.getId().equals(PROGRESS_ITEM_ID)) {
                    return 0;
                }

                if (o1.getId().equals(PROGRESS_ITEM_ID)) {
                    return 1;
                } else if (o2.getId().equals(PROGRESS_ITEM_ID)) {
                    return -1;
                } else {
                    return o1.getFetchOrderNumber().compareTo(o2.getFetchOrderNumber());
                }
            }

            @Override
            public boolean areContentsTheSame(ArtObject oldItem, ArtObject newItem) {
                WebImage oldWebImage = oldItem.getWebImage();
                WebImage newWebImage = newItem.getWebImage();

                if (oldWebImage == newWebImage && oldWebImage == null &&
                        oldItem.getTitle().equals(newItem.getTitle())) {
                    return true;
                }

                if (oldWebImage.getUrl().equals(newWebImage.getUrl())
                        && oldItem.getTitle().equals(newItem.getTitle())) {
                    return true;
                }

                return false;
            }

            @Override
            public boolean areItemsTheSame(ArtObject item1, ArtObject item2) {
                return item1.equals(item2);
            }
        });
    }

    @Override
    public Binder<ArtObject> onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case NORMAL_ITEM:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.collection_item, parent, false);

                return new InstaViewHolder(itemView);
            case PROGRESS:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.progress_item, parent, false);
                return new ProgressViewHolder(itemView);
        }

        throw new RuntimeException(String.format("wrong view type - %d", viewType));
    }

    @Override
    public int getItemViewType(int position) {
        if (sortedList.get(position).getId().equals(PROGRESS_ITEM_ID)) {
            return PROGRESS;
        }
        return NORMAL_ITEM;
    }

    @Override
    public void onBindViewHolder(Binder<ArtObject> holder, int position) {
        holder.bind(sortedList.get(position));
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }

    void showLoading(boolean show) {
        Timber.d("showLoading show - %s", show);
        ArtObject item = new ArtObject();
        item.setId(PROGRESS_ITEM_ID);
        item.setTitle("");
        item.setFetchOrderNumber(Integer.MAX_VALUE);
        if (show) {
            sortedList.add(item);
        } else {
            sortedList.remove(item);
        }
    }

    void addItems(Collection<ArtObject> artObjects) {
        sortedList.addAll(artObjects);
    }

    void clearList() {
        sortedList.clear();
    }

    class InstaViewHolder extends Binder<ArtObject> {
        @BindView(R.id.art_object_title)
        TextView title;
        @BindView(R.id.art_object_image)
        ImageView image;

        InstaViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bind(@NonNull ArtObject item) {
            title.setText(item.getTitle());
            Glide.with(image.getContext())
                    .load(item.getWebImage() == null ? "" : item.getWebImage().getUrl())
                    .placeholder(R.color.gray2)
                    .error(R.color.gray2)
                    .into(image);
        }
    }

    private class ProgressViewHolder extends Binder<ArtObject> {

        ProgressViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(@Nullable ArtObject chatMessage) {
        }
    }
}

package org.kidinov.rijksmuseum.ui.museum.agenda.agenda_day;

import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.kidinov.rijksmuseum.R;
import org.kidinov.rijksmuseum.data.model.agenda.Option;
import org.kidinov.rijksmuseum.ui.generic.Binder;

import java.util.Collection;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AgendaDayRecyclerViewAdapter extends RecyclerView.Adapter<Binder<Option>> {
    private SortedList<Option> sortedList;

    @Inject AgendaDayRecyclerViewAdapter() {
        sortedList = new SortedList<>(Option.class, new SortedListAdapterCallback<Option>(this) {

            @Override
            public int compare(Option o1, Option o2) {
                return o1.getDate().compareTo(o2.getDate());
            }

            @Override
            public boolean areContentsTheSame(Option oldItem, Option newItem) {
                return oldItem.getPeriod().getText().equals(newItem.getPeriod().getText()) &&
                        oldItem.getExposition().getDescription().equals(newItem.getExposition().getDescription()) &&
                        oldItem.getExpositionType().getFriendlyName()
                                .equals(newItem.getExpositionType().getFriendlyName()) &&
                        oldItem.getExposition().getPrice().getAmount()
                                .equals(newItem.getExposition().getPrice().getAmount());
            }

            @Override
            public boolean areItemsTheSame(Option item1, Option item2) {
                return item1.getId().equals(item2.getId());
            }
        });
    }

    @NonNull static String formatPrice(@NonNull Option item) {
        return String.format(String.valueOf(item.getExposition().getPrice().getAmount()), "%1$,.2f") +
                " " +
                "€";
    }

    @Override
    public Binder<Option> onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.option_item, parent, false);
        return new AgendaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Binder<Option> holder, int position) {
        holder.bind(sortedList.get(position));
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }

    void addOptions(Collection<Option> options) {
        sortedList.addAll(options);
    }

    class AgendaViewHolder extends Binder<Option> {
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.price)
        TextView price;
        @BindView(R.id.exposition_type)
        TextView expositionType;
        @BindView(R.id.exposition_description)
        TextView description;

        AgendaViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bind(@NonNull Option item) {
            time.setText(item.getPeriod().getText());
            expositionType.setText(item.getExpositionType().getFriendlyName());
            description.setText(item.getExposition().getDescription());
            price.setText(formatPrice(item));
        }

    }
}

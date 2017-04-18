package org.kidinov.rijksmuseum.ui.museum.agenda.agenda_day;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import org.kidinov.rijksmuseum.R;
import org.kidinov.rijksmuseum.data.model.agenda.Agenda;
import org.kidinov.rijksmuseum.ui.base.BaseActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AgendaDayFragment extends Fragment implements AgendaDayView {
    public static final String DAY_BUNDLE_KEY = "day";
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.view_flipper)
    ViewFlipper viewFlipper;
    @Inject
    AgendaDayRecyclerViewAdapter adapter;
    @Inject
    AgendaDayPresenter presenter;
    private String date;

    public AgendaDayFragment() {
    }

    public static AgendaDayFragment newInstance(@NonNull String day) {
        AgendaDayFragment fragment = new AgendaDayFragment();
        Bundle args = new Bundle();
        args.putString(DAY_BUNDLE_KEY, day);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        date = getArguments().getString(DAY_BUNDLE_KEY);
    }

    @Override
    public @Nullable View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.agenda_day_fragment, container, false);
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);
        ButterKnife.bind(this, view);

        setupSwipeRefreshLayout();
        setupList();

        presenter.attachView(this);
        presenter.loadAgendaForDate(date, false);
        presenter.subscribeOnAgenda(date);

        return view;
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            onRetryClick();
        });
    }

    private void setupList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void changeScreenState(int state) {
        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(viewFlipper.findViewById(state)));
    }

    @OnClick(R.id.retry_button)
    void onRetryClick() {
        presenter.loadAgendaForDate(date, true);
    }

    @Override
    public void onDestroyView() {
        presenter.detachView();
        super.onDestroyView();
    }

    @Override
    public void showProgress() {
        changeScreenState(R.id.progress_view);
    }

    @Override
    public void hideRefreshSign() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showAgenda(Agenda agenda) {
        changeScreenState(R.id.swipe_refresh_layout);
        adapter.addOptions(agenda.getOptions());
    }

    @Override
    public void showError() {
        changeScreenState(R.id.error_view);
    }

    @Override
    public void showNoNetworkNotification() {
        Snackbar.make(viewFlipper, R.string.network_error, Snackbar.LENGTH_LONG).show();
    }
}

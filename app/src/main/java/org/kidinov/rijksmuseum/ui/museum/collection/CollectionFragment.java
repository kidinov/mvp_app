package org.kidinov.rijksmuseum.ui.museum.collection;

import android.os.Bundle;
import android.os.Handler;
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
import org.kidinov.rijksmuseum.data.model.collection.ArtObject;
import org.kidinov.rijksmuseum.ui.base.BaseActivity;
import org.kidinov.rijksmuseum.ui.generic.EndlessRecyclerOnScrollListener;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CollectionFragment extends Fragment implements CollectionView {
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.view_flipper)
    ViewFlipper viewFlipper;

    @Inject
    CollectionAdapter adapter;
    @Inject
    CollectionPresenter presenter;
    private EndlessRecyclerOnScrollListener listener;

    public static CollectionFragment newInstance() {
        return new CollectionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public @Nullable View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);
        View view = inflater.inflate(R.layout.collection_fragment, container, false);
        ButterKnife.bind(this, view);

        setupList();
        setupSwipeRefreshLayout();

        presenter.attachView(this);
        presenter.subscribeToCollection();
        presenter.reloadItems(false);

        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();

        presenter.detachView();
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            onRetryClick();
        });
    }

    private void setupList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        listener = new EndlessRecyclerOnScrollListener(layoutManager) {

            @Override
            public void onLoadMore() {
                presenter.loadMore();
            }
        };
        recyclerView.addOnScrollListener(listener);
    }

    @OnClick(R.id.retry_button)
    void onRetryClick() {
        presenter.reloadItems(true);
        listener.setPreviousTotal(0);
    }

    private void changeScreenState(int state) {
        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(viewFlipper.findViewById(state)));
    }

    private void changeScreenState(View state) {
        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(state));
    }

    @Override
    public void showProgress() {
        changeScreenState(R.id.progress_view);
    }

    @Override
    public void showProgressOfPagination() {
        //Handler here to avoid invoking sorted list modification when RV scrolling
        new Handler().post(() -> adapter.showLoading(true));
    }

    @Override
    public void showCollectionItems(List<ArtObject> items) {
        changeScreenState(swipeRefreshLayout);
        adapter.addItems(items);
    }

    @Override
    public void showError() {
        changeScreenState(R.id.error_view);
    }

    @Override
    public void hideRefreshSign() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void stopPaginationLoading() {
        new Handler().post(() -> adapter.showLoading(false));
    }

    @Override
    public void showNoNetworkNotification() {
        Snackbar.make(viewFlipper, R.string.network_error, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void clearList() {
        adapter.clearList();
    }

    @Override
    public void setTitle(String title) {
        getActivity().setTitle(title);
    }
}

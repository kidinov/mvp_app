package org.kidinov.rijksmuseum.ui.museum.agenda;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewpagerindicator.CirclePageIndicator;

import org.kidinov.rijksmuseum.R;
import org.kidinov.rijksmuseum.ui.base.BaseActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AgendaFragment extends Fragment implements AgendaView {
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.view_pager_indicator)
    CirclePageIndicator viewPagerIndicator;

    AgendaViewPagerAdapter adapter;

    @Inject
    AgendaPresenter presenter;

    public AgendaFragment() {
    }

    public static AgendaFragment newInstance() {
        return new AgendaFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new AgendaViewPagerAdapter(this);
    }

    @Override
    public @Nullable View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);
        View view = inflater.inflate(R.layout.agenda_fragment, container, false);
        ButterKnife.bind(this, view);

        setupViewPager();

        presenter.attachView(this);
        presenter.pageSelected(0);

        return view;
    }

    private void setupViewPager() {
        viewPager.setAdapter(adapter);
        ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override public void onPageSelected(int position) {
                presenter.pageSelected(position);
            }

            @Override public void onPageScrollStateChanged(int state) {
            }
        };
        viewPager.addOnPageChangeListener(onPageChangeListener);
        viewPagerIndicator.setOnPageChangeListener(onPageChangeListener);
        viewPagerIndicator.setViewPager(viewPager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
    }

    @Override public void setTitle(String title) {
        getActivity().setTitle(title);
    }
}

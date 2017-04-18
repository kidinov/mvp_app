package org.kidinov.rijksmuseum.ui.museum;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.kidinov.rijksmuseum.R;
import org.kidinov.rijksmuseum.ui.base.BaseActivity;
import org.kidinov.rijksmuseum.ui.museum.agenda.AgendaFragment;
import org.kidinov.rijksmuseum.ui.museum.collection.CollectionFragment;
import org.kidinov.rijksmuseum.util.AndroidComponentUtil;
import org.kidinov.rijksmuseum.util.BusEvents;
import org.kidinov.rijksmuseum.util.RxEventBus;
import org.kidinov.rijksmuseum.util.SyncOnConnectionAvailable;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class MuseumActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, MuseumView {
    private static final String TEST_FLAG = "test_flag";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @Inject
    MuseumPresenter presenter;
    @Inject
    RxEventBus eventBus;

    private CompositeSubscription subscriptions;

    public static Intent getTestIntent() {
        Intent intent = new Intent();
        intent.putExtra(TEST_FLAG, true);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.museum_activity);
        getActivityComponent().inject(this);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setTitle("");
        setupDrawer();
        navigationView.setNavigationItemSelectedListener(this);

        presenter.attachView(this);
        if (!getIntent().getBooleanExtra(TEST_FLAG, false) && savedInstanceState == null) {
            presenter.agendaClicked();
        }

        subscriptions = new CompositeSubscription();
        subscriptions.add(eventBus.filteredObservable(BusEvents.AuthenticationError.class)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(x -> handleAuthError(), e -> Timber.e(e, "")));

        AndroidComponentUtil.toggleComponent(this, SyncOnConnectionAvailable.class, true);
    }

    private void handleAuthError() {
        Snackbar.make(drawerLayout, R.string.museum_activity_auth_error, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        AndroidComponentUtil.toggleComponent(this, SyncOnConnectionAvailable.class, false);
        subscriptions.unsubscribe();
        presenter.detachView();
        super.onDestroy();
    }

    private void setupDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.museum_activity_navigation_drawer_open,
                R.string.museum_activity_navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_agenda:
                presenter.agendaClicked();
                break;
            case R.id.nav_collection:
                presenter.collectionClicked();
                break;
        }

        return true;
    }

    @Override
    public void showAgenda() {
        navigationView.setCheckedItem(R.id.nav_agenda);
        switchMainFragment(AgendaFragment.newInstance(), "AgendaFragment");
    }

    @Override
    public void showCollection() {
        navigationView.setCheckedItem(R.id.nav_collection);
        switchMainFragment(CollectionFragment.newInstance(), "CollectionFragment");
    }

    private void switchMainFragment(Fragment replacement, String tag) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment fragment = supportFragmentManager.findFragmentByTag(tag);

        if (fragment == null) {
            fragment = replacement;
        }

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.content, fragment, tag)
                .commit();
    }

    @Override
    public void closeDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

}

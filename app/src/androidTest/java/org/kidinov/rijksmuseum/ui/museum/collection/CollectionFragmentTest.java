package org.kidinov.rijksmuseum.ui.museum.collection;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.kidinov.rijksmuseum.R;
import org.kidinov.rijksmuseum.data.DataManager;
import org.kidinov.rijksmuseum.data.model.collection.ArtObject;
import org.kidinov.rijksmuseum.data.model.collection.Collection;
import org.kidinov.rijksmuseum.test.common.TestComponentRule;
import org.kidinov.rijksmuseum.test.common.TestDataFactory;
import org.kidinov.rijksmuseum.ui.museum.MuseumActivity;
import org.kidinov.rijksmuseum.util.C;
import org.kidinov.rijksmuseum.util.RxEventBus;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * UI testing of {@link CollectionFragment}
 */
@RunWith(AndroidJUnit4.class)
public class CollectionFragmentTest {

    private final TestComponentRule component = new TestComponentRule(InstrumentationRegistry.getTargetContext());
    private final ActivityTestRule<MuseumActivity> activityTestRule =
            new ActivityTestRule<>(MuseumActivity.class, false, false);

    // TestComponentRule needs to go first to make sure the Dagger ApplicationTestComponent is set
    // in the Application before any Activity is launched.
    @Rule
    public final TestRule chain = RuleChain.outerRule(component).around(activityTestRule);

    @Test
    public void listOfArtObjectsShows() {
        Collection oldCollection = TestDataFactory.makeCollection(C.COLLECTION_PAGE_SIZE, "old");
        oldCollection.setCount(20);
        Collection newCollection = TestDataFactory.makeCollection(C.COLLECTION_PAGE_SIZE, "new");
        newCollection.getArtObjects().addAll(oldCollection.getArtObjects());
        newCollection.setCount(20);
        for (int i = 0; i < newCollection.getArtObjects().size(); i++) {
            newCollection.getArtObjects().get(i).setFetchOrderNumber(i);
        }

        DataManager mockDataManager = component.getMockDataManager();
        RxEventBus mockEventBus = component.getEventbus();
        when(mockEventBus.filteredObservable(any())).thenReturn(Observable.empty());
        when(mockDataManager.getSavedCollectionAsObservable()).thenReturn(Observable.just(oldCollection)
                .observeOn(AndroidSchedulers.mainThread()));
        when(mockDataManager.getCollection(anyInt(), anyBoolean())).thenReturn(Observable.just(oldCollection));
        when(mockDataManager.getSavedCollection()).thenReturn(oldCollection);
        when(mockDataManager.getMoreCollectionFromServer(anyInt())).thenReturn(Observable.just(newCollection));
        activityTestRule.launchActivity(MuseumActivity.getTestIntent());

        MuseumActivity activity = activityTestRule.getActivity();
        activity.runOnUiThread(activity::showCollection);

        int position = 0;
        for (ArtObject item : oldCollection.getArtObjects()) {
            onView(withId(R.id.recycler_view))
                    .perform(RecyclerViewActions.scrollToPosition(position));
            onView(withText(item.getTitle()))
                    .check(matches(isDisplayed()));
            position++;
        }
    }

}
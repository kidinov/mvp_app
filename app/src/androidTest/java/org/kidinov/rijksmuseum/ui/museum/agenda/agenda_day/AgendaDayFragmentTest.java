package org.kidinov.rijksmuseum.ui.museum.agenda.agenda_day;

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
import org.kidinov.rijksmuseum.data.model.agenda.Agenda;
import org.kidinov.rijksmuseum.data.model.agenda.Option;
import org.kidinov.rijksmuseum.test.common.TestComponentRule;
import org.kidinov.rijksmuseum.test.common.TestDataFactory;
import org.kidinov.rijksmuseum.ui.museum.MuseumActivity;
import org.kidinov.rijksmuseum.ui.museum.collection.CollectionFragment;
import org.kidinov.rijksmuseum.util.C;
import org.kidinov.rijksmuseum.util.DateUtil;
import org.kidinov.rijksmuseum.util.RxEventBus;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.when;

/**
 * UI testing of {@link CollectionFragment}
 */
@RunWith(AndroidJUnit4.class)
public class AgendaDayFragmentTest {

    private final TestComponentRule component = new TestComponentRule(InstrumentationRegistry.getTargetContext());
    private final ActivityTestRule<MuseumActivity> activityTestRule =
            new ActivityTestRule<>(MuseumActivity.class, false, false);

    // TestComponentRule needs to go first to make sure the Dagger ApplicationTestComponent is set
    // in the Application before any Activity is launched.
    @Rule
    public final TestRule chain = RuleChain.outerRule(component).around(activityTestRule);

    @Test
    public void listOfArtObjectsShows() {
        RxEventBus mockEventBus = component.getEventbus();
        when(mockEventBus.filteredObservable(any())).thenReturn(Observable.empty());

        DataManager mockDataManager = component.getMockDataManager();
        List<Agenda> agendaList = new ArrayList<>();
        for (int i = 0; i < C.DAYS_TO_SHOW_IN_AGENDA; i++) {
            String date = DateUtil.getDateNDaysDiffAndFormat(i, C.AGENDA_API_DATE_FORMAT);
            Agenda agenda = TestDataFactory.makeRandomAgenda(date, 10);
            agendaList.add(agenda);

            when(mockDataManager.getSavedAgendaByDate(date)).thenReturn(agenda);
            when(mockDataManager.getAgendaForDate(any(), anyBoolean())).thenReturn(Observable.just(agenda));
            when(mockDataManager.getSavedAgendaByDateAsObservable(date)).thenReturn(Observable.just(agenda));
        }

        activityTestRule.launchActivity(MuseumActivity.getTestIntent());
        MuseumActivity activity = activityTestRule.getActivity();
        activity.runOnUiThread(activity::showAgenda);

        for (int i = 0; i < C.DAYS_TO_SHOW_IN_AGENDA; i++) {
            Agenda agenda = agendaList.get(i);
            int position = 0;
            for (Option item : agenda.getOptions()) {
                onView(allOf(withId(R.id.recycler_view), isDisplayed()))
                        .perform(RecyclerViewActions.scrollToPosition(position));
                onView(withText(item.getPeriod().getText()))
                        .check(matches(isDisplayed()));
                onView(withText(item.getExpositionType().getFriendlyName()))
                        .check(matches(isDisplayed()));
                onView(withText(item.getExposition().getDescription()))
                        .check(matches(isDisplayed()));
                onView(withText(AgendaDayRecyclerViewAdapter.formatPrice(item)))
                        .check(matches(isDisplayed()));
                position++;
            }

            //Check title
            onView(withText(agenda.getAgendaDateString()))
                    .check(matches(isDisplayed()));

            onView(withId(R.id.view_pager)).perform(swipeLeft());
        }
    }

}
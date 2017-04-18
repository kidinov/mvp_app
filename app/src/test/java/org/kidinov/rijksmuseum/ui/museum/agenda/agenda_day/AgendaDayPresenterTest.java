package org.kidinov.rijksmuseum.ui.museum.agenda.agenda_day;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kidinov.rijksmuseum.data.DataManager;
import org.kidinov.rijksmuseum.data.model.agenda.Agenda;
import org.kidinov.rijksmuseum.data.remote.RetrofitException;
import org.kidinov.rijksmuseum.test.common.TestDataFactory;
import org.kidinov.rijksmuseum.util.RxEventBus;
import org.kidinov.rijksmuseum.util.RxSchedulersOverrideRule;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JVM test of {@link AgendaDayPresenter}
 * class with mocked dependencies
 */
@RunWith(MockitoJUnitRunner.class)
public class AgendaDayPresenterTest {

    @Mock
    AgendaDayView agendaDayView;
    @Mock
    DataManager mockDataManager;
    @Mock
    RxEventBus mockEventBus;

    private AgendaDayPresenter presenter;

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() {
        presenter = new AgendaDayPresenter(mockDataManager, mockEventBus);
        presenter.attachView(agendaDayView);
    }

    @After
    public void tearDown() {
        presenter.detachView();
    }

    @Test
    public void loadAgendaForDateShowProgressAndHideRefresh() {
        String date = "13-13-13";
        Agenda agenda = TestDataFactory.makeAgenda(date, 10, "");
        when(mockDataManager.getAgendaForDate(date, true))
                .thenReturn(Observable.just(agenda));

        presenter.loadAgendaForDate(date, true);
        verify(agendaDayView).showProgress();
        verify(agendaDayView).hideRefreshSign();
        verify(agendaDayView, never()).showError();
    }

    @Test
    public void loadAgendaForDateNotShowRefreshIfDataInDb() {
        String date = "13-13-13";
        Agenda agenda = TestDataFactory.makeAgenda(date, 10, "");
        when(mockDataManager.getAgendaForDate(date, true))
                .thenReturn(Observable.just(agenda));
        when(mockDataManager.getSavedAgendaByDate(date))
                .thenReturn(agenda);

        presenter.loadAgendaForDate(date, true);
        verify(agendaDayView, never()).showProgress();
        verify(agendaDayView).hideRefreshSign();
        verify(agendaDayView, never()).showError();
    }

    @Test
    public void loadAgendaForDateShowErrorIfNetworkErrorButNoDataInDb() {
        String date = "13-13-13";
        when(mockDataManager.getAgendaForDate(date, true))
                .thenReturn(Observable.error(TestDataFactory.makeRetrofitException(RetrofitException.Kind.NETWORK)));

        presenter.loadAgendaForDate(date, true);
        verify(agendaDayView).showProgress();
        verify(agendaDayView).hideRefreshSign();
        verify(agendaDayView).showError();
    }

    @Test
    public void loadAgendaForDateNotShowErrorIfNetworkErrorAndDataInDb() {
        String date = "13-13-13";
        Agenda agenda = TestDataFactory.makeAgenda(date, 10, "");
        when(mockDataManager.getSavedAgendaByDate(date))
                .thenReturn(agenda);
        when(mockDataManager.getAgendaForDate(date, true))
                .thenReturn(Observable.error(TestDataFactory.makeRetrofitException(RetrofitException.Kind.NETWORK)));

        presenter.loadAgendaForDate(date, true);
        verify(agendaDayView, never()).showProgress();
        verify(agendaDayView).hideRefreshSign();
        verify(agendaDayView, never()).showError();
    }

}
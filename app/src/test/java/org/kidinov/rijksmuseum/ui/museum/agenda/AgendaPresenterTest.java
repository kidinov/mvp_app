package org.kidinov.rijksmuseum.ui.museum.agenda;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kidinov.rijksmuseum.util.C;
import org.kidinov.rijksmuseum.util.DateUtil;
import org.kidinov.rijksmuseum.util.RxSchedulersOverrideRule;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

/**
 * JVM test of {@link AgendaPresenter}
 * class with mocked dependencies
 */
@RunWith(MockitoJUnitRunner.class)
public class AgendaPresenterTest {

    @Mock
    AgendaView agendaView;

    private AgendaPresenter presenter;

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() {
        presenter = new AgendaPresenter();
        presenter.attachView(agendaView);
    }

    @After
    public void tearDown() {
        presenter.detachView();
    }

    @Test
    public void pageSelectedCallsSetTitle() {
        int position = 3;
        presenter.pageSelected(position);
        verify(agendaView).setTitle(DateUtil.getDateNDaysDiffAndFormat(position, C.AGENDA_API_DATE_FORMAT));
    }

}
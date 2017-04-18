package org.kidinov.rijksmuseum.ui.museum.agenda;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import org.kidinov.rijksmuseum.ui.museum.agenda.agenda_day.AgendaDayFragment;
import org.kidinov.rijksmuseum.util.C;
import org.kidinov.rijksmuseum.util.DateUtil;

class AgendaViewPagerAdapter extends FragmentPagerAdapter {
    AgendaViewPagerAdapter(AgendaFragment fragment) {
        super(fragment.getChildFragmentManager());
    }

    @Override
    public Fragment getItem(int position) {
        return AgendaDayFragment.newInstance(DateUtil.getDateNDaysDiffAndFormat(position, C.AGENDA_API_DATE_FORMAT));
    }

    @Override
    public int getCount() {
        return C.DAYS_TO_SHOW_IN_AGENDA;
    }
}

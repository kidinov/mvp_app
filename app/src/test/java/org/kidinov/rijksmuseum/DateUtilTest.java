package org.kidinov.rijksmuseum;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.kidinov.rijksmuseum.util.C;
import org.kidinov.rijksmuseum.util.DateUtil;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * JVM test of {@link DateUtil}
 */
@RunWith(MockitoJUnitRunner.class)
public class DateUtilTest {

    @Test
    public void getDateDiffCountsDiffProperly() {
        int diff = 100;
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MILLISECOND, diff);

        assertEquals(DateUtil.getDateDiff(new Date(), cal.getTime(), TimeUnit.MILLISECONDS), diff);
    }

    @Test
    public void getDateNDaysDiffCountsProperly() {
        int diff = 3;
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, diff);

        assertTrue(DateUtil.getDateNDaysDiff(diff).getTime() - 100 <= cal.getTime().getTime());
    }

    @Test
    public void getDateNDaysDiffAndFormatCountsProperly() {
        SimpleDateFormat sdf = new SimpleDateFormat(C.AGENDA_API_DATE_FORMAT);

        int diff = 3;
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, diff);

        assertEquals(DateUtil.getDateNDaysDiffAndFormat(diff, C.AGENDA_API_DATE_FORMAT), sdf.format(cal.getTime()));
    }

    @Test
    public void parseDateProperly() throws ParseException {
        String date = "2016-10-12";
        SimpleDateFormat sdf = new SimpleDateFormat(C.AGENDA_API_DATE_FORMAT);

        assertEquals(DateUtil.parseDate(date, C.AGENDA_API_DATE_FORMAT), sdf.parse(date));
    }

}

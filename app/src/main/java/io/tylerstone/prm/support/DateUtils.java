package io.tylerstone.prm.support;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by tyler on 4/30/2017.
 */

public class DateUtils {

    public static long getTrimmedDate(Date date) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);

        return cal.getTime().getTime();
    }

    public static long getToday() {
        return DateUtils.getTrimmedDate(new Date());
    }

    public static long getTomorrow() {
        Calendar tomorrowCal = Calendar.getInstance();
        tomorrowCal.setTime(new Date());
        tomorrowCal.add(Calendar.DAY_OF_YEAR, 1);
        return DateUtils.getTrimmedDate(tomorrowCal.getTime());
    }

    public static long getNextDayFrom(Date date) {
        Calendar nextDayCal = Calendar.getInstance();
        nextDayCal.setTime(date);
        nextDayCal.add(Calendar.DAY_OF_YEAR, 1);
        return DateUtils.getTrimmedDate(nextDayCal.getTime());
    }

    public static long getNextWeekFrom(Date date) {
        Calendar nextWeekCal = Calendar.getInstance();
        nextWeekCal.setTime(new Date());
        nextWeekCal.add(Calendar.DAY_OF_YEAR, 8); // we're picking an extra day because it will be set to midnight.
        return DateUtils.getTrimmedDate(nextWeekCal.getTime());
    }
}

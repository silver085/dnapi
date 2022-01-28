package com.dn.DNApi.Facades.Utils;

import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static Date getOneWeekEarlier(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -7);
        return cal.getTime();
    }

    public static Date getOneDayEarlier(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public static int getSecondsDifference(Date start, Date end){
        Instant i1 = start.toInstant();
        Instant i2 = end.toInstant();
        Duration duration = Duration.between(i1,i2);
        return (int) duration.getSeconds();
    }
}

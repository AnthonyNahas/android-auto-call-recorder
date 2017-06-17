package com.anthonynahas.autocallrecorder.utilities.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by anahas on 02.06.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 02.06.17
 */
@Singleton
public class DateTimeHelper {

    @Inject
    public DateTimeHelper() {
    }

    public String getLocalFormatterDate(long l) {
        //DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());
        DateFormat dateFormatter = new SimpleDateFormat("HH:mm MMM.dd yyyy");
        String date = dateFormatter.format(new Date(l));
        return date;
    }

    public String getTimeString(int duration) {
        int minutes = (int) Math.floor(duration / 1000 / 60);
        int seconds = (duration / 1000) - (minutes * 60);
        return minutes + ":" + String.format("%02d", seconds);
    }

}

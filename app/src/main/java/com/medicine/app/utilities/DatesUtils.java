package com.medicine.app.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DatesUtils {
    private final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private final static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a");
    public static String formatDate(Date date) {
        return date != null ? FORMAT.format(date) : "";
    }
    public static String formatDateOnly(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String formatTimeOnly(Date date) {
        return TIME_FORMAT.format(date);
    }
}
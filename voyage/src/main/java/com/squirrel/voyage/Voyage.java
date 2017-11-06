package com.squirrel.voyage;

import android.os.Environment;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Locale;

public final class Voyage {

    private static final File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Voyage.txt");

    public static synchronized void e(final String tag, final String msg) {
        try {
            Files.append(format(tag, msg), file, Charset.defaultCharset());
        } catch (IOException e) {
            throw new AssertionError("Failed in writing " + file);
        }
    }

    public static void e(String tag, boolean b) {
        e(tag, Boolean.toString(b));
    }

    public static void e(String tag, int i) {
        e(tag, Integer.toString(i));
    }

    public static void e(String tag, long l) {
        e(tag, Long.toString(l));
    }

    public static void e(String tag, float f) {
        e(tag, Float.toString(f));
    }

    public static void e(String tag, double d) {
        e(tag, Double.toString(d));
    }

    public static void e(String tag, Object o) {
        e(tag, o != null ? o.toString() : "null");
    }

    private static String format(String tag, String msg) {
        Date date = new Date();
        return String.format(Locale.getDefault(), "%tF %tT.%tL %s %s%n", date, date, date, tag, msg);
    }
}
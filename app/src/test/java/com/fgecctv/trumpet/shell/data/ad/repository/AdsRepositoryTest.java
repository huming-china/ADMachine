package com.fgecctv.trumpet.shell.data.ad.repository;

import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.assertEquals;

public class AdsRepositoryTest {
    @Test
    public void test() throws InterruptedException {
        final Lock lock = new ReentrantLock();
        final Condition condition = lock.newCondition();
        System.out.println("A");
        lock.lock();
        new Thread() {
            @Override
            public void run() {
                lock.lock();
                System.out.println("D");
                condition.signalAll();
                System.out.println("E");
                lock.unlock();
            }
        }.start();
        System.out.println("B");

        Thread.sleep(1000);
        System.out.println("C");

        condition.await();

        System.out.println("F");
        lock.unlock();
    }

    @Test
    public void testA() throws ParseException {
        String d = "1989-09-01 12:43:23";

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date parse = format.parse(d);

        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(parse);
        calendar.set(Calendar.YEAR, 1989);
        calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 43);
        calendar.set(Calendar.SECOND, 23);
        calendar.set(Calendar.MILLISECOND, 0);

        assertEquals(parse.getTime(), calendar.getTimeInMillis());
    }
}
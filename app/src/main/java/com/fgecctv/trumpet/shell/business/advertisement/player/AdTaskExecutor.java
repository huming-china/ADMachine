package com.fgecctv.trumpet.shell.business.advertisement.player;

import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fgecctv.trumpet.shell.data.ad.Repeat;
import com.fgecctv.trumpet.shell.data.ad.repository.Ad;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class AdTaskExecutor {
    private CyclicPlayer player;
    private List<Ad> adRecords;
    private Handler handler = new Handler();
    private long currentTime;
    private Runnable assignTasksForToday = new Runnable() {
        @Override
        public void run() {
            AdTaskExecutor.this.currentTime = System.currentTimeMillis();
            assign(createTasksForToday());
            setRunTomorrow();
        }
    };

    public AdTaskExecutor(CyclicPlayer player) {
        this.player = player;
    }

    AdTaskExecutor(List<Ad> ads, long currentTime) {
        this.adRecords = ads;
        this.currentTime = currentTime;
    }

    private void removeIneffectiveAdRecord(List<Ad> ads) {
        for (int i = ads.size() - 1; i >= 0; i--) {
            Ad ad = ads.get(i);
            if (getCalendarAtMidnightForTomorrow().getTimeInMillis() <= ad.getEffectiveDate())
                ads.remove(i);
        }
    }

    public void execute(@NonNull List<Ad> adRecords) {
        this.adRecords = new ArrayList<>(adRecords);

        handler.postAtFrontOfQueue(assignTasksForToday);
    }

    private void setRunTomorrow() {
        Calendar tomorrow = getCalendarAtMidnightForTomorrow();
        handler.postDelayed(assignTasksForToday, tomorrow.getTimeInMillis() - currentTime);
    }

    private void removeExpiredAdRecord(List<Ad> ads) {
        Iterator<Ad> iterator = ads.iterator();
        while (iterator.hasNext())
            if (iterator.next().getExpiryDate() <= currentTime)
                iterator.remove();
    }

    private void assign(@NonNull List<TimedAdTask> tasks) {
        player.clear();
        player.setPlaylist(tasks);
        player.invalidate();
    }

    public void abort() {
        player.clear();
        player.invalidate();
    }

    private List<TimedAdTask> createTasksForToday() {
        List<Ad> ads = new ArrayList<>(adRecords);
        removeExpiredAdRecord(ads);
        removeIneffectiveAdRecord(ads);
        removeEarlierEffectiveAdRecordsWithTheSamePriority(ads);
        sortAdRecordsByPriorityInDescending(ads);
        Calendar today = getCalendarAtMidnightForToday();
        return createTasksForToday(today, ads);
    }

    private List<TimedAdTask> createTasksForToday(Calendar midnightOfToday, List<Ad> ads) {
        List<TimedAdTask> tasks = new ArrayList<>();
        for (Ad a : ads)
            for (Repeat repeat : a.getRepeats()) {
                long startTime = Math.max(a.getEffectiveDate(), midnightOfToday.getTimeInMillis() + repeat.starts);
                long endTime = Math.min(a.getExpiryDate(), midnightOfToday.getTimeInMillis() + repeat.ends);
                if (startTime <= endTime && currentTime < endTime)
                    tasks.add(new TimedAdTask.Builder()
                            .setStartTime(startTime)
                            .setEndTime(endTime)
                            .setId(a.getId())
                            .setUri(Uri.parse(a.getUri()))
                            .setDuration(a.getDuration())
                            .setExclusive(a.isExclusive())
                            .setFrameOfMonitorWindow(a.getMonitorWindowLocation())
                            .create());
            }
        return tasks;
    }

    private Calendar getCalendarAtMidnightForToday() {
        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(currentTime);

        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        return today;
    }

    private Calendar getCalendarAtMidnightForTomorrow() {
        Calendar today = getCalendarAtMidnightForToday();
        today.add(Calendar.DATE, 1);
        return today;
    }

    private void removeEarlierEffectiveAdRecordsWithTheSamePriority(List<Ad> ads) {
        if (theSamePriority(ads).isEmpty())
            return;
        List<Ad> list = getTheEarlierAdRecord(theSamePriority(ads));
        for (Ad record : list)
            ads.remove(record);
    }

    private void sortAdRecordsByPriorityInDescending(List<Ad> ads) {
        Collections.sort(ads, new Comparator<Ad>() {
            @Override
            public int compare(Ad lhs, Ad rhs) {
                checkArgument(lhs.getPriority() != rhs.getPriority());
                return lhs.getPriority() < rhs.getPriority() ? -1 : 1;
            }
        });
    }

    private List<Ad> theSamePriority(List<Ad> tasks) {
        List<Ad> theSamePriorities = new ArrayList<>();
        for (int i = 0; i < tasks.size() - 1; i++)
            for (int j = i + 1; j < tasks.size(); j++)
                if (tasks.get(i).getPriority() == tasks.get(j).getPriority()) {
                    theSamePriorities.add(tasks.get(i));
                    theSamePriorities.add(tasks.get(j));
                }

        if (theSamePriorities.isEmpty()) return theSamePriorities;
        return getNewList(theSamePriorities);
    }

    private List<Ad> getTheEarlierAdRecord(List<Ad> ads) {
        List<Ad> theEarlierRecords = new ArrayList<>();
        for (int i = 0; i < ads.size() - 1; i++)
            for (int j = i + 1; j < ads.size(); j++)
                if (ads.get(i).getPriority() == ads.get(j).getPriority()) {
                    if (ads.get(i).getEffectiveDate() >= ads.get(j).getEffectiveDate()) {
                        theEarlierRecords.add(ads.get(j));
                    } else {
                        theEarlierRecords.add(ads.get(i));
                    }
                }
        return theEarlierRecords;
    }

    @Nullable
    private List<Ad> getNewList(List<Ad> adRecords) {
        List<Ad> list = new ArrayList<>();
        for (int i = 0; i < adRecords.size(); i++) {
            Ad record = adRecords.get(i);
            if (!list.contains(record)) {
                list.add(record);
            }
        }
        return list;
    }
}
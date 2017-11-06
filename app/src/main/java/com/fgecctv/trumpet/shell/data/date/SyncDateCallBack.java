package com.fgecctv.trumpet.shell.data.date;

import com.fgecctv.trumpet.shell.network.http.response.TimeResponse;

public interface SyncDateCallBack {
    void onDateIsTheSame(TimeResponse.Date date);
    void onDateIsDifferent (TimeResponse.Date date);
}

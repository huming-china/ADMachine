/*************************************************************************
	> File Name: rtc-test.c
	> Author: Dechao
	> Mail: Dechao@163.com 
	> Created Time: Wed 04 Jan 2017 04:18:47 PM CST
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <linux/rtc.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include <android/log.h>
#include <string.h>
#include <unistd.h>

#define RTC_PATH "/dev/rtc0"

#define TAG "rtc-control.c"

int OpenRtc(void) {
    int fd;
    fd = open(RTC_PATH, O_RDONLY);
    if (fd == -1)
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Failed to open %s ..\n", RTC_PATH);
    return fd;
}

int CloseRtcHandler(int fd) {
    if (fd > 0)
        close(fd);
    return 0;
}

int RtcSetAlarm(int fd, struct rtc_time *tm) {
    int retval = -1;
    struct rtc_wkalrm alarm;
    alarm.enabled = 1;
    alarm.time = *tm;
    retval = ioctl(fd, RTC_WKALM_SET, &alarm);
//		retval = ioctl(fd, RTC_ALM_SET, tm);
    if (retval == -1) {
        perror("RtcSetAlarm ioctl");
    }
    return retval;
}

int RtcGetAlarm(int fd, struct rtc_time *tm) {
    int retval = -1;
    struct rtc_wkalrm alarm;
    retval = ioctl(fd, RTC_WKALM_RD, &alarm);
    if (retval == -1) {
        perror("RtcGetAlarm ioctl");
        return retval;
    }
    memcpy(tm, &alarm.time, sizeof(struct rtc_time));
    return retval;
}

int RtcEnableAlarm(int fd) {
    int retval = ioctl(fd, RTC_AIE_ON, NULL);
    if (retval == -1) {
        perror("RtcEnableAlarm ioctl");
    }
    return retval;
}

int RtcDisableAlarm(int fd) {
    int retval = -1;
    retval = ioctl(fd, RTC_AIE_OFF, NULL);
    if (retval == -1) {
        perror("RtcDisableAlarm ioctl");
    }
    return retval;
}


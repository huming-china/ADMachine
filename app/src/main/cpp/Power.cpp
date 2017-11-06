#include <jni.h>
#include <linux/rtc.h>
#include "librtc.h"

#define TAG "Power.cpp"

extern "C"
void
Java_com_fgecctv_hardware_Power_enable(
        JNIEnv *env,
        jobject /* this */) {
    int fd = OpenRtc();
    RtcEnableAlarm(fd);
    CloseRtcHandler(fd);
}

extern "C"
void
Java_com_fgecctv_hardware_Power_disable(
        JNIEnv *env,
        jobject /* this */) {
    int fd = OpenRtc();
    RtcDisableAlarm(fd);
    CloseRtcHandler(fd);
}

extern "C"
void
Java_com_fgecctv_hardware_Power_off(
        JNIEnv *env,
        jobject /* this */) {
    ExternalPoweroff();
}

extern "C"
void
Java_com_fgecctv_hardware_Power_setNextTurnOnTime(
        JNIEnv *env,
        jobject /* this */,
        jint year,
        jint month,
        jint date,
        jint dayOfWeek,
        jint hourOfDay,
        jint minute) {

    struct rtc_time nextTurnOnTime = {
            .tm_wday = dayOfWeek,
            .tm_year = year,
            .tm_mon = month,
            .tm_mday = date,
            .tm_hour = hourOfDay,
            .tm_min = minute,
            .tm_sec = 0,
    };

    int fd = OpenRtc();
    RtcSetAlarm(fd, &nextTurnOnTime);
    CloseRtcHandler(fd);
}

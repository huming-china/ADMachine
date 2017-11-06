#ifndef __RTC_LIB__
#define __RTC_LIB__

#include <linux/rtc.h>

int OpenRtc(void);

int CloseRtcHandler(int fd);

int RtcSetAlarm(int fd, struct rtc_time *tm);

int RtcGetAlarm(int fd, struct rtc_time *tm);

int RtcEnableAlarm(int fd);

int RtcDisableAlarm(int fd);

int RtcGetTime(int fd, struct rtc_time *tm);

int RtcSetTime(int fd, struct rtc_time *tm);

/**
Func: External Poweroff
Param: void
Return: 1=Success
       -1=Failed
**/
int ExternalPoweroff(void);

#endif


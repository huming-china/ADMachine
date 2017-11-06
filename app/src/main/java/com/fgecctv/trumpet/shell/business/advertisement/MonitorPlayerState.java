package com.fgecctv.trumpet.shell.business.advertisement;

class MonitorPlayerState {

    private boolean surfaceValid;
    private boolean monitorConfigured;

    void setSurfaceValid(boolean valid) {
        surfaceValid = valid;
    }

    boolean isReady() {
        return surfaceValid && monitorConfigured;
    }

    void setMonitorConfigured(boolean monitorConfigured) {
        this.monitorConfigured = monitorConfigured;
    }
}

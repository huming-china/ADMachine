package com.fgecctv.trumpet.shell.business.bind;

interface BindUserInterface {

    void onLoginSuccess();

    void onLoginFailure(String message);

    void loading();

    void showAdvertisement();

    boolean getBindingStatus();

    void setBindingStatus(boolean state);
}

package com.fgecctv.trumpet.shell.network.http.request;

public class BindRequestBody implements RequestBody {
    public String action = "binding";
    public String languageCode = "EN";
    public String id;
    public String name;
    public String ip;
    public String mac;
    public String address;
    public String firmware;
    public String resolution;
    public String version;
    public String createDate;
    public int volume;
    public String userCode;
    public String password;
}

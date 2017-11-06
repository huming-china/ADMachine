package com.fgecctv.trumpet.shell.network.http.request;

public class SyncRequestBody implements RequestBody {
    public String action = "syncResources";
    public String terminalId;
}

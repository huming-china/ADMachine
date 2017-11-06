package com.fgecctv.trumpet.shell.network.http.request;

public class ProgressOfDownload implements RequestBody {
    public String action = "downloadProgress";
    public String terminalId;
    public String programId;
    public String progress;
}

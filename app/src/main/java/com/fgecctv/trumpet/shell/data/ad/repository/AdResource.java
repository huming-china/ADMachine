package com.fgecctv.trumpet.shell.data.ad.repository;

class AdResource {

    private String id;

    private String url;

    AdResource(String id, String url) {
        this.id = id;
        this.url = url;
    }

    String getId() {
        return id;
    }

    String getUrl() {
        return url;
    }
}
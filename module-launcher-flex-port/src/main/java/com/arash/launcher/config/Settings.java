package com.arash.launcher.config;

import java.util.HashMap;
import java.util.List;

public class Settings {
    private HashMap<String, Object> container = new HashMap<>();

    public void setPort(int port) {
        container.put("port", port);
    }

    public int getPort() {
        return (int) container.getOrDefault("port", 8080);
    }

    public void setCurrentUri(List<String> currentUriList) {
        container.put("currentUri", currentUriList);
    }

    public List<String> getCurrentUri() {
        return (List<String>) container.get("currentUri");
    }
}

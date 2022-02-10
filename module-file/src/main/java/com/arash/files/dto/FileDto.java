package com.arash.files.dto;

import java.io.File;

public class FileDto {
    private String name;
    private String type;
    private long size;

    public FileDto() {
    }

    public FileDto initByFile(File file) {
        name = file.getName();
        size = file.length();
        int i;
        String fileName = file.getName();
        if (file.isDirectory()) {
            type = "dir";
        } else if ((i = fileName.lastIndexOf('.')) >= 0) {
            type = fileName.substring(i);
        } else {
            type = "unknown";
        }
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}

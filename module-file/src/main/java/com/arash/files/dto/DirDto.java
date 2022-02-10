package com.arash.files.dto;

import java.util.List;

public class DirDto {
    private String path;
    private List<FileDto> files;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<FileDto> getFiles() {
        return files;
    }

    public void setFiles(List<FileDto> files) {
        this.files = files;
    }
}

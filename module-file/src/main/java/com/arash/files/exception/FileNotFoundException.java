package com.arash.files.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.File;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FileNotFoundException extends StorageException {
    public FileNotFoundException(String message) {
        super(message);
    }

    public FileNotFoundException(File file) {
        this(file.getAbsolutePath() + " not found");
    }

    public FileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

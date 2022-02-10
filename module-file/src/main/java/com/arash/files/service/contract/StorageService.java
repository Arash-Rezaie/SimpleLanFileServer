package com.arash.files.service.contract;

import com.arash.files.dto.DirDto;
import org.springframework.core.io.Resource;

public interface StorageService {
    String store(String originalFileName, byte[] file);

    DirDto listDir(String path);

    Resource load(String filename);
}

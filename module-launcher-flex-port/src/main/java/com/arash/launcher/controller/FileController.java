package com.arash.launcher.controller;

import com.arash.files.dto.FileDto;
import com.arash.files.service.contract.StorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/files")
public class FileController {
    private StorageService storageService;

    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @RequestMapping("/list")
    @ResponseBody
    public Object listAllFiles(@RequestBody Map<String, String> param) {
        return storageService.listDir(param.get("path"));
    }

    @RequestMapping("/download")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@RequestBody Map<String, String> param) {
        String path = param.get("path");
        Resource resource = storageService.load(path);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @RequestMapping("/upload")
    @ResponseBody
    public ResponseEntity<String> uploadFile(@RequestBody MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        byte[] bytes = file.getBytes();
        String fileName = storageService.store(originalFileName, bytes);
        return ResponseEntity.ok()
                .header(HttpHeaders.ACCEPT)
                .body(String.format("{\"status\":\"'%s' uploaded\"}", fileName));
    }
}

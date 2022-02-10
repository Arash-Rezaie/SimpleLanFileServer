package com.arash.files.service;

import com.arash.files.dto.DirDto;
import com.arash.files.dto.FileDto;
import com.arash.files.exception.FileNotFoundException;
import com.arash.files.exception.StorageException;
import com.arash.files.service.contract.StorageService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class StorageServiceImp implements StorageService {
    private final Path rootLocation;

    @Autowired
    private FactoryBean<FileDto> fileDtoFactory;

    @Autowired
    private FactoryBean<DirDto> dirDtoFactory;

    public StorageServiceImp(@Value("${storage.location}") String rootLocation) {
        this.rootLocation = Paths.get(rootLocation);
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage location", e);
        }
    }

    @Override
    public String store(String originalFileName, byte[] file) {
        String filename = StringUtils.cleanPath(Objects.requireNonNull(originalFileName)).trim();
        try {
            if (file == null || file.length == 0)
                throw new StorageException("Failed to store empty file " + filename);

            if (filename.contains("..")) //for security
                throw new StorageException("Cannot store file with relative path outside current directory " + filename);

            Path path = rootLocation.resolve(filename);
            while (Files.exists(path)) {
                filename = path.getFileName().toString();
                int i = filename.lastIndexOf('.');
                filename = i >= 0 ?
                        filename.substring(0, i) + " _" + filename.substring(i) :
                        filename + " _";
                path = rootLocation.resolve(filename);
            }

            FileUtils.writeByteArrayToFile(path.toFile(), file);
            return filename;
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    @Override
    public DirDto listDir(String path) {
        try {
            File f = new File(path);
            f = new File(FilenameUtils.normalize(f.getAbsolutePath()));
            if (!f.exists() || !f.isDirectory())
                throw new FileNotFoundException(f);
            DirDto dirDto = dirDtoFactory.getObject();
            dirDto.setPath(f.getAbsolutePath());
            List<FileDto> lst = new ArrayList<>();
            dirDto.setFiles(lst);
            File[] files = f.listFiles();
            if (files != null && files.length > 0) {
                Arrays.stream(files)
                        .map(this::getFileDto)
                        .sorted(Comparator.comparing(FileDto::getName))
                        .forEach(lst::add);
            }
            return dirDto;
        } catch (Exception e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }

    private FileDto getFileDto(File file) {
        try {
            return fileDtoFactory.getObject().initByFile(file);
        } catch (Exception e) {
            throw new StorageException("Some thing wet wrong, Please try again.");
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path p = Paths.get(filename);
            Resource resource = new UrlResource(p.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new FileNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new FileNotFoundException("Could not read file: " + filename, e);
        }
    }
}

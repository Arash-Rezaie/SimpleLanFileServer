package com.arash.files.app;

import com.arash.files.dto.DirDto;
import com.arash.files.dto.FileDto;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;

@SpringBootApplication(scanBasePackages = "com.arash.files")
@PropertySource("classpath:module-file.properties")
public class FileApp {
    public static void main(String[] args) {
        SpringApplication.run(FileApp.class);
    }

    @Bean
    @Scope("singleton")
    public FactoryBean<FileDto> getFileDtoFactory() {
        return new FactoryBean<FileDto>() {
            @Override
            public FileDto getObject() {
                return new FileDto();
            }

            @Override
            public Class<?> getObjectType() {
                return FileDto.class;
            }
        };
    }

    @Bean
    @Scope("singleton")
    public FactoryBean<DirDto> getDirDtoFactory() {
        return new FactoryBean<DirDto>() {
            @Override
            public DirDto getObject() {
                return new DirDto();
            }

            @Override
            public Class<?> getObjectType() {
                return DirDto.class;
            }
        };
    }
}
package com.arash.launcher.app;

import com.arash.launcher.launchparty.CurrentUriFinder;
import com.arash.launcher.config.Settings;
import com.arash.launcher.launchparty.BrowserLauncher;
import com.arash.launcher.launchparty.FlexPortLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@SpringBootApplication(scanBasePackages = {"com.arash"})
public class Application {
    private static final Settings settings = new Settings();

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(Application.class);
        new Launcher(args, settings)
                .addLaunchParties(
                        FlexPortLauncher.class
                        ,CurrentUriFinder.class
                        ,BrowserLauncher.class
                )
                .execute(app);
    }

    @Bean
    public Settings getSettings() {
        return settings;
    }

    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        resolver.setResolveLazily(true);
        return resolver;
    }
}

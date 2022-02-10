package com.arash.launcher.launchparty;

import com.arash.launcher.app.CmdOption;
import com.arash.launcher.config.Settings;
import org.springframework.boot.SpringApplication;

import java.util.List;
import java.util.Map;

public interface LaunchParty {
    enum When {
        BEFORE_LAUNCH(1), AFTER_LAUNCH(2);

        private final int val;

        When(int value) {
            this.val = value;
        }

        public int value() {
            return val;
        }
    }

    /**
     * @return all options you may need
     */
    List<CmdOption> getOptions();

    /**
     * @return LauncherParty.When
     */
    int when();

    /**
     * @param userInputOptions user input options
     * @param settings setting option. This object would be shared across whole application
     * @param app the spring application instance
     * @throws Exception in case of any exception, just throw it
     */
    void execute(Map<String, String> userInputOptions, Settings settings, SpringApplication app) throws Exception;

}

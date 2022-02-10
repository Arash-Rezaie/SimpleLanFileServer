package com.arash.launcher.app;

import com.arash.launcher.config.Settings;

import java.util.Map;

/**
 * You can determine the option format by this class
 */
public class CmdOption {
    String optionShort, optionLong, description;
    boolean isNecessary = false;


    /**
     * @param optionShort starts with "-", no "=" at ending
     * @param optionLong  starts with "--", no "=" at ending
     * @param description a simple description
     */
    public CmdOption(String optionShort, String optionLong, String description) {
        this.optionShort = optionShort;
        this.optionLong = optionLong;
        this.description = description;
    }

    /**
     * @param optionNecessary no extra character at beginning and ending
     * @param description     a simple description
     */
    public CmdOption(String optionNecessary, String description) {
        this.optionLong = optionNecessary;
        this.description = description;
        this.isNecessary = true;
    }

    public String getOptionShort() {
        return optionShort;
    }

    public String getOptionLong() {
        return optionLong;
    }

    public String getDescription() {
        return description;
    }

    public boolean isNecessary() {
        return isNecessary;
    }

    boolean check(String value) {
        return value.equals(optionLong) || value.equals(optionShort);
    }

    public String findValueInUserInput(Map<String, String> userInput, String defaultValue) {
        String v = null;
        if (optionShort != null)
            v = userInput.get(optionShort);
        if (optionLong != null && v == null)
            v = userInput.get(optionLong);
        return v == null ? defaultValue : v;
    }
}


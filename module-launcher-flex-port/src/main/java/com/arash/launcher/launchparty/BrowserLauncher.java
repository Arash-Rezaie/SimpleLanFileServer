package com.arash.launcher.launchparty;

import com.arash.launcher.app.CmdOption;
import com.arash.launcher.config.Settings;
import com.arash.launcher.tools.TablePrinter;
import org.springframework.boot.SpringApplication;

import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BrowserLauncher implements LaunchParty {
    private final CmdOption myOption = new CmdOption(null, "--noAutoLaunch", "do not run default browser automatically");

    @Override
    public List<CmdOption> getOptions() {
        List<CmdOption> lst = new LinkedList<>();
        lst.add(myOption);
        lst.add(new CmdOption("-p", null, null));
        lst.add(new CmdOption(null, "--port", null));
        lst.add(new CmdOption(null, "--help", null));
        return lst;
    }

    @Override
    public int when() {
        return When.AFTER_LAUNCH.value();
    }

    @Override
    public void execute(Map<String, String> userInputOptions, Settings settings, SpringApplication app) throws Exception {
        printBoundedAddress(settings);
        if (!userInputOptions.containsKey(myOption.getOptionLong())) {
            try {
                System.setProperty("java.awt.headless", "false");
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(new URI(String.format("http://%s:%s",
                        settings.getCurrentUri() != null && settings.getCurrentUri().size() > 0 ?
                                settings.getCurrentUri().get(0) : "localhost",
                        settings.getPort())));
            } catch (Exception e) {
            }
        }
    }

    private void printBoundedAddress(Settings settings) {
        System.out.println("\n\n");
        String portStr = settings.getPort() + "";
        List<String[]> tbl = new ArrayList<>();
        tbl.add(new String[]{"address", "port"});
        tbl.add(new String[]{"-------", "----"});
        settings.getCurrentUri().forEach(item -> tbl.add(new String[]{item, portStr}));
        TablePrinter.print(tbl);
    }
}

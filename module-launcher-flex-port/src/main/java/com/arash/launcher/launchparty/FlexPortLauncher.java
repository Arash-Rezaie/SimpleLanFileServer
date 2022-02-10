package com.arash.launcher.launchparty;

import com.arash.launcher.app.CmdOption;
import com.arash.launcher.config.Settings;
import org.springframework.boot.SpringApplication;

import java.io.IOException;
import java.util.*;

public class FlexPortLauncher implements LaunchParty {
    private final int timeout = 3;
    private final CmdOption myOption = new CmdOption("-p", "--port", "The port you desire run server on");

    private static class OutOfRange extends Exception {
        public OutOfRange(String message) {
            super(message);
        }
    }

    @Override
    public List<CmdOption> getOptions() {
        List<CmdOption> lst = new LinkedList<>();
        lst.add(myOption);
        return lst;
    }

    @Override
    public int when() {
        return When.BEFORE_LAUNCH.value();
    }

    @Override
    public void execute(Map<String, String> userInputOptions, Settings settings, SpringApplication app) throws Exception {
        String portStr = myOption.findValueInUserInput(userInputOptions, null);
        int targetPort = -1;
        try {
            targetPort = Integer.parseInt(portStr);
        } catch (Exception ignored) {
        }

        if (targetPort < 0)
            targetPort = readUserPortByDelay();
        settings.setPort(targetPort);
        app.setDefaultProperties(Collections.singletonMap("server.port", settings.getPort()));
    }

    private int readUserPortByDelay() {
        int targetPort = 8080;
        System.out.println("No port number is passed in, so " + targetPort + " will be used as default port. If you are willing to change it, press \"Enter\" to interrupt:");
        try {
            waitTimeout();
            if (System.in.available() > 0)
                targetPort = getUserDefinedPort();
        } catch (InterruptedException | IOException ignored) {
        }
        return targetPort;
    }

    private void waitTimeout() throws InterruptedException, IOException {
        final int partitionTime = 500;
        int _1SecCounter = 1000 / partitionTime;
        int loop = timeout * 1000 / partitionTime;
        for (int i = 0; i < loop && System.in.available() <= 0; i++) {
            if (i % _1SecCounter == 0)
                System.out.print(". ");
            Thread.sleep(partitionTime);
        }
    }

    private int getUserDefinedPort() {
        System.out.println("\ntype in your desired port:");
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNextLine())
            scanner.nextLine();
        int targetPort;
        while (true) {
            try {
                System.out.print("> ");
                targetPort = Integer.parseInt(scanner.nextLine());
                if (targetPort > 65535 || targetPort < 0)
                    throw new OutOfRange("port range is 0 - 65535");
                return targetPort;
            } catch (OutOfRange e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("Only a number is acceptable!");
            }
        }
    }
}

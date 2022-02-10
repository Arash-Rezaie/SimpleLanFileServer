package com.arash.launcher.app;

import com.arash.launcher.config.Settings;
import com.arash.launcher.launchparty.LaunchParty;
import com.arash.launcher.tools.TablePrinter;
import org.springframework.boot.SpringApplication;

import java.util.*;
import java.util.stream.Collectors;

public class Launcher {
    private final List<CmdOption> options = new LinkedList<CmdOption>() {
        @Override
        public boolean addAll(Collection<? extends CmdOption> co) {
            if (co != null)
                for (CmdOption c : co)
                    add(c);
            return true;
        }

        @Override
        public boolean add(CmdOption c) {
            boolean exists = false;
            for (CmdOption o : options) {
                if ((o.optionShort != null && (o.optionShort.equals(c.optionShort) || o.optionShort.equals(c.optionLong)))
                        ||
                        (o.optionLong != null && (o.optionLong.equals(c.optionShort) || o.optionLong.equals(c.optionLong)))) {
                    exists = true;
                    break;
                }
            }
            if (!exists)
                super.add(c);
            return true;
        }
    };
    private final List<LaunchParty> launchParties = new LinkedList<>();
    private final Map<String, String> args;
    private final Settings settings;

    Launcher(String[] args, Settings settings) {
        this.args = readUserInput(args);
        this.settings = settings;
        options.add(new CmdOption("-h", "--help", "show this help"));
    }

    @SafeVarargs
    final Launcher addLaunchParties(Class<? extends LaunchParty>... launchParties) throws InstantiationException, IllegalAccessException {
        return addLaunchParties(Arrays.asList(launchParties));
    }

    Launcher addLaunchParties(List<Class<? extends LaunchParty>> launchParties) throws InstantiationException, IllegalAccessException {
        List<LaunchParty> lst = new LinkedList<>();
        for (Class<? extends LaunchParty> cls : launchParties)
            lst.add(cls.newInstance());
        lst.forEach(party -> options.addAll(party.getOptions()));
        this.launchParties.addAll(lst);
        return this;
    }

    private Map<String, String> readUserInput(String[] args) {
        Map<String, String> userInputs = new HashMap<>();
        int eqIndex;
        for (String s : args) {
            eqIndex = s.indexOf('=');
            if (eqIndex > 0)
                userInputs.put(s.substring(0, eqIndex), s.substring(eqIndex + 1));
            else
                userInputs.put(s, null);
        }
        return userInputs;
    }

    private void checkOptionsCorrectness() {
        //check necessary options
        for (CmdOption o : options)
            if (o.isNecessary && !args.containsKey(o.optionLong))
                throw new RuntimeException(o.optionLong + " is not provided!");

        //check not defined options
        for (String arg : args.keySet())
            if (options.stream().noneMatch(opt -> opt.check(arg)))
                throw new RuntimeException("option \"" + arg + "\" is not defined!");

    }

    private void execute(SpringApplication app, int when) throws Exception {
        for (LaunchParty lp : launchParties) {
            if ((lp.when() & when) > 0)
                lp.execute(args, settings, app);
        }
    }

    public void execute(SpringApplication app) {
        try {
            checkOptionsCorrectness();
            if (args.containsKey("-h") || args.containsKey("--help")) {
                showHelp();
            } else {
                execute(app, LaunchParty.When.BEFORE_LAUNCH.value());
                app.run();
                execute(app, LaunchParty.When.AFTER_LAUNCH.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
            showHelp();
        }
    }

    private void showHelp() {
        System.out.println("\ncommand format:\njava -jar jar-file [option[=value]]");
        System.out.println("\n--- Available options ---------");
        TablePrinter.print(options.stream()
                .sorted(Comparator.comparingInt(o -> o.isNecessary ? 1 : 0))
                .map(cmdOption -> new String[]{
                        (cmdOption.optionShort != null ? cmdOption.optionShort + ", " : "") + cmdOption.optionLong,
                        cmdOption.description
                })
                .collect(Collectors.toList()));
        System.out.println();
    }
}

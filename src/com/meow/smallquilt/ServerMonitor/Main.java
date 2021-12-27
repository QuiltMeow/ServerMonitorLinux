package com.meow.smallquilt.ServerMonitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

public class Main {

    private static final int MAX_INTERVAL = 86400;
    private static final String CONFIG_FILE = "./Config.ew";
    private static final List<Thread> WORK = new ArrayList<>();
    private static MonitorList setting;

    private static boolean checkSetting() {
        List<MonitorConfig> configList = setting.getConfig();
        if (configList.isEmpty()) {
            return false;
        }

        for (MonitorConfig config : configList) {
            int port = config.getPort();
            if (port <= 0 || port > 65535) {
                return false;
            }

            int interval = config.getInterval();
            if (interval <= 0 || interval > MAX_INTERVAL) {
                return false;
            }
        }
        return true;
    }

    private static void loadConfig() throws Exception {
        Yaml yaml = new Yaml(new Constructor(MonitorList.class));

        File file = new File(CONFIG_FILE);
        try (InputStream is = new FileInputStream(file)) {
            try {
                setting = yaml.load(is);
            } catch (YAMLException ex) {
                throw new RuntimeException("無效的設定檔案", ex);
            }
        }

        if (!checkSetting()) {
            throw new RuntimeException("設定檔案數值錯誤");
        }
    }

    public static void main(String[] args) {
        System.out.println("伺服器自動重啟工具");
        try {
            loadConfig();
        } catch (Exception ex) {
            System.err.println("發生例外狀況 : " + ex.getMessage());
            return;
        }

        List<MonitorConfig> configList = setting.getConfig();
        configList.forEach(config -> {
            Thread task = new Thread(new MonitorTask(config));
            WORK.add(task);

            task.start();
            System.out.println("已載入斷線偵測設定 : " + config.getName());
        });

        for (Thread task : WORK) {
            try {
                task.join();
            } catch (InterruptedException ex) {
                return;
            }
        }
    }
}

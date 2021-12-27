package com.meow.smallquilt.ServerMonitor;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MonitorTask implements Runnable {

    private static final int SLEEP_TIME = 1000;
    private static final int TIMEOUT = 5000;
    private static final Runtime RUNTIME = Runtime.getRuntime();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy / MM / dd HH : mm : ss");
    private static final Lock LOCK = new ReentrantLock();
    private final MonitorConfig config;
    private int run;

    public MonitorTask(MonitorConfig config) {
        this.config = config;
    }

    public static boolean ping(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.setSoTimeout(TIMEOUT);
            socket.connect(new InetSocketAddress(host, port), TIMEOUT);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public void run() {
        String name = config.getName();
        String host = config.getHost();
        int port = config.getPort();
        int interval = config.getInterval() * 1000;

        String close = config.getCloseCommand();
        String start = config.getStartCommand();
        while (true) {
            try {
                Thread.sleep(interval);

                LOCK.lock();
                try {
                    if (!ping(host, port)) {
                        System.out.println("[" + DATE_TIME_FORMATTER.format(LocalDateTime.now()) + "]");
                        System.out.println("[" + name + "] 主機 : " + host + " 端口 : " + port);
                        System.err.println("目標伺服器無法連線");

                        try {
                            if (!close.isEmpty()) {
                                Process process = RUNTIME.exec(close);
                                process.waitFor();
                                System.out.println("伺服器關閉指令執行完畢");
                                Thread.sleep(SLEEP_TIME);
                            }

                            if (!start.isEmpty()) {
                                Process process = RUNTIME.exec(start);
                                process.waitFor();
                                System.out.println("伺服器啟動指令執行完畢");
                                Thread.sleep(SLEEP_TIME);
                            }
                            ++run;
                            System.out.println("伺服器已重啟 " + run + " 次");
                        } catch (Exception ex) {
                            System.err.println("執行指令時發生例外狀況 : " + ex.getMessage());
                        }
                    }
                } finally {
                    LOCK.unlock();
                }
            } catch (InterruptedException ex) {
                break;
            }
        }
    }
}

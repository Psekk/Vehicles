package me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.threads;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

//todo maybe add multiple thread support by having a static list of tasks and a synchronized getting of a task + removal
public class ChatThread extends Thread {
    @Getter
    private final List<Runnable> tasks = new ArrayList<>();

    public boolean running = false;

    @Override
    public void run() {
        this.setDaemon(true);
        running = true;
        while(running) {
            if (tasks.size() < 1) {
                continue;
            }
            this.setDaemon(false);
            for (Runnable task : tasks) {
                task.run();
            }
            this.setDaemon(true);
        }
    }

    public void add(Runnable task) {
        tasks.add(task);
    }

    public void remove(Runnable task) {
        if (!tasks.contains(task)) {
            return;
        }
        tasks.remove(task);
    }
}

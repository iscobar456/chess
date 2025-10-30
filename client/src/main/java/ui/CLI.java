package ui;

import java.util.HashMap;

public class CLI {
    boolean isAuthorized = false;

    public void help() {
        System.out.println("""
                Help message...
                """);
    }

    public void quit() {
        System.out.println("Goodbye!");
    }

    public boolean processCommand(String command) {
        HashMap<String, Runnable> handlers = new HashMap<>();
        handlers.put("help", () -> help());
        handlers.put("quit", () -> quit());
        handlers.put("login", () -> help());
        handlers.put("register", () -> help());
        if (isAuthorized) {
            handlers.put("logout", () -> help());
            handlers.put("create", () -> help());
            handlers.put("list", () -> help());
            handlers.put("join", () -> help());
            handlers.put("observe", () -> help());
        }

        return !command.equals("exit");
    }
}

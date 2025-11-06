package ui;

import serverfacade.ServerFacade;

import java.util.HashMap;
import java.util.Scanner;

public class CLI {
    Scanner scanner;
    ServerFacade server;

    public CLI() {
        scanner = new Scanner(System.in);
        server = new ServerFacade("http", "localhost", 5321);
    }
    boolean isAuthorized = false;

    public void help() {
        System.out.println("""
                Help message...
                """);
    }

    public void quit() {
        System.out.println("Goodbye!");
    }

    public void login() throws Exception {
        System.out.printf("Username: ");
        String username = scanner.nextLine();
        String password = scanner.nextLine();
        String authToken = server.login(username, password);
    }

    public void register() {

    }

    public void logout() {

    }

    public void create() {

    }

    public void list() {

    }

    public void join() {

    }

    public void observe() {

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

        return !command.equals("quit");
    }
}

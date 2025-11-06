package ui;

import serverfacade.Client;
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
        System.out.printf("Password: ");
        String password = scanner.nextLine();

        try {
            server.login(username, password);
        } catch (Client.BadRequestResponse e) {
            System.out.println("Password and username are required fields.");
        } catch (Client.UnauthorizedResponse e) {
            System.out.println("Incorrect username or password.");
        }
    }

    public void register() throws Exception {
        System.out.printf("Username: ");
        String username = scanner.nextLine();
        System.out.printf("Password: ");
        String password = scanner.nextLine();
        System.out.printf("Email: ");
        String email = scanner.nextLine();

        try {
            server.register(username, password, email);
        } catch (Client.BadRequestResponse e) {
            System.out.println("Username, password, and email are required fields.");
        } catch (Client.UnauthorizedResponse e) {
            System.out.println("Incorrect username or password.");
        }
    }

    public void logout() throws Exception {
        try {
            server.logout();
        } catch (Client.UnauthorizedResponse e) {
            System.out.println("Not logged in.");
        }
    }

    public void create() throws Exception {
//        System.out.printf("Game name: ");
//        String gameName = scanner.nextLine();
//
//        try {
//            server.createGame(gameName);
//        } catch (Client.BadRequestResponse e) {
//            System.out.println("");
//        } catch (Client.UnauthorizedResponse e) {
//            System.out.println("Incorrect username or password.");
//        }
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

        try {
            handlers.get(command).run();
        } catch (Exception e) {
            System.out.println("An error occurred and the operation was unsuccessful.");
        }

        return !command.equals("quit");
    }
}

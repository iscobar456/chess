package ui;

import serverfacade.Client;
import serverfacade.ServerFacade;

import java.util.HashMap;
import java.util.Scanner;

public class CLI {
    Scanner scanner;
    ServerFacade server;
    boolean isAuthorized = false;

    public CLI() {
        scanner = new Scanner(System.in);
        server = new ServerFacade("http", "localhost", 5321);
    }

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
            isAuthorized = true;
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
            isAuthorized = true;
        } catch (Client.BadRequestResponse e) {
            System.out.println("Username, password, and email are required fields.");
        } catch (Client.UnauthorizedResponse e) {
            System.out.println("Incorrect username or password.");
        }
    }

    public void logout() throws Exception {
        try {
            server.logout();
            isAuthorized = false;
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

    public interface CLIRunnable {
        void run() throws Exception;
    }

    public boolean processCommand(String command) {
        HashMap<String, CLIRunnable> handlers = new HashMap<>();
        handlers.put("help", () -> help());
        handlers.put("quit", () -> quit());
        handlers.put("login", () -> login());
        handlers.put("register", () -> register());
        if (isAuthorized) {
            handlers.put("logout", () -> login());
            handlers.put("create", () -> create());
            handlers.put("list", () -> list());
            handlers.put("join", () -> join());
            handlers.put("observe", () -> observe());
        }

        try {
            handlers.get(command).run();
        } catch (Exception e) {
            System.out.println("An error occurred and the operation was unsuccessful.");
        }

        return !command.equals("quit");
    }
}

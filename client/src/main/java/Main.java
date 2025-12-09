import ui.CLI;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        CLI cli;
        try {
            cli = new CLI();
        } catch (Exception e) {
            System.out.println("Unable to connect to server");
            return;
        }
        System.out.print(">>> ");
        System.out.flush();
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();
            if (!cli.processCommand(command)) {
                break;
            }
        }
    }
}
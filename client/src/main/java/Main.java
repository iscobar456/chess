import ui.CLI;

import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        while (true) {
            System.out.printf("%n>>> ");
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();

            CLI cli = new CLI();
            if (!cli.processCommand(command)) {
                System.out.println("Goodbye!");
                break;
            }
        }
    }
}
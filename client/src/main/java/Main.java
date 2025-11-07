import ui.CLI;

import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        CLI cli = new CLI();
        while (true) {
            System.out.printf("%n>>> ");
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();
            if (!cli.processCommand(command)) {
                break;
            }
        }
    }
}
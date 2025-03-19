package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

// has the menu
public class Client {
    boolean LOGGED_IN = false;

    public static void main(String[] args) {
//        var serverUrl = "http://localhost:8080";
//        if (args.length == 1) {
//            serverUrl = args[0];
//        }
//
//        new Server(serverUrl).run();

        displayPreLogin();
    }

    private static void displayPreLogin() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        Scanner scanner = new Scanner(System.in);
        // display pre login menu
        while (true) {
            out.println("[Logged Out]");
            out.println("\t1. Register");
            out.println("\t2. Login");
            out.println("\t3. Help");
            out.println("\t4. Quit\n");
            out.print(">> ");

            String response = scanner.nextLine();

            switch (response) {
                case "1":
                    // register logic
                case "2":
                    // Login logic
                case "3":
                    // help
                case "4":
                    return;
            }
        }
    }
}

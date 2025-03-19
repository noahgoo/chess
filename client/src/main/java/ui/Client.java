package ui;

import net.ServerFacade;
import request.LoginRequest;
import request.RegisterRequest;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

// has the menu
public class Client {
    static boolean QUIT = false;

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
        ServerFacade serverFacade = new ServerFacade();
        // display pre login menu
        while (true) {
            out.println("[Logged Out]");
            out.println("\t1. Register");
            out.println("\t2. Login");
            out.println("\t3. Help");
            out.println("\t4. Quit");
            out.print(">> ");

            String response = scanner.next();

            switch (response) {
                case "1":
                    RegisterRequest registerRequest = getRegisterRequest(out, scanner);
                    serverFacade.register(registerRequest);
                    // out.println(registerRequest);
                    break;
                case "2":
                    LoginRequest loginRequest = getLoginRequest(out, scanner);
                    serverFacade.login(loginRequest);
                    displayPostLogin(out, scanner);
                    break;
                case "3":
                    // help
                case "4":
                    return;
            }

            if (QUIT) {
                return;
            }
        }
    }

    private static RegisterRequest getRegisterRequest(PrintStream out, Scanner scanner) {
        out.println("\nPlease provide <USERNAME> <PASSWORD> <EMAIL>");
        out.print(">> ");
        return new RegisterRequest(scanner.next(), scanner.next(), scanner.next());
    }

    private static LoginRequest getLoginRequest(PrintStream out, Scanner scanner) {
        out.println("\nPlease provide your <USERNAME> <PASSWORD>");
        out.print(">> ");
        return new LoginRequest(scanner.next(), scanner.next());
    }

    private static void displayPostLogin(PrintStream out, Scanner scanner) {
        while (true) {
            out.println("[Logged In]");
            out.println("\t1. Create Game");
            out.println("\t2. List Games");
            out.println("\t3. Join Game");
            out.println("\t4. Observe Game");
            out.println("\t5. Logout");
            out.println("\t6. Help");
            out.println("\t7. Quit");

            String response = scanner.next();

            switch (response) {
                case "5":
                    // logout
                case "7":
                    QUIT = true;
                    return;
            }
        }
    }

}

package me.purplegg.proxy;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ConsoleUtils {

    public static void clearConsole() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();;
        } catch(Exception e) {}
    }

    public static void delay(int milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private interface CLibrary extends Library {
        CLibrary INSTANCE = (CLibrary)
                Native.loadLibrary((Platform.isWindows() ? "kernel32" : "c"),
                        CLibrary.class);

        boolean SetConsoleTitleA(String title);
    }

    public static void setTitle(String title) {
        CLibrary.INSTANCE.SetConsoleTitleA(title);
    }

    public static void printWarning() {
        clearConsole();
        print(Main.Warning, ConsoleColor.red);
        for (int i = 12; i >= 0; i--) {
            print(i + " seconds.", ConsoleColor.red);
            delay(1000);
        }
    }

    public static void showLoading() {
        clearConsole();
        print(Main.AsciiArtRedHat, ConsoleColor.red);
        delay(500);
        clearConsole();
        print(Main.AsciiArtRedHatGlich1, ConsoleColor.red);
        delay(100);
        clearConsole();
        print(Main.AsciiArtRedHatGlich2, ConsoleColor.red);
        delay(100);
        clearConsole();
        print(Main.AsciiArtRedHatGlich1, ConsoleColor.red);
        delay(100);
        clearConsole();
        print(Main.AsciiArtRedHatGlich2, ConsoleColor.red);
        delay(100);
        clearConsole();
        print(Main.AsciiArtRedHat, ConsoleColor.red);
        delay(500);
        clearConsole();
    }

    public static void printOptions() {
        print(Main.Options, ConsoleColor.purple);
    }

    public static void printLogo() {
        print(Main.AsciiArtLogo, ConsoleColor.purple);
    }

    public static int getOption() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("root/> ");
            String option = scanner.nextLine().trim();
            if (!option.isEmpty()) {
                try {
                    return Math.abs(Integer.parseInt(option));
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    public static void print(String message, ConsoleColor color) {
        // +"\033[0m"
        Main.logger.info((color == ConsoleColor.black ? "\033[30m" :
                color == ConsoleColor.red ? "\033[31m" :
                color == ConsoleColor.green ? "\033[32m" :
                color == ConsoleColor.yellow ? "\033[33m" :
                color == ConsoleColor.blue ? "\033[34m" :
                color == ConsoleColor.purple ? "\033[35m" :
                color == ConsoleColor.cyan ? "\033[36m" :
                color == ConsoleColor.white ? "\033[37m" : "\033[0m")+message+"\033[0m");
    }

    public static void spoofOption(int option) throws Exception {
        switch (option) {
            case 0:
                System.exit(0);
                break;
            case 1:
                // TODO Automatic proxy generator for all protocols
                break;
            case 2:
                // TODO Automatic HTTP/S proxy generator
                break;
            case 3:
                // TODO Automatic SOCK4/5 proxy generator
                break;
            case 4:
                // TODO Manual HTTP/S proxy checker
                break;
            case 5:
                // TODO Manual SOCK4/5 proxy checker
                break;
            case 6:
                // TODO Manual proxy checker for all protocols
                break;
            case 7:
                // TODO Save unchecked web proxies
                break;
            default:
                printOptions();
                break;
        }
    }

    public static void validateLicense() {
        String license = "";
        Scanner scanner = new Scanner(System.in);
        String LicenseKey = "Z7CyEi2piNbtsxn2";
        while (!license.equals(LicenseKey)) {
            print("Enter your license key", ConsoleColor.yellow);
            license = scanner.nextLine().trim();
        }
    }
}

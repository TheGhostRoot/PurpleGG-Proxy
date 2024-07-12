package me.purplegg.proxy;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

import java.net.Proxy;
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
                    printOptions();
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

    public static int getThreads() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            ConsoleUtils.print("How many threads? ", ConsoleColor.yellow);
            String option = scanner.nextLine().trim();
            if (!option.isEmpty()) {
                try {
                    return Math.abs(Integer.parseInt(option));
                } catch (NumberFormatException ignored) {}
            }
        }
    }

    public static int getAmount() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            ConsoleUtils.print("How many proxies? ", ConsoleColor.yellow);
            String option = scanner.nextLine().trim();
            if (!option.isEmpty()) {
                try {
                    return Math.abs(Integer.parseInt(option));
                } catch (NumberFormatException ignored) {
                    return -1;
                }
            }
        }
    }

    public static String getOutputPath() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            ConsoleUtils.print("Enter the path of output file with the file extension like .txt ? ", ConsoleColor.yellow);
            String option = scanner.nextLine().trim();
            if (!option.isEmpty()) {
                return option;
            }
        }
    }

    public static String getProxyPath() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            ConsoleUtils.print("Enter the path of input proxy file with the file extension like .txt ? ", ConsoleColor.yellow);
            String option = scanner.nextLine().trim();
            if (!option.isEmpty()) {
                return option;
            }
        }
    }

    public static void spoofOption(int option) {
        if (option == 0) {
            System.exit(0);
            return;
        }
        int threads = getThreads();
        int amount = getAmount();
        String outputFile = getOutputPath();
        switch (option) {
            case 1:
                ProxyHandler.runAutomaticProxyGenerator(threads, amount, Proxy.Type.HTTP, outputFile);
                break;
            case 2:
                ProxyHandler.runAutomaticProxyGenerator(threads, amount, Proxy.Type.SOCKS, outputFile);
                break;
            case 3:
                String httpFile = getProxyPath();
                ProxyHandler.runManualProxyGenerator(httpFile, "", threads, amount, Proxy.Type.HTTP,
                        outputFile);
                break;
            case 4:
                String socksFile = getProxyPath();
                ProxyHandler.runManualProxyGenerator(socksFile, "", threads, amount, Proxy.Type.SOCKS,
                        outputFile);
                break;
            case 5:
                ProxyHandler.saveProxies(ProxyHandler.getProxiesFromInternet(Proxy.Type.HTTP, amount), outputFile);
                break;
            case 6:
                ProxyHandler.saveProxies(ProxyHandler.getProxiesFromInternet(Proxy.Type.SOCKS, amount), outputFile);
                break;
            case 7:
                ProxyHandler.runScanWholeInternet(Proxy.Type.HTTP, threads, amount, outputFile);
                break;
            case 8:
                ProxyHandler.runScanWholeInternet(Proxy.Type.SOCKS, threads, amount, outputFile);
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

package me.purplegg.proxy;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.logging.Logger;

public class Main {
    public static Logger logger = Logger.getGlobal();
    public static ObjectMapper mapper = new ObjectMapper();

    public static String Warning = new StringBuilder("    WARNING!!!\n")
            .append("  THIS IS NOT A MALICIOUS PROGRAM. PURPLE GG WILL NEVER GIVE YOU VIRUSES. DON'T TRUST OTHER SOURCES THEN OUR PURPLE GG DISCORD: https://discord.gg/wm8pVfW5dF\n")
            .append("  By using this program you automatically agree to our ToS (Terms of Service) in our discord.\n")
            .append("  This program gets proxies from the internet and saves only the good ones that are low latency and working.\n" )
            .toString();

    public static String Options = new StringBuilder(" [0] Exit\n")
            .append(" [1] Automatic HTTP/S proxy generator\n").append(" [2] Automatic SOCKS4/5 proxy generator\n\n")
            .append(" [3] Manual HTTP/S proxy checker\n").append(" [4] Manual SOCKS4/5 proxy checker\n")
            .append(" [5] Save unchecked HTTP/S web proxies\n").append(" [6] Save unchecked SOCKS4/5 web proxies\n")
            .toString();

    public static String AsciiArtLogo = new StringBuilder( "\n     ███████████                                 ████                █████████    █████████   \n")
            .append("    ░░███░░░░░███                               ░░███               ███░░░░░███  ███░░░░░███  \n")
            .append("     ░███    ░███ █████ ████ ████████  ████████  ░███   ██████     ███     ░░░  ███     ░░░   \n")
            .append("     ░██████████ ░░███ ░███ ░░███░░███░░███░░███ ░███  ███░░███   ░███         ░███           \n")
            .append("     ░███░░░░░░   ░███ ░███  ░███ ░░░  ░███ ░███ ░███ ░███████    ░███    █████░███    █████  \n")
            .append("     ░███         ░███ ░███  ░███      ░███ ░███ ░███ ░███░░░     ░░███  ░░███ ░░███  ░░███   \n")
            .append("     █████        ░░████████ █████     ░███████  █████░░██████     ░░█████████  ░░█████████   \n")
            .append("    ░░░░░          ░░░░░░░░ ░░░░░      ░███░░░  ░░░░░  ░░░░░░       ░░░░░░░░░    ░░░░░░░░░    \n")
            .append("                                       ░███                                                   \n")
            .append("                                       █████                                                  \n")
            .append("                                      ░░░░░                                                   \n")
            .append("     █████   ███   █████                      ██████                                          \n")
            .append("    ░░███   ░███  ░░███                      ███░░███                                         \n")
            .append("     ░███   ░███   ░███   ██████   ██████   ░███ ░░░   ██████  ████████                       \n")
            .append("     ░███   ░███   ░███  ███░░███ ███░░███ ███████    ███░░███░░███░░███                      \n")
            .append("     ░░███  █████  ███  ░███ ░███░███ ░███░░░███░    ░███████  ░███ ░░░                       \n")
            .append("      ░░░█████░█████░   ░███ ░███░███ ░███  ░███     ░███░░░   ░███                           \n")
            .append("        ░░███ ░░███     ░░██████ ░░██████   █████    ░░██████  █████                          \n")
            .append("         ░░░   ░░░       ░░░░░░   ░░░░░░   ░░░░░      ░░░░░░  ░░░░░                           \n\n")
            .toString();

    public static String AsciiArtRedHat = new StringBuilder("\n                       _.+sd$$$$$$$$$bs+._                   \n")
            .append("                   .+d$$$$$$$$$$$$$$$$$$$$$b+.               \n")
            .append("                .sd$$$$$$$P^*^T$$$P^*\"*^T$$$$$bs.           \n")
            .append("              .s$$$$$$$$P*     `*' _._  `T$$$$$$$s.          \n")
            .append("            .s$$$$$$$$$P          ` :$;   T$$$$$$$$s.        \n")
            .append("           s$$$$$$$$$$;  db..+s.   `**'    T$$$$$$$$$s       \n")
            .append("         .$$$$$$$$$$$$'  `T$P*'             T$$$$$$$$$$.     \n")
            .append("        .$$$$$$$$$$$$P                       T$$$$$$$$$$.    \n")
            .append("       .$$$$$$$$$$$$$b                       `$$$$$$$$$$$.   \n")
            .append("      :$$$$$$$$$$$$$$$.                       T$$$$$$$$$$$;  \n")
            .append("      $$$$$$$$$P^*' :$$b.                     d$$$$$$$$$$$$  \n")
            .append("     :$$$$$$$P'      T$$$$bs._               :P'`*^T$$$$$$$; \n")
            .append("     $$$$$$$P         `*T$$$$$b              '      `T$$$$$$ \n")
            .append("    :$$$$$$$b            `*T$$$s                      :$$$$$;\n")
            .append("    :$$$$$$$$b.                                        $$$$$;\n")
            .append("    $$$$$$$$$$$b.                                     :$$$$$$\n")
            .append("    $$$$$$$$$$$$$bs.                                 .$$$$$$$\n")
            .append("    $$$$$$$$$$$$$$$$$bs.                           .d$$$$$$$$\n")
            .append("    :$$$$$$$$$$$$$P*\"*T$$bs,._                  .sd$$$$$$$$$;\n")
            .append("    :$$$$$$$$$$$$P     TP^**T$bss++.._____..++sd$$$$$$$$$$$$;\n")
            .append("     $$$$$$$$$$$$b           `T$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ \n")
            .append("     :$$$$$$$$$$$$b.           `*T$$P^*\"*\"*^^*T$$$$$$$$$$$$; \n")
            .append("      $$$b       `T$b+                        :$$$$$$$BUG$$  \n")
            .append("      :$P'         `\"'               ,._.     ;$$$$$$$$$$$;  \n")
            .append("       \\                            `*TP*     d$$P*******$   \n")
            .append("        \\                                    :$$P'      /    \n")
            .append("         \\                                  :dP'       /     \n")
            .append("          `.                               d$P       .'      \n")
            .append("            `.                             `'      .'        \n")
            .append("              `-.                               .-'          \n")
            .append("                 `-.                         .-'             \n")
            .append("                    `*+-._             _.-+*'                \n")
            .append("                          `\"*-------*\"'                      \n\n")
            .toString();

    public static String AsciiArtRedHatGlich1 = new StringBuilder("\n                       _.+sd$$$$$$$$$bs+._                   \n")
            .append("                    .+d$$$$$$$$$$$$$$$$$$$$$b+.               \n")
            .append("                   .sd$$$$$$$P^*^T$$$P^*\"*^T$$$$$bs.           \n")
            .append("              .s$$$$$$$$P*     `*' _._  `T$$$$$$$s.          \n")
            .append("            .s$$$$$$$$$P          ` :$;   T$$$$$$$$s.        \n")
            .append("              s$$$$$$$$$$;  db..+s.   `**'    T$$$$$$$$$s       \n")
            .append("         .$$$$$$$$$$$$'  `T$P*'             T$$$$$$$$$$.     \n")
            .append("           .$$$$$$$$$$$$P                       T$$$$$$$$$$.    \n")
            .append("       .$$$$$$$$$$$$$b                       `$$$$$$$$$$$.   \n")
            .append("        :$$$$$$$$$$$$$$$.                       T$$$$$$$$$$$;  \n")
            .append("          $$$$$$$$$P^*' :$$b.                     d$$$$$$$$$$$$  \n")
            .append("       :$$$$$$$P'      T$$$$bs._               :P'`*^T$$$$$$$; \n")
            .append("     $$$$$$$P         `*T$$$$$b              '      `T$$$$$$ \n")
            .append("       :$$$$$$$b            `*T$$$s                      :$$$$$;\n")
            .append("    :$$$$$$$$b.                                        $$$$$;\n")
            .append("       $$$$$$$$$$$b.                                     :$$$$$$\n")
            .append("    $$$$$$$$$$$$$bs.                                 .$$$$$$$\n")
            .append("      $$$$$$$$$$$$$$$$$bs.                           .d$$$$$$$$\n")
            .append("        :$$$$$$$$$$$$$P*\"*T$$bs,._                  .sd$$$$$$$$$;\n")
            .append("     :$$$$$$$$$$$$P     TP^**T$bss++.._____..++sd$$$$$$$$$$$$;\n")
            .append("     $$$$$$$$$$$$b           `T$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ \n")
            .append("          :$$$$$$$$$$$$b.           `*T$$P^*\"*\"*^^*T$$$$$$$$$$$$; \n")
            .append("      $$$b       `T$b+                        :$$$$$$$BUG$$  \n")
            .append("             :$P'         `\"'               ,._.     ;$$$$$$$$$$$;  \n")
            .append("       \\                            `*TP*     d$$P*******$   \n")
            .append("            \\                                    :$$P'      /    \n")
            .append("         \\                                  :dP'       /     \n")
            .append("                 `.                               d$P       .'      \n")
            .append("            `.                             `'      .'        \n")
            .append("                    `-.                               .-'          \n")
            .append("                   `-.                         .-'             \n")
            .append("                     `*+-._             _.-+*'                \n")
            .append("                          `\"*-------*\"'                      \n\n")
            .toString();

    public static String AsciiArtRedHatGlich2 = new StringBuilder("\n                       _.+sd$$$$$$$$$bs+._                   \n")
            .append("                    .+d$$$$$$$$$$$$$$$$$$$$$b+.               \n")
            .append("             .sd$$$$$$$P^*^T$$$P^*\"*^T$$$$$bs.           \n")
            .append("              .s$$$$$$$$P*     `*' _._  `T$$$$$$$s.          \n")
            .append("        .s$$$$$$$$$P          ` :$;   T$$$$$$$$s.        \n")
            .append("        s$$$$$$$$$$;  db..+s.   `**'    T$$$$$$$$$s       \n")
            .append("      .$$$$$$$$$$$$'  `T$P*'             T$$$$$$$$$$.     \n")
            .append("           .$$$$$$$$$$$$P                       T$$$$$$$$$$.    \n")
            .append("   .$$$$$$$$$$$$$b                       `$$$$$$$$$$$.   \n")
            .append("    :$$$$$$$$$$$$$$$.                       T$$$$$$$$$$$;  \n")
            .append("     $$$$$$$$$P^*' :$$b.                     d$$$$$$$$$$$$  \n")
            .append("    :$$$$$$$P'      T$$$$bs._               :P'`*^T$$$$$$$; \n")
            .append("  $$$$$$$P         `*T$$$$$b              '      `T$$$$$$ \n")
            .append("    :$$$$$$$b            `*T$$$s                      :$$$$$;\n")
            .append("  :$$$$$$$$b.                                        $$$$$;\n")
            .append("    $$$$$$$$$$$b.                                     :$$$$$$\n")
            .append(" $$$$$$$$$$$$$bs.                                 .$$$$$$$\n")
            .append("   $$$$$$$$$$$$$$$$$bs.                           .d$$$$$$$$\n")
            .append("      :$$$$$$$$$$$$$P*\"*T$$bs,._                  .sd$$$$$$$$$;\n")
            .append("   :$$$$$$$$$$$$P     TP^**T$bss++.._____..++sd$$$$$$$$$$$$;\n")
            .append("   $$$$$$$$$$$$b           `T$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ \n")
            .append("        :$$$$$$$$$$$$b.           `*T$$P^*\"*\"*^^*T$$$$$$$$$$$$; \n")
            .append("    $$$b       `T$b+                        :$$$$$$$BUG$$  \n")
            .append("             :$P'         `\"'               ,._.     ;$$$$$$$$$$$;  \n")
            .append("     \\                            `*TP*     d$$P*******$   \n")
            .append("      \\                                    :$$P'      /    \n")
            .append("        \\                                  :dP'       /     \n")
            .append("              `.                               d$P       .'      \n")
            .append("       `.                             `'      .'               \n")
            .append("                  `-.                               .-'          \n")
            .append("            `-.                         .-'             \n")
            .append("                  `*+-._             _.-+*'                \n")
            .append("                      `\"*-------*\"'                      \n\n")
            .toString();

    public static void main(String[] args) {
        Logger.getLogger("").getHandlers()[0].setFormatter(new LoggerFormatter());
        ConsoleUtils.setTitle("Purple GG | Woofer");
        ConsoleUtils.clearConsole();
        ConsoleUtils.validateLicense();
        ConsoleUtils.printWarning();
        ConsoleUtils.showLoading();
        ConsoleUtils.printLogo();
        ConsoleUtils.printOptions();
        while (true) {
            try {
                ConsoleUtils.spoofOption(ConsoleUtils.getOption());
            } catch (Exception e) {
                break;
            }
        }
    }
}
package me.purplegg.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ProxyHandler {

    public static Map<String, Object> getRequestMapData(HttpURLConnection connection) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return (Map<String, Object>) Main.mapper.readValue(response.toString(), Map.class);
    }

    public static List<Map<String, Object>> getRequestListData(HttpURLConnection connection) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return (List<Map<String, Object>>) Main.mapper.readValue(response.toString(), List.class);
    }

    public static List<String> getRequestTextData(HttpURLConnection connection) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        List<String> responseLines = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            responseLines.add(line.replace("https://", "").replace("http://", "")
                    .replace("socks5://", "").replace("socks4://", ""));
        }

        reader.close();
        return responseLines;
    }

    public static List<PurpleProxy> getGeoNodeProxies(Proxy.Type protocol, int amount) throws Exception {
        List<PurpleProxy> allProxies = new ArrayList<>();
        HttpURLConnection connection = (HttpURLConnection)
                URI.create("https://proxylist.geonode.com/api/proxy-list?limit="+
                        amount+"&page=1&sort_by=lastChecked&sort_type=desc").toURL().openConnection();
        connection.setRequestMethod("GET");
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            Map<String, Object> jsonMap = getRequestMapData(connection);
            for (Map<String, Object> webProxy : (List<Map<String, Object>>) jsonMap.get("data")) {
                List<String> protos = ((List<String>) webProxy.get("protocols"));
                int port = Integer.parseInt(String.valueOf(webProxy.get("port")));
                if ((protos.contains("socks4") || protos.contains("socks5")) && protocol.equals(Proxy.Type.SOCKS)) {
                    allProxies.add(new PurpleProxy(String.valueOf(webProxy.get("ip")),
                            port, "", "", protocol, -1));
                }

                if ((protos.contains("http") || protos.contains("https")) && protocol.equals(Proxy.Type.HTTP)) {
                    allProxies.add(new PurpleProxy(String.valueOf(webProxy.get("ip")),
                            port, "", "", protocol, -1));
                }

            }
        }
        connection.disconnect();
        ConsoleUtils.print("Got "+allProxies.size()+" proxies from geonode.com", ConsoleColor.green);
        return allProxies;
    }

    public static List<PurpleProxy> getProxyScrapeProxies(Proxy.Type protocol, int amount) throws Exception {
        List<PurpleProxy> allProxies = new ArrayList<>();
        HttpURLConnection connection = (HttpURLConnection)
                URI.create("https://api.proxyscrape.com/v3/free-proxy-list/get?request=displayproxies&proxy_format=protocolipport&format=json&limit="+
                        amount).toURL().openConnection();
        connection.setRequestMethod("GET");
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            for (Map<String, Object> webProxy : (List<Map<String, Object>>) getRequestMapData(connection).get("proxies")) {
                String protos = String.valueOf(webProxy.get("protocol"));
                if ((protos.contains("socks") && protocol.equals(Proxy.Type.SOCKS)) ||
                        (protos.contains("http") && protocol.equals(Proxy.Type.HTTP))) {
                    allProxies.add(new PurpleProxy(String.valueOf(webProxy.get("ip")),
                            Integer.parseInt(String.valueOf(webProxy.get("port"))), "", "", protocol,
                            -1));
                }

            }
        }
        connection.disconnect();
        ConsoleUtils.print("Got "+allProxies.size()+" proxies from proxyscrape.com", ConsoleColor.green);
        return allProxies;
    }

    public static List<PurpleProxy> getGimmeProxyProxies(Proxy.Type protocol, int amount) throws Exception {
        List<PurpleProxy> allProxies = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            HttpURLConnection connection = (HttpURLConnection)
                    URI.create("https://gimmeproxy.com/api/getProxy").toURL().openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Map<String, Object> jsonMap = getRequestMapData(connection);
                String proto = String.valueOf(jsonMap.get("protocol"));
                if ((proto.contains("socks") && protocol.equals(Proxy.Type.SOCKS)) ||
                        (proto.contains("http") && protocol.equals(Proxy.Type.HTTP))) {
                    allProxies.add(new PurpleProxy(String.valueOf(jsonMap.get("ip")),
                            Integer.parseInt(String.valueOf(jsonMap.get("port"))), "", "",
                            protocol, -1));
                }
            }
            connection.disconnect();
        }
        ConsoleUtils.print("Got "+allProxies.size()+" proxies from gimmeproxy.com", ConsoleColor.green);
        return allProxies;
    }

    public static List<PurpleProxy> getGithubTheSpeedXProxies(Proxy.Type protocol, int amount) throws Exception {
        List<PurpleProxy> allProxies = new ArrayList<>();
        List<URL> urls = new ArrayList<>();

        if (protocol.equals(Proxy.Type.HTTP)) {
            urls.add(URI.create("https://raw.githubusercontent.com/TheSpeedX/SOCKS-List/master/http.txt").toURL());
        } else {
            urls.add(URI.create("https://raw.githubusercontent.com/TheSpeedX/SOCKS-List/master/socks5.txt").toURL());
            urls.add(URI.create("https://raw.githubusercontent.com/TheSpeedX/SOCKS-List/master/socks4.txt").toURL());
        }
        allProxies = handleScrapingProxies(protocol, amount, urls, allProxies);
        ConsoleUtils.print("Got "+allProxies.size()+" proxies from github.com from TheSpeedX", ConsoleColor.green);
        return allProxies;
    }

    public static List<PurpleProxy> getOpenProxyProxies(Proxy.Type protocol, int amount) throws Exception {
        List<PurpleProxy> allProxies = new ArrayList<>();
        List<URL> urls = new ArrayList<>();
        if (protocol.equals(Proxy.Type.HTTP)) {
            urls.add(URI.create("https://openproxylist.xyz/http.txt").toURL());
        } else {
            urls.add(URI.create("https://openproxylist.xyz/socks4.txt").toURL());
            urls.add(URI.create("https://openproxylist.xyz/socks5.txt").toURL());
        }
        allProxies = handleScrapingProxies(protocol, amount, urls, allProxies);
        ConsoleUtils.print("Got "+allProxies.size()+" proxies from openproxylist.xyz", ConsoleColor.green);
        return allProxies;
    }

    public static List<PurpleProxy> getVPNFallProxies(Proxy.Type protocol, int amount) throws Exception {
        List<PurpleProxy> allProxies = new ArrayList<>();
        HttpURLConnection connection = (HttpURLConnection)
                URI.create("https://vpn.fail/free-proxy/json").toURL().openConnection();
        connection.setRequestMethod("GET");
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            for (Map<String, Object> webProxy : getRequestListData(connection)) {
                String[] proxyParts = String.valueOf(webProxy.get("proxy")).split(":");
                String protos = String.valueOf(webProxy.get("type"));
                if ((protos.contains("socks") && protocol.equals(Proxy.Type.SOCKS)) ||
                        (protos.contains("http") && protocol.equals(Proxy.Type.HTTP))) {
                    if (allProxies.size() >= amount) {
                        break;
                    }
                    allProxies.add(new PurpleProxy(proxyParts[0], Integer.parseInt(proxyParts[1]),
                            "", "", protocol, -1));
                }

            }
        }
        connection.disconnect();
        ConsoleUtils.print("Got "+allProxies.size()+" proxies from vpn.fail", ConsoleColor.green);
        return allProxies;
    }

    public static List<PurpleProxy> getGithubJetkaiProxies(Proxy.Type protocol, int amount) throws Exception {
        List<PurpleProxy> allProxies = new ArrayList<>();
        List<URL> urls = new ArrayList<>();
        if (protocol.equals(Proxy.Type.HTTP)) {
            urls.add(URI.create("https://raw.githubusercontent.com/jetkai/proxy-list/main/online-proxies/txt/proxies-https.txt").toURL());
            urls.add(URI.create("https://raw.githubusercontent.com/jetkai/proxy-list/main/online-proxies/txt/proxies-http.txt").toURL());
        } else {
            urls.add(URI.create("https://raw.githubusercontent.com/jetkai/proxy-list/main/online-proxies/txt/proxies-socks4.txt").toURL());
            urls.add(URI.create("https://raw.githubusercontent.com/jetkai/proxy-list/main/online-proxies/txt/proxies-socks5.txt").toURL());
        }
        allProxies = handleScrapingProxies(protocol, amount, urls, allProxies);
        ConsoleUtils.print("Got "+allProxies.size()+" proxies from github jetkai", ConsoleColor.green);
        return allProxies;
    }

    public static List<PurpleProxy> getGithubProxifyProxies(Proxy.Type protocol, int amount) throws Exception {
        List<PurpleProxy> allProxies = new ArrayList<>();
        List<URL> urls = new ArrayList<>();
        if (protocol.equals(Proxy.Type.HTTP)) {
            urls.add(URI.create("https://raw.githubusercontent.com/proxifly/free-proxy-list/main/proxies/protocols/http/data.txt").toURL());
        } else {
            urls.add(URI.create("https://raw.githubusercontent.com/proxifly/free-proxy-list/main/proxies/protocols/socks4/data.txt").toURL());
            urls.add(URI.create("https://raw.githubusercontent.com/proxifly/free-proxy-list/main/proxies/protocols/socks5/data.txt").toURL());
        }
        allProxies = handleScrapingProxies(protocol, amount, urls, allProxies);
        ConsoleUtils.print("Got "+allProxies.size()+" proxies from github proxify", ConsoleColor.green);
        return allProxies;
    }

    public static List<PurpleProxy> getGithubAlexProxies(Proxy.Type protocol, int amount) throws Exception {
        List<PurpleProxy> allProxies = new ArrayList<>();
        List<URL> urls = new ArrayList<>();
        if (protocol.equals(Proxy.Type.HTTP)) {
            urls.add(URI.create("https://alexa.lr2b.com/http.txt").toURL());
        } else {
            urls.add(URI.create("https://alexa.lr2b.com/socks4.txt").toURL());
            urls.add(URI.create("https://alexa.lr2b.com/socks5.txt").toURL());
        }
        allProxies = handleScrapingProxies(protocol, amount, urls, allProxies);
        ConsoleUtils.print("Got "+allProxies.size()+" proxies from alex lr2b", ConsoleColor.green);
        return allProxies;
    }

    public static List<PurpleProxy> getGithubSunnyProxies(Proxy.Type protocol, int amount) throws Exception {
        List<PurpleProxy> allProxies = new ArrayList<>();
        List<URL> urls = new ArrayList<>();
        if (protocol.equals(Proxy.Type.HTTP)) {
            urls.add(URI.create("https://sunny9577.github.io/proxy-scraper/generated/http_proxies.txt").toURL());
        } else {
            urls.add(URI.create("https://sunny9577.github.io/proxy-scraper/generated/socks5_proxies.txt").toURL());
            urls.add(URI.create("https://sunny9577.github.io/proxy-scraper/generated/socks4_proxies.txt").toURL());
        }
        allProxies = handleScrapingProxies(protocol, amount, urls, allProxies);
        ConsoleUtils.print("Got "+allProxies.size()+" proxies from github sunny", ConsoleColor.green);
        return allProxies;
    }

    public static List<PurpleProxy> getGithubZaeemProxies(Proxy.Type protocol, int amount) throws Exception {
        List<PurpleProxy> allProxies = new ArrayList<>();
        List<URL> urls = new ArrayList<>();
        if (protocol.equals(Proxy.Type.HTTP)) {
            urls.add(URI.create("https://raw.githubusercontent.com/Zaeem20/FREE_PROXIES_LIST/master/http.txt").toURL());
            urls.add(URI.create("https://raw.githubusercontent.com/Zaeem20/FREE_PROXIES_LIST/master/https.txt").toURL());
        } else {
            urls.add(URI.create("https://raw.githubusercontent.com/Zaeem20/FREE_PROXIES_LIST/master/socks4.txt").toURL());
        }
        allProxies = handleScrapingProxies(protocol, amount, urls, allProxies);
        ConsoleUtils.print("Got "+allProxies.size()+" proxies from github Zaeem20", ConsoleColor.green);
        return allProxies;
    }

    public static List<PurpleProxy> getGithubVakhovProxies(Proxy.Type protocol, int amount) throws Exception {
        List<PurpleProxy> allProxies = new ArrayList<>();
        List<URL> urls = new ArrayList<>();
        if (protocol.equals(Proxy.Type.HTTP)) {
            urls.add(URI.create("https://raw.githubusercontent.com/vakhov/fresh-proxy-list/master/http.txt").toURL());
            urls.add(URI.create("https://raw.githubusercontent.com/vakhov/fresh-proxy-list/master/https.txt").toURL());
        } else {
            urls.add(URI.create("https://raw.githubusercontent.com/vakhov/fresh-proxy-list/master/socks4.txt").toURL());
            urls.add(URI.create("https://raw.githubusercontent.com/vakhov/fresh-proxy-list/master/socks5.txt").toURL());
        }
        allProxies = handleScrapingProxies(protocol, amount, urls, allProxies);
        ConsoleUtils.print("Got "+allProxies.size()+" proxies from github vakhov", ConsoleColor.green);
        return allProxies;
    }

    public static List<PurpleProxy> getGithubKangProxies(Proxy.Type protocol, int amount) throws Exception {
        List<PurpleProxy> allProxies = new ArrayList<>();
        List<URL> urls = new ArrayList<>();
        if (protocol.equals(Proxy.Type.HTTP)) {
            urls.add(URI.create("https://raw.githubusercontent.com/officialputuid/KangProxy/KangProxy/http/http.txt").toURL());
            urls.add(URI.create("https://raw.githubusercontent.com/officialputuid/KangProxy/KangProxy/https/https.txt").toURL());
        } else {
            urls.add(URI.create("https://raw.githubusercontent.com/officialputuid/KangProxy/KangProxy/socks4/socks4.txt").toURL());
            urls.add(URI.create("https://raw.githubusercontent.com/officialputuid/KangProxy/KangProxy/socks5/socks5.txt").toURL());
        }
        allProxies = handleScrapingProxies(protocol, amount, urls, allProxies);
        ConsoleUtils.print("Got "+allProxies.size()+" proxies from github kang", ConsoleColor.green);
        return allProxies;
    }

    public static List<PurpleProxy> getGithubAnonymProxies(Proxy.Type protocol, int amount) throws Exception {
        List<PurpleProxy> allProxies = new ArrayList<>();
        List<URL> urls = new ArrayList<>();
        if (protocol.equals(Proxy.Type.HTTP)) {
            urls.add(URI.create("https://raw.githubusercontent.com/Anonym0usWork1221/Free-Proxies/main/proxy_files/http_proxies.txt").toURL());
            urls.add(URI.create("https://raw.githubusercontent.com/Anonym0usWork1221/Free-Proxies/main/proxy_files/https_proxies.txt").toURL());
        } else {
            urls.add(URI.create("https://raw.githubusercontent.com/Anonym0usWork1221/Free-Proxies/main/proxy_files/socks4_proxies.txt").toURL());
            urls.add(URI.create("https://raw.githubusercontent.com/Anonym0usWork1221/Free-Proxies/main/proxy_files/socks5_proxies.txt").toURL());
        }
        allProxies = handleScrapingProxies(protocol, amount, urls, allProxies);
        ConsoleUtils.print("Got "+allProxies.size()+" proxies from github Anonym0usWork1221", ConsoleColor.green);
        return allProxies;
    }

    public static List<PurpleProxy> getProxiesFromInternet(Proxy.Type protocol, int amount) {
        List<PurpleProxy> allProxies = new ArrayList<>();
        if (amount < 0) {
            amount = 100;
        }
        try {
            allProxies.addAll(getGeoNodeProxies(protocol, amount));
            allProxies.addAll(removeDuplication(allProxies, getProxyScrapeProxies(protocol, amount)));
            allProxies.addAll(removeDuplication(allProxies, getGimmeProxyProxies(protocol, amount)));
            allProxies.addAll(removeDuplication(allProxies, getGithubTheSpeedXProxies(protocol, amount)));
            allProxies.addAll(removeDuplication(allProxies, getOpenProxyProxies(protocol, amount)));
            allProxies.addAll(removeDuplication(allProxies, getVPNFallProxies(protocol, amount)));
            allProxies.addAll(removeDuplication(allProxies, getGithubJetkaiProxies(protocol, amount)));
            allProxies.addAll(removeDuplication(allProxies, getGithubProxifyProxies(protocol, amount)));
            allProxies.addAll(removeDuplication(allProxies, getGithubAlexProxies(protocol, amount)));
            allProxies.addAll(removeDuplication(allProxies, getGithubSunnyProxies(protocol, amount)));
            allProxies.addAll(removeDuplication(allProxies, getGithubZaeemProxies(protocol, amount)));
            allProxies.addAll(removeDuplication(allProxies, getGithubVakhovProxies(protocol, amount)));
            allProxies.addAll(removeDuplication(allProxies, getGithubKangProxies(protocol, amount)));
            allProxies.addAll(removeDuplication(allProxies, getGithubAnonymProxies(protocol, amount)));
            ConsoleUtils.print("Total proxies from the web "+allProxies.size(), ConsoleColor.green);
            return allProxies;
        } catch (Exception e) {
            ConsoleUtils.print("Couldn't get proxies from the web. ERROR "+e.getMessage(), ConsoleColor.red);
        }
        return allProxies;
    }

    private static List<PurpleProxy> handleScrapingProxies(Proxy.Type protocol, int amount, List<URL> urls,
                                                           List<PurpleProxy> allProxies) throws Exception {
        for (URL url : urls) {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                allProxies = handleAddingProxies(protocol, amount, connection, allProxies);
            }
            connection.disconnect();
        }
        return allProxies;
    }

    public static List<PurpleProxy> removeDuplication(List<PurpleProxy> all, List<PurpleProxy> toCheck) {
        List<PurpleProxy> proxies = new ArrayList<>();
        for (PurpleProxy proxy : toCheck) {
            if (all.stream().noneMatch(purpleProxy ->
                    purpleProxy.ip.equals(proxy.ip) && purpleProxy.port == proxy.port)) {
                proxies.add(proxy);
            }
        }
        return proxies;
    }

    private static List<PurpleProxy> handleAddingProxies(Proxy.Type protocol, int amount,
                                                         HttpURLConnection connection,
                                                         List<PurpleProxy> allProxies) throws Exception {
        for (String line : getRequestTextData(connection)) {
            String[] addressIp = line.split(":");
            if (allProxies.size() >= amount) {
                break;
            }
            allProxies.add(new PurpleProxy(addressIp[0], Integer.parseInt(String.valueOf(addressIp[1])),
                    "", "", protocol, -1));
        }
        return allProxies;
    }

    public static List<PurpleProxy> getProxiesFromFiles(Proxy.Type protocol, String httpPath,
                                                        String socksPath, int amount) {
        List<PurpleProxy> allProxies = new ArrayList<>();
        if (protocol.equals(Proxy.Type.DIRECT)) {
            allProxies.addAll(readProxiesFromFile(httpPath, amount));
            allProxies.addAll(removeDuplication(allProxies, readProxiesFromFile(socksPath, amount)));

        } else if (protocol.equals(Proxy.Type.HTTP)) {
            allProxies.addAll(readProxiesFromFile(httpPath, amount));

        } else {
            allProxies.addAll(readProxiesFromFile(socksPath, amount));
        }
        ConsoleUtils.print("Total proxies from the file "+allProxies.size(), ConsoleColor.green);
        return allProxies;
    }

    public static List<PurpleProxy> readProxiesFromFile(String path, int amount) {
        List<PurpleProxy> proxies = new ArrayList<>();
        if (path.length() == 0) {
            return proxies;
        }
        try {
            int currentLine = 0;
            for (String line : Files.readAllLines(Paths.get(path))) {
                if (currentLine >= amount && amount > -1) {
                    break;
                }
                currentLine++;
                // http://IP:PORT
                // http // IP PORT

                // http://username:password@IP:PORT
                //  http //username password@IP PORT
                String[] proxyParts = line.split(":");
                proxyParts[1] = proxyParts[1].replace("//", "");
                int port = Integer.parseInt(proxyParts[proxyParts.length - 1]);
                String ip = proxyParts[proxyParts.length - 2];
                String username = proxyParts[1];
                String password = "";
                if (ip.contains("@")) {
                    String[] ipPart = ip.split("@");
                    password = ipPart[0];
                    ip = ipPart[1];
                }

                String tempIp = new String(ip);
                if (proxies.stream().noneMatch(purpleProxy ->
                            purpleProxy.ip.equals(tempIp) && purpleProxy.port == port)) {
                    proxies.add(new PurpleProxy(ip, port, username, password,
                            proxyParts[0].contains("socks") ? Proxy.Type.SOCKS : Proxy.Type.HTTP, -1));
                }
            }
            return proxies;

        } catch (IOException e) {
            return proxies;
        }
    }

    public static List<PurpleProxy> runCheckers(List<PurpleProxy> proxies, int threads) {
        List<PurpleProxy> validProxies = new ArrayList<>();
        try {
            ExecutorService executor = Executors.newFixedThreadPool(threads);
            AtomicInteger index = new AtomicInteger(-1);

            Runnable task = () -> {
                while (true) {
                    int i = index.getAndIncrement();
                    if (i >= proxies.size()) {
                        break;
                    }
                    PurpleProxy proxy = proxies.get(i);
                    if (proxy.checkProxy()) {
                        synchronized (validProxies) {
                            ConsoleUtils.print("Online - " +proxy.protocol.name().toUpperCase() +
                                    " - "+ proxy.ip + ":"+proxy.port, ConsoleColor.green);
                            validProxies.add(proxy);
                        }
                    }
                }
            };

            for (int i = 0; i < threads; i++) {
                executor.submit(task);
            }

            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } catch (Exception ignore) {}
        ConsoleUtils.print("Checked "+validProxies.size()+" proxies", ConsoleColor.green);
        return validProxies;
    }

    public static void saveProxies(List<PurpleProxy> proxies, String outputPath) {
        try {
            PrintWriter out = new PrintWriter(outputPath);
            for (PurpleProxy proxy : proxies) {
                out.println(proxy.toString());
            }
            out.close();
            ConsoleUtils.print("Saved "+proxies.size() + " proxies to "+outputPath, ConsoleColor.green);

        } catch (Exception e) {
            ConsoleUtils.print("Couldn't save the proxies to "+outputPath, ConsoleColor.red);
        }
    }

    public static void runAutomaticProxyGenerator(int threads, int proxies, Proxy.Type proxyType, String outputPath) {
        saveProxies(runCheckers(getProxiesFromInternet(proxyType, proxies), threads), outputPath);
    }

    public static void runManualProxyGenerator(String httpFilePath, String socksFilePath, int threads, int proxies,
                                               Proxy.Type proxyType, String outputPath) {
        saveProxies(runCheckers(getProxiesFromFiles(proxyType, httpFilePath, socksFilePath, proxies), threads), outputPath);
    }

    private static long currentIP = 1;

    public static void runScanWholeInternet(Proxy.Type proxyType, int threads, int proxies, String outputPath) {
        List<PurpleProxy> ips = new ArrayList<>();
        List<String> addresses = new ArrayList<>();
        String ip = generateNextPublicIPv4();
        while (ip != null) {
            addresses.add(ip);
            for (int port = 80; port <= 49151; port++) {
                ips.add(new PurpleProxy(ip, port, "", "", proxyType, -1));
            }
            if (addresses.size() >= proxies) {
                break;
            }
            ip = generateNextPublicIPv4();
        }
        saveProxies(runCheckers(ips, threads), outputPath);
    }


    private static String generateNextPublicIPv4() {
        String ip;
        do {
            ip = longToIP(currentIP);
            currentIP++;
            if (currentIP > 4294967295L) {
                // We've exhausted all possible IPs
                return null;
            }
        } while (isPrivateOrLocalhost(ip));

        return ip;
    }

    private static String longToIP(long ip) {
        return String.format("%d.%d.%d.%d",
                (ip >> 24) & 0xFF,
                (ip >> 16) & 0xFF,
                (ip >> 8) & 0xFF,
                ip & 0xFF);
    }

    private static boolean isPrivateOrLocalhost(String ip) {
        String[] octets = ip.split("\\.");
        int first = Integer.parseInt(octets[0]);
        int second = Integer.parseInt(octets[1]);

        // Check for private IP ranges
        if (first == 10) return true; // 10.0.0.0 to 10.255.255.255
        if (first == 172 && second >= 16 && second <= 31) return true; // 172.16.0.0 to 172.31.255.255
        if (first == 192 && second == 168) return true; // 192.168.0.0 to 192.168.255.255

        // Check for localhost
        if (first == 127) return true; // 127.0.0.0 to 127.255.255.255

        // Check for 0.0.0.0
        if (first == 0) return true;

        // Check for 255.255.255.255
        return ip.equals("255.255.255.255");
    }

}


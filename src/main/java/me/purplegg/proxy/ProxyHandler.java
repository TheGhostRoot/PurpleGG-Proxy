package me.purplegg.proxy;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProxyHandler {

    public static Map<String, Object> getRequestData(HttpURLConnection connection) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return (Map<String, Object>) Main.mapper.readValue(response.toString(), Map.class);
    }

    public static List<PurpleProxy> getGeoNodeProxies(Proxy.Type protocol, int amount) throws Exception {
        List<PurpleProxy> allProxies = new ArrayList<>();
        HttpURLConnection connection = (HttpURLConnection)
                URI.create("https://proxylist.geonode.com/api/proxy-list?limit="+
                        amount+"&page=1&sort_by=lastChecked&sort_type=desc").toURL().openConnection();
        connection.setRequestMethod("GET");
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            Map<String, Object> jsonMap = getRequestData(connection);
            connection.disconnect();
            Object proxyList = jsonMap.get("data");
            ConsoleUtils.print(proxyList.getClass().getSimpleName(), ConsoleColor.green);
            for (Map<String, Object> webProxy : (List<Map<String, Object>>) proxyList) {
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
            Map<String, Object> jsonMap = getRequestData(connection);
            connection.disconnect();
            Object proxyList = jsonMap.get("proxies");
            ConsoleUtils.print(proxyList.getClass().getSimpleName(), ConsoleColor.green);
            for (Map<String, Object> webProxy : (List<Map<String, Object>>) proxyList) {
                String protos = String.valueOf(webProxy.get("protocol"));
                int port = Integer.parseInt(String.valueOf(webProxy.get("port")));
                if (protos.contains("socks") && protocol.equals(Proxy.Type.SOCKS)) {
                    allProxies.add(new PurpleProxy(String.valueOf(webProxy.get("ip")),
                            port, "", "", protocol, -1));
                }

                if (protos.contains("http") && protocol.equals(Proxy.Type.HTTP)) {
                    allProxies.add(new PurpleProxy(String.valueOf(webProxy.get("ip")),
                            port, "", "", protocol, -1));
                }

            }
        }
        ConsoleUtils.print("Got "+allProxies.size()+" proxies from proxyscrape.com", ConsoleColor.green);
        return allProxies;
    }

    public static List<PurpleProxy> getProxiesFromInternet(Proxy.Type protocol, int amount) {
        List<PurpleProxy> allProxies = new ArrayList<>();
        try {
            allProxies.addAll(getGeoNodeProxies(protocol, amount));
            allProxies.addAll(getProxyScrapeProxies(protocol, amount));
            return allProxies;
        } catch (Exception ignore) {
            ConsoleUtils.print("Couldn't get proxies from the web", ConsoleColor.red);
        }

        return allProxies;
    }

    public static List<PurpleProxy> getProxiesFromFiles(Proxy.Type protocol, String httpPath,
                                                        String socksPath, int amount) {
        List<PurpleProxy> allProxies = new ArrayList<>();
        if (protocol.equals(Proxy.Type.DIRECT)) {
            allProxies.addAll(readProxiesFromFile(httpPath, amount));
            allProxies.addAll(readProxiesFromFile(socksPath, amount));

        } else if (protocol.equals(Proxy.Type.HTTP)) {
            allProxies.addAll(readProxiesFromFile(httpPath, amount));

        } else {
            allProxies.addAll(readProxiesFromFile(socksPath, amount));
        }
        return allProxies;
    }

    public static List<PurpleProxy> readProxiesFromFile(String path, int amount) {
        List<PurpleProxy> proxies = new ArrayList<>();
        try {
            int currentLine = 0;
            for (String line : Files.readAllLines(Paths.get(path))) {
                if (currentLine >= amount) {
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

                proxies.add(new PurpleProxy(ip, port, username, password,
                        proxyParts[0].contains("socks") ? Proxy.Type.SOCKS : Proxy.Type.HTTP, -1));
            }
            return proxies;

        } catch (IOException e) {
            return proxies;
        }
    }

    public static List<PurpleProxy> runCheckers(List<PurpleProxy> proxies, int threads) {
        List<PurpleProxy> validProxies = new ArrayList<>();
        // TODO Run this in multithreading
        for (PurpleProxy proxy : proxies) {
            if (proxy.checkProxy()) {
                validProxies.add(proxy);
            }
        }
        return validProxies;
    }

    public static void saveProxies(List<PurpleProxy> proxies, String outputPath) {
        try {
            PrintWriter out = new PrintWriter("filename.txt");
            for (PurpleProxy proxy : proxies) {
                out.println(proxy.toString());
            }
            out.close();
        } catch (Exception e) {
            ConsoleUtils.print("Couldn't save the proxies to "+outputPath, ConsoleColor.red);
        }
    }

    public static void runAutomaticProxyGenerator(int threads, int proxies, Proxy.Type proxyType, String outputPath) {
        saveProxies(runCheckers(getProxiesFromInternet(proxyType, proxies), threads), outputPath);
    }

}

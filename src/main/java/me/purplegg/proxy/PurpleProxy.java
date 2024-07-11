package me.purplegg.proxy;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URI;
import java.util.Base64;

public class PurpleProxy {
    public int port;
    public String ip;
    public Type protocol;
    public float latency;
    public String username;
    public String password;

    public PurpleProxy(String ip, int port, String username, String password, Type protocol, float latency) {
        this.ip = ip;
        this.port = port;
        this.protocol = protocol;
        this.latency = latency;
        this.username = username;
        this.password = password;
    }

    public boolean checkProxy() {
        Proxy proxy = new Proxy(protocol, new InetSocketAddress(ip, port));
        try {
            long startTime = System.currentTimeMillis();
            HttpURLConnection connection = (HttpURLConnection) URI.create("http://httpbin.org/ip").toURL().openConnection(proxy);
            connection.setRequestMethod("GET");
            if (isAuth()) {
                connection.setRequestProperty("Proxy-Authorization", "Basic " +
                        new String(Base64.getEncoder().encode((username + ":" + password).getBytes())));
            }
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            long endTime = System.currentTimeMillis();

            if (connection.getResponseCode() == 200) {
                this.latency = endTime - startTime;
                return true;
            }
            connection.disconnect();
            return false;

        } catch (IOException e) {
            return false;
        }
    }

    private boolean isAuth() {
        return username.length() > 0 && password.length() > 0;
    }

    @Override
    public String toString() {
        return protocol.name().toLowerCase() + "://" + (isAuth() ? username + ":" + password + "@" : "") + ip +":"+port;
    }
}

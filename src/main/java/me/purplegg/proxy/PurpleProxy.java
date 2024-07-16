package me.purplegg.proxy;

import java.net.*;
import java.net.Proxy.Type;

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
            if (isAuth()) {
                Authenticator.setDefault(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password.toCharArray());
                    }
                });
            }
            HttpURLConnection connection = (HttpURLConnection) URI.create(Main.host).toURL().openConnection(proxy);
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(1500);
            connection.setReadTimeout(1500);
            connection.setInstanceFollowRedirects(true);
            long endTime = System.currentTimeMillis();
            this.latency = endTime - startTime;
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return true;
            }
            connection.disconnect();
            Authenticator.setDefault(null);
            throw new Exception();
        } catch (Exception e) {
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

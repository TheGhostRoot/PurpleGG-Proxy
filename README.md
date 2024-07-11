### Purple GG's Proxy
- This program verifies if a proxy is a valid proxy and how slow it is.
- It uses multithreading to work as fast as it can.
- Has an option to get proxies from the web or from a file.
- Then checks if the proxy is working and how slow it is.
- At the end it saves all the validated proxies in a file.
- You can also get proxies from the web without validating then.

### Proxy File format Example
We support `HTTP`, `HTTPS`, `SOCKS4` and `SOCKS5` with username and password as optional
```
https://ip:port
http://username:password@ip:port
socks4://ip:port
socks5://username:password@ip:port
```



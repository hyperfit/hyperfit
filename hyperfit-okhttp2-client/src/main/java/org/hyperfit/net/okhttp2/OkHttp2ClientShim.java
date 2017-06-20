package org.hyperfit.net.okhttp2;

import com.squareup.okhttp.*;


import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.net.CookieHandler;
import java.net.Proxy;
import java.net.ProxySelector;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A shim that wraps OkHttpClient for easier testing
 */
public class OkHttp2ClientShim {

    private final OkHttpClient wrappedClient;

    public OkHttp2ClientShim(OkHttpClient wrappedClient){
        if (wrappedClient == null) {
            throw new IllegalArgumentException("wrappedClient cannot be null");
        }

        this.wrappedClient = wrappedClient;
    }

    public void setConnectTimeout(long timeout, TimeUnit unit) {
        wrappedClient.setConnectTimeout(timeout, unit);
    }

    public int getConnectTimeout() {
        return wrappedClient.getConnectTimeout();
    }

    public void setReadTimeout(long timeout, TimeUnit unit) {
        wrappedClient.setReadTimeout(timeout, unit);
    }

    public int getReadTimeout() {
        return wrappedClient.getReadTimeout();
    }

    public void setWriteTimeout(long timeout, TimeUnit unit) {
        wrappedClient.setWriteTimeout(timeout, unit);
    }

    public int getWriteTimeout() {
        return wrappedClient.getWriteTimeout();
    }

    public OkHttpClient setProxy(Proxy proxy) {
        return wrappedClient.setProxy(proxy);
    }

    public Proxy getProxy() {
        return wrappedClient.getProxy();
    }

    public OkHttpClient setProxySelector(ProxySelector proxySelector) {
        return wrappedClient.setProxySelector(proxySelector);
    }

    public ProxySelector getProxySelector() {
        return wrappedClient.getProxySelector();
    }

    public OkHttpClient setCookieHandler(CookieHandler cookieHandler) {
        return wrappedClient.setCookieHandler(cookieHandler);
    }

    public CookieHandler getCookieHandler() {
        return wrappedClient.getCookieHandler();
    }


    public OkHttpClient setCache(Cache cache) {
        return wrappedClient.setCache(cache);
    }

    public Cache getCache() {
        return wrappedClient.getCache();
    }

    public OkHttpClient setSocketFactory(SocketFactory socketFactory) {
        return wrappedClient.setSocketFactory(socketFactory);
    }

    public SocketFactory getSocketFactory() {
        return wrappedClient.getSocketFactory();
    }

    public OkHttpClient setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        return wrappedClient.setSslSocketFactory(sslSocketFactory);
    }

    public SSLSocketFactory getSslSocketFactory() {
        return wrappedClient.getSslSocketFactory();
    }

    public OkHttpClient setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        return wrappedClient.setHostnameVerifier(hostnameVerifier);
    }

    public HostnameVerifier getHostnameVerifier() {
        return wrappedClient.getHostnameVerifier();
    }

    public OkHttpClient setAuthenticator(Authenticator authenticator) {
        return wrappedClient.setAuthenticator(authenticator);
    }

    public Authenticator getAuthenticator() {
        return wrappedClient.getAuthenticator();
    }

    public OkHttpClient setConnectionPool(ConnectionPool connectionPool) {
        return wrappedClient.setConnectionPool(connectionPool);
    }

    public ConnectionPool getConnectionPool() {
        return wrappedClient.getConnectionPool();
    }

    public OkHttpClient setFollowSslRedirects(boolean followProtocolRedirects) {
        return wrappedClient.setFollowSslRedirects(followProtocolRedirects);
    }

    public boolean getFollowSslRedirects() {
        return wrappedClient.getFollowSslRedirects();
    }

    public OkHttpClient setDispatcher(Dispatcher dispatcher) {
        return wrappedClient.setDispatcher(dispatcher);
    }

    public Dispatcher getDispatcher() {
        return wrappedClient.getDispatcher();
    }

    public OkHttpClient setProtocols(List<Protocol> protocols) {
        return wrappedClient.setProtocols(protocols);
    }

    public List<Protocol> getProtocols() {
        return wrappedClient.getProtocols();
    }

    public Call newCall(Request request) {
        return wrappedClient.newCall(request);
    }

    public OkHttpClient cancel(Object tag) {
        return wrappedClient.cancel(tag);
    }

}

package org.hyperfit.net.okhttp3;

import java.net.CookieHandler;
import java.net.Proxy;
import java.net.ProxySelector;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.OkHttpClient.Builder;

public class OkHttp3ClientShim {
    private Builder builder;
    private OkHttpClient wrappedClient;

    public OkHttp3ClientShim(OkHttpClient wrappedClient) {
        if (wrappedClient == null) {
            throw new NullPointerException("Error while creating http client. Client cannot be null.");
        } else {
            this.wrappedClient = wrappedClient;
        }
    }

    public void setConnectTimeout(long timeout, TimeUnit unit) {
        this.builder.connectTimeout(timeout, unit);
    }

    public int getConnectTimeout() {
        return this.wrappedClient.connectTimeoutMillis();
    }

    public void setReadTimeout(long timeout, TimeUnit unit) {
        this.builder.readTimeout(timeout, unit);
    }

    public int getReadTimeout() {
        return this.wrappedClient.readTimeoutMillis();
    }

    public void setWriteTimeout(long timeout, TimeUnit unit) {
        this.builder.writeTimeout(timeout, unit);
    }

    public int getWriteTimeout() {
        return this.wrappedClient.writeTimeoutMillis();
    }

    public Builder setProxy(Proxy proxy) {
        return this.builder.proxy(proxy);
    }

    public Proxy getProxy() {
        return this.wrappedClient.proxy();
    }

    public Builder setProxySelector(ProxySelector proxySelector) {
        return this.builder.proxySelector(proxySelector);
    }

    public ProxySelector getProxySelector() {
        return this.wrappedClient.proxySelector();
    }

    public OkHttpClient setCookieHandler(CookieHandler cookieHandler) {
        return null;
    }

    public CookieHandler getCookieHandler() {
        return null;
    }

    public Builder setCache(Cache cache) {
        return this.builder.cache(cache);
    }

    public Cache getCache() {
        return this.wrappedClient.cache();
    }

    public Builder setSocketFactory(SocketFactory socketFactory) {
        return this.builder.socketFactory(socketFactory);
    }

    public SocketFactory getSocketFactory() {
        return this.wrappedClient.socketFactory();
    }

    public Builder setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        return this.builder.sslSocketFactory(sslSocketFactory);
    }

    public SSLSocketFactory getSslSocketFactory() {
        return this.wrappedClient.sslSocketFactory();
    }

    public Builder setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        return this.builder.hostnameVerifier(hostnameVerifier);
    }

    public HostnameVerifier getHostnameVerifier() {
        return this.wrappedClient.hostnameVerifier();
    }

    public Builder setAuthenticator(Authenticator authenticator) {
        return this.builder.authenticator(authenticator);
    }

    public Authenticator getAuthenticator() {
        return this.wrappedClient.authenticator();
    }

    public Builder setConnectionPool(ConnectionPool connectionPool) {
        return this.builder.connectionPool(connectionPool);
    }

    public ConnectionPool getConnectionPool() {
        return this.wrappedClient.connectionPool();
    }

    public Builder setFollowSslRedirects(boolean followProtocolRedirects) {
        return this.builder.followSslRedirects(followProtocolRedirects);
    }

    public boolean getFollowSslRedirects() {
        return this.wrappedClient.followSslRedirects();
    }

    public Builder setDispatcher(Dispatcher dispatcher) {
        return this.builder.dispatcher(dispatcher);
    }

    public Dispatcher getDispatcher() {
        return this.wrappedClient.dispatcher();
    }

    public Builder setProtocols(List<Protocol> protocols) {
        return this.builder.protocols(protocols);
    }

    public List<Protocol> getProtocols() {
        return this.wrappedClient.protocols();
    }

    public Call newCall(Request request) {
        return this.wrappedClient.newCall(request);
    }

    public OkHttpClient cancel(Object tag) {
        return null;
    }
}


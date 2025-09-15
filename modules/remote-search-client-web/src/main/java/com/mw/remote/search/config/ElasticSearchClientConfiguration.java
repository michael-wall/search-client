package com.mw.remote.search.config;

public class ElasticSearchClientConfiguration {
    private String host;
    private int port;
    private int connectTimeout;
    private int socketTimeout;

    public ElasticSearchClientConfiguration(String host, int port, int connectTimeout, int socketTimeout) {
        this.host = host;
        this.port = port;
        this.connectTimeout = connectTimeout;
        this.socketTimeout = socketTimeout;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }
}
package com.mw.remote.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

public class ElasticSearchClientFactory {
    private ElasticSearchClientConfiguration esClientConfiguration;

    public ElasticSearchClientFactory(ElasticSearchClientConfiguration esClientConfiguration) {
        this.esClientConfiguration = esClientConfiguration;
    }

    public RestHighLevelClient getClient() {
        RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(esClientConfiguration.getHost(), esClientConfiguration.getPort()))
                .setRequestConfigCallback(
                        requestConfigBuilder -> requestConfigBuilder
                                .setConnectTimeout(esClientConfiguration.getConnectTimeout())
                                .setSocketTimeout(esClientConfiguration.getConnectTimeout()));
        return new RestHighLevelClient(restClientBuilder);
    }

    public void setEsClientConfiguration(ElasticSearchClientConfiguration esClientConfiguration) {
        this.esClientConfiguration = esClientConfiguration;
    }
}

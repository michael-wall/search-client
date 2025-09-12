package com.mw.remote.search.client;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.mw.remote.search.config.ElasticSearchClientConfiguration;
import com.mw.remote.search.config.ElasticSearchClientFactory;

import java.io.IOException;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import org.elasticsearch.index.query.*;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
//import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
//import org.elasticsearch.search.aggregations.metrics.max.Max;
//import org.elasticsearch.search.aggregations.metrics.min.Min;
//import org.elasticsearch.search.aggregations.metrics.scripted.ScriptedMetric;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.collapse.CollapseBuilder;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

public class ElasticSearchClientUtil{

	private static RestHighLevelClient client = null;
	
    // singleton method
    private static synchronized RestHighLevelClient getClient() {
       if (client == null) {
            //The config parameters for the connection (dissemination.properties)
            String esHost = PropsUtil.get("pic.es.host");
            String esPort = PropsUtil.get("pic.es.port");
            String connectTimeout = PropsUtil.get("pic.es.connect.timeout");
            String socketTimeout = PropsUtil.get("pic.es.socket.timeout");
            
            _log.info("esHost: " + esHost + ", esPort: " + esPort + ", connectTimeout: " + connectTimeout + ", socketTimeout: " + socketTimeout);
            
            ElasticSearchClientConfiguration esClientConfiguration = new ElasticSearchClientConfiguration(esHost, Integer.parseInt(esPort), Integer.parseInt(connectTimeout), Integer.parseInt(socketTimeout));
            ElasticSearchClientFactory esClientFactory = new ElasticSearchClientFactory(esClientConfiguration);
            client = esClientFactory.getClient();
        }
        return client;
    }

    public static void close() throws IOException {
        getClient().close();
    }	
	
    
    public static String getUser(String index, String id) throws IOException {

        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        sourceBuilder.query(QueryBuilders.matchQuery("_id", id));
        sourceBuilder.from(0);
        sourceBuilder.size(1);

        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = getClient().search(searchRequest, RequestOptions.DEFAULT);

        SearchHits searchHits = searchResponse.getHits();
        
        _log.info(searchHits.getTotalHits().value);

        if (searchHits.getTotalHits().value > 0) {
        	return (String)searchHits.getHits()[0].getSourceAsMap().get("emailAddress");
        }
        
        return null;
    }    
    
    
	private static final Log _log = LogFactoryUtil.getLog(ElasticSearchClientUtil.class);    
}
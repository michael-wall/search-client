package com.mw.remote.search.client;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.mw.remote.search.config.ElasticSearchClientConfiguration;
import com.mw.remote.search.config.ElasticSearchClientFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.Cardinality;
import org.elasticsearch.search.aggregations.metrics.Max;
import org.elasticsearch.search.aggregations.metrics.Min;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

public class ElasticSearchClientUtil{

	private static RestHighLevelClient client = null;
	
    // singleton method
    private static synchronized RestHighLevelClient getClient() {
    	
    	_log.info(ScoreMode.Avg);
    	
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
    
    public static void searchUsers(String index, String sortField, boolean sortAscending) throws IOException {

        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        SortOrder sortOrder = SortOrder.ASC;
        
        if (!sortAscending) sortOrder = SortOrder.DESC;
        
        if (sortField.equalsIgnoreCase("timestamp")) {
            sourceBuilder.sort(SortBuilders.scriptSort(
                    new Script("doc['timestamp'].value.getMonthValue()"), ScriptSortBuilder.ScriptSortType.NUMBER)
            .order(sortOrder));
        } else {
        	 sourceBuilder.sort(sortField,sortOrder); 	
        }
        
        for (SortBuilder sort : sourceBuilder.sorts()) {
            _log.info("Sorting: " + sort);
        }
        
        sourceBuilder.query(QueryBuilders.matchQuery("entryClassName", "com.liferay.portal.kernel.model.User"));

        searchRequest.source(sourceBuilder);
        searchRequest.searchType(SearchType.QUERY_THEN_FETCH);
      
        SearchResponse searchResponse = getClient().search(searchRequest, RequestOptions.DEFAULT);

        SearchHits searchHits = searchResponse.getHits();
        
        _log.info("=== User Search Sort by " + sortField + " Sort Order " + sortOrder + " Output Start ===");
        _log.info("SearchHits count: " + searchHits.getTotalHits().value);
        
        for (SearchHit hit : searchHits.getHits()) {
        	 _log.info(hit.getSourceAsMap().get("emailAddress"));
        }
        _log.info("=== User Search Sort by " + sortField + " Sort Order " + sortOrder + " Output End ===");
    }    
    
    public static AggregationResponseTO userAggregations(String index) throws IOException {

        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        
        //Needed for this example as entryClassPK is stored as keyword field...
        Map<String, Object> runtimeField = new HashMap<>();
        runtimeField.put("type", "long");
        runtimeField.put("script", "emit(Long.parseLong(doc['entryClassPK'].value))");

        Map<String, Object> runtimeMappings = new HashMap<>();
        runtimeMappings.put("entryClassPK_long", runtimeField);

        sourceBuilder.runtimeMappings(runtimeMappings);

        sourceBuilder.aggregation(AggregationBuilders.cardinality("cardinality_entryClassPK").field("entryClassPK_long"));
        sourceBuilder.aggregation(AggregationBuilders.avg("avg_entryClassPK").field("entryClassPK_long"));
        sourceBuilder.aggregation(AggregationBuilders.min("min_entryClassPK").field("entryClassPK_long"));
        sourceBuilder.aggregation(AggregationBuilders.max("max_entryClassPK").field("entryClassPK_long"));
        
        sourceBuilder.query(QueryBuilders.matchQuery("entryClassName", "com.liferay.portal.kernel.model.User"));

        sourceBuilder.size(0);
        searchRequest.source(sourceBuilder);
        searchRequest.searchType(SearchType.QUERY_THEN_FETCH);

        SearchResponse searchResponse = getClient().search(searchRequest, RequestOptions.DEFAULT);

        Aggregations aggregations = searchResponse.getAggregations();
        
        Avg avg = aggregations.get("avg_entryClassPK");
        Cardinality cardinality = aggregations.get("cardinality_entryClassPK");
        Min min = aggregations.get("min_entryClassPK");
        Max max = aggregations.get("max_entryClassPK");
        
        AggregationResponseTO  aggregationResponseTO = new  AggregationResponseTO();
        
        aggregationResponseTO.setAvg(avg.getValue());
        aggregationResponseTO.setCardinality(cardinality.getValue());
        aggregationResponseTO.setMin((long)min.getValue());
        aggregationResponseTO.setMax((long)max.getValue());
        
        _log.info("=== User Aggregations Output Start ===");
        _log.info("Avg: " + aggregationResponseTO.getAvg());
        _log.info("Cardinality: " + aggregationResponseTO.getCardinality());
        _log.info("Min: " + aggregationResponseTO.getMin());
        _log.info("Max: " + aggregationResponseTO.getMax());
        _log.info("=== User Aggregations Output End ===");
        
        return aggregationResponseTO;
    }
    
    public static void nestedObjectAggregation(String index, String entryClassName) throws IOException {
    	
    	SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        
		sourceBuilder.aggregation(AggregationBuilders.nested("nested_fields", "nestedFieldArray")
				.subAggregation(AggregationBuilders.terms("by_fieldName").field("nestedFieldArray.fieldName")
						.subAggregation(AggregationBuilders.terms("values").field("nestedFieldArray.value_keyword_lowercase"))));
          
        sourceBuilder.query(QueryBuilders.matchQuery("entryClassName", entryClassName));
		
        sourceBuilder.size(0);        
        searchRequest.source(sourceBuilder);
        searchRequest.searchType(SearchType.QUERY_THEN_FETCH);

        SearchResponse searchResponse = getClient().search(searchRequest, RequestOptions.DEFAULT);

        Map<String, Map<String, Long>> result = new HashMap<>();

        Aggregations aggs = searchResponse.getAggregations();
        
        Nested nested = aggs.get("nested_fields");
        Terms byFieldName = nested.getAggregations().get("by_fieldName");

        for (Terms.Bucket fieldBucket : byFieldName.getBuckets()) {     	
            String key = fieldBucket.getKeyAsString();
            Map<String, Long> valuesMap = new HashMap<>();

            Terms values = fieldBucket.getAggregations().get("values");
            for (Terms.Bucket valueBucket : values.getBuckets()) {
                valuesMap.put(valueBucket.getKeyAsString(), valueBucket.getDocCount());
            }

            result.put(key, valuesMap);
        }
        
        _log.info("=== Object Nested Aggregation Output Start ===");
        _log.info(result);
        _log.info("=== Object Nested Aggregation Output End ===");
    }
    	
	private static final Log _log = LogFactoryUtil.getLog(ElasticSearchClientUtil.class);    
}
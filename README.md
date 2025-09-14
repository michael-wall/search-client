## Introduction ##
- This repository contains a POC showing the use of Elasticsearch RestHighLevelClient in Liferay DXP 2025.Q1 based on the provided ElasticSearchClientConfiguration, ElasticSearchClientFactory and ElasticSearchClientUtil classes.
- I have recreated some simplified examples here using the out of box Liferay indexed Elasticsearch User documents as well as Elasticsearch classes such as ScriptSortBuilder and AggregationBuilders to show the code compiling, deploying and running successfully in Liferay DXP 2025.Q1.
- This is a ‘proof of concept’ that is being provided ‘as is’ without any support coverage or warranty.
- It was built and tested using Liferay DXP QR 2025.Q1.0 with JDK 21 at compile time and runtime.
- It uses Elasticsearch and Elasticsearch Client version 7.17.26 dependencies.
- **Note that this POC is intended only for use in a local Liferay DXP developer environment that is using the embedded Sidecar Elasticsearch.**
- **The POC uses deprecated classes to match the original code and is NOT intended to be taken as 'best practice' or a recommendation to follow the patterns included in the sample code.**

## Local Setup ##
- While the Liferay server is running go to Control Panel > System > Virtual Instances and note the 'Instance ID' for the current Virtual Instance.
- Confirm the embedded Sidecar Elasticsearch HTTP REST port. It is typically 9201 i.e. http://localhost:9201
- Stop the Liferay server.
- Build the 3 custom OSGi modules:
  - remote-search-client
  - remote-search-client-test
  - remote-search-client-web
- Add the following Portal Properties:
  - pic.es.host=localhost
  - pic.es.port=9201 **(using the Elasticsearch HTTP REST port from above)**
  - pic.es.connect.timeout=10000
  - pic.es.socket.timeout=10000
  - pic.es.elasticsearchIndex=liferay-xxxxxxxxxxx **(where xxxxxxxxxxx is the Instance ID from above)**
- Start the Liferay server.
- Deploy the 3 custom OSGi modules and check the Liferay logs to confirm they deployed successfully.
- Login and create a new Widget Page (e.g. called 'Search Client') in a Liferay Site and add the Sample > Remote Search Client Web widget.

## ElasticSearchClientUtil.java searchUsers method ##
- Searches for all User Documents with sorting using Elasticsearch classes such as SortBuilders, SortBuilder, SortOrder, ScriptSortBuilder and Script.
- It also uses standard Elasticsearch classes such as SearchRequest, RequestOptions, SearchSourceBuilder, QueryBuilders, SearchResponse, SearchHits and SearchHit.

## ElasticSearchClientUtil.java userAggregations method ##
- Aggregates across all User Documents using Elasticsearch classes such as AggregationBuilders, Aggregations, Avg, Cardinality, Min and Max.
- It also uses standard Elasticsearch classes such as SearchRequest, RequestOptions, SearchSourceBuilder, QueryBuilders, SearchResponse, SearchHits and SearchHit.

## Triggering the sample Elasticsearch client code with Remote Search Client Web widget ##
- Visit the Search Client page to trigger the code.
- The output of the searchUsers method with different sorting arguments is written to the Liferay logs.
- The output of the userAggregations method is displayed onscreen as well as written to the Liferay logs.
- The onscreen userAggregations output should be something like:
  - Cardinality: **6**
  - Avg: **37084.666666666664**
  - Min: **20123**
  - Max: **45704**

## Triggering  the sample Elasticsearch client code with custom Gogo Shell commands ##
 - The output of the custom Gogo Shell commands is written to the Liferay logs.
- Launch the Gogo Shell.
- To test userAggregations run command:
  - **searchTest:userAggregations**
- To test searchUsers one of the following commands where the first argument is the sort field and the second argument is the sort order (true for ascending, false for descending):
  - **searchTest:searchUsers emailAddress true**
  - **searchTest:searchUsers emailAddress false**
  - **searchTest:searchUsers timestamp true**
  - **searchTest:searchUsers timestamp false**

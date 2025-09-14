package com.mw.remote.search.client.test;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.mw.remote.search.client.ElasticSearchClientUtil;

import java.io.IOException;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

@Component(
		immediate = true,
				property = {"osgi.command.scope=searchTest", "osgi.command.function=searchUser", "osgi.command.function=userAggregations"},
		service = SearchTest.class)
public class SearchTest {

	@Activate
	protected void activate(Map<String, Object> properties) throws Exception {
		_log.info("activated");
	}
	
	public void searchUsers(String sortField, boolean sortAscending) {
		String elasticsearchIndex = PropsUtil.get("pic.es.elasticsearchIndex");
	
		try {
			ElasticSearchClientUtil.searchUsers(elasticsearchIndex, sortField, sortAscending);
			
			System.out.print("Check the Liferay logs for output.");
		} catch (IOException e) {
			_log.error(e);
			
			System.out.print("Error occurred, check the Liferay logs..");
		}
	}
	
	public void userAggregations() {
		String elasticsearchIndex = PropsUtil.get("pic.es.elasticsearchIndex");
		
		try {
			ElasticSearchClientUtil.userAggregations(elasticsearchIndex);
			
			System.out.print("Check the Liferay logs for output.");
		} catch (IOException e) {
			_log.error(e);
			
			System.out.print("Error occurred, check the Liferay logs..");
		}
	}
	
	private static final Log _log = LogFactoryUtil.getLog(SearchTest.class);
}
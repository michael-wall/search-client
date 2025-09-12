package com.mw.test;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.mw.remote.search.client.ElasticSearchClientUtil;

import java.io.IOException;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

@Component(
		immediate = true,
				property = {"osgi.command.function=searchUser", "osgi.command.scope=searchTest"},
		service = SearchTest.class)
public class SearchTest {

	@Activate
	protected void activate(Map<String, Object> properties) throws Exception {
		_log.info("activated");
	}
	
	public void searchUser(String index, String id) {
		
		String response = null;
		try {
			response = ElasticSearchClientUtil.getUser(index, id);
			
			_log.info("response: " + response);
			
		} catch (IOException e) {
			_log.error(e);
		}
	}
	
	private static final Log _log = LogFactoryUtil.getLog(SearchTest.class);
}
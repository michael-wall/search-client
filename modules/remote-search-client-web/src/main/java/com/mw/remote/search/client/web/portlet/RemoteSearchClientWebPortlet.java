package com.mw.remote.search.client.web.portlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.PropsUtil;
import com.mw.remote.search.client.AggregationResponseTO;
import com.mw.remote.search.client.ElasticSearchClientUtil;
import com.mw.remote.search.client.web.constants.RemoteSearchClientWebPortletKeys;

import java.io.IOException;
import java.util.Map;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Michael Wall
 */
@Component(
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=false",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + RemoteSearchClientWebPortletKeys.REMOTE_SEARCH_CLIENT_WEB,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user",
		"javax.portlet.version=3.0"
	},
	service = Portlet.class
)
public class RemoteSearchClientWebPortlet extends MVCPortlet {
	
	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		if (_log.isInfoEnabled()) _log.info("activated");
	}	
	
	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		
		String elasticsearchIndex = PropsUtil.get("pic.es.elasticsearchIndex");
		
		ElasticSearchClientUtil.searchUsers(elasticsearchIndex, "emailAddress", true);
		ElasticSearchClientUtil.searchUsers(elasticsearchIndex, "emailAddress", false);
		ElasticSearchClientUtil.searchUsers(elasticsearchIndex, "timestamp", true);
		ElasticSearchClientUtil.searchUsers(elasticsearchIndex, "timestamp", false);
		AggregationResponseTO aggregationResponseTO = ElasticSearchClientUtil.userAggregations(elasticsearchIndex);
		
		renderRequest.setAttribute("aggregationResponseTO", aggregationResponseTO);

		super.doView(renderRequest, renderResponse);
		
		return;
	}
	
 	private static final Log _log = LogFactoryUtil.getLog(RemoteSearchClientWebPortlet.class);		
	
}
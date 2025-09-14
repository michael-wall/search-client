<%@ include file="./init.jsp" %>

<%
AggregationResponseTO aggregationResponseTO = (AggregationResponseTO)request.getAttribute("aggregationResponseTO");
%>

<br /><br />
<p>
	Cardinality: <strong><%= aggregationResponseTO.getCardinality() %></strong><br />
	Avg: <strong><%= aggregationResponseTO.getAvg() %></strong><br />
	Min: <strong><%= aggregationResponseTO.getMin() %></strong><br />
	Max: <strong><%= aggregationResponseTO.getMax() %></strong><br />
</p>
<%@ include file="./init.jsp" %>

<%
AggregationResponseTO aggregationResponseTO = (AggregationResponseTO)request.getAttribute("aggregationResponseTO");
List<UserResponseTO> userResponseTOs = (List<UserResponseTO>)request.getAttribute("userResponseTOs");
%>

<br />
<h2>Search Users Response</h2>
<br />
<% for (UserResponseTO userResponseTO : userResponseTOs) { %>
	- <%= userResponseTO.toString() %><br />
<% } %>
<br />
<h2>User Aggregations Response</h2>
<br />
<p>
	- Cardinality: <strong><%= aggregationResponseTO.getCardinality() %></strong><br />
	- Avg: <strong><%= aggregationResponseTO.getAvg() %></strong><br />
	- Min: <strong><%= aggregationResponseTO.getMin() %></strong><br />
	- Max: <strong><%= aggregationResponseTO.getMax() %></strong><br />
</p>
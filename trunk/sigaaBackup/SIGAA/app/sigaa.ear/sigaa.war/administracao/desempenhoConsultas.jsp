<%@page import="java.util.Iterator"%>
<%@page import="br.ufrn.arq.managment.QueryPerformance"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="br.ufrn.arq.util.Formatador"%>

<h2><ufrn:subSistema /> > Desempenho das Consultas</h2>

<table border="1" align="center" class="listagem">
<%
	Collection querys = QueryPerformance.getInstance().getQuerys();
	Iterator it = querys.iterator();
	out.println("<caption class=listagem>Desempenho das Consultas</caption>");
	while ( it.hasNext() ) {
		out.println("<tr>");
		String query = (String) it.next();

		if ( query.contains(",") ) {
			StringTokenizer st = new StringTokenizer(query,",");
			out.println("<td>" +  st.nextToken() + "</td>");
			out.println("<td>" +  st.nextToken() + "</td>");
			out.println("<td>" +  st.nextToken() + "</td>");

		} else {
			out.println("<td colspan='3'>" + query + "</td>");
		}
		out.println("<td>" + Formatador.getInstance().formatarMoeda(
						QueryPerformance.getInstance().getMedia(query) ) + "</td>");
		out.println("</tr>");
   	}
%>
</table>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

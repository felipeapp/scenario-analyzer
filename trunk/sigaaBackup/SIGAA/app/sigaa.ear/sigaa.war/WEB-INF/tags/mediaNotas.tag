<%@tag import="br.ufrn.sigaa.avaliacao.dominio.ResultadoAvaliacaoDocente"%>
<%@tag import="br.ufrn.sigaa.avaliacao.dominio.MediaNotas"%>

<%@ attribute name="resultado"    required="true"  type="br.ufrn.sigaa.avaliacao.dominio.ResultadoAvaliacaoDocente" %>
<%@ attribute name="idPergunta"   required="true"  type="java.lang.Integer" %>
<%@ attribute name="somenteMedia" required="false" type="java.lang.Boolean" %>

<%
	MediaNotas mediaNotas = new MediaNotas();
	for (MediaNotas media : resultado.getMediaNotas()) {
		if (media.getPergunta().getId() == idPergunta) {
			mediaNotas = media;
			break;
		}
	}
%>
<% if (somenteMedia != null && somenteMedia.booleanValue()) { %>
	<% if (mediaNotas.getMedia() != null) { %>
	<td align="right"><%=String.format("%.2f", mediaNotas.getMedia()) %></td>
	<% } else { %>
	<td align="right">N/A</td>
	<% } %>
<% } else { %>
	<% if (mediaNotas.getMedia() != null) { %>
	<td align="right"><%=String.format("%.2f", mediaNotas.getMedia()) %></td>
	<td align="right"><%=String.format("%.2f", mediaNotas.getDesvioPadrao()) %></td>
	<% } else { %>
	<td align="right">N/A</td>
	<td align="right">N/A</td>
	<% } %>
<% } %>
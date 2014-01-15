
<%@ tag import="br.ufrn.sigaa.avaliacao.dominio.RespostaPergunta"%>

<%@ attribute name="pergunta" required="true"  type="java.lang.Integer" %>
<%@ attribute name="alternativa" required="true" type="java.lang.Integer" %>
<%@ attribute name="aval" required="true" type="br.ufrn.sigaa.avaliacao.dominio.AvaliacaoInstitucional" %>
<%@ attribute name="readOnly" required="false" type="java.lang.Boolean" %>

<%
	RespostaPergunta resp = aval.getResposta(pergunta, null, alternativa);
	if (readOnly == null) readOnly = false;
%>


<input type="checkbox" <%=readOnly?"disabled":"" %> name="p_${ pergunta }" value="${ alternativa }" <%= (resp != null && resp.getResposta() != null && resp.getResposta().equals(alternativa)) ? "checked=\"checked\"" : "" %>/>
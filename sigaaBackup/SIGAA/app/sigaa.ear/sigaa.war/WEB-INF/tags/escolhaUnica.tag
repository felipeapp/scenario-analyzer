<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ tag import="br.ufrn.sigaa.avaliacao.dominio.RespostaPergunta"%>

<%@ attribute name="pergunta" required="true"  type="java.lang.Integer" %>
<%@ attribute name="alternativa" required="true" type="java.lang.Integer" %>
<%@ attribute name="aval" required="true" type="br.ufrn.sigaa.avaliacao.dominio.AvaliacaoInstitucional" %>
<%@ attribute name="tid" required="false" type="java.lang.Integer" %>
<%@ attribute name="readOnly" required="false" type="java.lang.Boolean" %>

<%
	RespostaPergunta resp = aval.getResposta(pergunta, tid, alternativa);
	if (readOnly == null) readOnly = false;
%>

<c:if test="${ empty tid }">
<input type="radio" <%=readOnly?"disabled":"" %> name="p_${ pergunta }" value="${ alternativa }" <%= (resp != null && resp.getResposta() != null && resp.getResposta().equals(alternativa)) ? "checked=\"checked\"" : "" %>/>
</c:if>
<c:if test="${ not empty tid }">
<input type="radio" <%=readOnly?"disabled":"" %> name="p_${ pergunta }_${ tid }" value="${ alternativa }" <%= (resp != null && resp.getResposta() != null && resp.getResposta().equals(alternativa)) ? "checked=\"checked\"" : "" %>/>
</c:if>

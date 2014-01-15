<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ tag import="br.ufrn.sigaa.avaliacao.dominio.RespostaPergunta"%>

<%@ attribute name="pergunta" required="true" type="java.lang.Integer" %>
<%@ attribute name="tid" required="false" type="java.lang.Integer" %>
<%@ attribute name="aval" required="true" type="br.ufrn.sigaa.avaliacao.dominio.AvaliacaoInstitucional" %>
<%@ attribute name="readOnly" required="false" type="java.lang.Boolean" %>

<%
	RespostaPergunta resp = aval.getResposta(pergunta, tid, null);
	if (readOnly == null) readOnly = false;
%>

<c:if test="${not readOnly}">
	<img src="/sigaa/avaliacao/tick<%= resp == null || resp.getResposta() == null || (resp.getResposta() != 1) ? "_disable" : "" %>.png" onclick="marcarResposta(this, '1')" class="resposta"/><br/>
<%--	<img src="/sigaa/avaliacao/no_answer<%= resp == null || resp.getResposta() == null || (resp.getResposta() != 0) ? "_disable" : "" %>.png" onclick="marcarResposta(this, '0')" class="resposta"/><br/> --%> 
	<img src="/sigaa/avaliacao/cross<%= resp == null || resp.getResposta() == null || (resp.getResposta() != -1) ? "_disable" : "" %>.png"  onclick="marcarResposta(this, '-1')" class="resposta"/> <br/>
</c:if>
<c:if test="${readOnly}">
	<img src="/sigaa/avaliacao/tick<%= resp == null || resp.getResposta() == null || (resp.getResposta() != 1) ? "_disable" : "" %>.png" /><br/>
	<%-- <img src="/sigaa/avaliacao/tick<%= resp == null || resp.getResposta() == null || (resp.getResposta() != 0) ? "_disable" : "" %>.png" /><br/> --%>
	<img src="/sigaa/avaliacao/cross<%= resp == null || resp.getResposta() == null || (resp.getResposta() != -1) ? "_disable" : "" %>.png" /><br/>
</c:if>

<c:if test="${ empty tid }">
<input type="hidden" name="p_${ pergunta }" id="p_${ pergunta }" value="<%= resp != null && resp.getResposta() != null ? resp.getResposta() : "" %>">
</c:if>
<c:if test="${ not empty tid }">
<input type="hidden" name="p_${ pergunta }_${ tid }" id="p_${ pergunta }_${ tid }" value="<%= resp != null && resp.getResposta() != null ? resp.getResposta() : "" %>">
</c:if>
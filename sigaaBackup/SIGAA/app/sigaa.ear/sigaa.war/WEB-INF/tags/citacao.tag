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
<input type="text" name="c_${ p.id }" maxlength="60" size="30" value="<%= (resp != null && resp.getCitacao() != null) ? resp.getCitacao() : "" %>" <%=readOnly?"disabled":"" %>/>
</c:if>
<c:if test="${ not empty tid }">
<input type="text" name="c_${ p.id }_${ tid }" maxlength="60" size="30" value="<%= (resp != null && resp.getCitacao() != null) ? resp.getCitacao() : "" %>" <%=readOnly?"disabled":"" %>/>
</c:if>
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

<c:if test="${ empty tid }">
<select name="p_${ pergunta }" <%=readOnly?"disabled":"" %> >
</c:if>
<c:if test="${ not empty tid }">
<select name="p_${ pergunta }_${ tid }" <%=readOnly?"disabled":"" %> >
</c:if>
	<c:if test="${!acesso.acessibilidade}">
		<option value="" <%= resp == null || resp.getResposta() == null ? "selected=\"selected\"" : "" %>></option>
	</c:if>
	<option value="10" <%= resp != null && resp.getResposta() != null && resp.getResposta() == 10 ? "selected=\"selected\"" : "" %>>10</option>
	<option value="9" <%= resp != null && resp.getResposta() != null && resp.getResposta() == 9 ? "selected=\"selected\"" : "" %>>9</option>
	<option value="8" <%= resp != null && resp.getResposta() != null && resp.getResposta() == 8 ? "selected=\"selected\"" : "" %>>8</option>
	<option value="7" <%= resp != null && resp.getResposta() != null && resp.getResposta() == 7 ? "selected=\"selected\"" : "" %>>7</option>
	<option value="6" <%= resp != null && resp.getResposta() != null && resp.getResposta() == 6 ? "selected=\"selected\"" : "" %>>6</option>
	<option value="5" <%= resp != null && resp.getResposta() != null && resp.getResposta() == 5 ? "selected=\"selected\"" : "" %>>5</option>
	<option value="4" <%= resp != null && resp.getResposta() != null && resp.getResposta() == 4 ? "selected=\"selected\"" : "" %>>4</option>
	<option value="3" <%= resp != null && resp.getResposta() != null && resp.getResposta() == 3 ? "selected=\"selected\"" : "" %>>3</option>
	<option value="2" <%= resp != null && resp.getResposta() != null && resp.getResposta() == 2 ? "selected=\"selected\"" : "" %>>2</option>
	<option value="1" <%= resp != null && resp.getResposta() != null && resp.getResposta() == 1 ? "selected=\"selected\"" : "" %>>1</option>
	<option value="0" <%= resp != null && resp.getResposta() != null && resp.getResposta() == 0 ? "selected=\"selected\"" : "" %>>0</option>
	<option value="-1" <%= resp != null && resp.getResposta() != null && resp.getResposta() == -1 ? "selected=\"selected\"" : "" %>>N/A</option>
</select>
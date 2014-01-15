	<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>

<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="title">Ensino Médio</h2>

<table width="100%" border="0" cellspacing="0" cellpadding="2" class="subSistema">
<tr valign="top">
    <td width="33%">
		<%@include file="/WEB-INF/jsp/ensino/medio/menu/curso.jsp"%>
	</td>
	<td width="33%">
		<%@include file="/WEB-INF/jsp/ensino/medio/menu/discente.jsp"%>
	</td>
	<td width="33%">
		<%@include file="/WEB-INF/jsp/ensino/medio/menu/disciplina.jsp"%>
		<%@include file="/WEB-INF/jsp/ensino/medio/menu/turma.jsp"%>
	</td>
</table>
<div class="linkRodape">
    <html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

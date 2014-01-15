
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h:outputText value="#{ arquivosTurma.create }"/>

<table width="100%">
<c:forEach var="item" items="${ arquivosTurma.conteudoMes }">
<tr>
	<td align="center"><input type="checkbox" name="aula" value="${ item.id }"/></td>
	<td>${ item.data }</td>
	<td>${ item.titulo }<br/>${ titulo.descricao }</td>
</tr>
</c:forEach>
</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
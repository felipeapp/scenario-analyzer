<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<style>
	span.info { color: #666; font-size: 0.8em }
</style>

<table class="subFormulario"  width="100%">
	
	<thead>
		<tr>
			<td>Data de Ocorrência</td>
			<td>Tipo</td>
			<td>Realizado Por</td>
		</tr>
	</thead>

	<c:if test="${empty consultarDiscenteMBean.obj.movimentacoes}">
		<tr><td colspan="4" align="center">
		<font color="red">Não há nenhum registro de movimentação</font>
		</td></tr>
	</c:if>

	<c:if test="${not empty consultarDiscenteMBean.obj.movimentacoes}">
	<c:forEach var="movimentacao_" items="${consultarDiscenteMBean.obj.movimentacoes}" varStatus="status">
	<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
		<td>
			<fmt:formatDate value="${movimentacao_.dataCadastro}" pattern="dd/MM/yyyy"/>
			(${movimentacao_.anoReferencia}.${movimentacao_.periodoReferencia})
		</td>
		<td>${movimentacao_.tipo.descricao}</td>
		<td>${movimentacao_.regCriacao.usuario.pessoa.nome}</td>
	</tr>
	</c:forEach>
	</c:if>
</table>
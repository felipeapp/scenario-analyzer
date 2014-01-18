<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<style>
	span.info { color: #666; font-size: 0.8em }
</style>

<table class="subFormulario"  width="100%">
	
	<thead>
		<tr>
			<td>Data de Ocorr�ncia</td>
			<td>Situa��o Antiga</td>
			<td>Situa��o Nova</td>
			<td>Opera��o</td>
		</tr>
	</thead>

	<c:if test="${empty historicoDocenteRede.alteracoes}">
		<tr><td colspan="4" align="center">
		<font color="red">N�o h� nenhum registro de altera��o na situa��o do docente</font>
		</td></tr>
	</c:if>

	<c:if test="${not empty historicoDocenteRede.alteracoes}">
	<c:forEach var="alteracao" items="${historicoDocenteRede.alteracoes}" varStatus="status">
	<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
		<td>
			<fmt:formatDate value="${alteracao.dataAlteracao}" pattern="dd/MM/yyyy"/>
			<c:if test="${not empty alteracao.usuario}">
				<br /><span class="info">(por ${alteracao.usuario.pessoa.nome})</span>
			</c:if>	
		</td>
		<td>${alteracao.situacaoAntiga.descricao}</td>
		<td>${alteracao.situacaoNova.descricao}</td>
		<td>${alteracao.descricaoAlteracao}</td>
	</tr>
	</c:forEach>
	</c:if>
</table>
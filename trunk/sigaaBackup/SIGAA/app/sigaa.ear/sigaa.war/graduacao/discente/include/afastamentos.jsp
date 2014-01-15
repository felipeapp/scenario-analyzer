<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<style>
	span.info { color: #666; font-size: 0.8em }
</style>

<table class="subFormulario"  width="100%">
	
	<thead>
		<tr>
			<td>Período</td>
			<td>Tipo</td>
			<td>Data Ocorrência</td>
			<td>Data Retorno</td>
		</tr>
	</thead>

	<c:if test="${empty historicoDiscente.afastamentos}">
		<tr><td colspan="4" align="center">
		<font color="red">Não há nenhum afastamento registrado para este aluno</font>
		</td></tr>
	</c:if>

	<c:if test="${not empty historicoDiscente.afastamentos}">
	<c:forEach var="afastamento" items="${historicoDiscente.afastamentos}" varStatus="status">
	<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
		<td>${afastamento.anoPeriodoReferencia}</td>
		<td>${afastamento.tipoMovimentacaoAluno.descricao}</td>
		<td>
			<fmt:formatDate value="${afastamento.dataOcorrencia}" pattern="dd/MM/yyyy"/>
			<c:if test="${not empty afastamento.usuarioCadastro}">
				<br /><span class="info">(por ${afastamento.usuarioCadastro.pessoa.nomeResumido})</span>
			</c:if>
		</td>
		<td>
			<fmt:formatDate value="${afastamento.dataRetorno}" pattern="dd/MM/yyyy"/>
			<c:if test="${not empty afastamento.usuarioRetorno}">
				<br /><span class="info">(por ${afastamento.usuarioRetorno.pessoa.nomeResumido})</span>
			</c:if>
		</td>
	</tr>

	<c:if test="${not empty afastamento.observacao}">
		<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
			<td> </td>
			<td colspan="3"> <span class="info"> ${afastamento.observacao} </span> </td>
		</tr>
	</c:if>
	</c:forEach>
	</c:if>
</table>
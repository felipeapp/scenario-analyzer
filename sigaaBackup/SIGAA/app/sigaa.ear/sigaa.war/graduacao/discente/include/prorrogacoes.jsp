<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<table class="subFormulario"  width="100%">

	<thead>
		<td>Tipo</td>
		<td style="text-align: right;">Número de Períodos</td>
		<td style="text-align: center;">Data</td>
		<td>Usuário que realizou</td>
	</thead>

	<c:if test="${empty historicoDiscente.prorrogacoes}">
		<tr><td colspan="4" align="center">
		<font color="red">Não há nenhuma prorrogação de prazo registrada para este aluno</font>
		</td></tr>
	</c:if>

	<c:if test="${not empty historicoDiscente.prorrogacoes}">
	<c:forEach var="prorrogacao" items="#{historicoDiscente.prorrogacoes}" varStatus="status">
	<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
		<td>${prorrogacao.tipoMovimentacaoAluno.descricao}</td>
		<td style="text-align: right">${prorrogacao.valorMovimentacao}</td>
		<td style="text-align: center;"><fmt:formatDate value="${prorrogacao.dataOcorrencia}" pattern="dd/MM/yyyy"/></td>
		<td>${prorrogacao.usuarioCadastro.pessoa}</td>
	</tr>
	</c:forEach>
	</c:if>
</table>
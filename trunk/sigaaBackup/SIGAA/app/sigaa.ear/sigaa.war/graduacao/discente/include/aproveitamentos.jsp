<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<table class="subFormulario" width="100%" >

	<thead>
		<td>Disciplina</td>
		<td>Ano/Período</td>
		<td>Data Aproveitamento</td>
		<td>Usuário que realizou</td>
	</thead>

	<c:if test="${empty historicoDiscente.aproveitamentos}">
		<tr><td colspan="4" align="center">
		<font color="red">Não há nenhum aproveitamento registrado para este aluno</font>
		</td></tr>
	</c:if>

	<c:if test="${not empty historicoDiscente.aproveitamentos}">
	<c:forEach var="aproveitamentoVar" items="${historicoDiscente.aproveitamentos}" varStatus="status">
	<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
		<td> ${aproveitamentoVar.componenteDescricaoResumida}</td>
		<td> ${aproveitamentoVar.ano}-${aproveitamentoVar.periodo} </td>
		<td> <fmt:formatDate value="${aproveitamentoVar.dataCadastro}" pattern="dd/MM/yyyy"/> </td>
		<td> ${aproveitamentoVar.registroEntrada.usuario.pessoa}</td>
	</tr>
	</c:forEach>
	</c:if>
</table>
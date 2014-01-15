<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<table class="subFormulario" width="100%" >

	<thead>
		<td>Componente</td>
		<td>Ano/Per�odo</td>
		<td>Data</td>
		<td>Usu�rio que realizou</td>
	</thead>

	<c:if test="${empty historicoDiscente.retificacoes}">
		<tr><td colspan="4" align="center">
		<font color="red">N�o h� nenhuma retifica��o de nota registrada para este aluno</font>
		</td></tr>
	</c:if>

	<c:if test="${not empty historicoDiscente.retificacoes}">
	<c:forEach var="retificacao" items="${historicoDiscente.retificacoes}" varStatus="status">
	<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
		<td> ${retificacao.matriculaAlterada.componenteDescricaoResumida}</td>
		<td> ${retificacao.matriculaAlterada.ano} - ${retificacao.matriculaAlterada.periodo} </td>
		<td> <fmt:formatDate value="${retificacao.data}" pattern="dd/MM/yyyy"/> </td>
		<td> ${retificacao.registroEntrada.usuario.pessoa}</td>
	</tr>
	</c:forEach>
	</c:if>
</table>
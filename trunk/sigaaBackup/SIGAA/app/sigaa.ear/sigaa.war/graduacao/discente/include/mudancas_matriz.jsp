<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<!-- Mudan�as de Matrizes Curricular -->
<table class="subFormulario"  width="100%">

	<thead>
		<td>Data</td>
		<td>Matriz Antiga</td>
		<td>Usu�rio que realizou</td>
	</thead>

	<c:if test="${empty historicoDiscente.matrizes}">
		<tr><td colspan="3" align="center">
			<font color="red">N�o h� nenhuma mudan�a de matriz curricular registrada para este aluno.</font>
		</td></tr>
	</c:if>

	<c:if test="${not empty historicoDiscente.matrizes}">
	<c:forEach var="matriz" items="${historicoDiscente.matrizes}" varStatus="status">
	<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
		<td> <fmt:formatDate value="${matriz.data}" pattern="dd/MM/yyyy"/> </td>
		<td>${matriz.matrizAntiga}</td>
		<td>${matriz.entrada.usuario.pessoa}</td>
	</tr>
	</c:forEach>
	</c:if>
</table>
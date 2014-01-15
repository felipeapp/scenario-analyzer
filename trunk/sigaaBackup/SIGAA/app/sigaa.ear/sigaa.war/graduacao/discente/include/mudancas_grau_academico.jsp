<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<!-- Mudanças de graus acadêmicos -->
<table class="subFormulario"  width="100%">

	<thead>
		<td>Data</td>
		<td>Grau Acadêmico Antigo</td>
		<td>Usuário que realizou</td>
	</thead>

	<c:if test="${empty historicoDiscente.grausAcademicos}">
		<tr><td colspan="3" align="center">
			<font color="red">Não há nenhuma mudança de grau acadêmico para este aluno.</font>
		</td></tr>
	</c:if>

	<c:if test="${not empty historicoDiscente.grausAcademicos}">
	<c:forEach var="curriculoLoop" items="${historicoDiscente.grausAcademicos}" varStatus="status">
	<tr>
		<td> <fmt:formatDate value="${curriculoLoop.data}" pattern="dd/MM/yyyy"/> </td>
		<td>${curriculoLoop.curriculoAntigo}</td>
		<td>${curriculoLoop.entrada.usuario.pessoa}</td>
	</tr>
	</c:forEach>
	</c:if>
</table>
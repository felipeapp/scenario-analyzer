<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<!-- Mudanças de Curso -->
<table class="subFormulario"  width="100%">

	<thead>
		<td>Data</td>
		<td>Curso Antigo</td>
		<td>Usuário que realizou</td>
	</thead>

	<c:if test="${empty historicoDiscente.cursos}">
		<tr><td colspan="3" align="center">
			<font color="red">Não há nenhuma mudança de cursos registrada para este aluno.</font>
		</td></tr>
	</c:if>

	<c:if test="${not empty historicoDiscente.cursos}">
	<c:forEach var="curriculoLoop" items="${historicoDiscente.cursos}" varStatus="status">
	<tr>
		<td> <fmt:formatDate value="${curriculoLoop.data}" pattern="dd/MM/yyyy"/> </td>
		<td>${curriculoLoop.matrizAntiga.descricao}</td>
		<td>${curriculoLoop.entrada.usuario.pessoa}</td>
	</tr>
	</c:forEach>
	</c:if>
</table>
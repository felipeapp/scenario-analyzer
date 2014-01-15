<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<!-- Mudan�as de graus acad�micos -->
<table class="subFormulario"  width="100%">

	<thead>
		<td>Data</td>
		<td>Grau Acad�mico Antigo</td>
		<td>Usu�rio que realizou</td>
	</thead>

	<c:if test="${empty historicoDiscente.grausAcademicos}">
		<tr><td colspan="3" align="center">
			<font color="red">N�o h� nenhuma mudan�a de grau acad�mico para este aluno.</font>
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
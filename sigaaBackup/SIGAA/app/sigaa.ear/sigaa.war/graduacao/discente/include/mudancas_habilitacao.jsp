<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<!-- Mudan�as de Habilita��es -->
<table class="subFormulario"  width="100%">

	<thead>
		<td>Data</td>
		<td>Habilita��o Antiga</td>
		<td>Usu�rio que realizou</td>
	</thead>

	<c:if test="${empty historicoDiscente.habilitacoes}">
		<tr><td colspan="3" align="center">
			<font color="red">N�o h� nenhuma mudan�a de curr�culo registrada para este aluno.</font>
		</td></tr>
	</c:if>

	<c:if test="${not empty historicoDiscente.habilitacoes}">
	<c:forEach var="curriculoLoop" items="${historicoDiscente.habilitacoes}" varStatus="status">
	<tr>
		<td> <fmt:formatDate value="${curriculoLoop.data}" pattern="dd/MM/yyyy"/> </td>
		<td>${curriculoLoop.matrizAntiga.descricaoMin}</td>
		<td>${curriculoLoop.entrada.usuario.pessoa}</td>
	</tr>
	</c:forEach>
	</c:if>
</table>
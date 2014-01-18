<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:if test="${empty _largura}">
	<c:set var="_largura" value="100%" />
</c:if>

<table class="visualizacao" style="width:${_largura}%">

	<caption>Dados do Discente</caption>
	<tbody>
		<tr>
			<th> Nome: </th>
			<td colspan="3">${discente_.pessoa.nome}</td>
		</tr>	
		<tr>
			<th> Ano-Período Ingresso: </th>
			<td colspan="3">${discente_.anoIngresso}.${discente_.periodoIngresso} </td>
		</tr>
		<tr>
			<th> Curso: </th>
			<td>${discente_.dadosCurso.curso.nome }</td>
			<th> Programa:</th>
			<td>${discente_.dadosCurso.curso.programa.descricao }</td>
		</tr>
		<tr>
			<th> Instituição: </th>
			<td colspan="3">${discente_.dadosCurso.campus.instituicao.nome } - ${discente_.dadosCurso.campus.nome }</td>
		</tr>
		<tr>
			<th>Status: </th>
			<td colspan="3">${discente_.status.descricao }</td>
		</tr>
	</tbody>
</table>
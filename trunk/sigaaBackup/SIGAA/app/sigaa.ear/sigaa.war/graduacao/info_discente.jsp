<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<table class="visualizacao" style="width: 90%">
	<tr>
		<td width="14%"></td>
		<th width="3%" style="text-align: right;">Matrícula:</th>
		<td style="text-align: left;">${discente.matricula }</td>
	</tr>
	<tr>
		<td></td>
		<th style="text-align: right;"> Discente: </th>
		<td style="text-align: left;"> ${discente.pessoa.nome } </td>
	</tr>
	<c:if test="${not discente.stricto and discente.regular}">
		<tr>
			<td></td>
			<th style="text-align: right;">Curso: </th>
			<td style="text-align: left;">			
			<c:if test="${discente.graduacao}">  ${discente.matrizCurricular.descricao} </c:if>
			<c:if test="${not discente.graduacao}">  ${discente.curso.descricao } </c:if>
			</td>
		</tr>
	</c:if>
	<c:if test="${discente.stricto}">
		<tr>
			<td></td>
			<th style="text-align: right;"> Programa: </th>
			<td style="text-align: left;"> ${discente.gestoraAcademica.nome } </td>
		</tr>
		<c:if test="${discente.regular}">
			<tr>
				<td></td>
				<th style="text-align: right;"> Curso: </th>
				<td style="text-align: left;"> ${discente.curso.nomeCursoStricto } </td>
			</tr>
		</c:if>
	</c:if>
	<tr>
		<td></td>
		<th style="text-align: right;"> Status: </th>
		<td style="text-align: left;"> ${discente.statusString } </td>
	</tr>
	<tr>
		<td></td>
		<th style="text-align: right;">Tipo:</th>
		<td style="text-align: left;"> ${discente.tipoString } </td>
	</tr>
</table>
<br />
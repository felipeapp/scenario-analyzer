<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<table class="visualizacao" style="width: 90%">
	<caption>Dados do Discente</caption>
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
	<tr>
		<td></td>
		<th style="text-align: right;">Curso: </th>
		<td style="text-align: left;">${discente.curso.descricao }</td>
	</tr>
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
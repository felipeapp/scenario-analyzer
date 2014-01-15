<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<table class="visualizacao" >
	<tr>
		<th width="20%"> Matrícula: </th>
		<td colspan="3"> ${discente.matricula } </td>
	</tr>
	<tr>
		<th> Discente: </th>
		<td colspan="3"> ${discente.pessoa.nome } </td>
	</tr>
	<tr>
		<th> Status: </th>
		<td> ${discente.statusString } </td>
	</tr>
</table>
<br />
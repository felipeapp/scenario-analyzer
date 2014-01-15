<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	table.relatorio thead tr th, td {
		text-align: right;
		padding: 2px;
	}

	table.relatorio thead tr th {
		border-bottom: 1px solid #666;
	}

	table.relatorio thead th.label, td.label {
		text-align: left;
	}

	table.relatorio tbody td {
		border-bottom: 1px dashed #DDD;
	}

	table.relatorio tfoot tr td {
		border-top: 1px solid #666;
		font-weight: bold;
	}
</style>

<h2> Relatório com estatísticas de cadastro de projetos de pesquisa </h2>

<table width="60%" class="relatorio" style="margin: 0 auto;">

	<thead>
		<tr>
			<th class="label"> Mês </th>
			<th> Projetos Internos </th>
			<th> Projetos Externos </th>
		</tr>
	</thead>

	<tbody>
	<c:set var="totalProjetosInternos" value="0"/>
	<c:set var="totalProjetosExternos" value="0"/>

	<c:forEach var="linha" items="${ estatisticas }" varStatus="status">
		<tr>
			<td class="label"> ${linha.mes} </td>
			<td> ${linha.internos} </td>
			<td> ${linha.externos} </td>
		</tr>

		<c:set var="totalProjetosInternos" value="${totalProjetosInternos + linha.internos}"/>
		<c:set var="totalProjetosExternos" value="${totalProjetosExternos + linha.externos}"/>
	</c:forEach>
	</tbody>

	<tfoot>
		<tr>
			<td class="label"> TOTAL </td>
			<td> ${totalProjetosInternos} </td>
			<td> ${totalProjetosExternos}</td>
		</tr>
	</tfoot>
</table>

<br />
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
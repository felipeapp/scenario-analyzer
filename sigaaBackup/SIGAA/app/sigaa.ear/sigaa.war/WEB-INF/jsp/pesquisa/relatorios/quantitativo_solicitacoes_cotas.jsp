<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

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

<h2> Relatório Quantitativo de Solicitações de Cotas de Bolsas (${cota.descricao}) </h2>

<table width="100%" class="relatorio">

	<thead>
		<tr>
			<th class="label"> Centro </th>
			<th> Solicitadas (Proj. Novos) </th>
			<th> Solicitadas (Proj. Renovados) </th>
			<th> Solicitadas (Total) </th>
			<th> Balcão </th>
			<th> Voluntários </th>
			<th> Outras </th>
		</tr>
	</thead>

	<tbody>
	<c:set var="totalProjetosNovos" value="0"/>
	<c:set var="totalProjetosRenovados" value="0"/>
	<c:set var="totalCotas" value="0"/>
	<c:set var="totalBalcao" value="0"/>
	<c:set var="totalVoluntarios" value="0"/>
	<c:set var="totalOutras" value="0"/>

	<c:forEach var="linha" items="${ resultado }" varStatus="status">
		<tr>
			<td class="label"> ${linha.sigla} </td>
			<td> ${linha.solicitadas_projetos_novos} </td>
			<td> ${linha.solicitadas_projetos_renovados} </td>
			<td> ${linha.cotas} </td>
			<td> ${linha.balcao} </td>
			<td> ${linha.voluntarios} </td>
			<td> ${linha.outras} </td>
		</tr>

		<c:set var="totalProjetosNovos" value="${totalProjetosNovos + linha.solicitadas_projetos_novos}"/>
		<c:set var="totalProjetosRenovados" value="${totalProjetosRenovados + linha.solicitadas_projetos_renovados}"/>
		<c:set var="totalCotas" value="${totalCotas + linha.cotas}"/>
		<c:set var="totalBalcao" value="${totalBalcao + linha.balcao}"/>
		<c:set var="totalVoluntarios" value="${totalVoluntarios + linha.voluntarios}"/>
		<c:set var="totalOutras" value="${totalOutras + linha.outras}"/>
	</c:forEach>
	</tbody>

	<tfoot>
		<tr>
			<td class="label"> TOTAL </td>
			<td> ${totalProjetosNovos} </td>
			<td> ${totalProjetosRenovados}</td>
			<td> ${totalCotas}</td>
			<td> ${totalBalcao}</td>
			<td> ${totalVoluntarios}</td>
			<td> ${totalOutras}</td>
		</tr>
	</tfoot>
</table>

<br />
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	#relatorio table tr td {
		font-size: 0.9em;
	}

	#relatorio table tr td.espaco{
		height: 20px;
	}
	#relatorio table tr.titulo th {
		background: #DDD;
		text-align: center;
		font-weight: bold;
		padding: 4px;
		border-bottom: 1px solid #777;
	}
	#relatorio table tr td.nome, #relatorio table tr th.nome{
		border-right: 1px solid #888;
	}

	#relatorio table tr.bolsista td{
		padding: 2px 0 2px 15px;
		border-bottom: 1px dashed #444;
	}

	#relatorio table tr.total td{
		padding: 3px;
		border-top: 1px solid #222;
		font-weight: bold;
	}

	#relatorio table td.total {
		text-align: right;
		background: #EEE;
	}

	#relatorio table tfoot tr.total td {
		text-align: center;
		font-variant: small-caps;
		font-size: 1.1em;
	}

</style>

<h2> Relatório de Bolsistas Ativos </h2>

<table width="100%">
	<thead>
	<tr class="titulo">
		<th> Discente </th>
		<th> Orientador </th>
		<th> Modalidade </th>
	</tr>
	</thead>
	<tbody>
		<c:forEach var="membro" items="${lista}" varStatus="status">

			<tr class="bolsista">
				<td> ${ membro.discente.pessoa.nome } </td>
				<td> ${ membro.planoTrabalho.orientador.pessoa.nome}</td>
				<td> ${membro.tipoBolsaString} </td>
			</tr>

		</c:forEach>
	</tbody>

	<tfoot>
		<tr class="total">
			<td colspan="5"> Total de Bolsistas Ativos: ${ fn:length(lista) }</td>
		</tr>
	</tfoot>
</table>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
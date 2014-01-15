<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	table.listagem tr.entidade td{
		padding-left: 40px;
		font-style: italic;
		border-bottom: 1px dashed #BBB;
	}
	#relatorio table.listagem tr.ano td{
		font-weight: bold;
		background: #DDD;
		border-top: 1px solid #CCC;
		font-variant: small-caps;
		font-size: 1em;
	}
	table.listagem tr.tipoProjeto td{
		font-weight: bold;
		background: #EDF1F8;
		padding-left: 20px;
	}
	table.listagem tr.externo td{
		border-bottom: 1px solid #CCC;
	}
</style>

<h2> Relatório Sintético de Financiamentos de Projetos de Pesquisa </h2>

<table class="listagem">
	<caption> Projetos Encontrados: ${ form.anoInicio } a ${ form.anoFim } </caption>
	<tbody>
	<c:set var="totalInternos" value="0" />
	<c:set var="totalExternos" value="0" />
	<c:forEach var="linha" items="${ relatorio }">
		<tr class="ano">
			<td> ${ linha.key }  </td>
			<td style="text-align: right;"> ${ linha.value.total } projetos </td>
		</tr>
		<tr class="tipoProjeto">
			<td> Projetos Internos </td>
			<td style="text-align: right;"> ${ linha.value.totalInternos } </td>
		</tr>
		<tr class="tipoProjeto externo">
			<td> Projetos Externos </td>
			<td style="text-align: right;"> ${ linha.value.totalExternos } </td>
		</tr>
		<c:if test="${ not empty linha.value.financiamentos }">
			<c:forEach var="financiamento" items="${ linha.value.financiamentos }" varStatus="status">
				<tr class="entidade ${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }" >
					<td> ${ financiamento.key } </td>
					<td style="text-align: right;"> ${ financiamento.value } </td>
				</tr>
			</c:forEach>
		</c:if>

		<c:set var="totalInternos" value="${ totalInternos + linha.value.totalInternos }"/>
		<c:set var="totalExternos" value="${ totalExternos + linha.value.totalExternos }"/>
	</c:forEach>
		<tr class="ano">
			<td> Total de Projetos Internos </td>
			<td style="text-align: right;"> <b>${ totalInternos } projetos</b></td>
		</tr>
		<tr class="ano">
			<td> Total de Projetos Externos </td>
			<td style="text-align: right;"> <b>${ totalExternos } projetos</b></td>
		</tr>
		<tr class="ano">
			<td> Total de Projetos </td>
			<td style="text-align: right;"> <b>${ totalInternos + totalExternos } projetos</b></td>
		</tr>
	<tbody>
</table>

<br />
<div class="naoImprimir" style="text-align: center;">
	<a href="javascript: history.go(-1);"> Voltar </a>
</div>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
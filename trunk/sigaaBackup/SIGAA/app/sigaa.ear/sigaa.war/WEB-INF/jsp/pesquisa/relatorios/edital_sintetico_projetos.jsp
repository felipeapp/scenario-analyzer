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

<h2> Relatório Sintético de Submissão por Edital </h2>

<table class="tabelaRelatorio" width="100%">
	<tbody>

	<c:forEach var="linha" items="${ relatorio }" varStatus="status">

		<c:if test="${status.index != 0 && interno != linha['tipo'] }">
			<tr class="destaque">
				<td colspan="2"> Total </td>
				<td align="center"> ${total} </td>
			</tr>
			<c:set var="total" value="0"/>
		</c:if>

		<c:set var="total" value="${ total + linha['total'] }"/>
		<c:set var="totalGeral" value="${ totalGeral + linha['total'] }"/>

		<tr>
			<td> ${ linha['tipo'] }  </td>
			<td> ${ linha['situacao'] } </td>
			<td align="center"> ${ linha['total'] } </td>
		</tr>
		<c:set var="interno" value="${ linha['tipo'] }"/>
	</c:forEach>
		<tr class="destaque">
			<td colspan="2"> Total </td>
			<td align="center"> ${total} </td>
		</tr>

		<tr class="destaque">
			<td colspan="2"> Total Geral </td>
			<td align="center"> ${totalGeral} </td>
		</tr>

	<tbody>
</table>

<br />
<div class="naoImprimir" style="text-align: center;">
	<a href="javascript: history.go(-1);"> Voltar </a>
</div>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
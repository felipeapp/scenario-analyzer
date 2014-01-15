<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@page import="br.ufrn.arq.util.CalendarUtils"%>
<f:view>
	<h:outputText value="#{ relatoriosCenso.create }" />

<h2>Relatório Quantitativo de Projetos</h2>

<div id="parametrosRelatorio">
	<table >
		<tr>
			<th>Mês:</th>
			<td> ${relatoriosProjetoPesquisa.mes}</td>
		</tr>
		<tr>
			<th>Ano:</th>
			<td> ${relatoriosProjetoPesquisa.ano }</td>
		</tr>
		
		</table>
	</div>

	<br /><br />

	<table class="tabelaRelatorioBorda" width="100%">
		<thead>
		<tr>
			<th style="text-align: right;"> Internos   </th>
			<th style="text-align: right;"> Associados </th>
			<th style="text-align: right;"> Isolados   </th>
			<th style="text-align: right;"> Externos   </th>
		</tr>	
		</thead>
		
		<c:forEach items="${ relatoriosProjetoPesquisa.relatorioProjeto}" var="linha" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">		

				<td style="text-align: right;"> ${ linha.internos } </td>
  				<td style="text-align: right;"> ${ linha.associados } </td>
				<td style="text-align: right;"> ${ linha.isolados } </td>
				<td style="text-align: right;"> ${ linha.externos } </td>
			</tr>
		</c:forEach>
	</table>

	<br /><br />
	
	<table width="100%">
		<tr>
			<td style="font-weight: bold" align="center">Total de Planos de Trabalho Cadastrados: ${ relatoriosProjetoPesquisa.totalPlanoTrabalho } </td>
		</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
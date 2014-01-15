<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.header td {background-color: #DCDCDC; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.espaco td {padding: 13px; height: 10px;}
	tr.componentes td {padding: 0px 0px 0px; background-color: #eee; border-bottom: 1px dashed #888;}
	tr.componente td {padding: 0px 0px 0px; border: 1px solid #888;}
</style>
<f:view>
	<table width="100%">
  	  <tr>		
		<td class="listagem">
			<td align="center">
				<h2><b>Relatório de Projetos com Redução de Bolsas</b></h2>
			</td>
	</table>
	<br />
	<div id="parametrosRelatorio">
	<table>
		<c:if test="${ not empty relatorioReducaoBolsa.buscaNomeProjeto }">
			<tr>
				<th>Projeto:</th>
				<td><h:outputText value="#{relatorioReducaoBolsa.buscaNomeProjeto}" /></td>
			</tr>
		</c:if>
		<c:if test="${ not empty relatorioReducaoBolsa.buscaAnoProjeto }">
			<tr>
				<th>Ano do Projeto:</th>
				<td><h:outputText value="#{relatorioReducaoBolsa.buscaAnoProjeto}" /></td>
			</tr>
		</c:if>
	</table>
	</div>
	<br />

	<c:set var="total" value="0"/>
	
	<table class="tabelaRelatorio"  cellspacing="1" width="100%" style="font-size: 11px;" align="center">
		
		<tr class="header">
				<td align="left">Ano</td>
				<td align="left">Projeto</td>
				<td align="left">Situação</td>

		</tr>
		
		<c:forEach items="#{relatorioReducaoBolsa.projetosLocalizados}" var="linha" varStatus="indice">
			
 			<tr class="componente">
				<td style="text-align: right;">${linha.ano}</td>
				<td style="text-align: left;">${linha.titulo}</td>
				<td style="text-align: left;">${linha.situacaoProjeto.descricao}</td>
			</tr>			
		</c:forEach>
	</table>
	
	
	<br />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
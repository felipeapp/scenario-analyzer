<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.header td {background-color: #DCDCDC; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.espaco td {padding: 13px; height: 10px;}
	tr.componentes td {padding: 0px 0px 0px; background-color: #eee; border-bottom: 1px dashed #888;}
	tr.componente td {padding: 0px 0px 0px; border-bottom: 1px dashed #888;}
</style>
<f:view>
	<table width="100%">
  	  <tr>		
		<td class="listagem">
			<td align="center">
				<h2><b>Relatório Quantitativo de Ações Integradas Submetidas</b></h2>
			</td>
	</table>
<br />
<div id="parametrosRelatorio">
	<table >
		<tr>
			<th>Edital:</th>
			<td><h:outputText value="#{relatoriosAcaoAcademica.edital.descricao}"/></td>
		</tr>
		</table>
	</div>
	<br />
	
	<c:set var="total" value="0"/>
	
	<table class="tabelaRelatorio"  cellspacing="1" width="100%" style="font-size: 11px;" align="center">
		
		<c:forEach items="#{relatoriosAcaoAcademica.listaRelatorio}" var="linha" varStatus="indice">
			<tr class="header">
				<td align="right">Isolados</td>
				<td style="text-align: right;">Total</td>
			</tr>
 			<tr class="componente">
				<td align="right">Ensino</td>
				<td style="text-align: right;">${linha.isolados_ensino}</td>
			</tr>
			<tr class="componente">
				<td align="right">Pesquisa</td>
				<td style="text-align: right;">${linha.isolados_pesquisa}</td>
			</tr>	
			<tr class="componente">
				<td align="right">Extensão</td>
				<td style="text-align: right;">${linha.isolados_extensao}</td>
			</tr>	
			<tr class="componentes">
				<td align="right">Total Isolados</td>
				<td style="text-align: right;">${linha.total_isolados}</td>
			</tr>	
			<tr class="espaco">
				<td></td>
			</tr>
			<tr class="header">
				<td>Integrados</td>
				<td style="text-align: right;">Total</td>
			</tr>
			<tr class="componente">
				<td align="right">Três Dimensões</td>
				<td style="text-align: right;">${linha.associados_tres_dimensoes}</td>
			</tr>	
			<tr class="componente">
				<td align="right">Ensino e Pesquisa</td>
				<td style="text-align: right;">${linha.associados_ensino_pesquisa}</td>
			</tr>	
			<tr class="componente">
				<td align="right">Ensino e Extensão</td>
				<td style="text-align: right;">${linha.associados_ensino_extensao}</td>
			</tr>				
			<tr class="componente">
				<td align="right">Pesquisa e Extensão</td>
				<td style="text-align: right;">${linha.associados_pesquisa_extensao}</td>
			</tr>				
			<tr class="componentes">
				<td>Total de Integrados</td>
				<td style="text-align: right;">${linha.total_associados}</td>
			</tr>
			<c:set var="total" value="${linha.total_geral}"/>				
		</c:forEach>
	</table>
	
	<br /><br />
	
	<table cellspacing="1" width="100%" style="font-size: 11px;" align="center">
		<tr style="text-align: center;">
			<td><b></>Total Geral:</b> ${total}</td>
		</tr>				
	</table>
	
	<br />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
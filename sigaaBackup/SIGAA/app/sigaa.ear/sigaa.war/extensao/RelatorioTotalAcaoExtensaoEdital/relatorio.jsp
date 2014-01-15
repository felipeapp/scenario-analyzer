<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {border-bottom: 1px solid #555;  }
	tr.titulo td {border-bottom: 2.5px solid #555}
	tr.header td {padding: 0px ; background-color: #EFEFEF; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #c0c0c0; }
	tr.total td {padding: 5px 2px 2px; border: 1px solid #000; font-weight: bold;}
	tr.espaco td {padding: 12px;}
</style>
<f:view>

	<table width="100%" style="font-size: 11px;">
		<caption><td align="center"><h2>Total de Ações de Extensão que Concorreram a Editais Públicos</h2></td></b>
	</table>
	<br />
	<br />
	<div id="parametrosRelatorio">
		<table >
			<c:if test="${ relatorioTotalAcaoEdital.area.id > 0 }">
			<tr>
				<th>Área Temática: </th>
				<td> <h:outputText value="#{relatorioTotalAcaoEdital.area.descricao}" /></td>
			</tr>
			</c:if>
			<c:if test="${ relatorioTotalAcaoEdital.situacao.id > 0 }">
			<tr>
				<th>Situação: </th>
				<td> <h:outputText value="#{relatorioTotalAcaoEdital.situacao.descricao}" /></td>
			</tr>
			</c:if>
			<c:if test="${ not empty relatorioTotalAcaoEdital.dataInicio }">
			<tr>
				<th>Período da Execução: </th>
				<td> <h:outputText value="#{relatorioTotalAcaoEdital.dataInicio}" />
				a
				<h:outputText value="#{relatorioTotalAcaoEdital.dataFim}" /></td>
			</tr>
			</c:if>
				 
		</table>
	</div>
	
	<br />
	<c:set var="_tipoRelatorio" />
    <c:set var="_ano" />
    <c:set var="_total" value="0"/>
    <c:set var="_totalGeral" value="0"/>
    
	<table cellspacing="1" width="100%" style="font-size: 11px;">
		<tr class="titulo">
			<td colspan="6"></td>
		</tr>
		<c:forEach items="#{relatorioTotalAcaoEdital.lista}" var="linha" varStatus="indice">
		<c:set var="tipoAtual" value="${linha.area_tematica}"/>
		
		<c:if  test="${_tipoRelatorio != tipoAtual}">
			<tr class="componentes">
				<td colspan="6" style="background-color: #c0c0c0; border: 1px solid #555; " ><b>${linha.area_tematica}</b></td>
			</tr>
			<tr class="header" >
				<td align="left"> Projeto</td>
				<td align="left"> Edital</td>
				<td align="left"> Tipo de Atividade</td>
				<td align="left"> Classificação Financiadora</td>
				<td align="left"> Situação</td>
				<td align="center"> Período da Ação</td>
				
			</tr>
		</c:if>
		<c:set var="_tipoRelatorio" value="${tipoAtual}"/>
			<tr class="componentes">
				<td align="left">  ${linha.projeto} </td>
				<td align="left">  ${linha.edital} </td>
				<td align="left">  ${linha.tipo_atividade} </td>
				<td align="left">  ${linha.classificacao_financiadora} </td>
				<td align="left">  ${linha.situacao} </td>
				<td align="center"> <ufrn:format type="data" valor="${linha.inicio}" /> a <ufrn:format type="data" valor="${linha.fim}" />  </td>
				
				<c:set var="_total" value="${_total + 1}" />
			</tr>
			
		<c:set var="proximo" value="${relatorioTotalAcaoEdital.lista[indice.index+1].area_tematica}" ></c:set>
		<c:if test="${tipoAtual != proximo }">
			<tr class="componentes" >
				<td align="right" colspan="5"><b>Total:</b></td>
				<td align="right"><b>${_total}</b></td>
				<c:set var="_totalGeral" value="${_totalGeral + _total}"/>
			</tr>
			<tr class="espaco">
				<td colspan="6">&nbsp;</td>
			</tr>
			<c:set var="_total" value="0"/>
		</c:if>
		</c:forEach>
		<tr class="titulo">
			<td colspan="7" align="right"><b>Total Geral: ${_totalGeral}<b/></td>
		</tr>
	</table>
	<br />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
 <style>
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #DEDFE3;}
</style>
 
<f:view>

	<table width="100%" style="font-size: 12px;">
		<caption><td align="center"><b>RELATÓRIO SINTÉTICO DE ENTRADAS POR ANO </b></td></b>
	</table>
	<div id="parametrosRelatorio">
		<table >
			<tr>
				<th>Período:</th>
				<td>${ relatoriosLato.anoInicial } a ${ relatoriosLato.ano }</td>
			</tr>
		</table>
	</div>

	<br />
	<table width="45%" style="font-size: 10px;" border="1" align="center" class="tabelaRelatorio"; >
		<tr class="header">
			<td style="text-align: center;"><b>Ano</b></th> 	
			<td style="text-align: right;"><b>Total de Entradas</b></td> 		
		</tr>
			<c:set var="totalEntradas" value="0"/>
	<c:forEach items="#{relatoriosLato.relatorioAnoSintetico}" var="linha" varStatus="indice">
		<tr class="ano">
			<td style="text-align: center;"> ${ linha.key }  </td>
			<td style="text-align: right;"> ${ linha.value.entradas }  </td>
		</tr>
			<c:set var="totalEntradas" value="${ totalEntradas + linha.value.entradas }"/>
	</c:forEach>
		<tr class="total">
			<td style="text-align: right;"><b> Total Geral de Entradas: </b></td>
			<td style="text-align: right;"> <b>${ totalEntradas }</b></td>
		</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
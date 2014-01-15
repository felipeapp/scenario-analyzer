<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<h:outputText value="#{ relatorioRenovacaoBolsaMBean.create }" />
	
	<h2> Relatório Quantitativo de Renovação de Bolsas </h2>
	
  <div id="parametrosRelatorio">
	<table >
		<tr>
			<th>Cota de Bolsa Inicial:</th>
			<td> ${relatorioRenovacaoBolsaMBean.cotaInicial.descricao} </td>
		</tr>
		<tr>
			<th>Cota de Bolsa Atual:</th>
			<td>${relatorioRenovacaoBolsaMBean.cotaFinal.descricao} </td>
		</tr>
	</table>
  </div>
	
	<br />
	<table class="tabelaRelatorioBorda" width="50%" align="center">
		<thead>
		<tr>
			<th>Tipo de Bolsas</th>
			<th>Categoria</th>
			<th style="text-align: right;">Total</th>
		</tr>	
		</thead>
		<c:set var="total" value="0" />
		<c:forEach var="linha" items="${ relatorioRenovacaoBolsaMBean.listaBolsaPibic }">
			<tr>		
				<td> ${ linha.descricao} </td>
				<td> ${ linha.categoria } </td>
				<td style="text-align: right;"> ${ linha.count } </td>
				<c:set var="total" value="${total + linha.count}"/>
			</tr>
		</c:forEach>
			<tr>
				<td style="text-align: right;" colspan="2"><b>Total:</b></td>
				<td style="text-align: right;"><b>${total}</b></td>
			</tr>
		
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
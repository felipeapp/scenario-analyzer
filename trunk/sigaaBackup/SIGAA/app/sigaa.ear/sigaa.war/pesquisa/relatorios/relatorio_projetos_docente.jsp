<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<h:outputText value="#{ relatoriosCenso.create }" />
	<c:set var="relatorio_" value="${ relatoriosCenso.relatorio }"/>

	<h2>Relatório de Tempo de Dedicação à Pesquisa por Grau de Formação - Quadro Q78.D do Censo - Ano: ${relatoriosCenso.ano}</h2>
	<table class="listagem">
		<thead>
		<tr>
			<th rowspan="2">Grau de Formação</th>
			<th style="text-align: center;" colspan="4">Tempo de dedicação à pesquisa em percentual</th>
		</tr>
		<tr>
			<th style="text-align: center;"> Com 29% ou menos </th>
			<th style="text-align: center;"> Com 30% a 70% </th>
			<th style="text-align: center;"> Com 71% ou mais </th>
			<th style="text-align: center;"> Total </th>
		</tr>	
		</thead>
		
		<c:set var="total01" value="0"/>
		<c:set var="total02" value="0"/>
		<c:set var="total03" value="0"/>
		
		<c:forEach var="linha" items="${ relatorio_ }" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">		
				<td> ${ linha.value.formacao } </td>
				<td style="text-align: center;"> ${ linha.value.com29menos } </td>
				<td style="text-align: center;"> ${ linha.value.com30a70 } </td>
				<td style="text-align: center;"> ${ linha.value.com71mais } </td>
				<td style="text-align: center;"> ${ linha.value.com29menos + linha.value.com30a70 + linha.value.com71mais } </td>
				
				<c:set var="total01" value="${ total01 + linha.value.com29menos }"/>
				<c:set var="total02" value="${ total02 + linha.value.com30a70 }"/>
				<c:set var="total03" value="${ total03 + linha.value.com71mais }"/>
				
			</tr>
		</c:forEach>
		<tr>
			<td>Total</td>
			<td style="text-align: center;">${ total01 }</td>
			<td style="text-align: center;">${ total02 }</td>
			<td style="text-align: center;">${ total03 }</td>
			<td style="text-align: center;">${ total01 + total02 + total03 }</td>
		</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

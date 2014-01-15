<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<h:outputText value="#{ programa.create }" />
	<c:set var="relatorio_" value="${ relatoriosCenso.relatorio }"/>

	<h2>Relatório de Programas</h2>
	<table class="listagem">
		<thead>
		<tr>
			<th style="text-align: center;"> Nome </th>
		</tr>	
		</thead>
		
		<c:forEach var="linha" items="${programa.}" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">		
				<td> ${ linha.value.formacao } </td>
				<td style="text-align: center;"> ${ linha.value.com29menos } </td>
			</tr>
		</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

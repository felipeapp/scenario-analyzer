<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>

	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>

	<table class="tabelaRelatorioBorda" style="margin: auto; width: 60%;" >
		
		<tr class="header" style="background-color: #DEDFE3;">
			<th align="left">Módulo de Acesso</th>
			<th style="text-align:right;">Renovações</th>
		</tr>
		
		<c:set var="total_renovacao" value="0" scope="request" />
		
		<c:forEach var="v" items="#{_abstractRelatorioBiblioteca.resultados}" >
			<tr>
				<td>${v.key}</td>
				<td style="text-align:right;">${v.value}</td>
				<c:set var="total_renovacao" value="${total_renovacao +v.value}" scope="request" />
			</tr>
		</c:forEach>
		
		<tr class="header">
			<th align="left">Total</th>
			<th style="text-align:right;">${total_renovacao}</th>
		</tr>
		
	</table>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<f:view>

	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	<table class="tabelaRelatorioBorda" width="100%">
		<tr style="background-color: #DEDFE3">
			<th rowspan="2">Categoria</th>
			<th colspan="13" style="text-align: center">Quantidade</th> <%-- 12 meses + total = 13 --%>
		</tr>

		<tr style="background-color: #DEDFE3">
			<th style="text-align: right">Jan</th>
			<th style="text-align: right">Fev</th>
			<th style="text-align: right">Mar</th>
			<th style="text-align: right">Abr</th>
			<th style="text-align: right">Mai</th>
			<th style="text-align: right">Jun</th>
			<th style="text-align: right">Jul</th>
			<th style="text-align: right">Ago</th>
			<th style="text-align: right">Set</th>
			<th style="text-align: right">Out</th>
			<th style="text-align: right">Nov</th>
			<th style="text-align: right">Dez</th>
			<th style="text-align: right">Total</th>
		</tr>

		<c:forEach var="resultado" items="#{_abstractRelatorioBiblioteca.resultados}">
			<tr>
				<th style="white-space: nowrap;"><h:outputText value="#{resultado.key.descricao}" /></th>
				<c:forEach var="total" items="#{resultado.value}">
					<td style="text-align: right"><h:outputText value="#{total}" /></td>
				</c:forEach>
			</tr>
		</c:forEach>

		<tr style="font-weight: bold;">
			<th>Total</th>
			<c:forEach var="total" items="#{ _abstractRelatorioBiblioteca.totaisPorMes }" >
				<td style="text-align: right"><h:outputText value="#{total}" /></td>
			</c:forEach>
		</tr>
	</table>
	
	
<div style="margin-top:20px;">
		<hr  />
		<h4>Observação:</h4>
		<p>A quantidade mostrada se refere a quantidade de empréstimos + renovações </p>
</div>	
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
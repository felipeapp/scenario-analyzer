<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<f:view>

	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	<table class="tabelaRelatorioBorda" width="100%">
		<tr style="background-color: #EEE;">
			<th style="text-align: right;">#</th>
			<th style="text-align: right; width: 10%">Matrícula ou SIAPE</th>
			<th style="width: 40%">Nome</th>
			<th style="width: 40%">Curso ou Unidade</th>
			<th style="width:10%; text-align:  right;">Quantidade de Empréstimos</th>
		</tr>
		<c:forEach var="resultado" items="#{_abstractRelatorioBiblioteca.resultados}" varStatus="status" >
			<tr>
				<td style="text-align: right;">${status.index + 1}</td>
				<td style="text-align: right;"><h:outputText value="#{resultado[2]}" /></td>
				<td><h:outputText value="#{resultado[0]}" /></td>
				<td><h:outputText value="#{resultado[3]}" /></td>
				<td style="text-align: right;"><h:outputText value="#{resultado[1]}" /></td>
			</tr>
		</c:forEach>
	</table>

<div style="margin-top:20px;">
		<hr  />
		<h4>Observação:</h4>
		<p>A quantidade mostrada se refere a quantidade de empréstimos + renovações e não estão incluídos os empréstimos institucionais.</p>
</div>	

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
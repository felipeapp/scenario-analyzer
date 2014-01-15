<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>

	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	<table class="tabelaRelatorioBorda" style="width: 300px;" align="center">
		<thead>
			<tr style="background-color: #DEDFE3;">
				<th>Turno</th>
				<th style="text-align: right;">Usuários</th>
			</tr>
		</thead>
		
		<tbody>
			<tr>
				<td>Matutino</td>
				<td style="text-align: right;"><h:outputText value="#{_abstractRelatorioBiblioteca.resultados[0]}"/></td>
			</tr>
			<tr>
				<td>Vespertino</td>
				<td style="text-align: right;"><h:outputText value="#{_abstractRelatorioBiblioteca.resultados[1]}"/></td>
			</tr>
			<tr>
				<td>Noturno</td>
				<td style="text-align: right;"><h:outputText value="#{_abstractRelatorioBiblioteca.resultados[2]}"/></td>
			</tr>
			<tr style="background-color: #DEDFE3;">
				<th>Total</th>
				<th style="text-align: right;"><h:outputText value="#{_abstractRelatorioBiblioteca.total}"/></th>
			</tr>
		</tbody>
				
	</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
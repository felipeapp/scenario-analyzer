<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	
	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	<c:set var="totalGeralRelatorioEmprestimos" scope="request" value="0" /> 
	<c:set var="totalGeralRelatorioRenovacoes" scope="request" value="0" /> 
	
	<table class="tabelaRelatorioBorda" width="100%">
		<thead>
			<tr>
				<th style="text-align: right; width: 5%;">#</th>
				<th style="width: 65%;">Curso</th>
				<th style="text-align: right; width: 10%;">Empr�stimos</th>
				<th style="text-align: right; width: 10%;">Renova��es</th>
				<th style="text-align: right; width: 10%;">Empr�stimos + Renova��es</th>
			</tr>
		</thead>
		
		<c:forEach var="resultado" items="#{_abstractRelatorioBiblioteca.resultados}" varStatus="status" >
			<tr>
			
				<td style="text-align: right;">${status.index + 1}</td>
				<td><h:outputText value="#{resultado[0]}" /></td>
				<td style="text-align: right;"><h:outputText value="#{resultado[1]}" /></td>
				<td style="text-align: right;"><h:outputText value="#{resultado[3]}" /></td>
				<td style="text-align: right;"><h:outputText value="#{resultado[1] + resultado[3]}" /></td>
				
				<c:set scope="request" var="totalGeralRelatorioEmprestimos" value="${totalGeralRelatorioEmprestimos  + resultado[1] }"/>
				<c:set scope="request" var="totalGeralRelatorioRenovacoes" value="${totalGeralRelatorioRenovacoes  + resultado[3] }"/>
			</tr>
		</c:forEach>
		
		<tfoot>
			<tr>
				<td></td>
				<td style="font-weight: bold; text-align: center;">Total</td>
				<td style="text-align: right;"> ${totalGeralRelatorioEmprestimos}</td>
				<td style="text-align: right;"> ${totalGeralRelatorioRenovacoes}</td>
				<td style="text-align: right;">${totalGeralRelatorioEmprestimos + totalGeralRelatorioRenovacoes}</td>
			</tr>
		</tfoot>
	</table>	


	<div style="margin-top:20px;">
		<hr />
		<h4>Observa��o:</h4>
		<p> As renova��es mostradas neste relat�rio correspondem as renova��es realizadas no per�odo selecionado, n�o necessariamente 
		correspondem as renova��es dos empr�stimos tamb�m mostrados nesse relat�rio.</p>
	</div>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<f:view>


	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	<table class="tabelaRelatorioBorda" style="width: 100%; margin-bottom: 20px;">
		
		<%--    I M P R I M E      O       C A B E Ç A L H O   --%>
		<tr style="font-weight: bold; text-align: center; background-color: #DEDFE3;">
			<td style="width: 8%" rowspan="2"> ${_abstractRelatorioBiblioteca.descricaoAgrupamento1}</td>                       <%-- Coluna agrupamento --%>
			<td style="height: 30px;" colspan="${fn:length(_abstractRelatorioBiblioteca.colunas)}"> Classificação </td>   <%-- agrupamento principal desse relatório --%>
			<td style="text-align: right; width: 8%" rowspan="2"> Total </td>                                                                      <%-- Coluna totalização --%>
		</tr>	

		<tr>
			<c:forEach var="coluna" items="#{_abstractRelatorioBiblioteca.colunas}" varStatus="status">
					<td style="text-align: center; font-weight: bold; width: 8%; background-color: #DEDFE3;">
						${coluna}
					</td>
			</c:forEach>
		</tr>
		


		<%--    I M P R I M E      O      C O R P O   --%>


		<c:forEach var="resultado" items="#{_abstractRelatorioBiblioteca.matrixResultado}" varStatus="linha">
				<tr>
				
					<td style="font-weight: bold;"> ${_abstractRelatorioBiblioteca.linhas[linha.index]} </td>     <%-- Coluna agrupamento --%>
					
					<c:forEach var="resultado" items="#{_abstractRelatorioBiblioteca.matrixResultado[linha.index]}" varStatus="coluna">
						<td style="text-align: right; ${ linha.index == fn:length(_abstractRelatorioBiblioteca.linhas)-1 || coluna.index == fn:length(_abstractRelatorioBiblioteca.colunas)  ? 'font-weight: bold;' : '' } "> 
							${_abstractRelatorioBiblioteca.matrixResultado[linha.index][coluna.index]} 
						</td>
					</c:forEach>
				</tr>
		</c:forEach>
		
		
	</table>
	
	
	<div style="margin-top:20px;">
		<hr />
		<h4>Observações:</h4>
		<p> 1 - As quantidades mostradas nesse relatório incluem os empréstimos e renovações. </p>
		<p> 2 - As quantidades de empréstimos nesse relatório <strong>não</strong> incluem empréstimos de Títulos que já foram removidos do acervo. Com isso, a quantidade 
		mostrada será menor que a quantidade de empréstimos realizados.</p>
		<p> 3 - As renovações mostradas neste relatório correspondem as renovações realizadas no período selecionado, não necessariamente 
		correspondem as renovações dos empréstimos também mostrados nesse relatório.</p>
	</div>
	
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
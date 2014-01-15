<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<f:view>


	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	<table class="tabelaRelatorioBorda" style="width: 100%; margin-bottom: 20px;">
		
		<%--    I M P R I M E      O       C A B E � A L H O   --%>
		<tr style="font-weight: bold; text-align: center; background-color: #DEDFE3;">
			<td style="width: 8%" rowspan="2"> ${_abstractRelatorioBiblioteca.descricaoAgrupamento1}</td>                       <%-- Coluna agrupamento --%>
			<td style="height: 30px;" colspan="${fn:length(_abstractRelatorioBiblioteca.colunas)}"> Classifica��o </td>   <%-- agrupamento principal desse relat�rio --%>
			<td style="text-align: right; width: 8%" rowspan="2"> Total </td>                                                                      <%-- Coluna totaliza��o --%>
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
		<h4>Observa��es:</h4>
		<p> 1 - As quantidades mostradas nesse relat�rio incluem os empr�stimos e renova��es. </p>
		<p> 2 - As quantidades de empr�stimos nesse relat�rio <strong>n�o</strong> incluem empr�stimos de T�tulos que j� foram removidos do acervo. Com isso, a quantidade 
		mostrada ser� menor que a quantidade de empr�stimos realizados.</p>
		<p> 3 - As renova��es mostradas neste relat�rio correspondem as renova��es realizadas no per�odo selecionado, n�o necessariamente 
		correspondem as renova��es dos empr�stimos tamb�m mostrados nesse relat�rio.</p>
	</div>
	
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
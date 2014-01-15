<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<%@taglib uri="/tags/a4j" prefix="a4j"%>

<f:view>
	
	<a4j:keepAlive beanName="_abstractRelatorioBiblioteca" />
	
	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>


	<table class="tabelaRelatorioBorda" style="width: 100%; margin-bottom: 20px;">
	
		<thead>
			<tr>
				<th style="width: 10%;">N� do Sistema</th>
				<th style="width: 50%;">T�tulo</th>
				<th style="width: 10%;">Classifica��o</th>
				<th style="width: 10%;">Quantidade de Empr�stimos</th>
				<th style="width: 10%;">Quantidade de Renova��es</th>
				<th style="width: 10%;">Empr�stimos + Renova��es</th>
			</tr>
		</thead>
	
	
		<c:set var="totalEmprestimos" value="0" scope="request" />
		<c:set var="totalRenovacoes" value="0" scope="request" />
		<c:set var="totalGeral" value="0" scope="request" />
	
		<tbody>
			<c:forEach var="resultado" items="#{_abstractRelatorioBiblioteca.resultados}" varStatus="linha">
					<tr>
						<td> ${resultado[0]} </td>     <%-- N�mero de registro --%>
						<td> ${resultado[1]} </td>     <%-- Formato refer�ncia do T�tulo --%>
						<td> ${resultado[2]} </td>     <%-- Classifica��o Black ou CDU --%>
						<td style="text-align: right;"> ${resultado[3]} </td>     <%-- Qtd Empr�stimos --%>
						<td style="text-align: right;"> ${resultado[4]} </td>     <%-- Qtd Renova��es --%>
						<td style="font-weight: bold; text-align: right;"> ${resultado[5]} </td>                    <%-- Qtd Empr�stimos + Renova��es--%>
						
						<c:set var="totalEmprestimos" value="${totalEmprestimos + resultado[3]}" scope="request" />
						<c:set var="totalRenovacoes" value="${totalRenovacoes + resultado[4]}" scope="request" />
						<c:set var="totalGeral" value="${totalGeral + resultado[5]}" scope="request" />
						
					</tr>
			</c:forEach>
		</tbody>
		
		<tfoot>
			<tr style="font-weight: bold;">
				<td> </td>
				<td colspan="2"> Total da P�gina</td>
				<td style="text-align: right;"> ${totalEmprestimos}</td>
				<td style="text-align: right;"> ${totalRenovacoes}</td>
				<td style="text-align: right;"> ${totalGeral}</td>
			</tr>
		</tfoot>
			
	</table>
	

	<div style="margin-top:20px;">
		<hr />
		<h4>Observa��es:</h4>
		<p> 1 - As quantidades de empr�stimos nesse relat�rio <strong>n�o</strong> incluem empr�stimos de T�tulos que j� foram removidos do acervo. Com isso, a quantidade 
		mostrada ser� menor que a quantidade de empr�stimos realizados.</p>
		<p> 2 - As renova��es mostradas neste relat�rio correspondem as renova��es realizadas no per�odo selecionado, n�o necessariamente 
		correspondem as renova��es dos empr�stimos tamb�m mostrados nesse relat�rio.</p>
	</div>

	<%@include file="/biblioteca/controle_estatistico/rodape_impressao_relatorio_paginacao.jsp"%>

</f:view>

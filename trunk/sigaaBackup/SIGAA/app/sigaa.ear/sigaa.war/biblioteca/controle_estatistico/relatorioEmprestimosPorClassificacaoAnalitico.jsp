<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<%@taglib uri="/tags/a4j" prefix="a4j"%>

<f:view>
	
	<a4j:keepAlive beanName="_abstractRelatorioBiblioteca" />
	
	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>


	<table class="tabelaRelatorioBorda" style="width: 100%; margin-bottom: 20px;">
	
		<thead>
			<tr>
				<th style="width: 10%;">Nº do Sistema</th>
				<th style="width: 50%;">Título</th>
				<th style="width: 10%;">Classificação</th>
				<th style="width: 10%;">Quantidade de Empréstimos</th>
				<th style="width: 10%;">Quantidade de Renovações</th>
				<th style="width: 10%;">Empréstimos + Renovações</th>
			</tr>
		</thead>
	
	
		<c:set var="totalEmprestimos" value="0" scope="request" />
		<c:set var="totalRenovacoes" value="0" scope="request" />
		<c:set var="totalGeral" value="0" scope="request" />
	
		<tbody>
			<c:forEach var="resultado" items="#{_abstractRelatorioBiblioteca.resultados}" varStatus="linha">
					<tr>
						<td> ${resultado[0]} </td>     <%-- Número de registro --%>
						<td> ${resultado[1]} </td>     <%-- Formato referência do Título --%>
						<td> ${resultado[2]} </td>     <%-- Classificação Black ou CDU --%>
						<td style="text-align: right;"> ${resultado[3]} </td>     <%-- Qtd Empréstimos --%>
						<td style="text-align: right;"> ${resultado[4]} </td>     <%-- Qtd Renovações --%>
						<td style="font-weight: bold; text-align: right;"> ${resultado[5]} </td>                    <%-- Qtd Empréstimos + Renovações--%>
						
						<c:set var="totalEmprestimos" value="${totalEmprestimos + resultado[3]}" scope="request" />
						<c:set var="totalRenovacoes" value="${totalRenovacoes + resultado[4]}" scope="request" />
						<c:set var="totalGeral" value="${totalGeral + resultado[5]}" scope="request" />
						
					</tr>
			</c:forEach>
		</tbody>
		
		<tfoot>
			<tr style="font-weight: bold;">
				<td> </td>
				<td colspan="2"> Total da Página</td>
				<td style="text-align: right;"> ${totalEmprestimos}</td>
				<td style="text-align: right;"> ${totalRenovacoes}</td>
				<td style="text-align: right;"> ${totalGeral}</td>
			</tr>
		</tfoot>
			
	</table>
	

	<div style="margin-top:20px;">
		<hr />
		<h4>Observações:</h4>
		<p> 1 - As quantidades de empréstimos nesse relatório <strong>não</strong> incluem empréstimos de Títulos que já foram removidos do acervo. Com isso, a quantidade 
		mostrada será menor que a quantidade de empréstimos realizados.</p>
		<p> 2 - As renovações mostradas neste relatório correspondem as renovações realizadas no período selecionado, não necessariamente 
		correspondem as renovações dos empréstimos também mostrados nesse relatório.</p>
	</div>

	<%@include file="/biblioteca/controle_estatistico/rodape_impressao_relatorio_paginacao.jsp"%>

</f:view>

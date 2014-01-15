<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<style>
	.tabelaRelatorioBorda tr{
		text-align:center;
	}
	
	td.dadosNumeros{
		text-align: right;
		width: 50px;
	}
	
	tr.totalizacao{
		font-weight:bold;
	}
</style>

<f:view>

	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	
	<div><strong>T</strong>: Títulos <strong>F</strong>: Fascículos</div>

	<table class="tabelaRelatorioBorda" width="100%">
		<thead>
			<tr>
				<th rowspan="3" style="text-align:center;">Classe Principal</th>
				<th colspan="4" style="text-align:center;">Periódicos Correntes</th>
				<th colspan="4" style="text-align:center;">Periódicos Não-Correntes</th>
				<th colspan="2" rowspan="2" style="text-align:center;">Total</th>
			</tr>
			
			<tr>
				<th colspan="2" style="text-align:center;">Nacionais</th>
				<th colspan="2" style="text-align:center;">Internacionais</th>
				<th colspan="2" style="text-align:center;">Nacionais</th>
				<th colspan="2" style="text-align:center;">Internacionais</th>
			</tr>
			
			<tr>
				<th style="text-align:right;">T</th>
				<th style="text-align:right;">F</th>
				<th style="text-align:right;">T</th>
				<th style="text-align:right;">F</th>
				<th style="text-align:right;">T</th>
				<th style="text-align:right;">F</th>
				<th style="text-align:right;">T</th>
				<th style="text-align:right;">F</th>
				<th style="text-align:right;">T</th>
				<th style="text-align:right;">F</th>
			</tr>
		</thead>
		
		<tbody>
			<c:forEach var="resultado" items="#{_abstractRelatorioBiblioteca.resultados}">
				<tr>
					<td> ${resultado.classificacao}</td>
					
					<td class="dadosNumeros"> ${resultado.qtdTitulosCorrenteNacionais}</td>
					<td class="dadosNumeros"> ${resultado.qtdFasciculosCorrenteNacionais}</td>
					<td class="dadosNumeros"> ${resultado.qtdTitulosCorrenteInternacionais}</td>
					<td class="dadosNumeros"> ${resultado.qtdFasciculosCorrenteInternacionais}</td>
					
					<td class="dadosNumeros"> ${resultado.qtdTitulosNaoCorrenteNacionais}</td>
					<td class="dadosNumeros"> ${resultado.qtdFasciculosNaoCorrenteNacionais}</td>
					<td class="dadosNumeros"> ${resultado.qtdTitulosNaoCorrenteInternacionais}</td>
					<td class="dadosNumeros"> ${resultado.qtdFasciculosNaoCorrenteInternacionais}</td>
					
					<td class="dadosNumeros"> ${resultado.qtdTitulosPorClassificacao}</td>
					
					<td class="dadosNumeros"> ${resultado.qtdFasciculosPorClassificacao}</td>
					
					
					
					<c:set var="qtdTotalTitulosCorrentesNacionais" value="${qtdTotalTitulosCorrentesNacionais + resultado.qtdTitulosCorrenteNacionais}" scope="request" />
					<c:set var="qtdTotalFasciculosCorrenteNacionais" value="${qtdTotalFasciculosCorrenteNacionais + resultado.qtdFasciculosCorrenteNacionais}" scope="request" />
					<c:set var="qtdTotalTitulosCorrenteInternacionais" value="${qtdTotalTitulosCorrenteInternacionais + resultado.qtdTitulosCorrenteInternacionais}" scope="request" />
					<c:set var="qtdTotalFasciculosCorrenteInternacionais" value="${qtdTotalFasciculosCorrenteInternacionais + resultado.qtdFasciculosCorrenteInternacionais}" scope="request" />
					
					<c:set var="qtdTotalTitulosNaoCorrenteNacionais" value="${qtdTotalTitulosNaoCorrenteNacionais + resultado.qtdTitulosNaoCorrenteNacionais}" scope="request" />
					<c:set var="qtdTotalFasciculosNaoCorrenteNacionais" value="${qtdTotalFasciculosNaoCorrenteNacionais + resultado.qtdFasciculosNaoCorrenteNacionais}" scope="request" />
					<c:set var="qtdTotalTitulosNaoCorrenteInternacionais" value="${qtdTotalTitulosNaoCorrenteInternacionais + resultado.qtdTitulosNaoCorrenteInternacionais}" scope="request" />
					<c:set var="qtdTotalFasciculosNaoCorrenteInternacionais" value="${qtdTotalFasciculosNaoCorrenteInternacionais + resultado.qtdFasciculosNaoCorrenteInternacionais}" scope="request" />
					
					<c:set var="qtdTotalTitulosPorClassificacao" value="${qtdTotalTitulosPorClassificacao + resultado.qtdTitulosPorClassificacao}" scope="request" />
					<c:set var="qtdTotalFasciculosPorClassificacao" value="${qtdTotalFasciculosPorClassificacao + resultado.qtdFasciculosPorClassificacao}" scope="request" />
					
				</tr>
				
			</c:forEach>
				<tr class="totalizacao">
					<td> Total </td>
					<td class="dadosNumeros"> ${qtdTotalTitulosCorrentesNacionais}</td>
					<td class="dadosNumeros"> ${qtdTotalFasciculosCorrenteNacionais}</td>
					<td class="dadosNumeros"> ${qtdTotalTitulosCorrenteInternacionais}</td>
					<td class="dadosNumeros"> ${qtdTotalFasciculosCorrenteInternacionais}</td>
					<td class="dadosNumeros"> ${qtdTotalTitulosNaoCorrenteNacionais}</td>
					<td class="dadosNumeros"> ${qtdTotalFasciculosNaoCorrenteNacionais}</td>
					<td class="dadosNumeros"> ${qtdTotalTitulosNaoCorrenteInternacionais}</td>
					<td class="dadosNumeros"> ${qtdTotalFasciculosNaoCorrenteInternacionais}</td>
					<td class="dadosNumeros"> ${qtdTotalTitulosPorClassificacao}</td>
					<td class="dadosNumeros"> ${qtdTotalFasciculosPorClassificacao}</td>
				</tr>
			
		</tbody>  
		
	</table>
	
	<p style="margin-top: 15px; margin-bottom: 15px;">
		
		<li><strong>Corrente: </strong> O intervalo entre a data de registro do último fascículo de uma determinada assinatura e a data atual é menor que o tempo de expiração cadastrado no sistema. </li>
		<br/>
		<li><strong>Não Corrente: </strong> O intervalo entre a data de registro do último fascículo de uma determinada assinatura e a data atual é maior que o tempo de expiração cadastrado no sistema.</li>
	</p> 
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
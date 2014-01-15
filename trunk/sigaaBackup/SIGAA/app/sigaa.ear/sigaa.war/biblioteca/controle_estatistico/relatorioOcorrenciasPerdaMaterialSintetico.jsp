<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<f:view>
	
	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	
	<c:set var="total_ocorrencia" value="0" scope="request" />
	
	<table id="tabela" class="tabelaRelatorioBorda" width="100%" style="margin: auto;">
	
		<thead>
			<tr>
				<td style="width: 40%; background-color: #DEDEDE;">Biblioteca</td>
				<td style="width: 10%; background-color: #DEDEDE; text-align: right;">Em Aberto</td>
				<td style="width: 10%; background-color: #DEDEDE; text-align: right;">Reposto Similar</td>
				<td style="width: 10%; background-color: #DEDEDE; text-align: right;">Reposto Equivalente</td>
				<td style="width: 10%; background-color: #DEDEDE; text-align: right;">Substituído</td>
				<td style="width: 10%; background-color: #DEDEDE; text-align: right;">Não Reposto</td>
				<td style="width: 10%; background-color: #DEDEDE; text-align: right;">Não Baixado</td>
				<td style="width: 10%; background-color: #DEDEDE; text-align: right;">Total</td>
			</tr>
		</thead>
	
		<c:forEach var="resultado" items="${_abstractRelatorioBiblioteca.resultado}">
			<tr>
				<td style="text-align: right;">${resultado[1]}</td>
				<td style="text-align: right;">${resultado[2]}</td>
				<td style="text-align: right;">${resultado[3]}</td>
				<td style="text-align: right;">${resultado[4]}</td>
				<td style="text-align: right;">${resultado[5]}</td>
				<td style="text-align: right;">${resultado[6]}</td>
				<td style="text-align: right;">${resultado[7]}</td>
				<td style="text-align: right;">${resultado[2] + resultado[3] + resultado[4] + resultado[5] + resultado[6] + resultado[7]}</td>
				<c:set var="total_em_aberto" value="${total_em_aberto + resultado[2]}" scope="request" />
				<c:set var="total_similar" value="${total_similar + resultado[3]}" scope="request" />
				<c:set var="total_equivalente" value="${total_equivalente + resultado[4]}" scope="request" />
				<c:set var="total_substituido" value="${total_substituido + resultado[5]}" scope="request" />
				<c:set var="total_nao_reposto" value="${total_nao_reposto + resultado[6]}" scope="request" />
				<c:set var="total_nao_baixado" value="${total_nao_baixado + resultado[7]}" scope="request" />
				
			</tr>
		</c:forEach>
	
		<tbody>
			<tr style="font-weight: bold;">
				<td style="width: 40%;">Total</td>
				<td style="width: 10%;text-align: right;">${total_em_aberto}</td>
				<td style="width: 10%;text-align: right;">${total_similar}</td>
				<td style="width: 10%;text-align: right;">${total_equivalente}</td>
				<td style="width: 10%;text-align: right;">${total_substituido}</td>
				<td style="width: 10%;text-align: right;">${total_nao_reposto}</td>
				<td style="width: 10%;text-align: right;">${total_nao_baixado}</td>
				<td style="width: 10%;text-align: right;">${total_em_aberto + total_similar + total_equivalente
															+ total_substituido + total_nao_reposto + total_nao_baixado}</td>
			</tr>
		</tbody>
	
	</table>
	
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<f:view>
	
	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	<c:set var="meses" value="${fn:split('Janeiro Fevereiro Março Abril Maio Junho Julho Agosto Setembro Outubro Novembro Dezembro',' ')}" />
	
	<%--  Importante:  Essa listagem está ordenada, só mostrar as informações corretas para o usuário se o resultado da consulta estever ordenado igual a essa lista --%>
	<c:set var="classes" value="${_abstractRelatorioBiblioteca.classesPrincipaisClassificacaoEscolhida}" />
	
	
	
	<c:set var="columnWidth" value="${fn:length(classes)}" />
	
	<table id="tabela" class="tabelaRelatorioBorda" width="100%">

		<thead>
			<tr>
				<th rowspan="2" style="text-left;" rowspan="2">Mês</th>
				<th colspan="${fn:length(classes) }" style="text-align:center;">Classes</th>
				<th rowspan="2" align="right">Total</th>
			</tr>
			<tr>
				<c:forEach var="classe" items="#{ classes }">
					<th width="40" style="text-align:right;">${classe}</th>
				</c:forEach>
			</tr>
		</thead>
		
		<tbody>
			<c:forEach var="mes" begin="1" end="12">
				<tr>
					<td>${ meses[mes-1] }</td>
					
					<c:forEach var="classe" items="#{ classes }" >
						<td style="text-align: right; width: ${100/columnWidth}%">${ _abstractRelatorioBiblioteca.resultados[mes][classe] != null ? _abstractRelatorioBiblioteca.resultados[mes][classe] : 0 }</td>
					</c:forEach>
					
					<th style="text-align: right;">${ _abstractRelatorioBiblioteca.totalPorMes[mes] != null ? _abstractRelatorioBiblioteca.totalPorMes[mes] : 0 }</th>
				</tr>
			</c:forEach>
			
			<tr>
				<th>Total</th>
				<c:forEach var="classe" items="#{ classes }">
					<th style="text-align: right; width: ${100/columnWidth}%">${ _abstractRelatorioBiblioteca.totalPorClasse[classe] != null ? _abstractRelatorioBiblioteca.totalPorClasse[classe] : 0 }</th>
				</c:forEach>
				<th style="text-align: right;">${ _abstractRelatorioBiblioteca.totalGeral != null ? _abstractRelatorioBiblioteca.totalGeral : 0 }</th>
			</tr>

		</tbody>
	</table>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	.tabelaRelatorioBorda tr{
		text-align:center;
	}
</style>

<f:view>
	
	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	<c:set var="primeiraTabela" value="${ true }" />

	<c:forEach var="r" items="#{ _abstractRelatorioBiblioteca.resultados }">
		<c:if test="${r[0] != tipo}">
			<c:if test="${ not primeiraTabela }">
				</table>
			</c:if>			
			<table class="tabelaRelatorioBorda" style="margin-top:10px; margin-bottom: 10px; width:100%;">
				<caption>${r[0]}</caption>
				<thead>
					<tr>
						<th style="text-align:left;">Área</th>
						<th style="text-align:right;">Títulos</th>
						<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos or _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgmatExemplares }">
							<th style="text-align:right;${_abstractRelatorioBiblioteca.ctgMaterial != _abstractRelatorioBiblioteca.ctgMatTodos ? '_border-right:2px solid #CCCCCC;' : ''}">
								Exemplares
							</th>
						</c:if>
						<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos or _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgmatFasciculos }">
							<th style="text-align:right;_border-right:2px solid #CCCCCC;">Fascículos</th>
						</c:if>
					</tr>
				</thead>
				<c:set var="primeiraTabela" value="${ false }" />
				<c:set var="tipo" value="#{r[0]}" />
		</c:if>
			
			<tr ${r[1] == 'Total' ? 'style="font-weight:bold;"' : ''}>
				<td style="text-align:left;">${r[1] != null ? r[1] : "Sem área"}</td>
				<td style="text-align:right;">${r[5]}</td>
				<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos or _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgmatExemplares }">
					<td style="text-align:right;${_abstractRelatorioBiblioteca.ctgMaterial != _abstractRelatorioBiblioteca.ctgMatTodos ? '_border-right:2px solid #CCCCCC;' : ''}">
						${r[3]}
					</td>
				</c:if>
				<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos or _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgmatFasciculos }">
					<td style="text-align:right;_border-right:2px solid #CCCCCC;">${r[4]}</td>
				</c:if>
			</tr>

			<c:if test='${r[1] == "Total"}'>
				<tr><td colspan='5' style='border:none;'>&nbsp;</td></tr>
			</c:if>
	</c:forEach>
	</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
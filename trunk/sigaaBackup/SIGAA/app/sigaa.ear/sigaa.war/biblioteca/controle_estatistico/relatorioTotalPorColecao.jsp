<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	.tabelaRelatorioBorda tr{
		text-align:center;
	}
</style>

<f:view>

	
	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>

	<table class="tabelaRelatorioBorda" width="100%">
		<thead>
			<tr>
				<th style="text-align:left;">Coleção</th>
				<th style="text-align:right;">Títulos</th>
				<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos or _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgmatExemplares }">
					<th style="text-align:right;">Exemplares</th>
				</c:if>
				<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos or _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgmatFasciculos }">
					<th style="text-align:right;">Fascículos</th>
				</c:if>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="r" items="${_abstractRelatorioBiblioteca.resultados}">
				<tr>
					<td style="text-align:left;">${r.key}</td>
					<td style="text-align:right;">${r.value[0]}</td>
					<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos or _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgmatExemplares }">
						<td style="text-align:right;">${r.value[1]}</td>
					</c:if>
					<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos or _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgmatFasciculos }">
						<td style="text-align:right;">${r.value[2]}</td>
					</c:if>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td style="text-align: left;">Total</td>
				<td style="text-align: right;">${_abstractRelatorioBiblioteca.total[0]}</td>
				<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos or _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgmatExemplares }">
					<td style="text-align: right;">${_abstractRelatorioBiblioteca.total[1]}</td>
				</c:if>
				<c:if test="${ _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos or _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgmatFasciculos }">
					<td style="text-align: right;">${_abstractRelatorioBiblioteca.total[2]}</td>
				</c:if>
			</tr>
		</tfoot>
	</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
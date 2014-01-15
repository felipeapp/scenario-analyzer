<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<style>
	.totalizacao * {
		text-align: left;
	}
	
	.totalizacao th {
		font-weight: bold;
		padding-right: 5px;
	}
</style>

<f:view>

	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	<a4j:keepAlive beanName="_abstractRelatorioBiblioteca" />
	
	<div class="totalizacao">
		<table>
			<tr>
				<th>Total de Títulos: </th>
				<td>${ _abstractRelatorioBiblioteca.totalTitulos }</td>
			</tr>
			<tr>
				<th>Total de Materiais: </th>
				<td>${ _abstractRelatorioBiblioteca.totalMateriais }</td>
			</tr>
		</table>
	</div>


	<table class="tabelaRelatorioBorda" style="width:100%;">
		<thead>
			<tr>
				<th style="text-align:left;width: 80px;">Códigos de Barras</th>
				<th style="text-align:left;">Título</th>
				<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao1}">
					<th style="text-align:left;width: 65px;">${classificacaoBibliograficaMBean.descricaoClassificacao1}</th>
				</c:if>
				<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao2}">
					<th style="text-align:left;width: 65px;">${classificacaoBibliograficaMBean.descricaoClassificacao2}</th>
				</c:if>
				<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao3}">
					<th style="text-align:left;width: 65px;">${classificacaoBibliograficaMBean.descricaoClassificacao3}</th>
				</c:if>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="r" items="#{_abstractRelatorioBiblioteca.resultado}">
				<tr>
					<td style="text-align:left;">${r[0]}</td>
					<td style="text-align:left;">${r[1]}</td>
					<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao1}">
						<td style="text-align:left;">${r[2]}</td>
					</c:if>
					<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao2}">
						<td style="text-align:left;">${r[3]}</td>
					</c:if>
					<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao3}">
						<td style="text-align:left;">${r[4]}</td>
					</c:if>
				</tr>
			</c:forEach>
		</tbody>
	</table>


	<%@include file="/biblioteca/controle_estatistico/rodape_impressao_relatorio_paginacao.jsp"%>

</f:view>

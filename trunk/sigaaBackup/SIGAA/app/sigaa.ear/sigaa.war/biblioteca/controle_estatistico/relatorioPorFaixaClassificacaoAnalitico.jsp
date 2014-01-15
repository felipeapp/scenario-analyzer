<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<f:view>

	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	
	<a4j:keepAlive beanName="_abstractRelatorioBiblioteca" />

	<table class="tabelaRelatorioBorda" style="width:100%;">
		<thead>
			<tr>
				<th style="text-align:left;width:130px;">C�digos de Barras</th>
				<th style="text-align:left;">T�tulo</th>
				<th style="text-align:left;width:130px;">Classifica��o Bibliogr�fica</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="r" items="#{_abstractRelatorioBiblioteca.resultado}">
				<tr>
					<td style="text-align:left;">${r[0]}</td>
					<td style="text-align:left;">${r[1]}</td>
					<td style="text-align:left;">${r[2]}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>


	<%@include file="/biblioteca/controle_estatistico/rodape_impressao_relatorio_paginacao.jsp"%>

</f:view>

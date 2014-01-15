<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>

	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	<table class="tabelaRelatorioBorda" style="margin-left:auto; margin-right:auto; width: 100%;">
	
		<thead>
			<tr>
				<th>${_abstractRelatorioBiblioteca.descricaoAgrupamento1}</th>
				<th style="text-align: right;">Títulos</th>
				<th style="text-align: right;">Materiais Baixados</th>
			</tr>
		</thead>
		
		<tbody>
		
			<c:set var="totalTitulos" value="0" scope="request" />
			<c:set var="totalMateriais" value="0" scope="request" />
		
			<c:forEach var="resultado" items="#{ _abstractRelatorioBiblioteca.resultadoSintetico }">
				<tr>
					<th>${ resultado[0] }</th>
					<td style="text-align: right;">${ resultado[2]  }</td>
					<td style="text-align: right;">${ resultado[1]  }</td>
					
					
					<c:set var="totalMateriais" value="${totalMateriais + resultado[1] }" scope="request" />
					<c:set var="totalTitulos" value="${totalTitulos + resultado[2] }" scope="request" />
					
				</tr>
			</c:forEach>
		
		</tbody>
		
		<tfoot>
			<tr>
				<td>Total</td>
				<td style="text-align: right;"><h:outputText value="#{ totalTitulos }" />*</td>
				<td style="text-align: right;"><h:outputText value="#{ totalMateriais }" /></td>
			</tr>
		</tfoot>
		
	</table>
	
	<p style="margin-top: 15px; margin-bottom: 15px;">
	<strong>*</strong> &nbsp&nbsp&nbsp A totalização de Títulos apresentada nesse campo se refere a quantidade de Títulos dos materiais mostrados no relatório.
	Não representa o número real de Título no acervo e pode estar acima do número real, pois se os materiais ligados a um
	mesmo Título possuírem as informações usadas no agrupamento diferentes, o Título será contado mais de uma vez. <br/> 
	&nbsp&nbsp&nbsp&nbsp Por exemplo, se dois ou mais materiais baixados de um mesmo Título estiverem em coleções diferentes, e o relatório for agrupado por 
	coleção, o mesmo Título será contado em todas as coleções em que seus materiais estiverem.
	</p> 
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>

	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	<table class="tabelaRelatorioBorda" style="margin-left:auto; margin-right:auto; width: 100%;">
	
		<thead>
			<tr>
				<th>${_abstractRelatorioBiblioteca.descricaoAgrupamento1}</th>
				<th style="text-align: right;">T�tulos</th>
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
	<strong>*</strong> &nbsp&nbsp&nbsp A totaliza��o de T�tulos apresentada nesse campo se refere a quantidade de T�tulos dos materiais mostrados no relat�rio.
	N�o representa o n�mero real de T�tulo no acervo e pode estar acima do n�mero real, pois se os materiais ligados a um
	mesmo T�tulo possu�rem as informa��es usadas no agrupamento diferentes, o T�tulo ser� contado mais de uma vez. <br/> 
	&nbsp&nbsp&nbsp&nbsp Por exemplo, se dois ou mais materiais baixados de um mesmo T�tulo estiverem em cole��es diferentes, e o relat�rio for agrupado por 
	cole��o, o mesmo T�tulo ser� contado em todas as cole��es em que seus materiais estiverem.
	</p> 
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
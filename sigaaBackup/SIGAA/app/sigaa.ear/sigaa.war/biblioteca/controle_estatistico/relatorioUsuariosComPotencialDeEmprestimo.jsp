<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>

	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	<table class="tabelaRelatorioBorda" width="50%" align="center">
		<thead>
			<tr>
				<th>Categoria</th>
				<th style="text-align: right;">Quantidade</th>
			</tr>
		</thead>
		
		<tbody>
			<c:forEach var="valor" items="#{_abstractRelatorioBiblioteca.resultados}">
				<tr>
					<td><h:outputText value="#{valor.key}" /></td>
					<td style="text-align: right;"><h:outputText value="#{valor.value}" /></td>
				</tr>
			</c:forEach>
			<c:if test="${ fn:length(_abstractRelatorioBiblioteca.resultados) > 1 }">
				<tr style="font-weight: bold;">
		        	<th>Total</th>
			   	    <th style="text-align: right;"><h:outputText value="#{_abstractRelatorioBiblioteca.total}"/></th>	        	
		        </tr>
			</c:if>
		</tbody>
		
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>

	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	<table class="tabelaRelatorioBorda" width="100%">
		<thead>
			<tr style="background-color: #DEDFE3;">
				<th>Categoria do Usuário</th>
				<th style="text-align: right;">Quantidade de Usuários com empréstimos devolvidos</th>
			</tr>
		</thead>
		
		<tbody>
			<c:forEach items="#{ _abstractRelatorioBiblioteca.resultados }" var="linha">
				<tr>
					<td><h:outputText value="#{linha.key.descricao}"/></td>
					<td style="text-align: right;"><h:outputText value="#{linha.value}"/></td>
				</tr>
			</c:forEach>
			<c:if test="${ fn:length( _abstractRelatorioBiblioteca.resultados ) > 1 }" >
				<tr style="background-color: #DEDFE3;">
					<th>Total</th>
					<th style="text-align: right;"><h:outputText value="#{_abstractRelatorioBiblioteca.total}"/></th>
				</tr>
			</c:if>
		</tbody>
				
	</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
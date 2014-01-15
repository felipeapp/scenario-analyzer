<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<f:view>

	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	<table class="tabelaRelatorioBorda" width="100%">
		
		<thead>
			<tr style="background-color: #DEDFE3;">
				<th>Informações do Título</th>
				<th style="text-align: right;">Quantidade de Empréstimos</th>
				<th style="text-align: right;">Quantidade de Materiais</th>
				<th style="text-align: right;">Empréstimos por Material</th>
			</tr>
		</thead>
		
		<tbody>
			<c:forEach items="#{ _abstractRelatorioBiblioteca.resultados }" var="linha">
				<tr>
					<td><h:outputText value="#{linha.titulo}"/></td>
					<td style="text-align: right; width: 12%;"><h:outputText value="#{linha.qtdEmprestimos}"/></td>
					<td style="text-align: right; width: 12%;"><h:outputText value="#{linha.qtdMateriais}"/></td>
					<td style="text-align: right; width: 12%;"><ufrn:format type="valor" valor="${linha.mediaEmprestimos}"/></td>
				</tr>
			</c:forEach>
		</tbody>
		
	</table>
	
	<table class="tabelaRelatorioBorda" width="100%">
		
		<tbody>
			<tr style="background-color: #DEDFE3;">
				<th style="width: 64%;">Total de Títulos</th>
				<th style="text-align: right;"><h:outputText value="#{_abstractRelatorioBiblioteca.totalTitulos}"/></th>
			</tr>
			<tr style="background-color: #DEDFE3;">
				<th style="width: 64%;">Total de Empréstimos</th>
				<th style="text-align: right;"><h:outputText value="#{_abstractRelatorioBiblioteca.totalEmprestimos}"/></th>
			</tr>
			<tr style="background-color: #DEDFE3;">
				<th style="width: 64%;">Total de Materiais</th>
				<th style="text-align: right;"><h:outputText value="#{_abstractRelatorioBiblioteca.totalMateriais}"/></th>
			</tr>
			<tr style="background-color: #DEDFE3;">
				<th style="width: 64%;">Total de Empréstimos por Material</th>
				<th style="text-align: right;"><ufrn:format type="valor" valor="${_abstractRelatorioBiblioteca.totalMediaEmprestimos}"/></th>
			</tr>
		</tbody>
		
	</table>
	
	<c:if test="${ _abstractRelatorioBiblioteca.totalTitulos == _abstractRelatorioBiblioteca.LIMITE }">
		<p>* O limite máximo de ${_abstractRelatorioBiblioteca.LIMITE} títulos foi alcançado. Apenas os ${_abstractRelatorioBiblioteca.LIMITE} primeiros estão sendo mostrados.</p>
	</c:if>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
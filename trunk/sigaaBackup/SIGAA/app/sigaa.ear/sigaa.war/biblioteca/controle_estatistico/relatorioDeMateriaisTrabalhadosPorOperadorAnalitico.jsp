<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<%@taglib uri="/tags/a4j" prefix="a4j"%>

<f:view>
	
	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>

	<a4j:keepAlive beanName="_abstractRelatorioBiblioteca" />
	

	<table class="tabelaRelatorioBorda" width="100%">
		
		<tr style="background-color: #DEDFE3">
			<th style="text-align: center;">Data do cadastro</th>
			
			<c:if test="${_abstractRelatorioBiblioteca.tipoDeAcervo == _abstractRelatorioBiblioteca.tipoAcervoMateriais}">
				<th width="15%">Código de Barras</th>
			</c:if>
			
			<th style="text-align: right;">Nº Sistema</th>
			<th width="30%">Título</th>
			<th>Autor</th>
			<th>Edição</th>
			<th>Ano</th>
			<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao1 && _abstractRelatorioBiblioteca.classificacao1Escolhida}"> 
				<th>${classificacaoBibliograficaMBean.descricaoClassificacao1}</th>
			</c:if>
			<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao2 && _abstractRelatorioBiblioteca.classificacao2Escolhida}"> 
				<th>${classificacaoBibliograficaMBean.descricaoClassificacao2}</th>
			</c:if>
			<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao3 && _abstractRelatorioBiblioteca.classificacao3Escolhida}"> 
				<th>${classificacaoBibliograficaMBean.descricaoClassificacao3}</th>
			</c:if>
			<th>Nº Chamada</th>
		</tr>
		
		<c:set var="filtroPessoa" value="" scope="request"/>
		<c:set var="filtroBiblioteca" value="" scope="request"/>
		<c:forEach var="linha" items="#{_abstractRelatorioBiblioteca.linhas}" varStatus="status">
			
			<c:if test="${ filtroPessoa != linha.nomePessoa}">			
				<c:set var="filtroPessoa" value="${linha.nomePessoa}"  scope="request"/>
				<c:set var="filtroBiblioteca" value=""  scope="request"/>
				
				<c:if test="${ not status.first }">
					<tr style="height: 30px;">
						<td colspan="11">&nbsp;</td>
					</tr>
				</c:if>
								
				<tr style="background-color: #828282; color: White; font-weight: bold; height: 30px;">
					<td colspan="11" style="text-align: center;">
						<%-- <hr style="border-top: solid 1px black;" /> --%>
						${linha.nomePessoa}
						<%-- <hr style="border-top: solid 1px black;" /> --%>
					</td>
				</tr>
			</c:if>
			
			<c:if test="${ filtroBiblioteca != linha.descricaoBiblioteca}">
				<c:set var="filtroBiblioteca" value="${linha.descricaoBiblioteca}"  scope="request"/>
				<tr style="background-color: #EEEEEE; font-weight: bold; height: 30px;">
					<td colspan="11">${linha.descricaoBiblioteca}</td>
				</tr>
			</c:if>
			
			<tr>
				<td style="text-align: center;"> <ufrn:format type="data" valor="${linha.dataCriacao}"/> </td>
				
				<c:if test="${_abstractRelatorioBiblioteca.tipoDeAcervo == _abstractRelatorioBiblioteca.tipoAcervoMateriais}">
					<td style="font-weight: bold;"> <h:outputText value="#{linha.codigoBarras}" />  </td>
				</c:if>
				
				<td style="text-align: right;"> <h:outputText value="#{linha.numerodoSistema}" /> </td>
				<td> <h:outputText value="#{linha.titulo}" /> </td>
				<td> <h:outputText value="#{linha.autor}" /> </td>
				<td> <h:outputText value="#{linha.edicao}" /> </td>
				<td> <h:outputText value="#{linha.ano}" /> </td>
				
				<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao1 && _abstractRelatorioBiblioteca.classificacao1Escolhida}"> 
					<td> <h:outputText value="#{linha.classificacao1}" /> </td>
				</c:if>
				<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao2 && _abstractRelatorioBiblioteca.classificacao2Escolhida}"> 
					<td> <h:outputText value="#{linha.classificacao2}" /> </td>
				</c:if>
				<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao3 && _abstractRelatorioBiblioteca.classificacao3Escolhida}"> 
					<td> <h:outputText value="#{linha.classificacao3}" /> </td>
				</c:if>
				
				<td> <h:outputText value="#{linha.chamada}" /> </td>
			</tr>
		</c:forEach>
	</table>

	<p style="margin-top: 15px; margin-bottom: 15px;">
		<c:if test="${_abstractRelatorioBiblioteca.tipoDeAcervo == _abstractRelatorioBiblioteca.tipoAcervoMateriais}">
			&nbsp&nbsp&nbsp <strong>Observação:</strong> A listagem de materiais apresentada não considera os materiais removidos ou baixados do acervo.
		</c:if>	
		
		<c:if test="${_abstractRelatorioBiblioteca.tipoDeAcervo == _abstractRelatorioBiblioteca.tipoAcervoTitulos}">
			&nbsp&nbsp&nbsp <strong>Observação:</strong> A listagem de Títulos apresentada não considera os Títulos removidos do acervo.
		</c:if>	
			
	</p>
	
	
	<%@include file="/biblioteca/controle_estatistico/rodape_impressao_relatorio_paginacao.jsp"%>

</f:view>

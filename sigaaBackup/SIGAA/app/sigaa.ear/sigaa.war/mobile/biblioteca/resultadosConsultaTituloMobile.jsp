<%@include file="/mobile/commons/cabecalho.jsp"%>

<f:view>
	
	<h:form>
		
		<h:commandButton value="Menu Principal" action="#{operacoesBibliotecaMobileMBean.telaMenuPrincipalBibliotecaMobile}"/> <br/><br/>
		<h:commandButton value="<< Voltar Tela Pesquisa" action="#{operacoesBibliotecaMobileMBean.telaConsultaTituloMobile}"/> <br/><br/>
		
			<c:if test="${fn:length(operacoesBibliotecaMobileMBean.titulosResumidos) > 0}">				
				
				<table class="listagemMobile">
						<caption> ${fn:length(operacoesBibliotecaMobileMBean.titulosResumidos)} Título(s) Encontrado(s)</caption>
						
						<tbody>
			
							<c:forEach var="titulo" items="#{operacoesBibliotecaMobileMBean.titulosResumidos }" varStatus="status">
										
									<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
										<th colspan="6"><small>Título:</small></th>
									</tr>
			
									<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
										<td colspan="6">
											<c:out value="${titulo.titulo}"></c:out>
										</td>
									</tr>				
			
									<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
										<th colspan="6">Autor:</th>
									</tr>
			
									<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
										<td colspan="6">
											<c:out value="${titulo.autor}"></c:out>
										</td>
									</tr>
			
									<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
										<th colspan="4">Quantidade de Materiais Inf.:</th>
										<td colspan="1">
											<c:out value="${titulo.quantidadeMateriaisAtivosTitulo}"></c:out>
										</td>
										<td colspan="1">
											<h:commandButton value="Ver" action="#{operacoesBibliotecaMobileMBean.visualizarMateriais}" rendered="#{titulo.quantidadeMateriaisAtivosTitulo > 0}">
												<f:setPropertyActionListener target="#{operacoesBibliotecaMobileMBean.idTituloSelecionado}" value="#{titulo.idTituloCatalografico}" />
											</h:commandButton>
										</td>
									</tr>
			
									<tr>
										<td colspan="6">
											<hr style="color:#111155"/>
										</td>	
									</tr>
	
							</c:forEach>
				
						</tbody>
						
				</table>
				
			</c:if>
		
	</h:form>
		
</f:view>

<%@include file="/mobile/commons/rodape.jsp" %>
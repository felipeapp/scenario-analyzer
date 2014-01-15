<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>  <ufrn:subSistema /> &gt; Planilhas Bibliograficas</h2>

<f:view>

	<h:form id="formEscolhePlanilhaBibliografico">

		<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>
		
		<%-- Mander os dados da pesquisa se o usuário clicar no botão voltar --%>
		<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>

		<table class="formulario" width="80%">
				
				<caption class="listagem">Escolha uma Planilha</caption>
				
				<tr>
					<td style="text-align:center">
						
							<h:selectOneMenu id="planilhasCatalogacao" value="#{catalogacaoMBean.idPlanilhaEscolhida}" 
											valueChangeListener="#{catalogacaoMBean.carregaDadosPlanilha}" onchange="submit()">	
								<f:selectItem itemValue="-1" itemLabel="--- SELECIONE ---"/>
								<f:selectItems value="#{catalogacaoMBean.allPlanilhaBibliograficaComboBox}" />
									    
							</h:selectOneMenu>
				
					</td>
				</tr>
		
				<c:if test="${catalogacaoMBean.idPlanilhaEscolhida != -1  }">
				<tr>
					
					<td>
					
						<table class="subFormulario" width="100%">
		
							<caption class="listagem">Dados da Planilha</caption>
		
							<tr>
								<td width="20%" style="text-align: right;">
									Formato Material:	
								</td>
								<td colspan="3">
									${catalogacaoMBean.tituloTemp.formatoMaterial.descricaoCompleta}	
								</td>
							</tr>
							<c:if test="${catalogacaoMBean.tituloTemp.camposControle != null}">
							
								<c:forEach var="campoControle" items="#{catalogacaoMBean.tituloTemp.camposControle}">
									<tr>
										<td width="20%" style="text-align: right;">
										${campoControle.etiqueta.tag}:	
										</td>
										<td colspan="3">
										${campoControle.dado}	
										</td>
									</tr>						
			
			
								</c:forEach>
							</c:if>
							
							<c:if test="${catalogacaoMBean.tituloTemp.camposDados != null}">
								<c:forEach var="campoDados" items="#{catalogacaoMBean.tituloTemp.camposDados}">
									<tr>
										<td width="20%" style="text-align: right;">
										${campoDados.etiqueta.tag}:
										</td>
										<td style="width: 5%;">
			
											<c:if test="${campoDados.indicador1 == ' '}">
												_ 
											</c:if> 
											<c:if test="${campoDados.indicador1 != ' '}">
												${campoDados.indicador1}	
											</c:if>
			
										</td>
										<td style="width: 5%;">
			
											<c:if test="${campoDados.indicador2 == ' '}">
												_ 
											</c:if> 
											<c:if test="${campoDados.indicador2 != ' '}">
												${campoDados.indicador2}	
											</c:if>
			
										</td>
										<td style="width: 80%;">		
											<c:forEach var="subCampo" items="#{campoDados.subCampos}">
												<c:out value="$"/>${subCampo.codigo}&nbsp&nbsp&nbsp
											</c:forEach>		
										</td>
			
									</tr>						
			
			
								</c:forEach>
							</c:if>
							
						</table>
		
						</td>
						
				    </tr>
				    
				</c:if>
		
				<tfoot>
					<tr>
						<td> 
						<h:commandButton value="<< Voltar" action="#{pesquisaTituloCatalograficoMBean.telaPesquisaTitulo}" />
						<h:commandButton value="Cancelar" onclick="#{confirm}" immediate="true" action="#{catalogacaoMBean.cancelar}" />
						<h:commandButton value="Próximo passo >>" action="#{catalogacaoMBean.iniciarPlanilha}" 
									rendered="#{catalogacaoMBean.idPlanilhaEscolhida != -1  }"/>
						</td>
					</tr>
				</tfoot>
		
			</table>

	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>


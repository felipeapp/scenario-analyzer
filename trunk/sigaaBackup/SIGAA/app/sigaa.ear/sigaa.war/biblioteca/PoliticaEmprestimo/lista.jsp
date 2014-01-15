<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@ taglib uri="/tags/primefaces-p" prefix="p"%>


<c:set var="confirmAlteracaoPolitica" value="if (!confirm('Confirma a altera��o nas informa��es das pol�ticas ?')) return false" scope="request" />

<h2><ufrn:subSistema /> > Gerenciar Pol�ticas de Empr�stimo</h2>

<div class="descricaoOperacao"> 
    <p>Uma pol�tica de empr�stimo define os prazos e quantidades que um usu�rio ter� direito nos empr�stimos de materiais para as bibliotecas do sistema.</p>
    <br/>
    <p>Os prazos e quantidades s�o determinados pelas seguintes vari�veis: </p>
      
    <div style="padding-left: 100px;">
	    <ol>
	    	<li>Biblioteca do Empr�stimo</li>
	    	<li>V�nculo do Usu�rio</li>
	    	<li>Tipo do Empr�stimo Realizado</li>
	    	<li>O Status de Material</li>
	    	<li>O Tipo de Material</li>
	    </ol>
    </div>
    
    <p>
		<c:if test="${! visualizarPoliticasDeEmprestimoMBean.sistemaPermiteConfigurarPoliticasDiferentePorBiblioteca}"> 
			 <strong>Observa��o:</strong> Todas as bibliotecas est�o utilizando a pol�tica da Biblioteca Central. 
		</c:if>
	</p> 
	 <br/>
	
    
    <p>Se o prazo for contado em horas, o usu�rio precisa renovar ou devolver at� a hora marcada para n�o ficar com d�bitos. Se o prazo for marcado em dias, o
    usu�rio ter� at� o final do dia, 23h e 59 min, para quitar seu compromisso com a biblioteca.</p>
    <p> <strong> As pol�ticas da biblioteca central nunca podem ser desativadas. Para uma biblioteca setorial, caso n�o existam pol�ticas
    ativas, ela usar� automaticamente as pol�ticas da biblioteca central.</strong> </p>
    <p><strong>IMPORTANTE: </strong>Ativando-se as pol�ticas das bibliotecas setoriais a quantidade de materiais que o usu�rio poder� tomar emprestado ser� acrescida.
    <p>Ele poder� realizar a quantidade de empr�stimos definidas anteriormente para a biblioteca central, mais a
    quantidade de empr�stimo definida na pol�tica da biblioteca setorial, mas apenas para os materiais da biblioteca setorial cuja pol�tica foi ativada.</p> 
</div>

<f:view>

	<p:resources />

	<link rel="stylesheet" type="text/css" href="/sigaa/css/primefaces_skin.css" />

	<a4j:keepAlive beanName="politicaEmprestimoMBean"></a4j:keepAlive>
	
	
	
	<h:form id="formPoliticaEmprestimo">

		
		<%-- Submete se o usu�rio cancelou o cadatro de pol�tica, usado para impedir o uso do bot�o voltar do navegador --%>
		<h:inputHidden id="cancelado" value="#{politicaEmprestimoMBean.cancelado}" />


	
		<div class="infoAltRem" style="width:90%;">
			<c:if test="${politicaEmprestimoMBean.usuarioTemPermissaoAlteracao}">
			
				<h:graphicImage value="/img/adicionar.gif" />
				<a4j:commandLink actionListener="#{politicaEmprestimoMBean.adicionarPoliticaVazia}" reRender="formPoliticaEmprestimo" value="Criar Nova Pol�tica" />
				
				<h:graphicImage value="/img/addUnd.gif" />: Atribuir Tipo de Empr�stimo
				
				<h:graphicImage value="/img/alterar_old.gif" />: Alterar Status/Tipo de Material
				
				<h:graphicImage value="/img/delete.gif" />: Remover Pol�tica
				
			</c:if>
		</div> 

		
		<a4j:outputPanel ajaxRendered="true">
	
			
			<%-- 
			 --  Os models panel do Prime Faces para adicionar/ remover status/ tipo materiais associados a pol�tica   
			 --%>
			
			<p:dialog header="Alterar os Status do Materiais da Pol�tica" widgetVar="modelPanelStatusMateriais" modal="true" width="350" height="200">
				
				<table style="width: 100%;">
					<c:if test="${not empty politicaEmprestimoMBean.statusQuePodemSerEmprestados}">
						<c:forEach var="statusMaterial" items="#{politicaEmprestimoMBean.statusQuePodemSerEmprestados}" varStatus="status">
							<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
								<td style="width: 80%;">
									${statusMaterial.descricao}
								</td>
								<td style="width: 20%;">
									<h:selectBooleanCheckbox id="checkSelecionarStatus" label="Selecionar" value="#{statusMaterial.selecionado}" />
								</td>
							</tr>
						</c:forEach>
					</c:if>
					
					
					<tfoot>
						<tr>
							<td colspan="2" style="text-align: center;">
								 <a4j:commandButton id="cmdButtonAtualizarStatussMateriaisPolitica" value="Atualizar" 
						  		 			actionListener="#{politicaEmprestimoMBean.atualizarStatusMateriaisPolitica}" 
					    					reRender="formPoliticaEmprestimo"
											onclick="modelPanelStatusMateriais.hide();">
								</a4j:commandButton>
							</td>
						</tr>
					</tfoot>
					
				</table>
				
			</p:dialog>
		
		
			<p:dialog header="Alterar os Tipos de Materiais da Pol�tica" widgetVar="modelPanelTiposMateriais" modal="true" width="350" height="400">
				
				<table style="width: 100%;">
					<c:if test="${not empty politicaEmprestimoMBean.tiposMateriaisQuePodemSerEmprestados}">
						<c:forEach var="tipoMaterial" items="#{politicaEmprestimoMBean.tiposMateriaisQuePodemSerEmprestados}" varStatus="status">
							<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
								<td style="width: 80%;">
									${tipoMaterial.descricao}
								</td>
								<td style="width: 20%;">
									<h:selectBooleanCheckbox id="checkSelecionarTipoMaterial" label="Selecionar" value="#{tipoMaterial.selecionado}" />
								</td>
							</tr>
						</c:forEach>
					</c:if>
					
					<tfoot>
						<tr>
							<td colspan="2" style="text-align: center;">
								 <a4j:commandButton id="cmdButtonAtualizarTiposMateriaisPolitica" value="Atualizar" 
						  		 			actionListener="#{politicaEmprestimoMBean.atualizarTiposMateriaisPolitica}" 
					    					reRender="formPoliticaEmprestimo"
											onclick="modelPanelTiposMateriais.hide();">
								</a4j:commandButton>
							</td>
						</tr>
					</tfoot>
					
				</table>
			
			</p:dialog>
			
			
			<p:dialog header="Atribuir Tipo de Empr�stimo � Pol�tica" widgetVar="modelPanelTiposEmprestimos" modal="true" width="350" height="250">
				
				<table style="width: 100%;">
					<c:if test="${not empty politicaEmprestimoMBean.tiposEmprestimosAtivosPodemSerAssociadosAPoliticas}">
						
						<tr>
							<td style="width: 100%;">
								<h:selectOneRadio id="raidoSelecionarTipoEmprestimo" label="Selecionar" layout="pageDirection" value="#{politicaEmprestimoMBean.idTipoEmprestimoSelecionadoNovasPoliticas}" >
									<c:forEach var="tipoEmprestimo" items="#{politicaEmprestimoMBean.tiposEmprestimosAtivosPodemSerAssociadosAPoliticas}" varStatus="status">
										 <f:selectItem id="raidoTipoEmprestimo" itemLabel="#{tipoEmprestimo.descricao}" itemValue="#{tipoEmprestimo.id}" />
									</c:forEach>
								</h:selectOneRadio>
							</td>
						</tr>
						
					</c:if>
					
					<tr style="height: 20px;">
					</tr>
					
					<tfoot>
						<tr>
							<td colspan="2" style="text-align: center;">
								 <a4j:commandButton id="cmdButtonAtualizarTipoEmprestimoPolitica" value="Atualizar" 
						  		 			actionListener="#{politicaEmprestimoMBean.atualizarTipoEmprestimoPolitica}" 
					    					reRender="formPoliticaEmprestimo"
											onclick="modelPanelTiposEmprestimos.hide();">
								</a4j:commandButton>
							</td>
						</tr>
					</tfoot>
					
				</table>
			
			</p:dialog>
			
			
			
			<%-- A tabela que lista as pol�ticas, tamb�m permite o usu�rio alterar as informa��es delas --%>
			
		
			<table class="formulario" style="border-bottom-width: 0px; width: 100%;">
				
				<caption>Pol�tica de Empr�stimo</caption>
				
				<tr>
					<th style="width: 40%;" class="obrigatorio">Biblioteca:</th>
					<td style="width: 60%;">
						<h:selectOneMenu id="biblioteca" value="#{politicaEmprestimoMBean.bibliotecaDasPoliticas.id}">
							<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
							<f:selectItems value="#{politicaEmprestimoMBean.bibliotecasAtivas}"/>
							<a4j:support event="onchange" reRender="formPoliticaEmprestimo:selectTipoUsuario" actionListener="#{politicaEmprestimoMBean.escolheuBibliotecaDasPoliticas}" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<th class="obrigatorio">V�nculo do Usu�rio:</th>
					<td>
						<h:selectOneMenu id="selectTipoUsuario" value="#{politicaEmprestimoMBean.valorVinculoSelecionado}" disabled="#{!politicaEmprestimoMBean.escolheuBiblioteca}">
							<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
							<f:selectItems value="#{politicaEmprestimoMBean.vinculosUsuarioBiblioteca}"/>
							<a4j:support event="onchange" reRender="formPoliticaEmprestimo:dataTablePoliticas" actionListener="#{politicaEmprestimoMBean.carregaPoliticasUsuario}" />
						</h:selectOneMenu>
					</td>
				</tr>
		
				<tr>
					<td colspan="2">
							
							<t:div rendered="#{ politicaEmprestimoMBean.quantidadePoliticasEmprestimo > 0 && politicaEmprestimoMBean.escolheuBiblioteca && politicaEmprestimoMBean.escolheuTipoUsuario }">
					
								<t:dataTable id="dataTablePoliticas" var="politica"  value="#{politicaEmprestimoMBean.dataModelPoliticas}" style="width:100%;" >
								
										<t:column style="text-align: left; width:20%;">
				 							
				 							<f:facet name="header"> <h:outputText value="Tipo do Empr�stimo" /> </f:facet>
				 							
				 							<h:outputText value="#{politica.tipoEmprestimo.descricao}" ></h:outputText>
				 							
				 							<a4j:commandLink title="Atribuir Tipo de Empr�stimo" actionListener="#{politicaEmprestimoMBean.carregaTipoEmprestimosQuePodemSerAssociadosAPoliticas}"
													rendered="#{politica.id <= 0}"
													ajaxSingle="true" oncomplete="modelPanelTiposEmprestimos.show();">
												<f:param name="idPolitica" value="#{politica.id}" />
												<h:graphicImage url="/img/addUnd.gif" alt="Atribuir Tipo de Empr�stimo" style="padding-left: 10px;" />
											</a4j:commandLink>
				 							
				 						</t:column>
				 						
				 						<t:column style="text-align: left; width:20%;">
				 							
				 							<f:facet name="header"> <h:outputText value="Status dos Materiais" /> </f:facet>
				 							
				 							<h:outputText value="#{politica.statusMateriaisFormatados}" ></h:outputText>
				 							
				 							<a4j:commandLink title="Alterar Status" actionListener="#{politicaEmprestimoMBean.carregaStatusEmprestaveis}"
													ajaxSingle="true" oncomplete="modelPanelStatusMateriais.show();">
												<f:param name="idPolitica" value="#{politica.id}" />
												<h:graphicImage url="/img/alterar_old.gif" alt="Alterar Status" style="padding-left: 10px;" />
											</a4j:commandLink>
											
				 						</t:column>
				 						
				 						
				 						<t:column style="text-align: left; width:20%;">
				 							
				 							
				 							<f:facet name="header"> <h:outputText value="Tipo dos Materiais" /> </f:facet>
				 							
				 							<h:outputText value="#{politica.tiposMateriaisFormatados}" ></h:outputText>
										
												
											<a4j:commandLink title="Alterar Tipo de Material" actionListener="#{politicaEmprestimoMBean.carregaTipoMateriaisEmprestaveis}"
													ajaxSingle="true" oncomplete="modelPanelTiposMateriais.show();">
												<f:param name="idPolitica" value="#{politica.id}" />
												<h:graphicImage url="/img/alterar_old.gif" alt="Alterar Tipo de Material"  style="padding-left: 10px;"/>
											</a4j:commandLink>
												
				 						
				 						</t:column>
				 						
				 						
				 						<t:column style="text-align: center;">
				 						
				 								<f:facet name="header"> <h:outputText value="Quantidade de Materiais"/> </f:facet>
				 						
				 								<h:outputText value="#{politica.quantidadeMateriais}" rendered="#{! politicaEmprestimoMBean.usuarioTemPermissaoAlteracao}" />
								 				
								 				<h:inputText value="#{politica.quantidadeMateriais}"  label="Quantidade de Materiais" rendered="#{ politicaEmprestimoMBean.usuarioTemPermissaoAlteracao}" maxlength="2" size="3" onkeyup="return formatarInteiro(this);" />
								 				
				 						
				 						</t:column>
				 						
				 						
				 						<t:column style="text-align: center;">
				 						
				 								<f:facet name="header"> <h:outputText value="Prazo do Empr�stimo" /> </f:facet>
				 						
				 								<h:outputText value="#{politica.prazoEmprestimo}"  rendered="#{! politicaEmprestimoMBean.usuarioTemPermissaoAlteracao}" />
										 		
										 		<h:inputText value="#{politica.prazoEmprestimo}" label="Prazo do Empr�stimo" rendered="#{politicaEmprestimoMBean.usuarioTemPermissaoAlteracao}" maxlength="2" size="3" onkeyup="return formatarInteiro(this);" />
											 		
				 						
				 						</t:column>
				 						
				 						
				 						<t:column style="text-align: center;">
				 						
				 								<f:facet name="header"> <h:outputText value="Unidade" /> </f:facet>
				 						
				 								<h:outputText value="#{politica.descricaoTipoPrazo}"  rendered="#{! politicaEmprestimoMBean.usuarioTemPermissaoAlteracao}" />
										 		
											 	<h:selectOneMenu value="#{politica.tipoPrazo}" rendered="#{politicaEmprestimoMBean.usuarioTemPermissaoAlteracao}">
													<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
													<f:selectItems value="#{politicaEmprestimoMBean.tiposPrazo}"/>
												</h:selectOneMenu>
				 						
				 						</t:column>
				 						
				 						
				 						<t:column style="text-align: center;">
				 						
				 								<f:facet name="header"> <h:outputText value="Quantidade de Renova��es" /> </f:facet>
				 						
				 								<h:outputText value="#{politica.quantidadeRenovacoes}" rendered="#{! politicaEmprestimoMBean.usuarioTemPermissaoAlteracao}" />
										 	
										 		<h:inputText value="#{politica.quantidadeRenovacoes}" label="Quantidade de Renova��es" rendered="#{politicaEmprestimoMBean.usuarioTemPermissaoAlteracao}" maxlength="2" size="3" onkeyup="return formatarInteiro(this);" />
										 		
				 						</t:column>
				 						
				 						
				 						<t:column style="text-align: center;">
				 						
			 									<f:facet name="header"> <h:outputText value="" /> </f:facet>
			 									
												<a4j:commandLink title="Remover Pol�tica" actionListener="#{politicaEmprestimoMBean.removerPoliticaSelecionada}" rendered="#{politicaEmprestimoMBean.usuarioTemPermissaoAlteracao}"
														reRender="formPoliticaEmprestimo">
													<h:graphicImage url="/img/delete.gif" alt="Remover Pol�tica" />
												</a4j:commandLink>
				 						
				 						</t:column>
				 						
								</t:dataTable>
								
							</t:div>
							
							<t:div rendered="#{ politicaEmprestimoMBean.quantidadePoliticasEmprestimo == 0 && politicaEmprestimoMBean.escolheuBiblioteca && politicaEmprestimoMBean.escolheuTipoUsuario }"
								style="text-align:center; color:red;">
							N�o existem pol�ticas de empr�stimos cadastradas
							</t:div>
						
					</td>
				</tr>
				
				<tfoot>
					<tr>
						<td colspan="2" align="center">
						
							
							<h:commandButton id="cmdGravarMudancaPoliticas"  value="Gravar"  action="#{politicaEmprestimoMBean.gravarAlteracoesPoliticas}" 
												onclick="#{confirmAlteracaoPolitica}" rendered="#{politicaEmprestimoMBean.usuarioTemPermissaoAlteracao 
																							&& politicaEmprestimoMBean.escolheuBiblioteca && politicaEmprestimoMBean.escolheuTipoUsuario}"/>
							
							
							<h:commandButton value="Cancelar" onclick="#{confirm};"  action="#{politicaEmprestimoMBean.cancelar}" immediate="true" id="cancelar"/>
						</td>
					</tr>
				</tfoot> 
				
			</table>
			
		</a4j:outputPanel>
		
			
			
		<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>
		
	</h:form>

</f:view>


	<script>
			/**
			 * M�todo auxiliar que seleciona os comboboxes com os valores iniciais ap�s o usu�rio clicar
			 * no bot�o "Cancelar", impedindo que o usu�rio volte pelo browser visualizando dados incorretos.
			 *
			 * Sempre o usu�rio vai ter que selecionar novamente os comboxs, impedindo errros do bot�o voltar.
			 */
			function cancelar(){
				document.getElementById("formPoliticaEmprestimo:biblioteca").value = -1;
				document.getElementById("formPoliticaEmprestimo:selectTipoUsuario").value = -1;
				document.getElementById("formPoliticaEmprestimo:cancelado").value = true;
			}
		</script>





<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<h2><ufrn:subSistema /> > Políticas de Empréstimo</h2>

<div class="descricaoOperacao"> 
    <p>Uma política de empréstimo define os prazos e quantidades que um usuário terá direito nos empréstimos de materiais informacionais para as bibliotecas.</p>
    <br/>
    <p>Os prazos e quantidades são determinados pelas seguintes variáveis: </p>
      
    <div style="padding-left: 100px;">
	    <ol>
	    	<li>Biblioteca do Empréstimo</li>
	    	<li>Vínculo do Usuário</li>
	    	<li>Tipo do Empréstimo Realizado</li>
	    	<li>Status do Material</li>
	    	<li>Tipo do Material</li>
	    	
	    </ol>
    </div>
   
   
    <br/><br/>
    <p>
		<c:if test="${! visualizarPoliticasDeEmprestimoMBean.sistemaPermiteConfigurarPoliticasDiferentePorBiblioteca}"> 
			 <strong>Observação:</strong> Todas as bibliotecas estão utilizando a política da Biblioteca Central. 
		</c:if>
	</p> 
	
	<br/>
	
</div>

<f:view>

	<a4j:keepAlive beanName="visualizarPoliticasDeEmprestimoMBean"></a4j:keepAlive>
	
	<h:form id="formVisualizarPoliticaPolitica">
	
	
		<a4j:outputPanel ajaxRendered="true">
	
			<table class="formulario" style="border-bottom-width: 0px; width: 100%;">
				
				<caption>Política de Empréstimo</caption>
				
				<tr>
					<th colspan="2"  style="height: 20px;"></th>
				</tr>
				
				<tr>
					<th colspan="2" style="text-align: center;"> 
						<h:outputText value="O senhor(a) está utilizando a Política de Empréstimo de:" 
								rendered="#{visualizarPoliticasDeEmprestimoMBean.usuarioBiblioteca != null}"/>
						<h:outputText value="O senhor(a) está utilizando nenhuma Política de Empréstimo no momento" style="color: red;" 
								rendered="#{visualizarPoliticasDeEmprestimoMBean.usuarioBiblioteca == null}"/>		
					</th>
				</tr>
			
				<tr>
					<th colspan="2" style="text-align: center;  font-weight: bold;">
						<h:outputText value="#{visualizarPoliticasDeEmprestimoMBean.usuarioBiblioteca.vinculo.descricao}" 
								rendered="#{visualizarPoliticasDeEmprestimoMBean.usuarioBiblioteca != null}"/>
					</th>
				</tr>
				
				<tr>
					<th colspan="2"  style="height: 20px;"></th>
				</tr>
				
				<tr>
					<th style="width: 40%;">Biblioteca:</th>
					<td style="width: 60%;">
						<h:selectOneMenu id="biblioteca" value="#{visualizarPoliticasDeEmprestimoMBean.bibliotecaDasPoliticas.id}">
							<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
							<f:selectItems value="#{visualizarPoliticasDeEmprestimoMBean.bibliotecasAtivas}"/>
							<a4j:support event="onchange" reRender="formPoliticaEmprestimo:selectTipoUsuario" actionListener="#{visualizarPoliticasDeEmprestimoMBean.escolheuBibliotecaDasPoliticas}" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<th>Vínculo do Usuário:</th>
					<td>
						<h:selectOneMenu id="selectTipoUsuario" value="#{visualizarPoliticasDeEmprestimoMBean.valorVinculoSelecionado}" disabled="#{!visualizarPoliticasDeEmprestimoMBean.escolheuBiblioteca}">
							<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
							<f:selectItems value="#{visualizarPoliticasDeEmprestimoMBean.vinculosUsuarioBiblioteca}"/>
							<a4j:support event="onchange" reRender="formPoliticaEmprestimo:dataTablePoliticas" actionListener="#{visualizarPoliticasDeEmprestimoMBean.carregaPoliticasUsuario}" />
						</h:selectOneMenu>
					</td>
				</tr>
		
				<tr>
					<td colspan="2">
							
							<t:div rendered="#{ visualizarPoliticasDeEmprestimoMBean.quantidadePoliticasEmprestimo > 0 && visualizarPoliticasDeEmprestimoMBean.escolheuBiblioteca && visualizarPoliticasDeEmprestimoMBean.escolheuTipoUsuario }">
					
								<t:dataTable id="dataTablePoliticas" var="politica"  value="#{visualizarPoliticasDeEmprestimoMBean.dataModelPoliticas}" style="width:100%;" >
								
										<t:column style="text-align: left; width:20%;">
				 							
				 							<f:facet name="header"> <h:outputText value="Tipo do Empréstimo" /> </f:facet>
				 							
				 							<h:outputText value="#{politica.tipoEmprestimo.descricao}" ></h:outputText>
				 							
				 						</t:column>
				 						
				 						<t:column style="text-align: left; width:20%;">
				 							
				 							<f:facet name="header"> <h:outputText value="Status dos Materiais" /> </f:facet>
				 							
				 							<h:outputText value="#{politica.statusMateriaisFormatados}" ></h:outputText>
											
				 						</t:column>
				 						
				 						
				 						<t:column style="text-align: left; width:20%;">
				 							
				 							
				 							<f:facet name="header"> <h:outputText value="Tipo dos Materiais" /> </f:facet>
				 							
				 							<h:outputText value="#{politica.tiposMateriaisFormatados}" ></h:outputText>
				 						
				 						</t:column>
				 						
				 						
				 						<t:column style="text-align: center;">
				 						
				 								<f:facet name="header"> <h:outputText value="Quantidade de Materiais"/> </f:facet>
				 						
				 								<h:outputText value="#{politica.quantidadeMateriais}" />
								 				
								 				
								 				
				 						
				 						</t:column>
				 						
				 						
				 						<t:column style="text-align: center;">
				 						
				 								<f:facet name="header"> <h:outputText value="Prazo do Empréstimo" /> </f:facet>
				 						
				 								<h:outputText value="#{politica.prazoEmprestimo}" />

				 						</t:column>
				 						
				 						
				 						<t:column style="text-align: center;">
				 						
				 								<f:facet name="header"> <h:outputText value="Unidade" /> </f:facet>
				 						
				 								<h:outputText value="#{politica.descricaoTipoPrazo}" />
										 		
				 						</t:column>
				 						
				 						
				 						<t:column style="text-align: center;">
				 						
				 								<f:facet name="header"> <h:outputText value="Quantidade de Renovações" /> </f:facet>
				 						
				 								<h:outputText value="#{politica.quantidadeRenovacoes}" />
										 		
				 						</t:column>
				 						
				 						
								</t:dataTable>
								
							</t:div>
						
					</td>
				</tr>
				
				<tfoot>
					<tr>
						<td colspan="2" align="center">
							<h:commandButton value="Cancelar" action="#{visualizarPoliticasDeEmprestimoMBean.cancelar}" immediate="true" id="cancelar"/>
						</td>
					</tr>
				</tfoot> 
				
			</table>
			
		</a4j:outputPanel>
	
	</h:form>
	
</f:view>
	


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2 class="tituloPagina"> <ufrn:subSistema/> > Selecionar dias para bolsa de alimentação</h2>
	<br>

	<a4j:keepAlive beanName="diasAlimentacaoMBean" />
	
	<h:form id="form">
	
	<h:messages showDetail="true"/>
		<table class="formulario" width="100%">

			<caption class="listagem">Selecionar os dias de alimentação</caption>

			<tr>
				<td>
					<b> Matrícula: </b> <h:outputText value="#{diasAlimentacaoMBean.discente.matricula}" /> 
				</td>
			</tr>
			<tr>
				<td>
					<b> Discente: </b> <h:outputText value="#{diasAlimentacaoMBean.discente.nome}" /> 
				</td>
			</tr>
				
			<tr>
				<td>
					<rich:panel>
							<f:facet name="header">
						      <h:outputText value="CAFÉ"/>
						    </f:facet>
		    				<h:selectManyCheckbox id="cafe" value="#{diasAlimentacaoMBean.selectedItemsCafe}">
								<f:selectItem itemLabel="Segunda" itemValue="1" />
								<f:selectItem itemLabel="Terça" itemValue="2" />
								<f:selectItem itemLabel="Quarta" itemValue="3" />
								<f:selectItem itemLabel="Quinta" itemValue="4" />
								<f:selectItem itemLabel="Sexta" itemValue="5" />
								<f:selectItem itemLabel="Sábado" itemValue="6" />
								<f:selectItem itemLabel="Domingo" itemValue="7" />
							</h:selectManyCheckbox>
					</rich:panel>
				</td>
			</tr>
			<tr>
				<td>
					<rich:panel>
							<f:facet name="header">
						      <h:outputText value="ALMOÇO"/>
						    </f:facet>

		    				<h:selectManyCheckbox id="almoco" value="#{diasAlimentacaoMBean.selectedItemsAlmoco}">
								<f:selectItem itemLabel="Segunda" itemValue="1" />
								<f:selectItem itemLabel="Terça" itemValue="2" />
								<f:selectItem itemLabel="Quarta" itemValue="3" />
								<f:selectItem itemLabel="Quinta" itemValue="4" />
								<f:selectItem itemLabel="Sexta" itemValue="5" />
								<f:selectItem itemLabel="Sábado" itemValue="6" />
								<f:selectItem itemLabel="Domingo" itemValue="7" />
							</h:selectManyCheckbox>
					</rich:panel>
				</td>
			</tr>
			<tr>
				<td>
					<rich:panel>
							<f:facet name="header">
						      <h:outputText value="JANTAR"/>
						    </f:facet>
					    
		    				<h:selectManyCheckbox id="jantar" value="#{diasAlimentacaoMBean.selectedItemsJanta}">
								<f:selectItem itemLabel="Segunda" itemValue="1" />
								<f:selectItem itemLabel="Terça" itemValue="2" />
								<f:selectItem itemLabel="Quarta" itemValue="3" />
								<f:selectItem itemLabel="Quinta" itemValue="4" />
								<f:selectItem itemLabel="Sexta" itemValue="5" />
								<f:selectItem itemLabel="Sábado" itemValue="6" />
								<f:selectItem itemLabel="Domingo" itemValue="7" />
							</h:selectManyCheckbox>
					</rich:panel>
				</td>
			</tr>
			
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton value="Definir dias de alimentação" action="#{diasAlimentacaoMBean.cadastrar}"/>
					<h:commandButton value="Cancelar" action="#{diasAlimentacaoMBean.cancelar}" id="cancelarOperacao" onclick="#{confirm}" />
				</td>
			</tr>
		</tfoot>

		</table>

	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2 class="tituloPagina"> <ufrn:subSistema/> > Selecionar dias para bolsa de alimentação</h2>
	<br>
	
	<h:form id="form">
	
	<h:messages showDetail="true"/>
		<table class="listagem" width="100%">

			<caption class="listagem">Dias de alimentação definidos</caption>

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
					<rich:dataTable align="center" value="#{diasAlimentacaoMBean.listagemBolsaAuxilio}" var="xxx">

						<!-- ALMOÇO -->
		
							<f:facet name="header">
						      <h:outputText value="ALMOÇO"/>
						    </f:facet>
					    
						    <rich:column>
								 <f:facet name="header">
								 	<h:outputText value="Segunda"/>
								 </f:facet>
						    		<h:outputText value="#{xxx.diasAlimentacao.segundaA}" />
						    </rich:column>
						  	
						  	<rich:column>
						    	<f:facet name="header">
						      		<h:outputText value="Terça"/>
						    	</f:facet>
						    		<h:outputText value="#{xxx.diasAlimentacao.tercaA}" />
						    </rich:column>
						    
						    <rich:column>
							    <f:facet name="header">
							      <h:outputText value="Quarta"/>
							    </f:facet>
						    		<h:outputText value="#{xxx.diasAlimentacao.quartaA}" />
						    </rich:column>
						    
						    <rich:column>
						    	<f:facet name="header">
							      <h:outputText value="Quinta"/>
							    </f:facet>
						    		<h:outputText value="#{xxx.diasAlimentacao.quintaA}" />
						    </rich:column>
						    
						    <rich:column>
							    <f:facet name="header">
							      <h:outputText value="Sexta"/>
							    </f:facet>
						    		<h:outputText value="#{xxx.diasAlimentacao.sextaA}" />
						    </rich:column>
						    
							<rich:column>
								<f:facet name="header">
							      <h:outputText value="Sábado"/>
							    </f:facet>
						      		<h:outputText value="#{xxx.diasAlimentacao.sabadoA}" />
						    </rich:column>
						    
						    <rich:column>
							    <f:facet name="header">
							      <h:outputText value="Domingo"/>
							    </f:facet>
						    		<h:outputText value="#{xxx.diasAlimentacao.domingoA}" />
						    </rich:column>
						</rich:dataTable>
				
				</td>
			</tr>
			
			<tr>
				<td>
					<rich:dataTable align="center" value="#{diasAlimentacaoMBean.listagemBolsaAuxilio}" var="xxx">
					
					<!-- JANTA -->
   				
   						<f:facet name="header">
					      <h:outputText value="JANTAR"/>
					    </f:facet>
   					    						
						    <rich:column>
								 <f:facet name="header">
								 	<h:outputText value="Segunda"/>
								 </f:facet>
						    		<h:outputText value="#{xxx.diasAlimentacao.segundaJ}" />
						    </rich:column>
						  	<rich:column>
						    
						    	<f:facet name="header">
						      		<h:outputText value="Terça"/>
						    	</f:facet>
						    		<h:outputText value="#{xxx.diasAlimentacao.tercaJ}" />
						    </rich:column>
						    
						    <rich:column>
							    <f:facet name="header">
							      <h:outputText value="Quarta"/>
							    </f:facet>
						    		<h:outputText value="#{xxx.diasAlimentacao.quartaJ}" />
						    </rich:column>
						    
						    <rich:column>
						    	<f:facet name="header">
							      <h:outputText value="Quinta"/>
							    </f:facet>
						    		<h:outputText value="#{xxx.diasAlimentacao.quintaJ}" />
						    </rich:column>
						    
						    <rich:column>
							    <f:facet name="header">
							      <h:outputText value="Sexta"/>
							    </f:facet>
						    		<h:outputText value="#{xxx.diasAlimentacao.sextaJ}" />
						    </rich:column>
						    
							<rich:column>
								<f:facet name="header">
							      <h:outputText value="Sábado"/>
							    </f:facet>
						      		<h:outputText value="#{xxx.diasAlimentacao.sabadoJ}" />
						    </rich:column>
						    
						    <rich:column>
							    <f:facet name="header">
							      <h:outputText value="Domingo"/>
							    </f:facet>
						    		<h:outputText value="#{xxx.diasAlimentacao.domingoJ}" />
						    </rich:column>
					
					</rich:dataTable>
				</td>
			</tr>
		
		<tfoot>
			<tr>
				<td align="center">
					<h:commandButton value="Voltar" action="#{buscaBolsaAuxilioMBean.instanciar}" />
				</td>
			</tr>
		</tfoot>
		
		</table>

	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
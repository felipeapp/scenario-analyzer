<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2> <ufrn:subSistema /> &gt; Formulário de Evolução</h2>

<h:form id="form">
	<h:inputHidden value="#{cadastroFormularioEvolucaoMBean.confirmButton}" />
	<h:inputHidden value="#{cadastroFormularioEvolucaoMBean.obj.id}" />

	<table class="formulario" width="80%">
		<caption class="listagem">Resumo do Formulário de Evolução</caption>
		<tr>
			<th class="rotulo">Formulário:</th>
			<td> <h:outputText id="labelFormulario" value="#{cadastroFormularioEvolucaoMBean.obj.nivelInfantil}" /> </td>
		</tr>
		<tr>
			<th class="rotulo"><b>Data Cadastro:<b/></th>
			<td> <h:outputText id="data" value="#{cadastroFormularioEvolucaoMBean.obj.dataCadastro}" /> </td>
		</tr>
	</table>
	<br />
	
	<table class="formulario" width="100%">
		<tr>
		<td>
		
		<rich:dataTable id="tabelaBlocos" var="bloco" value="#{cadastroFormularioEvolucaoMBean.blocos}" width="100%">
		
			<rich:column>
			
				<f:facet name="header"><f:verbatim><b>BLOCOS/ÁREAS</b></f:verbatim></f:facet>
				<h:outputText style="font-weight: bold; font-style: italic;" value="#{bloco.descricao}"/>
				
				<c:choose>
				
					<c:when test="${bloco.subareas.size <= 0}">
						
						<rich:dataTable id="tabelaAreas" var="areas" value="#{bloco}" width="100%">
					
							<rich:column>
								<h:outputText value="#{areas.descricao}"/>
							</rich:column>
							
							<rich:column>
							
								<rich:dataTable id="tabelaConteudos" var="conteudo" value="#{areas.conteudos}" width="100%">
						
									<rich:column>
									
										<f:facet name="header"><f:verbatim><b>CONTEÚDOS/OBJETIVOS</b></f:verbatim></f:facet>
										<h:outputText style="font-weight: bold; font-style: italic;" value="#{conteudo.descricaoOrdem}"/>
										
										<rich:dataTable id="tabelaObjetivos" var="objetivo" value="#{conteudo.objetivos}" width="100%">
						
											<rich:column>
												<h:outputText value="#{objetivo.descricaoOrdem}"/>
											</rich:column>
										
										</rich:dataTable>
								
									</rich:column>
								
								</rich:dataTable>
								
							</rich:column>
						
						</rich:dataTable>
					
					</c:when>
				
					<c:otherwise>
					
						<rich:dataTable id="tabelaAreas" var="areas" value="#{bloco.subareas}" width="100%">
					
							<rich:column>
								<h:outputText value="#{areas.descricao}"/>
							</rich:column>
							
							<rich:column>
							
								<rich:dataTable id="tabelaConteudos" var="conteudo" value="#{areas.conteudos}" width="100%">
						
									<rich:column>
									
										<f:facet name="header"><f:verbatim><b>CONTEÚDOS/OBJETIVOS</b></f:verbatim></f:facet>
										<h:outputText style="font-weight: bold; font-style: italic;" value="#{conteudo.descricaoOrdem}"/>
										
										<rich:dataTable id="tabelaObjetivos" var="objetivo" value="#{conteudo.objetivos}" width="100%">
						
											<rich:column>
												<h:outputText value="#{objetivo.descricaoOrdem}"/>
											</rich:column>
										
										</rich:dataTable>
								
									</rich:column>
								
								</rich:dataTable>
								
							</rich:column>
						
						</rich:dataTable>
					
					</c:otherwise>
				
					
				
				</c:choose>
				
			</rich:column>
		
		</rich:dataTable>	
		
		</td>
		</tr>
		
		<tfoot>
			<tr>
				<td colspan="4">
					<h:commandButton value="Alterar" action="#{cadastroFormularioEvolucaoMBean.alterar}" id="alterar"/> 
					<h:commandButton value="Voltar" action="#{cadastroFormularioEvolucaoMBean.voltar}" id="voltaLista"/>
					<h:commandButton value="Cancelar" action="#{cadastroFormularioEvolucaoMBean.cancelar}" onclick="#{confirm}" immediate="true" id="cancelar"/>
				</td>
			</tr>
		</tfoot>
	
	</table>
											
		
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

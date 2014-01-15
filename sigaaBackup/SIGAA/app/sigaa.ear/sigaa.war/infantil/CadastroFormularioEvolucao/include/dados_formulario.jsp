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
				
			</rich:column>
		
		</rich:dataTable>	
		
		</td>
		</tr>
		
	
	</table>
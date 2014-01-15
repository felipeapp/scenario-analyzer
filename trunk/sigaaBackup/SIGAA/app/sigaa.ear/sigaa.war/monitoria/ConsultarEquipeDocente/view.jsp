<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2>Visualização de Orientador</h2>
	<br>


	<h:outputText value="#{consultarEquipeDocente.create}"/>
	
<h:form>
	<table class="tabelaRelatorio" width="100%">
		<caption class="listagem"> DADOS DO DOCENTE NO PROJETO </caption>

<tbody>
	
	<tr>
		<th width="20%">Docente:</th>
		<td>		
			<h:outputText value="#{consultarEquipeDocente.obj.servidor.siapeNome}"/> 
		</td>
	</tr>

    <c:if test="${acesso.monitoria}">
	    <tr>
	        <th width="20%">E-Mail:</th>
	        <td>        
	            <h:outputText value="#{consultarEquipeDocente.obj.servidor.pessoa.email}"/> 
	        </td>
	    </tr>
	    
	    <tr>
	        <th width="20%">Telefone:</th>
	        <td>        
	            <h:outputText value="#{consultarEquipeDocente.obj.servidor.pessoa.telefone}"/> 
	        </td>
	    </tr>
    </c:if>

	<tr>
		<th>Projeto:</th>
		<td>		
			<h:outputText value="#{consultarEquipeDocente.obj.projetoEnsino.anoTitulo}"/> 
		</td>
	</tr>	
	
	<tr>
		<th>Data de Entrada:</th>
		<td>		
			<h:outputText value="#{consultarEquipeDocente.obj.dataEntradaProjeto}">
				<f:convertDateTime pattern="dd/MM/yyyy"/>
			</h:outputText>
		</td>
	</tr>						


	<tr>
		<th>Data de Saída:</th>
		<td>		
			<h:outputText value="#{consultarEquipeDocente.obj.dataSaidaProjeto}">
				<f:convertDateTime pattern="dd/MM/yyyy"/>
			</h:outputText>
		</td>
	</tr>						

	<tr>
		<th>É Coordenador(a):</th>
		<td>		
			<h:outputText value="#{consultarEquipeDocente.obj.coordenador ? 'SIM': 'NÃO'}" />
		</td>
	</tr>						


	<tr>
		<th>Início Coordenação:</th>
		<td>		
			<h:outputText value="#{consultarEquipeDocente.obj.dataInicioCoordenador}">
				<f:convertDateTime pattern="dd/MM/yyyy"/>
			</h:outputText>
		</td>
	</tr>						


	<tr>
		<th>Fim Coordenação:</th>
		<td>		
			<h:outputText value="#{consultarEquipeDocente.obj.dataFimCoordenador}">
				<f:convertDateTime pattern="dd/MM/yyyy"/>
			</h:outputText>
		</td>
	</tr>						


	<tr>
		<th>Situação:</th>
		<td>		
			<h:outputText value="#{consultarEquipeDocente.obj.ativo ? 'ATIVO': 'INATIVO'}" />
		</td>
	</tr>						


	<tr>
		<td colspan="2">
			<table class="subFormulario" width="100%">		
				<caption class="listagem"> Orientações </caption>
						<tr>
							<td>
								<t:dataTable value="#{consultarEquipeDocente.obj.orientacoes}" var="orientacao" align="center" width="100%">
									<t:column>
										<f:facet name="header">
											<f:verbatim>Discente</f:verbatim>
										</f:facet>
										<h:outputText value="#{orientacao.discenteMonitoria.discente.matriculaNome}">						
												<f:convertDateTime pattern="dd/MM/yyyy"/>
										</h:outputText>
									</t:column>
	
	
									<t:column>
										<f:facet name="header">
											<f:verbatim>Início</f:verbatim>
										</f:facet>
										<h:outputText value="#{orientacao.dataInicio}">						
												<f:convertDateTime pattern="dd/MM/yyyy"/>
										</h:outputText>
									</t:column>
			
									<t:column>
										<f:facet name="header">
											<f:verbatim>Fim</f:verbatim>
										</f:facet>
										<h:outputText value="#{orientacao.dataFim}">						
												<f:convertDateTime pattern="dd/MM/yyyy"/>
										</h:outputText>
									</t:column>
			
									<t:column>
										<f:facet name="header">
											<f:verbatim>Status</f:verbatim>
										</f:facet>
										<h:outputText value="#{orientacao.status}" />						
									</t:column>
								
							</t:dataTable>
					
					
							<c:if test="${empty consultarEquipeDocente.obj.orientacoes}">
								<center><font color="red">DOCENTE NÃO ORIENTA ALUNOS NESTE PROJETO</font></center>
							</c:if>
							
						 </td>
					</tr>
					
					
					<tr>
						<td colspan="2">
							<table class="subFormulario" width="100%">		
								<caption class="listagem"> Componentes Curriculares do Docente </caption>
									<tr>
										<td>
											<t:dataTable value="#{consultarEquipeDocente.obj.docentesComponentes}" var="comp" align="center" width="100%">
		
												<t:column>
													<f:facet name="header"><f:verbatim>Componente Curricular</f:verbatim></f:facet>
													<h:outputText value="#{comp.componenteCurricularMonitoria.disciplina.codigoNome}" />						
												</t:column>
											</t:dataTable>
								
									<c:if test="${empty consultarEquipeDocente.obj.docentesComponentes}">
										<center><font color='red'>DOCENTE SEM COMPONENTES CURRICULARES RELACIONADOS</font></center>
									</c:if>
									
								  </td>
								</tr>
								
								</table>
						</td>
					</tr>			
				
				</table>
		</td>
	</tr>
	</tbody>
</table>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
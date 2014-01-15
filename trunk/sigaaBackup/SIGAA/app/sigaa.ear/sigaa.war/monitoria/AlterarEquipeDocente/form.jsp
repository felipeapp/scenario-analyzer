<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<c:if test="${!acesso.monitoria}">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</c:if>

	
	
<h:form>

	<h:outputText value="#{alterarEquipeDocente.create}" />	
	<h:inputHidden value="#{alterarEquipeDocente.confirmButton}" id="confirmButton"/>	
	<a4j:keepAlive beanName="alterarEquipeDocente"></a4j:keepAlive>

  <h2><ufrn:subSistema /> > Alterar Docente do Projeto</h2>

  <table class="formulario" width="100%">

		<caption class="listagem"> DADOS DO DOCENTE DO PROJETO </caption>

		<tbody>
			
			<tr>
				<th width="20%">Docente:</th>
				<td>
					<b><h:outputText value="#{alterarEquipeDocente.obj.servidor.siapeNome}"/></b> 
				</td>
			</tr>
			
			<tr>
				<th>Projeto:</th>
				<td>				
					<b><h:outputText value="#{alterarEquipeDocente.obj.projetoEnsino.anoTitulo}"/></b> 
				</td>
			</tr>	


			<tr>
				<th>Data de Entrada:</th>
				<td>
					<c:if test="${alterarEquipeDocente.confirmButton != 'Excluir Docente do Projeto'}">
						<t:inputCalendar size="10" value="#{alterarEquipeDocente.obj.dataEntradaProjeto}" renderAsPopup="true" renderPopupButtonAsImage="true"	id="idDataEntrada"/>					
						<span class="required"></span>
					</c:if>
					
					<c:if test="${alterarEquipeDocente.confirmButton == 'Excluir Docente do Projeto'}">
						<b><h:outputText value="#{alterarEquipeDocente.obj.dataEntradaProjeto}"><f:convertDateTime pattern="dd/MM/yyyy"/></h:outputText></b>
					</c:if>
					
				</td>
			</tr>						
		
		
			<tr>
				<th>Data de Saída:</th>
				<td>
					<c:if test="${(alterarEquipeDocente.confirmButton != 'Finalizar Docente no Projeto')}">
						<b><h:outputText value="#{alterarEquipeDocente.obj.dataSaidaProjeto}"><f:convertDateTime pattern="dd/MM/yyyy"/></h:outputText></b>
					</c:if>				
					
					<c:if test="${alterarEquipeDocente.confirmButton == 'Finalizar Docente no Projeto'}">
						<t:inputCalendar size="10" value="#{alterarEquipeDocente.obj.dataSaidaProjeto}" renderAsPopup="true" renderPopupButtonAsImage="true"	id="idDataSaida"/>					
						<span class="required"></span>
					</c:if>
				</td>
			</tr>						
		
			<tr>
				<th>É Coordenador(a):</th>
				<td>		
					<b><h:outputText value="#{alterarEquipeDocente.obj.coordenador ? 'SIM': 'NÃO'}" /></b>
				</td>
			</tr>						
		
			<tr>
				<th>Início Coordenação:</th>
				<td>		
					<c:if test="${alterarEquipeDocente.obj.coordenador}">
						<t:inputCalendar size="10" value="#{alterarEquipeDocente.obj.dataInicioCoordenador}" renderAsPopup="true" renderPopupButtonAsImage="true"	id="idDataInicioCoord"/>					
						<span class="required"></span>
					</c:if>
				</td>
			</tr>						
		
		
			<tr>
				<th>Fim Coordenação:</th>
				<td>		
					<c:if test="${(alterarEquipeDocente.obj.coordenador) and (alterarEquipeDocente.confirmButton == 'Finalizar Docente no Projeto')}">
						<t:inputCalendar size="10" value="#{alterarEquipeDocente.obj.dataFimCoordenador}" renderAsPopup="true" renderPopupButtonAsImage="true"	id="idDataFimCoord"/>					
						<span class="required"></span>
					</c:if>
					
					<c:if test="${(alterarEquipeDocente.obj.coordenador) or (alterarEquipeDocente.confirmButton != 'Finalizar Docente no Projeto')}">
						<b><h:outputText value="#{alterarEquipeDocente.obj.dataFimCoordenador}"><f:convertDateTime pattern="dd/MM/yyyy"/></h:outputText></b>
					</c:if>					
				</td>
			</tr>						
		
			<tr>
				<th>Ativo:</th>
				<td>
					<b><h:outputText value="#{alterarEquipeDocente.obj.ativo ? 'SIM': 'NÃO'}" /></b>
				</td>
			</tr>						
		
		<c:if test="${alterarEquipeDocente.confirmButton != 'Excluir Docente do Projeto'}">
		
			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%">		
						<caption class="listagem"> Orientações do Docente </caption>
							<tr>
								<td>
									<t:dataTable value="#{alterarEquipeDocente.obj.orientacoes}" var="orient" align="center" width="100%">

										<t:column>
											<f:facet name="header">
												<f:verbatim>Discente</f:verbatim>
											</f:facet>
											<h:outputText value="#{orient.discenteMonitoria.discente.matriculaNome}" />						
										</t:column>
					
										<t:column>
											<f:facet name="header">
												<f:verbatim>Início</f:verbatim>
											</f:facet>
											<h:outputText value="#{orient.dataInicio}"><f:convertDateTime pattern="dd/MM/yyyy"/></h:outputText>
										</t:column>
					
										<t:column>
											<f:facet name="header">
												<f:verbatim>Fim</f:verbatim>
											</f:facet>
											<h:outputText value="#{orient.dataFim}"><f:convertDateTime pattern="dd/MM/yyyy"/></h:outputText>
										</t:column>
				
									
										<t:column>
											<f:facet name="header">
												<f:verbatim>Status</f:verbatim>
											</f:facet>
											<h:outputText value="#{orient.status}" />
										</t:column>
									
							</t:dataTable>
						
							<c:if test="${empty alterarEquipeDocente.obj.orientacoes}">
								<center><font color='red'>DOCENTE NÃO ORIENTA ALUNOS NESTE PROJETO</font></center>
							</c:if>
							
						  </td>
						</tr>
						
						</table>
				</td>
			</tr>
			
		</c:if>


		<c:if test="${alterarEquipeDocente.confirmButton != 'Excluir Docente do Projeto'}">		
			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%">		
						<caption class="listagem"> Componentes Curriculares do Docente </caption>
							<tr>
								<td>
									<t:dataTable value="#{alterarEquipeDocente.obj.docentesComponentes}" var="comp" align="center" width="100%">

										<t:column>
											<f:facet name="header"><f:verbatim>Componente Curricular</f:verbatim></f:facet>
											<h:outputText value="#{comp.componenteCurricularMonitoria.disciplina.codigoNome}" />						
										</t:column>
									</t:dataTable>
						
							<c:if test="${empty alterarEquipeDocente.obj.docentesComponentes}">
								<center><font color='red'>DOCENTE SEM COMPONENTES CURRICULARES RELACIONADOS</font></center>
							</c:if>
							
						  </td>
						</tr>
						
						</table>
				</td>
			</tr>			
		</c:if>


		</tbody>

		<tfoot>
			<tr>
				<td colspan="2">
				<h:commandButton value="#{alterarEquipeDocente.confirmButton }" action="#{ alterarEquipeDocente.alterar }"/>
				<h:commandButton value="Cancelar" action="#{ alterarEquipeDocente.cancelar }"/>
		    	</td>
		    </tr>
		</tfoot>

	</table>


</h:form>
<br/>
<br/>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
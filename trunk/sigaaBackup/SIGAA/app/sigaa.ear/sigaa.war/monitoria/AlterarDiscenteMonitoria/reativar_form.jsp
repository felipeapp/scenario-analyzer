<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<c:if test="${!acesso.monitoria}">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</c:if>
	<a4j:keepAlive beanName="consultarMonitor" />
	
<h:form>

	<h:outputText value="#{alterarDiscenteMonitoria.create}" />

  <h2><ufrn:subSistema /> > Reabrir Monitoria</h2>

  <table class="formulario" width="100%">

		<caption class="listagem"> DADOS DA MONITORIA </caption>

		<tbody>
			
			<tr>
				<td width="20%">
					Projeto de Monitoria:
				</td>
				<td>				
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.projetoEnsino.titulo}"/></b> 
				</td>
			</tr>	

			<tr>
				<td>
					Discente:
				</td>
				<td>
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.discente.matriculaNome}"/></b> 
				</td>
			</tr>
			
			<tr>
				<td>
					Curso:
				</td>
				<td>		
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.discente.curso.nomeCompleto}"/></b> 
				</td>
			</tr>
			
			
			
			<tr>
				<td>
					Tipo de V�nculo:
				</td>
				<td>				
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.tipoVinculo.descricao}"/></b>
				</td>
			</tr>	
			
			<tr>
				<td>
					Situa��o:
				</td>
				<td>
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.situacaoDiscenteMonitoria.descricao}"/></b>
				</td>
			</tr>	
			
			<tr>
				<td>
					Classifica��o:
				</td>
				<td>
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.classificacao}"/>�</b>
				</td>
			</tr>		
		
			<tr>
				<td>
					Nota da prova escrita:
				</td>
				<td>
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.notaProva}"/></b>
				</td>
			</tr>
			<tr>
				<td>
					Nota Final:
				</td>
				<td>
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.nota}"/></b>
				</td>
			</tr>		
		
			<tr>
				<td>
					Data de Entrada:
				</td>
				<td>					
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.dataInicio}"><f:convertDateTime pattern="dd/MM/yyyy"/></h:outputText></b>																									
				</td>
			</tr>						
		
		
			<tr>
				<td>
					Data de Sa�da:
				</td>
				<td>
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.dataFim}"><f:convertDateTime pattern="dd/MM/yyyy"/></h:outputText></b>
				</td>
			</tr>						
		
		
			<tr>
				<td>
					Discente Ativo:
				</td>
				<td>
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.ativo ? 'SIM': 'N�O'}" /></b>
				</td>
			</tr>						
		
		
			
			<tr>
				<td>
					Observa��es:
				</td>
				<td>
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.observacao}"/></b> 
				</td>
			</tr>
			
			
		
		
			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%">		
						<caption class="listagem"> Orienta��es do Discente </caption>
							<tr>
								<td>
									<t:dataTable value="#{alterarDiscenteMonitoria.obj.orientacoes}" var="orient" align="center" width="100%">

										<t:column>
											<f:facet name="header">
												<f:verbatim>Docente</f:verbatim>
											</f:facet>
											<h:outputText value="#{orient.equipeDocente.servidor.siapeNome}" />						
										</t:column>
					
										<t:column>
											<f:facet name="header">
												<f:verbatim>In�cio</f:verbatim>
											</f:facet>
											<h:outputText value="#{orient.dataInicio}" rendered="#{orient.finalizada}"><f:convertDateTime pattern="dd/MM/yyyy"/></h:outputText>
											<t:inputCalendar size="10" value="#{orient.dataInicio}" rendered="#{!orient.finalizada}" renderAsPopup="true" renderPopupButtonAsImage="true"	/>																				
										</t:column>
					
										<t:column>
											<f:facet name="header">
												<f:verbatim>Fim</f:verbatim>
											</f:facet>
											<h:outputText value="#{orient.dataFim}" rendered="#{orient.finalizada}"><f:convertDateTime pattern="dd/MM/yyyy"/></h:outputText>
											<t:inputCalendar size="10" value="#{orient.dataFim}" rendered="#{!orient.finalizada}" renderAsPopup="true" renderPopupButtonAsImage="true"	/>										
										</t:column>
				
									
										<t:column>
											<f:facet name="header">
												<f:verbatim></f:verbatim>
											</f:facet>									
											<h:selectBooleanCheckbox value="#{orient.finalizar}" id="finalizar_orientacao" rendered="#{!orient.finalizada}"/>
											<h:outputLabel value="Finalizar" for="finalizar_orientacao" rendered="#{!orient.finalizada}"/> 											
											<h:outputText value="#{orient.status}" rendered="#{orient.finalizada}"/>
										</t:column>
									
							</t:dataTable>
						
							<c:if test="${empty alterarDiscenteMonitoria.obj.orientacoes}">
								<center><font color='red'>DISCENTE SEM ORIENTADORES RELACIONADOS</font></center>
							</c:if>
							
						  </td>
						</tr>
						
						</table>
				</td>
			</tr>
			
		



		

			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%">		
						<caption class="listagem"> Docentes do Projeto </caption>
							<tr>
								<td>
								<t:dataTable value="#{alterarDiscenteMonitoria.docentes}" var="docente" align="center" width="100%">
		
									<t:column>
										<h:selectBooleanCheckbox value="#{docente.selecionado}" rendered="#{docente.ativo}"/>
									</t:column>
		
									<t:column>
										<f:facet name="header">
											<f:verbatim>Docente</f:verbatim>
										</f:facet>
										<h:outputText value="#{docente.servidor.siapeNome}" />						
									</t:column>
							</t:dataTable>
							
							<c:if test="${empty alterarDiscenteMonitoria.docentes}">
								<center><font color='red'>N�O H� ORIENTADORES DISPON�VEIS</font></center>
							</c:if>
							
						 </td>
						</tr>
						</table>
				</td>
			</tr>

		

		
			</tbody>

			<tfoot>
				<tr>
					<td colspan="2">					
					<h:commandButton value="#{alterarDiscenteMonitoria.confirmButton}" action="#{ alterarDiscenteMonitoria.alterar }"/>
					<h:commandButton value="Cancelar" action="#{ alterarDiscenteMonitoria.cancelar }"/>
			    	</td>
			    </tr>
			</tfoot>

	</table>


</h:form>
<br/>
<br/>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
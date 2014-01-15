<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<c:if test="${!acesso.monitoria}">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</c:if>
	
	<a4j:keepAlive beanName="consultarMonitor" />
<h:form id="form">

	<h:outputText value="#{alterarDiscenteMonitoria.create}" />

  <h2><ufrn:subSistema /> > Alterar Monitor</h2>

  <table class="formulario" width="100%">

		<caption class="listagem"> DADOS DA MONITORIA </caption>

		<tbody>
			
			<tr>
				<td width="20%" align="right">
					Projeto de Monitoria:
				</td>
				<td>				
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.projetoEnsino.titulo}"/></b> 
				</td>
			</tr>	

			<tr>
				<td align="right">
					Discente:
				</td>
				<td>
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.discente.matriculaNome}"/></b> 
				</td>
			</tr>
			
			<tr>
				<td align="right">
					Curso:
				</td>
				<td>		
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.discente.curso.nomeCompleto}"/></b>
				</td>
			</tr>
			
			<tr>
				<td align="right">
					Tipo de Vínculo:
				</td>
				<td>				
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.tipoVinculo.descricao}" rendered="#{not empty alterarDiscenteMonitoria.obj.tipoVinculo.descricao}"/></b>
				</td>
			</tr>	
			
			<tr>
				<td align="right">
					Situação:
				</td>
				<td>
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.situacaoDiscenteMonitoria.descricao}"/></b>
				</td>
			</tr>	
			
			<tr>
				<td align="right">
					Classificação:
				</td>
				<td>
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.classificacao}"/>º</b>
				</td>
			</tr>		
		
			<tr>
				<td align="right">
					Nota da prova escrita:
				</td>
				<td>
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.notaProva}"/></b>
				</td>
			</tr>
			<tr>
				<td align="right">
					Nota Final:
				</td>
				<td>
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.nota}"/></b>
				</td>
			</tr>		
		
			<tr>
				<td align="right" class="${alterarDiscenteMonitoria.obj.assumiuMonitoria ? 'required': ''}">
					Data de Entrada:  &nbsp;
				</td>
				<td>
					
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.dataInicio}" id="outDataInicio" 
					   rendered="#{!alterarDiscenteMonitoria.obj.assumiuMonitoria}">
					       <f:convertDateTime pattern="dd/MM/yyyy"/>
					   </h:outputText>
					</b>
					<t:inputCalendar 
							size="10"
							rendered="#{alterarDiscenteMonitoria.obj.assumiuMonitoria}"
							maxlength="10"
							popupDateFormat="dd/MM/yyyy"
							onkeypress="return(formataData(this,event))"
							value="#{alterarDiscenteMonitoria.obj.dataInicio}"
							renderAsPopup="true"
							renderPopupButtonAsImage="true"
							id="inDataInicio">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>	
				</td>
			</tr>						
		
		
			<tr>
				<td align="right" class="${alterarDiscenteMonitoria.obj.assumiuMonitoria ? 'required':''}">
					Data de Saída:&nbsp;&nbsp;
				</td>
				
				<td>
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.dataFim}" id="outDataFim" 
					       rendered="#{!alterarDiscenteMonitoria.obj.assumiuMonitoria}">
					           <f:convertDateTime pattern="dd/MM/yyyy"/>
					    </h:outputText></b>
						<t:inputCalendar 
							size="10"
							rendered="#{alterarDiscenteMonitoria.obj.assumiuMonitoria}"
							maxlength="10"
							popupDateFormat="dd/MM/yyyy"
							onkeypress="return(formataData(this,event))"
							value="#{alterarDiscenteMonitoria.obj.dataFim}"
							renderAsPopup="true" renderPopupButtonAsImage="true"
							id="inDataFim">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
							
				</td>
			</tr>						
		
			<tr>
				<td align="right">
					Observações:
				</td>
				<td>
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.observacao}"/></b> 
				</td>
			</tr>
			
			
		<c:if test="${alterarDiscenteMonitoria.confirmButton != 'Excluir Monitoria'}">
		
			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%">		
						<caption class="listagem"> Orientações do Discente </caption>
							<tr>
								<td>
									<t:dataTable value="#{alterarDiscenteMonitoria.obj.orientacoes}" var="orient" align="center" width="100%" id="dtOrientacoes">

										<t:column>
											<f:facet name="header">
												<f:verbatim>Docente</f:verbatim>
											</f:facet>
											<h:outputText value="#{orient.equipeDocente.servidor.siapeNome}" />						
										</t:column>
					
										<t:column style="text-align: center;">
											<f:facet name="header">
												<f:verbatim><center>Início</center></f:verbatim>
											</f:facet>
											
											<t:inputCalendar
												size="10"
												maxlength="10"
												value="#{orient.dataInicio}"
												popupDateFormat="dd/MM/yyyy"												
												renderAsPopup="true"
												onkeypress="return(formataData(this,event))"
												renderPopupButtonAsImage="true" id="dataInicio" >
												<f:converter converterId="convertData"/>
											</t:inputCalendar>																				
										</t:column>
					
										<t:column style="text-align: center;">
											<f:facet name="header">
												<f:verbatim><center>Fim</center></f:verbatim>
											</f:facet>
											
											<t:inputCalendar 
												size="10"
												maxlength="10"
												value="#{orient.dataFim}"
												popupDateFormat="dd/MM/yyyy"
												onkeypress="return(formataData(this,event))"												
												renderAsPopup="true"
												renderPopupButtonAsImage="true" id="dataFim">
												<f:converter converterId="convertData"/>
											</t:inputCalendar>										
										</t:column>
				
									
										<t:column>
											<f:facet name="header">
												<f:verbatim></f:verbatim>
											</f:facet>									
											<h:selectBooleanCheckbox value="#{orient.finalizar}" id="finalizarOrientacao" rendered="#{!orient.finalizada}"/>
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
			
		</c:if>



		<c:if test="${alterarDiscenteMonitoria.confirmButton == 'Salvar'}">

			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%">		
						<caption class="listagem"> Docentes do Projeto </caption>
							<tr>
								<td>
								<t:dataTable value="#{alterarDiscenteMonitoria.docentes}" var="docente" align="center" width="100%" id="dtDocentes">
		
									<t:column>
										<h:selectBooleanCheckbox value="#{docente.selecionado}" rendered="#{docente.ativo}" id="docenteSelecionado"/>
									</t:column>
		
									<t:column>
										<f:facet name="header">
											<f:verbatim>Docente</f:verbatim>
										</f:facet>
										<h:outputText value="#{docente.servidor.siapeNome}" />						
									</t:column>
									
									<t:column style="text-align: center;">
											<f:facet name="header">
												<f:verbatim><center>Início <h:graphicImage  url="/img/required.gif" style="vertical-align: center;"/> </center></f:verbatim>
											</f:facet>											
											<t:inputCalendar
												size="10"
												maxlength="10"
												value="#{docente.dataInicioOrientacao}"
												popupDateFormat="dd/MM/yyyy"
												renderAsPopup="true"
												onkeypress="return(formataData(this,event))"
												renderPopupButtonAsImage="true"  id="dataInicio">
												<f:converter converterId="convertData"/>
											</t:inputCalendar>																				
										</t:column>
					
										<t:column style="text-align: center;">
											<f:facet name="header">
												<f:verbatim><center>Fim <h:graphicImage  url="/img/required.gif" style="vertical-align: center;"/></center></f:verbatim>
											</f:facet>
											<t:inputCalendar 
												size="10"
												maxlength="10"
												value="#{docente.dataFimOrientacao}"
												popupDateFormat="dd/MM/yyyy"
												onkeypress="return(formataData(this,event))"
												renderAsPopup="true"
												renderPopupButtonAsImage="true" id="dataFim">
												<f:converter converterId="convertData"/>
											</t:inputCalendar>										
										</t:column>
									
							</t:dataTable>
							
							<c:if test="${empty alterarDiscenteMonitoria.docentes}">
								<center><font color='red'>NÃO HÁ ORIENTADORES DISPONÍVEIS</font></center>
							</c:if>
							
						 </td>
						</tr>
						</table>
				</td>
			</tr>

		</c:if>
		
		</tbody>
		<c:if test="${alterarDiscenteMonitoria.confirmButton != 'Excluir Monitoria'}">
			<tr>
				<td colspan="2" align="center">
					<font color="red"><h:outputText value="Verificar envio de Relatórios: " rendered="#{acesso.monitoria}" escape="false"/></font>
					<h:selectOneRadio value="#{alterarDiscenteMonitoria.validarRelatorios}" id="validarRelatorios" rendered="#{acesso.monitoria}">
							<f:selectItem itemValue="true" itemLabel="Sim" id="sim" />
							<f:selectItem itemValue="false" itemLabel="Não" id="nao" />
					</h:selectOneRadio>				
				</td>			
			</tr>
		</c:if>	

		<tfoot>
				<tr>
					<td colspan="2">
										
					<h:commandButton value="#{alterarDiscenteMonitoria.confirmButton }" action="#{ alterarDiscenteMonitoria.alterar }" id="btAlterarDiscente"/>
					<h:commandButton value="<< Voltar" action="#{alterarDiscenteMonitoria.voltarListagem }" id="btVoltar"/>
					<h:commandButton value="Cancelar" action="#{ alterarDiscenteMonitoria.cancelar }" onclick="#{confirm}" id="btCancelar"/>
			    	</td>
			    </tr>
		</tfoot>

	</table>


</h:form>
<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center><br/>
<br/>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>

	<h2><ufrn:subSistema /> > Cadastrar Novo Docente no Projeto de Monitoria</h2>
	<a4j:keepAlive beanName="alterarEquipeDocente" />

	<table class="formulario" width="100%">
		<caption class="listagem">Cadastrar Novo Docente</caption>
	
		<tbody>

			<tr>			
				<th width="15%"> Projeto: </th>
				<td>
					<b><h:outputText value="#{alterarEquipeDocente.projeto.anoTitulo}" /></b>
				</td>
			</tr>

			<tr>
				<th width="15%"> Período: </th>
				<td>
					<b><h:outputText value="#{alterarEquipeDocente.projeto.projeto.dataInicio}" /> a <h:outputText value="#{alterarEquipeDocente.projeto.projeto.dataFim}" /></b>
				</td>
			</tr>


			<tr>
				<th width="15%"> Centro: </th>
				<td>
					<b><h:outputText value="#{alterarEquipeDocente.projeto.unidade.nome}" /></b>
				</td>
			</tr>

			<tr>
				<th width="15%"> Coordenador(a): </th>
				<td>
					<b><h:outputText value="#{alterarEquipeDocente.projeto.coordenacao.pessoa.nome}" /></b>
				</td>
			</tr>


			
			<tr>			
				<td colspan="2">
				
						<h:form id="formIncluirDocentes">
							<table class="subFormulario" width="100%">
								<caption class="listagem">Seleção do Docente</caption>
								
								<tr>
									<td class="required" width="20%">Docente:</td>
									<td >
										<h:inputHidden id="id" value="#{alterarEquipeDocente.obj.servidor.id}"></h:inputHidden>
										<h:inputText id="nome"	value="#{alterarEquipeDocente.obj.servidor.pessoa.nome}" size="60" />
						
										 <ajax:autocomplete
											source="formIncluirDocentes:nome" target="formIncluirDocentes:id"
											baseUrl="/sigaa/ajaxDocente" className="autocomplete"
											indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn,situacao=ativo"
											parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
											style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>

										<input type="hidden" name="idServidor" id="idServidor" />
										<ufrn:help img="/img/ajuda.gif">Apenas os docentes do Quadro Permanente da ${ configSistema['siglaInstituicao'] } serão listados</ufrn:help>
									</td>
								</tr>

								<tr>
									<td width="20%" class="required">Data Entrada no Projeto:</td>
									<td>
										<t:inputCalendar id="dataEntradaProjeto" size="10" value="#{alterarEquipeDocente.obj.dataEntradaProjeto}" 
											renderAsPopup="true" renderPopupButtonAsImage="true" onkeypress="return(formataData(this,event))"  maxlength="10"	/>
									</td>
								</tr>
								
								<tr>
									<td width="20%" class="required">Data Saída do Projeto:</td>
									<td>
										<t:inputCalendar id="dataSaidaProjeto" size="10" value="#{alterarEquipeDocente.obj.dataSaidaProjeto}" 
										renderAsPopup="true" renderPopupButtonAsImage="true" onkeypress="return(formataData(this,event))"  maxlength="10"/>
										
										
									</td>
								</tr>						
								
								<tr>
									<td colspan="2" class="subFormulario">Componentes Curriculares</td>
								</tr>
								
								
								<tr>
									<td colspan="2">
											<t:dataTable value="#{alterarEquipeDocente.projeto.componentesCurriculares}" var="comp" align="center" width="100%" id="compo" rowClasses="linhaImpar,linhaPar"  >
													<t:column>
														<f:facet name="header">
															<f:verbatim>Lista de Componentes Curriculares do Projeto</f:verbatim>
														</f:facet>					
														<h:selectBooleanCheckbox id="selecionaComp" value="#{comp.selecionado}" styleClass="noborder" />									
														<h:outputText value="#{comp.disciplina.codigoNome}" />
													</t:column>
											</t:dataTable>
										<br/>
									</td>
								</tr>
								
								<tr>
									<td colspan="2" class="subFormulario">Orientações para este docente</td>
								</tr>
								
								<tr>
									<td colspan="2">
											<t:dataTable value="#{alterarEquipeDocente.projeto.discentesExecutandoProjeto}" var="dm" align="center" width="100%" id="dis" rowClasses="linhaImpar,linhaPar"  >
													<t:column rendered="#{dm.ativo}">
														<f:facet name="header">
															<f:verbatim>Lista de Discentes Ativos do Projeto</f:verbatim>
														</f:facet>					
														<h:selectBooleanCheckbox value="#{dm.selecionado}" styleClass="noborder" id="compCurricular" rendered="#{dm.ativo}"/>									
														<h:outputText value="#{dm.discente.matriculaNome}" rendered="#{dm.ativo}"/> 
													</t:column>
													
													<t:column>
														<f:facet name="header">
															<f:verbatim>Início da Orientação</f:verbatim>
														</f:facet>
														<t:inputCalendar
															size="10"
															maxlength="10"
															value="#{dm.dataInicioOrientacao}"
															popupDateFormat="dd/MM/yyyy"
															renderAsPopup="true"
															onkeypress="return(formataData(this,event))"
															renderPopupButtonAsImage="true">
															<f:converter converterId="convertData"/>
														</t:inputCalendar>																				
													</t:column>
								
													<t:column>
														<f:facet name="header">
															<f:verbatim>Fim da Orientação</f:verbatim>
														</f:facet>
														<t:inputCalendar 
															size="10"
															maxlength="10"
															value="#{dm.dataFimOrientacao}"
															popupDateFormat="dd/MM/yyyy"
															onkeypress="return(formataData(this,event))"
															renderAsPopup="true"
															renderPopupButtonAsImage="true">
															<f:converter converterId="convertData"/>
														</t:inputCalendar>										
													</t:column>
											</t:dataTable>
										<br/>
									</td>
								</tr>
								
														
								
								<tfoot>
									<tr>
										<td colspan="2" align="center">	
											<center>
												<h:commandButton	action="#{alterarEquipeDocente.novoEquipeDocente}" value="Confirmar Cadastro"/>
												<h:commandButton	action="#{alterarEquipeDocente.cancelar}" value="Cancelar"/>
											</center>											
										</td>		
									</tr>
								</tfoot>
								
							</table>								
						</h:form>
				</td>
			</tr>
		</tbody>		

   </table>
   <div class="obrigatorio">Campos de preenchimento obrigatório.</div>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
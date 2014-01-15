<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<a4j:keepAlive beanName="objetivoMBean" />
<h:messages showDetail="true"></h:messages>

<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
<%@include file="/portais/docente/menu_docente.jsp"%>

	<h2><ufrn:subSistema /> > Objetivos & Resultados Esperados</h2>

	<h:form id="frmobjetivos">
	
			<table class="formulario" width="100%">
				<caption class="listagem">Objetivos Específicos / Atividades Realizadas</caption>
	
				<tr>
					<td>
						<table width="100%">
	 						
							<tr> 
								<td>
									<i>	Objetivos do Projeto:</i><span class="required"></span><br/>
									<h:inputTextarea id="objetivo" value="#{objetivoMBean.obj.objetivo}" rows="4" style="width: 98%"/>
								</td>
							</tr>
							<tr>
								<td>
									<h:panelGroup id="atividade">						
										<table width="100%">
											<tr>
												<td colspan="4" class="subFormulario"> Atividades Vinculadas ao Objetivo </td>
											</tr>
										
											<tr>
												<th class="obrigatorio"> Descrição das Atividades: </th>
												<td> <h:inputText id="atividades" value="#{objetivoMBean.objetivoAtividades.descricao}" size="80"/> </td>
											</tr>
											<tr>
												<th class="obrigatorio">Carga horária:</th>
												<td> <h:inputText id="cargaHoraria" value="#{objetivoMBean.objetivoAtividades.cargaHoraria}" 
													size="3" onkeyup="return formatarInteiro(this)"/>h </td>
											</tr>
											<tr>
												<th class="obrigatorio">Período</th>
												<td>
													<t:inputCalendar id="dataInicio" value="#{objetivoMBean.objetivoAtividades.dataInicio}" renderAsPopup="true" 
													renderPopupButtonAsImage="true" size="10" popupDateFormat="dd/MM/yyyy" onkeypress="return(formataData(this,event))" maxlength="10"/>
													a 
													<t:inputCalendar  id="dataFim" value="#{objetivoMBean.objetivoAtividades.dataFim}" renderAsPopup="true" 
													renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formataData(this,event))" maxlength="10"/>
												</td>
											</tr>
	
											<tr>
												<td colspan="4" class="subFormulario"> Membros da Atividade </td>
											</tr>
											<tr>
												<th class="obrigatorio">Membro:</th>
												<td>
													<h:selectOneMenu id="membroEquipeDocente" value="#{objetivoMBean.objetivoAtividades.membroAtividade.membroProjeto.id}">
														<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
														<f:selectItems value="#{ atividadeExtensao.allMembrosProjeto }" />
													</h:selectOneMenu>
												</td>
											</tr>
											<tr>
												<th class="obrigatorio">Carga horária Membro:</th>
												<td> <h:inputText id="cargaHorariaMembro" value="#{ objetivoMBean.objetivoAtividades.membroAtividade.cargaHoraria }" 
													size="3" onkeyup="return formatarInteiro(this)"/>h </td>
											</tr>
											
											<tr>
												<td></td>
												<td>
													<div class="infoAltRem">
														<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Membro
													</div>
													<t:dataTable id="membros" value="#{ objetivoMBean.objetivoAtividades.membrosAtividade }" var="membro" align="center" width="100%" 
														styleClass="listagem" rowClasses="linhaPar, linhaImpar">
														<t:column>
															<f:facet name="header">
															<f:verbatim>Membro Projeto</f:verbatim>
															</f:facet>
															<h:outputText value="#{ membro.membroProjeto.pessoa.nome }" />
														</t:column>
														<t:column>
															<f:facet name="header">
															<f:verbatim>Carga Horária</f:verbatim>
															</f:facet>
															<h:outputText value="#{ membro.cargaHoraria }" id="chMembro"/>
														</t:column>
														<t:column width="5%" styleClass="centerAlign">
															<a4j:commandButton image="/img/delete.gif" actionListener="#{objetivoMBean.removeMembro}"  
																title="Remover Membro" reRender="membros">
																<f:attribute name="idMembro" value="#{membro.membroProjeto.id}"/>
																<f:attribute name="idMembroAtividade" value="#{membro.id}"/>
															</a4j:commandButton>
														</t:column>
													</t:dataTable>
												</td>
											</tr>
											
											<tfoot>
												<tr>
	                                            	<td colspan="3" style="text-align: center;">
	                                            		<a4j:commandButton value="Adicionar Membro" reRender="membros, membroEquipeDocente, cargaHorariaMembro" 
	                                            			action="#{objetivoMBean.adicionarMembro}" alt="Adicionar Membro" title="Adicionar Membro"/>
	                                           			<a4j:commandButton value="Adicionar Atividade" reRender="membros, out,atividades, dataInicio, dataFim, ajaxErros, 
	                                           				cargaHorariaMembro, membroEquipeDocente, cargaHoraria" action="#{objetivoMBean.adicionarObjetivoAtividades}"/>
	                                            	</td>   
												</tr>
											</tfoot>
											
										</table>
									</h:panelGroup>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			
			<br />
			
			<h:panelGroup id="out">
			
				<div class="infoAltRem">
					<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Atividade
		    		<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Atividade
				</div>
			
				
				<table class="formulario" width="100%">
					<caption class="listagem"> Atividades Cadastradas </caption>
						<c:forEach items="#{ objetivoMBean.obj.atividadesPrincipais }" var="_atividade">
							<thead>
								<tr>
									<td> Atividade </td>
									<td width="30%" style="text-align: center;"> Período </td>
									<td width="10%" style="text-align: center;"> Carga Horária </td>
									<td width="3%" style="text-align: center;">
										<a4j:commandButton image="/img/alterar.gif" actionListener="#{ objetivoMBean.alterarAtividade }"  
											title="Alterar Atividade" reRender="atividade, out">
												<f:attribute name="posicao" value="#{ _atividade.posicao }"/>
												<f:attribute name="idAtividade" value="#{ _atividade.id }"/>
										</a4j:commandButton>
									</td>
									<td width="3%" style="text-align: center;">
										<a4j:commandButton image="/img/delete.gif" actionListener="#{objetivoMBean.removerObjetivoAtividades}"  
											title="Remover Atividade" reRender="out">
												<f:attribute name="posicao" value="#{_atividade.posicao}"/>
												<f:attribute name="idAtividade" value="#{_atividade.id}"/>
										</a4j:commandButton>
									</td>					
								</tr>
							</thead>
	
							<tr>
								<td> 
									<h:outputText value="#{_atividade.descricao}" id="descricaoAtividade" />
								</td>	
								<td style="text-align: center;">
									<h:outputText value="#{_atividade.dataInicio}">  
									  <f:convertDateTime pattern="dd/MM/yyyy"/>  
									</h:outputText>
									a
									<h:outputText value="#{_atividade.dataFim}">  
									  <f:convertDateTime pattern="dd/MM/yyyy"/>  
									</h:outputText>
								</td>	
								<td width="10%" style="text-align: center;">
									<h:outputText value="#{_atividade.cargaHoraria}" id="cargaHorariaAtividade" />h
								</td>	
								<td colspan="2"></td>
							</tr>
	
							<thead>
								<tr>
									<td colspan="2"> Membro Atividade </td>
									<td> Carga horária </td>
									<td colspan="2"> </td>
								</tr>
							</thead>
							
							<c:forEach items="#{ _atividade.membrosAtividade }" var="_membroAtividade" varStatus="indice">
								<tr class="${indice.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td colspan="2">
										<h:outputText value="#{ _membroAtividade.membroProjeto.pessoa.nome }" />
									</td>
									<td width="10%" style="text-align: center;">
										<h:outputText value="#{ _membroAtividade.cargaHoraria }" />h
									</td>
									<td colspan="2"></td>
								</tr>								
							</c:forEach>
							
							<tr>
								<td style="padding-top: 15px; padding-bottom: 15px;" colspan="6">
									<hr>
								</td>
							</tr>
							
						</c:forEach>
					   <tfoot>
							<tr>
								<td colspan="6" align="center">
									<h:panelGroup id="botoes">
										<h:commandButton action="#{objetivoMBean.voltarObjetivo}" value="<< Voltar" id="voltarObjetivos" />
									</h:panelGroup>
								</td>
							</tr>
					   </tfoot>
					</table>
			</h:panelGroup>
		</h:form>
		
	<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center><br/>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
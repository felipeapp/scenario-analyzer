<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
<%@include file="/portais/docente/menu_docente.jsp"%>

<h:panelGroup id="ajaxErros">
	<h:dataTable  value="#{atividadeExtensao.avisosAjax}" var="msg" rendered="#{not empty atividadeExtensao.avisosAjax}">
		<t:column><h:outputText value="<div id='painel-erros' style='position: relative; padding-bottom: 10px;'><ul class='erros'><li>#{msg.mensagem}</li></ul></div>" escape="false"/></t:column>
	</h:dataTable>
</h:panelGroup>

<a4j:keepAlive beanName="cadatraObjetivosExtensaoMBean" />

<h2><ufrn:subSistema /> > Objetivos e Resultados Esperados</h2>

	<h:form id="formObjetivos">

		<input type="hidden" name="idObjetivo" value="0" id="idObjetivo"/>
		<input type="hidden" name="id" value="0" id="id"/>
		
		<div class="infoAltRem">
			<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>
			<h:commandLink action="#{ objetivoMBean.cadastrarObjetivo }" value="Cadastrar Objetivo" />
			<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Objetivo do Programa
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Objetivo do Programa
		</div>

		<table class="formulario" style="width: 100%;">
			<caption class="listagem">Lista de Objetivos e Resultados Esperados Cadastrados</caption>

				<c:if  test="${not empty atividadeExtensao.objetivos}">
					<c:forEach items="#{ atividadeExtensao.objetivos }" var="objetivo" varStatus="status">
							<c:if test="${objetivo.id > 0 }">
								<tr>
									<td colspan="2" align="right" class="subFormulario" ><h:outputText value="#{objetivo.objetivo}" /></td>
									<td class="subFormulario" width="2%" nowrap="nowrap">
										
										<h:commandButton image="/img/alterar.gif" action="#{objetivoMBean.alterarObjetivo}"
											alt="Alterar Objetivo do Programa" title="Alterar Objetivo do Programa" 
											onclick="$(idObjetivo).value=#{objetivo.id};"
											rendered="#{objetivo.id > 0 }" />
										&nbsp;
										<h:commandButton image="/img/delete.gif" action="#{ objetivoMBean.inativarObjetivo }"
											alt="Remover Objetivo do Programa" title="Remover Objetivo do Programa" 
											onclick="$(idObjetivo).value=#{objetivo.id};return confirm('Deseja Remover este Objetivo da Ação de Extensão?')"
											rendered="#{objetivo.id > 0 }" />
									</td>
								</tr>
							
								<c:if  test="${not empty objetivo.atividadesPrincipais}">
									<tr>
										<td colspan="3">
											<table style="width: 100%;" id="tbAtividades">
												<tbody>
													<c:forEach items="#{objetivo.atividadesPrincipais}" var="atividade" varStatus="st1">
														<tr>
															<td colspan="2" class="subFormulario">									
																<b>Atividades Relacionadas:</b>
															</td>
															<td class="subFormulario">									
																<b>Período Realização:</b>
															</td>
															<td class="subFormulario" style="text-align: center;">									
																<b>Carga Horária:</b>
															</td>
														</tr>
	
														<tr>
															<td colspan="2">
																${st1.index + 1}. <h:outputText value="#{atividade.descricao}" />
															</td>
															<td>	
																<h:outputText value="#{atividade.dataInicio}" id="dataInicioAtividade"><f:convertDateTime pattern="dd/MM/yyyy"  /></h:outputText>
																	<c:if test="${not empty atividade.dataFim}">
																		&nbsp; a &nbsp; 
																	</c:if> 
																<h:outputText value="#{atividade.dataFim}" id="dataFimAtividade"><f:convertDateTime pattern="dd/MM/yyyy"  /></h:outputText>						
															</td>
															<td style="text-align: center;">
																<h:outputText value="#{ atividade.cargaHoraria }" /> h
															</td>
														</tr>		
														<tr>
															<td colspan="4" class="subFormulario">									
																<b>Participantes Relacionados:</b>
															</td>
														</tr>
														<c:if  test="${not empty atividade.membrosAtividade}">
															<tr>
																<c:forEach items="#{ atividade.membrosAtividade }" var="_membro" varStatus="st2">
																	<tr>
																		<td colspan="2">
																			${st2.index + 1}. <h:outputText value="#{ _membro.membroProjeto.pessoa.nome }" />
																		</td>
																		<td></td>
																		<td style="text-align: center;">	
																			<h:outputText value="#{ _membro.cargaHoraria }" /> h
																		</td>
																	</tr>		
																</c:forEach>
															</tr>
														</c:if>
														<tr>
															<td colspan="4">
																<hr>
															</td>
														</tr>
													</c:forEach>
											</table>
										</td>
									</tr>
								</c:if>
								
								<tr>
									<td colspan="3"><br/></td>
								</tr>
							</c:if>
					   </c:forEach>
					</c:if>

					<h:outputText value="<tr><td><center><font color='red'>Lista de objetivos vazia</font></center></td></tr>" rendered="#{empty atividadeExtensao.objetivos}" escape="false"/>				

			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Alterar Objetivos" action="#{objetivoMBean.alterarObjetivos}" id="avancar" />
						<h:commandButton value="Cancelar" action="#{objetivoMBean.cancelar}" id="cancelar"  onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
			</table>

		</h:form>
		<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center><br/>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
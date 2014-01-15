
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>


<f:view>
	<a4j:keepAlive beanName="importacaoDadosTurma" />
	<a4j:keepAlive beanName="planoCurso" />

	<h:form prependId="true">
	
		<h2><ufrn:subSistema/> &gt; <h:commandLink value="Plano de Curso" action="#{planoCurso.gerenciarPlanoCurso}" /> &gt; Importação de Dados de Turmas Anteriores</h2>
		
		<div class="descricaoOperacao">
			<p>Nesta tela, é possível selecionar que informações deseja importar para a nova turma. Selecione o tipo de conteúdo e, em seguida, selecione qual conteúdo específico deseja importar.</p>
			<p>Os tópicos de aula são ordenados pela data na turma virtual. Caso as datas não forem especificadas eles poderão aparecer desordenados.</p>
		</div>
		
			<style>
				.check {
					width:20px;
				}
				
				.tipo {
					width:100px;
				}
			</style>
		
			<table class="formulario" style="width:90%;">
			
				<caption>Selecione os conteúdos a importar</caption>
			
				<tbody>
				<%-- Plano de Curso --%>
				<tr><td>
				<a4j:region>
					<table class="subformulario" style="width:100%;">
						<caption>
							<h:selectBooleanCheckbox value="#{ importacaoDadosTurma.object.planoCurso }" id="planoCurso">
								<a4j:support event="onclick" actionListener="#{importacaoDadosTurma.prepararPlanoCurso}" reRender="panelPlanoCurso" />
							</h:selectBooleanCheckbox>&nbsp; Plano de Curso
							<a4j:status>
								<f:facet name="start">
									<h:graphicImage value="/img/indicator.gif" />
								</f:facet>
							</a4j:status>
						</caption>
						
						<tbody>
							<tr>
								<td>
									<style>
									
										.sup {
											background-color:#ECF4FE;
											font-weight:bold;
											border:1px solid #C0C0C0;
											width:99%;
											padding:4px;
											margin-top:5px;
										}
										.inf {
											background-color:#FFFFFF;
											border:1px solid #C0C0C0;
											width:99%;
											padding:4px;
											border-top:none
										}
									</style>
									
									<a4j:outputPanel id="panelPlanoCurso">
										<c:if test="${importacaoDadosTurma.object.planoCurso == true and empty importacaoDadosTurma.plano}">
											<div style="text-align:center;font-weight:bold;color:#FF0000;">Não há Plano de Curso cadastrado para a turma selecionada.</div>
										</c:if>
										<c:if test="${importacaoDadosTurma.object.planoCurso == true and not empty importacaoDadosTurma.plano}">
											<div class="sup">
													<h:outputText value="Metodologia" />
											</div>
											<div class="inf">
													<h:outputText value="#{importacaoDadosTurma.plano.metodologia}" />
											</div>
											<div class="sup">
													<h:outputText value="Procedimento de Avaliação da Apredizagem" /> 
											</div>	
											<div class="inf">
													<h:outputText value="#{importacaoDadosTurma.plano.procedimentoAvalicao}" />
											</div>
										</c:if>
									</a4j:outputPanel>
								</td>
							</tr>
						</tbody>
					</table>
				</a4j:region>
				</td></tr>
				
				<%-- Tópicos de Aulas --%>
				<tr><td>
				<a4j:region>
					<table class="subformulario" style="width:100%;">
						<caption>
							<h:selectBooleanCheckbox value="#{ importacaoDadosTurma.object.topicosDeAula }" id="topicosAula">
								<a4j:support event="onclick" actionListener="#{importacaoDadosTurma.preparaTopicosDeAula}" reRender="panelTopicosAula" />
							</h:selectBooleanCheckbox>&nbsp; Aulas
							<a4j:status>
								<f:facet name="start">
									<h:graphicImage value="/img/indicator.gif" />
								</f:facet>
							</a4j:status>
						</caption>
						
						<tbody>
							<tr>
								<td>
									<a4j:outputPanel id="panelTopicosAula">
										<c:if test="${importacaoDadosTurma.object.topicosDeAula == true and not empty importacaoDadosTurma.topicosDeAula}">
											<rich:dataTable columnClasses="check,descricao" var="ta" value="#{importacaoDadosTurma.topicosDeAula}" style="width:100%;">
												<rich:column>
												<f:facet name="header"></f:facet>
													<h:selectBooleanCheckbox id="checkboxAulas" value="#{ta.selecionado}" />
												</rich:column>
												<rich:column>
													<f:facet name="header"><h:outputText value="Data Inicial"/></f:facet>
													<h:selectOneMenu value="#{ta.dataInicio}" id="inicio">	   
														<f:selectItems value="#{topicoAula.aulasCombo}" />
													</h:selectOneMenu>
												</rich:column>
												
												<rich:column>
													<f:facet name="header"><h:outputText value="Data Final"/></f:facet>
													<h:selectOneMenu value="#{ta.dataFim}" id="fim">			    
														<f:selectItems value="#{topicoAula.aulasCombo}" />
													</h:selectOneMenu>
												</rich:column>
												<rich:column>
												<f:facet name="header"><h:outputText value="Descrição"/></f:facet>
													<div style="width:500px;overflow:hidden;"><h:outputText value="#{ta.descricao}" /></div>
												</rich:column>
											</rich:dataTable>
										</c:if>
										<c:if test="${importacaoDadosTurma.object.topicosDeAula == true and empty importacaoDadosTurma.topicosDeAula}">
											<div style="text-align:center;font-weight:bold;color:#FF0000;">Não há aulas cadastradas para a turma selecionada.</div>
										</c:if>
									</a4j:outputPanel>
								</td>
							</tr>
						</table>
					</a4j:region>
					</td></tr>
					
					<%-- Referências --%>
					<tr><td>
						<a4j:region>
							<table class="subformulario" style="width:100%;">
								<caption>
									<h:selectBooleanCheckbox value="#{ importacaoDadosTurma.object.referencias }" id="referencias">
										<a4j:support event="onclick" actionListener="#{importacaoDadosTurma.preparaReferencias}" reRender="panelReferencias" status="statusR" />
									</h:selectBooleanCheckbox> &nbsp; Indicações de Referências
									
									<a4j:status id="statusR">
										<f:facet name="start">
											<h:graphicImage value="/img/indicator.gif" />
										</f:facet>
									</a4j:status>
								</caption>
							
								<tr><td>
								<a4j:outputPanel id="panelReferencias">
									<c:if test="${importacaoDadosTurma.object.referencias == true and not empty importacaoDadosTurma.referencias}">
										<rich:dataTable columnClasses="check,tipo,descricao" var="r" value="#{importacaoDadosTurma.referencias}" style="width:100%;">
											<rich:column>
												<h:selectBooleanCheckbox id="checkboxReferencias" value="#{r.selecionada}" />
											</rich:column>
											<rich:column>
												<h:outputText value="#{r.tipoDesc}" />
											</rich:column>
											<rich:column>
												<h:outputText value="#{r.descricao}" />
											</rich:column>
												 
											</li>
										</rich:dataTable>
									</c:if>
									<c:if test="${importacaoDadosTurma.object.referencias == true and empty importacaoDadosTurma.referencias}">
												<div style="text-align:center;font-weight:bold;color:#FF0000;">Não há indicações de referências cadastradas para a turma selecionada.</div>
											</c:if>
								</a4j:outputPanel>
								</td></tr>
							</table>
						</a4j:region>
					</td></tr>
				</tbody>
				<tfoot>
					<tr>
						<td>
							<h:commandButton value="Importar" action="#{ importacaoDadosTurma.importar }"/>
							<h:commandButton value="<< Voltar" action="#{planoCurso.iniciarImportacaoDados}" />
						</td>
					</tr>
				</tfoot>
				
			</table>		
		
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

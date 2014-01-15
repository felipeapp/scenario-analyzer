
<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form id="formImportacao">


<fieldset>
<legend>Importação de Dados de Turmas Anteriores</legend>

<div class="descricaoOperacao">

	<p>Em breve, as outras opções de importação serão disponibilizadas.</p>
	<br/>
	<p>Os tópicos de aula são ordenados pela data na turma virtual. Caso as datas não forem especificadas eles poderão aparecer desordenados.</p>
	<%-- Selecione o que você deseja importar da turma anterior. --%>
</div>

	<table class="formulario" style="width:80%;">
			
		<caption>Selecione os conteúdos a importar</caption>
			
				<%-- Tópicos de Aulas --%>
		<tbody>
				<tr><td>
				<a4j:region>
					<table class="subformulario" style="width:100%;">
						<caption>
								<h:selectBooleanCheckbox value="true" id="topicosAula" onclick="selecionarTodos(this)"/>&nbsp; Aulas
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
										<c:if test="${ not empty importacaoDadosTurma.topicosDeAula}">
											<rich:dataTable id="tableImportacao" columnClasses="check,descricao" var="ta" value="#{importacaoDadosTurma.topicosDeAula}" style="width:100%;">
														
												<rich:column>
													<f:facet name="header"></f:facet>
													<h:selectBooleanCheckbox id="checkboxAulas" value="#{ta.selecionado}" onclick="mudarCor(this)"/>
												</rich:column>	
																																														
												<rich:column>
													<f:facet name="header"><h:outputText value="Título"/></f:facet>
													<h:outputText value="#{ta.descricao}" />
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
					
					<tr>
						<td>
							<a4j:region>
								<table class="subformulario" style="width:100%;">
									<caption>
									<h:selectBooleanCheckbox value="#{ importacaoDadosTurma.object.planoCurso }" id="plano" /> &nbsp;
									<h:outputText value="Plano de Curso"/>
									</caption>
								</table>
							</a4j:region>		
						</td>
					</tr>
					
					<tr>
						<td>
							<a4j:region>
								<table class="subformulario" style="width:100%;">
									<caption>
									<h:selectBooleanCheckbox value="#{ importacaoDadosTurma.object.conteudos }" id="conteudos" /> &nbsp;
									<h:outputText value="Conteúdos"/>
									</caption>
								</table>
							</a4j:region>		
						</td>
					</tr>
					
					<tr>
						<td>
							<a4j:region>
								<table class="subformulario" style="width:100%;">
									<caption>
										<h:selectBooleanCheckbox value="#{ importacaoDadosTurma.object.arquivos }" id="arquivos" /> &nbsp;
										<h:outputText value="Arquivos"/>
									</caption>
								</table>
							</a4j:region>		
						</td>
					</tr>
					
					<tr>
						<td>
							<a4j:region>
								<table class="subformulario" style="width:100%;">
									<caption>
										<h:selectBooleanCheckbox value="#{ importacaoDadosTurma.object.referencias }" id="referencias" /> &nbsp;
										<h:outputText value="Referências"/>	
									</caption>
								</table>
							</a4j:region>		
						</td>
					</tr>
					
					<tr>
						<td>
							<a4j:region>
								<table class="subformulario" style="width:100%;">
									<caption>
										<h:selectBooleanCheckbox value="#{ importacaoDadosTurma.object.videos }" id="videos" /> &nbsp;
										<h:outputText value="Vídeos"/>	
									</caption>
								</table>
							</a4j:region>		
						</td>
					</tr>
		</tbody>	
	</table>		
	
	<div class="botoes">
		<div class="form-actions">
		<h:commandButton value="Importar" action="#{ importacaoDadosTurma.importar }"/>
		</div>
		<div class="other-actions">
			<h:commandButton action="#{ importacaoDadosTurma.voltar }" value="<< Voltar"/> 
		</div>
	</div>

</fieldset>

<script type="text/javascript">

function mudarCor (elem) {
	var tdElement = elem.parentNode;
	var trElement = tdElement.parentNode;
	if ( elem.checked == false )
		trElement.style.cssText ="background-color:#FDF3E1";
	else
		trElement.style.cssText ="background-color:#FFF";
}

function selecionarTodos (elem) {

	var checked = true;
	if (elem != null)
		checked = elem.checked;
	
	var table = document.getElementById("formImportacao:tableImportacao");
	if  ( checked == false ) 
		var test = J(table.getElementsByTagName('tr')).each(
				function(item) {
	
					if ( item != 0 ){
						this.style.cssText ="background-color:#FDF3E1";
						var checkBox = J(this).find("input")[0];
						checkBox.checked = false;
					}	
				}
			);
	else
		J(table.getElementsByTagName('tr')).each(
				function(item) {
	
					if ( item != 0 ){
						this.style.cssText ="background-color:#FFF";
						var checkBox = J(this).find("input")[0];
						checkBox.checked = true;
					}	
				}
			);
}

selecionarTodos(null);

</script>	

</h:form>
</f:view>
<%@include file="/ava/rodape.jsp"%>

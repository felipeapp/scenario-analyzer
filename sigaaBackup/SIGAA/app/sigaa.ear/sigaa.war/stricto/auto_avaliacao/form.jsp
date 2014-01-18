<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
tinyMCE.init({
	mode : "textareas", theme : "advanced", width : "500", height : "250", language : "pt",
	theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
	theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
	theme_advanced_buttons3 : "",
	plugins : "searchreplace,contextmenu,advimage",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left"
});
</script>

<f:view>
	<a4j:keepAlive beanName="calendarioAplicacaoAutoAvaliacaoMBean" />
		
	<h2> <ufrn:subSistema /> &gt; Calendário de Aplicação da Auto Avaliação ${ calendarioAplicacaoAutoAvaliacaoMBean.nivelDescricao }</h2>
	
	<div class="descricaoOperacao">
		<p> <b>	Caro usuário, </b> </p>
		<p>
			Você poderá definir os parâmetros da Auto Avaliação, bem como o período de aplicação da mesma.
		</p> 	
	</div>
	
	<h:form id="form">
	<table class="formulario" >
		<caption>Dados da Auto Avaliação</caption>
		<tbody>
			<tr>
				<th class="required">Questionário:</th>
				<td>
					<h:selectOneMenu value="#{ calendarioAplicacaoAutoAvaliacaoMBean.obj.questionario.id }" id="idQuestionario">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{ calendarioAplicacaoAutoAvaliacaoMBean.questionariosCombo }"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required">Período de Aplicação:</th>
				<td>
					de 
					<t:inputCalendar id="dataInicio" renderAsPopup="true"
						renderPopupButtonAsImage="true" size="10" maxlength="10"
						onkeypress="return formataData(this,event)"
						readonly="#{calendarioAplicacaoAutoAvaliacaoMBean.readOnly}"
						disabled="#{calendarioAplicacaoAutoAvaliacaoMBean.readOnly}" 
						popupDateFormat="dd/MM/yyyy"
						value="#{calendarioAplicacaoAutoAvaliacaoMBean.obj.dataInicio}" 
						title="Data inicial">
							<f:converter converterId="convertData"/>
					</t:inputCalendar>
					a
					<t:inputCalendar id="dataFim" renderAsPopup="true"
						renderPopupButtonAsImage="true" size="10" maxlength="10"
						onkeypress="return formataData(this,event)"
						readonly="#{calendarioAplicacaoAutoAvaliacaoMBean.readOnly}"
						disabled="#{calendarioAplicacaoAutoAvaliacaoMBean.readOnly}" 
						popupDateFormat="dd/MM/yyyy"
						value="#{calendarioAplicacaoAutoAvaliacaoMBean.obj.dataFim}" 
						title="Data final">
							<f:converter converterId="convertData"/>
					</t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th class="required">Instruções Gerais:</th>
				<td>
					<h:inputTextarea rows="4" cols="60" value="#{ calendarioAplicacaoAutoAvaliacaoMBean.obj.instrucoesGerais }" id="instrucoesGerais"/>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario">
					<h:outputText value="Programas" rendered="#{ calendarioAplicacaoAutoAvaliacaoMBean.portalPpg }"/>
					<h:outputText value="Cursos" rendered="#{ !calendarioAplicacaoAutoAvaliacaoMBean.portalPpg }"/> 
					Aplicáveis ao Questionário
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<table class="formulario" width="100%">
						<tr>
							<th>
								<h:selectBooleanCheckbox value="#{ calendarioAplicacaoAutoAvaliacaoMBean.obj.aplicavelATodos }" id="aplicavelATodos"
									onchange="submit();" onclick="submit();"/>
							</th>
							<td>
								Este questionário é aplicável à todos os 
								<h:outputText value="Programas de Pós Graduação" rendered="#{ calendarioAplicacaoAutoAvaliacaoMBean.portalPpg }"/>
								<h:outputText value="Cursos de Lato Sensu" rendered="#{ !calendarioAplicacaoAutoAvaliacaoMBean.portalPpg }"/>
							</td>
							<td>
							</td>
						</tr>
						<c:if test="${ !calendarioAplicacaoAutoAvaliacaoMBean.obj.aplicavelATodos }">
							<tr>
								<td colspan="2">
								<div class="infoAltRem" style="width: 100%">
									<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>: Adicionar Programa
									<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Programa
								</div>
								</td>
							</tr>
							<c:if test="${ calendarioAplicacaoAutoAvaliacaoMBean.portalPpg }">
								<tr>
									<th>Programa:</th>
									<td>
										<h:selectOneMenu value="#{ calendarioAplicacaoAutoAvaliacaoMBean.programa.id }" 
											disabled="#{calendarioAplicacaoAutoAvaliacaoMBean.readOnly}" id="programa">
											<f:selectItems value="#{ unidade.allProgramaPosCombo }"/>
										</h:selectOneMenu>
									</td>
									<td>
										<h:commandLink action="#{ calendarioAplicacaoAutoAvaliacaoMBean.adicionaPrograma }" id="adicionaPrograma" title="Adicionar Programa">
											<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>
										</h:commandLink>
									</td>
								</tr>
								<c:if test="${ not empty calendarioAplicacaoAutoAvaliacaoMBean.obj.programas }">
									<c:forEach items="#{ calendarioAplicacaoAutoAvaliacaoMBean.obj.programas }" var="item">
									<tr>
										<td></td>
										<td>${ item.nome }</td>
										<td>
											<h:commandLink action="#{ calendarioAplicacaoAutoAvaliacaoMBean.removePrograma }" id="removePrograma" title="Remover Programa">
												<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>
												<f:param name="id" value="#{item.id}" />
											</h:commandLink>
										</td>
									</c:forEach>
								</c:if>
							</c:if>
							<c:if test="${ calendarioAplicacaoAutoAvaliacaoMBean.portalLatoSensu }">
								<tr>
									<th>Curso:</th>
									<td>
										<h:selectOneMenu value="#{ calendarioAplicacaoAutoAvaliacaoMBean.curso.id }" 
											disabled="#{calendarioAplicacaoAutoAvaliacaoMBean.readOnly}" id="curso">
											<f:selectItems value="#{ curso.allCursoEspecializacaoAceitoCombo }"/>
										</h:selectOneMenu>
									</td>
									<td>
										<h:commandLink action="#{ calendarioAplicacaoAutoAvaliacaoMBean.adicionaCurso }" id="adicionaCurso" title="Adicionar Curso">
											<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>
										</h:commandLink>
									</td>
								</tr>
								<c:if test="${ not empty calendarioAplicacaoAutoAvaliacaoMBean.obj.cursos }">
									<c:forEach items="#{ calendarioAplicacaoAutoAvaliacaoMBean.obj.cursos }" var="item">
									<tr>
										<td></td>
										<td>${ item.nome }</td>
										<td>
											<h:commandLink action="#{ calendarioAplicacaoAutoAvaliacaoMBean.removeCurso }" id="removeCurso" title="Remover Curso">
												<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>
												<f:param name="id" value="#{item.id}" />
											</h:commandLink>
										</td>
									</c:forEach>
								</c:if>
							</c:if>
						</c:if>
					</table>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2"> 
					<h:commandButton value="#{calendarioAplicacaoAutoAvaliacaoMBean.confirmButton}" action="#{calendarioAplicacaoAutoAvaliacaoMBean.cadastrar}" id="cadastrar"/> 
					<h:commandButton value="Cancelar" action="#{calendarioAplicacaoAutoAvaliacaoMBean.listar}" immediate="true" onclick="#{confirm}" id="listar"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>	

	<br/>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	
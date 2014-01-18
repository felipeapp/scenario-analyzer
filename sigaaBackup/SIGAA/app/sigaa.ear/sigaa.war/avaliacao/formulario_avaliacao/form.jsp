<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
#conteudo h3 { text-align: center; font-size: 1.1em; padding: 10px 0; color: #444; font-style: italic; }
</style>
<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
tinyMCE.init({
	mode : "textareas", theme : "advanced", width: "100%", height: "200", language : "pt",
	theme_advanced_buttons1 : "cut,copy,paste,separator,undo,redo,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,fontselect,fontsizeselect,separator,forecolor,backcolor,separator,sub,sup,charmap",
	theme_advanced_buttons2 : "",
	theme_advanced_buttons3 : "",
	plugins : "searchreplace,contextmenu,advimage",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left"
});
</script>

<f:view>
	
	<h2 class="title"><ufrn:subSistema /> &gt; Formulário de Avaliação Institucional</h2>

	<div class="descricaoOperacao">
		<p> <strong>Caro Usuário,</strong> </p>
		<p>Você poderá cadastrar um formulário destinado à Avaliação Institucional, informando o perfil
		do usuário que irá preencher (Docente, Discente ou Tutor), se as perguntas são referentes ao ensino presencial ou ao ensino à distâcia.</p>
		<p>Para determinar o período de preenchimento do formulário, verique o cadastro do Calendário de Avaliação no menu principal.</p>
	</div>

	<h:form id="form">
	
	<table class="formulario" width="80%">
		<caption>Informe os dados do Formulário</caption>
		<tbody>
			<tr>
				<th class="required">Título do Formulário:</th>
				<td>
					<h:inputText value="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.obj.titulo }" id="titulo" size="40" maxlength="40" />
				</td>
			</tr>
			<tr>
				<th class="required">Tipo da Avaliação Institucional:</th>
				<td>
					<h:selectOneMenu value="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.obj.tipoAvaliacao }" id="tipoAvaliacao">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.tiposAvaliacao }"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th><h:selectBooleanCheckbox value="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.obj.ead }" id="avaliaTurmas"/></th>
				<td>Este formulário é de avaliação do Ensino à Distância (EAD)</td>
			</tr>
			<tr>
				<th><h:selectBooleanCheckbox value="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.obj.agrupaTurmas }" id="agrupaTurmas"/></th>
				<td>Agrupar as turmas por disciplina</td>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario">Instruções ao entrevistado</td>
			</tr>
			<tr>
				<td colspan="2">
					<h:inputTextarea value="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.obj.instrucoesGerais }" id="instrucoesGerais"/>
				</td>
			</tr>
		</tbody>
	</table>
	<c:if test="${not empty cadastrarFormularioAvaliacaoInstitucionalMBean.obj.grupoPerguntas}">
	   <rich:tabPanel switchType="client" selectedTab="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.abaSelecionada }">
			<c:forEach items="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.obj.grupoPerguntas }" var="grupo" >
      				<rich:tab label="#{ grupo.titulo }" >
      					<h3>
      						${ grupo.descricao }
	      					<h:commandLink action="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.alteraGrupo }" id="alteraGrupo">
	      						<h:graphicImage alt="Altera Grupo" title="Altera Grupo" value="/img/alterar.gif"/>
								<f:param name="grupo" value="#{ grupo.titulo }"/>
							</h:commandLink> 
							<h:commandLink action="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.removeGrupo }" id="removeGrupo" onclick="#{ confirmDelete }">
							<h:graphicImage alt="Remove Grupo" title="Remove Grupo" value="/img/delete.gif" />
								<f:param name="grupo" value="#{ grupo.titulo }"/>
							</h:commandLink>
						</h3> 
					<c:if test="${not empty grupo.perguntas }">
						<ol>
						<c:forEach items="#{ grupo.perguntas }" var="pergunta" >
							<li>
								${pergunta.descricao} 
								<h:commandLink action="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.alteraPergunta }" id="alteraPergunta">
									<h:graphicImage alt="Altera Pergunta" title="Altera Pergunta" value="/img/alterar.gif"/>
									<f:param name="grupo" value="#{ grupo.titulo }"/>
									<f:param name="ordem" value="#{ pergunta.ordem }"/>
								</h:commandLink>
								<h:commandLink action="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.removePergunta }" id="removePergunta" onclick="#{ confirmDelete }">
									<h:graphicImage alt="Remove Pergunta" title="Remove Pergunta" value="/img/delete.gif"/>
									<f:param name="grupo" value="#{ grupo.titulo }"/>
									<f:param name="ordem" value="#{ pergunta.ordem }"/>
								</h:commandLink>
								<c:if test="${ pergunta.tipoPergunta.multiplaEscolha || pergunta.tipoPergunta.unicaEscolha }">
									<ol type="a">
										<c:forEach items="#{ pergunta.alternativas }" var="alternativa" varStatus="alternativaStatus" >
											<li>${alternativa.descricao} <h:outputText value="(permite citação)" rendered="#{ alternativa.permiteCitacao }" /></li>
										</c:forEach>
									</ol>
								</c:if>
								<br/>
								(Tipo: ${pergunta.tipoPergunta.descricao}) <h:outputText value=" - Avalia Turmas " rendered="#{pergunta.avaliarTurmas }" />
							</li>
						</c:forEach>
						</ol>
					</c:if>
					<ul>
						<li>
							<h:commandLink value="Adicionar Pergunta" id="adicionarPergunta" action="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.formNovaPergunta }">
		      					<f:param name="grupo" value="#{ grupo.titulo }"/>
		      				</h:commandLink>
						</li>
					</ul>
				</rich:tab>
			</c:forEach>
		</rich:tabPanel>
	</c:if>
	<table class="formulario" width="100%">
		<tr>
			<td colspan="2" class="caixaCinza" style="text-align: center">
				<h:commandButton value="Adicionar Grupo de Pergunta" action="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.formGrupoPergunta }" id="novoGrupoPergunta" />
			</td>
		</tr>
		<tr>
			<th class="required" width="45%">Grupo de Perguntas que determina a média geral da Avaliação:</th>
			<td>
				<h:selectOneMenu value="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.hashGrupoMediaGeral }" id="grupoMediaGeral">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.grupoPerguntasCombo }"/>
					</h:selectOneMenu>
			</td>
		</tr>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Cancelar" action="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.cancelar }" id="cancelar" onclick="#{confirm }" />
					<h:commandButton value="Próximo Passo >>" action="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.submeterFormulario }" id="proximoPasso" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>

	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		<br /><br />
	</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

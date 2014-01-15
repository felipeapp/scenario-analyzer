<%@include file="/ava/cabecalho.jsp" %>
<f:view>
	<a4j:keepAlive beanName="questionarioTurma" />
	<%@include file="/ava/menu.jsp" %>
	
	<h:form id="formAva" prependId="false">
		<a4j:region>
		<fieldset>
			<legend>${ questionarioTurma.questionario.id == 0 ? "Novo " : "Alterar" } Questionário</legend>
			
			<div class="descricaoOperacao">
				<p>Prezado(a) docente,</p>
				
				<p> Este formulário permite a configuração de um questionário e a definição de suas questões. </p>
				
				<p> Seu cadastro é feito em duas etapas: Na primeira são informados os dados do questionário; na segunda são adicionadas as questões.</p>
			</div>
			
			<h:inputHidden value="#{ questionarioTurma.questionario.id }" />
			
			<table class="formAva" style="width:90%;">
				<tr>
					<td colspan="2"><h2>Dados do questionário</h2></td>
				</tr>
			
				<tr>
					<th class="required">Título:</th>
					<td><h:inputText value="#{ questionarioTurma.questionario.titulo }" style="width:400px;" /></td>
				</tr>
				<tr>
					<th style="vertical-align:top;">Descrição:</th>
					<td><h:inputTextarea styleClass="mceEditor" id="descricao" value="#{ questionarioTurma.questionario.descricao }" cols="10" rows="10" style="width:80%"/></td>
				</tr>
			
				<tr>
					<th class="required">Tópico de Aula:</th>
					<td>
						<h:selectOneMenu id="aula" value="#{ questionarioTurma.questionario.aula.id }" rendered="#{ not empty topicoAula.comboIdentado }">
							<f:selectItem itemValue="-1" itemLabel=" -- SELECIONE UM TÓPICO DE AULA -- "/>
							<f:selectItems value="#{topicoAula.comboIdentado}" />
						</h:selectOneMenu>
						<h:selectOneMenu id="semAula" value="#{ questionarioTurma.questionario.aula.id }" styleClass="sem-topicos-aula" rendered="#{ empty topicoAula.comboIdentado }">
							<f:selectItem itemLabel="Nenhum Tópico de Aula foi cadastrado" itemValue="0"/>
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<th class="required">Início:</th>
					<td>
						<span  style="position:absolute;margin-top:-5px;">
							<t:inputCalendar id="Inicio" value="#{questionarioTurma.questionario.inicio}" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" title="Data Inicial"> 
								<f:convertDateTime pattern="dd/MM/yyyy"/>
							</t:inputCalendar> as 
							<h:selectOneMenu value="#{ questionarioTurma.questionario.horaInicio }"> <f:selectItems value="#{ questionarioTurma.horas }" /></h:selectOneMenu> h
							<h:selectOneMenu value="#{ questionarioTurma.questionario.minutoInicio }"> <f:selectItems value="#{ questionarioTurma.minutos }" /></h:selectOneMenu> m
						</span>
					</td>
					
				</tr>
	
				<tr>
					<th class="required">Fim:</th>
					<td>
						<span  style="position:absolute;margin-top:-5px;">
							<t:inputCalendar id="Fim" value="#{questionarioTurma.questionario.fim}" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" title="Data Final">
								<f:convertDateTime pattern="dd/MM/yyyy"/>
							</t:inputCalendar> as 
							<h:selectOneMenu value="#{ questionarioTurma.questionario.horaFim }"> <f:selectItems value="#{ questionarioTurma.horas }" /></h:selectOneMenu> h
							<h:selectOneMenu value="#{ questionarioTurma.questionario.minutoFim }"> <f:selectItems value="#{ questionarioTurma.minutos }" /></h:selectOneMenu> m
						</span>
					</td>
				</tr>
	
				<tr>
					<th class="required">Fim da visualização:</th>
					<td>
						<span  style="position:absolute;margin-top:-5px;">
							<t:inputCalendar id="FimVisualizacao" value="#{questionarioTurma.questionario.fimVisualizacao}" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" title="Data Final de Visualização">
								<f:convertDateTime pattern="dd/MM/yyyy"/>
							</t:inputCalendar> as
							<h:selectOneMenu value="#{ questionarioTurma.questionario.horaFimVisualizacao }"> <f:selectItems value="#{ questionarioTurma.horas }" /></h:selectOneMenu> h
							<h:selectOneMenu value="#{ questionarioTurma.questionario.minutoFimVisualizacao }"> <f:selectItems value="#{ questionarioTurma.minutos }" /></h:selectOneMenu> m
							<span class="texto-ajuda">
								Após esta data o questionário não poderá ser visualizado pelos discentes.
							</span>
						</span>
					</td>
				</tr>
				 
				<tr>
					<th>Misturar perguntas?</th>
					<td class="separarLabels">
						<h:selectOneRadio value="#{ questionarioTurma.questionario.misturarPerguntas }" styleClass="inline">
							<f:selectItems value="#{ questionarioTurma.simNao }" />
						</h:selectOneRadio>
						<span class="texto-ajuda">
							Em caso afirmativo, a <b>ordem das questões</b> será mudada aleatoriamente a cada vez que um discente responder ao questionário.
						</span>
					</td>
				</tr>
				
				<tr>
					<th>Misturar alternativas?</th>
					<td class="separarLabels">
						<h:selectOneRadio value="#{ questionarioTurma.questionario.misturarAlternativas }" styleClass="inline">
							<f:selectItems value="#{ questionarioTurma.simNao }" />
						</h:selectOneRadio>
						<span class="texto-ajuda">
							Em caso afirmativo, a <b>ordem das alternativas</b> será mudada aleatoriamente a cada vez que um discente responder ao questionário.
						</span>					
					</td>
				</tr>
				
				<tr>
					<th class="required">Quantidade máxima de tentativas:</th>
					<td><h:inputText id="tentativas" value="#{ questionarioTurma.questionario.tentativas }" style="width:25px;" maxlength="3" onkeyup="exibeMedias(this);return formatarInteiro(this);" onblur="exibeMedias(this);return formatarInteiro(this);" /></td>
				</tr>
				
				
				<tr>
					<th class="required">Duração de cada tentativa:</th>
					<td><h:inputText id="Duracao" value="#{ questionarioTurma.questionario.duracao }" style="width:25px;" maxlength="3" onkeyup="return formatarInteiro(this);" onblur="return formatarInteiro(this);" /> minutos</td>
				</tr>

				<tr id="medias" >
					<th class="required">Média da Nota:</th>
					<td>
						<h:selectOneMenu value="#{questionarioTurma.questionario.mediaNotas}">
							<f:selectItems value="#{questionarioTurma.tiposMedia}" />
						</h:selectOneMenu>
					</td>
				</tr>

				<tr><td colspan="2"><h2>Feedbacks</h2></td></tr>
				
				<tr>
					<td colspan="2" style="padding-left:100px;">
						<b>Os alunos podem visualizar as respostas antes do prazo de entrega do questionário?</b>
						<div style="padding-top:4px;">
							<h:selectOneMenu value="#{questionarioTurma.questionario.visualizarRespostas}">
								<f:selectItems value="#{questionarioTurma.tiposVisualizacoesFeedback}" />
							</h:selectOneMenu>
						</div>
					</td>
				</tr>
				
				<tr>
					<td colspan="2" style="padding-left:100px;">
						<b>Os alunos podem visualizar o feedback de cada pergunta antes do prazo de entrega do questionário?</b>
						<div style="padding-top:4px;">
							<h:selectOneMenu value="#{questionarioTurma.questionario.visualizarFeedback}">
								<f:selectItems value="#{questionarioTurma.tiposVisualizacoesFeedback}" />
							</h:selectOneMenu>
						</div>
					</td>
				</tr>
				
				<tr>
					<td colspan="2" style="padding-left:100px;">
						<b>Os alunos podem visualizar o Feedback geral antes do prazo de entrega do questionário?</b>
						<div style="padding-top:4px;">
							<h:selectOneMenu value="#{questionarioTurma.questionario.visualizarFeedbackGeral}">
								<f:selectItems value="#{questionarioTurma.tiposVisualizacoesFeedback}" />
							</h:selectOneMenu>
						</div>
					</td>
				</tr>
				
				<tr>
					<td colspan="2" style="padding-left:100px;">
						<b>Os alunos podem visualizar as notas antes do prazo de entrega do questionário?</b>
						<div style="padding-top:4px;">
							<h:selectOneMenu value="#{questionarioTurma.questionario.visualizarNota}">
								<f:selectItems value="#{questionarioTurma.tiposVisualizacoesFeedback}" />
							</h:selectOneMenu>
						</div>
					</td>
				</tr>
								
			</table>
			
			<h2>Feedback Geral</h2>
			
			<div class="descricaoOperacao">
				<p>Defina abaixo quais mensagens os discentes receberão de acordo com a pontuação recebida no questionário. O texto que fica entre duas porcentagens será exibido quando o aluno recebe uma nota entre essas duas porcentagens.</p>
			</div>
			
			<div style="font-weight:bold;text-align:center;"><h:outputText value="#{ questionarioTurma.mensagemFeedback }" escape="false" id="mensagemFeedback" /></div>
			
			<table class="formulario" style="width:60%;">
				<caption>Novo Feedback</caption>
				<tr><td style="text-align:center;">
					Alunos que obtiverem acertos entre
						<h:selectOneMenu id="minFeedback" value="#{ questionarioTurma.minFeedback }" style="width:50px;">
							<f:selectItems value="#{ questionarioTurma.zeroACem }"/>
						</h:selectOneMenu>% e

						<h:selectOneMenu id="maxFeedback" value="#{ questionarioTurma.maxFeedback }" style="width:50px;">
							<f:selectItems value="#{ questionarioTurma.zeroACem }"/>
						</h:selectOneMenu>% receberão o seguinte feedback:<br />
				</td></tr>
				<tr><td style="text-align:center;">
					<h:inputTextarea id="txtFeedback" style="width:80%;height:100px;" value="#{ questionarioTurma.txtFeedback }"></h:inputTextarea>
				</td></tr>
				<tfoot>
					<tr>
						<td>
							<a4j:commandButton value="Adicionar Feedback" id="adicionarFeedback"
								actionListener="#{questionarioTurma.adicionarFeedback}" 
								reRender="mensagemFeedback, dataTableFeedbacks, txtFeedback, minFeedback, maxFeedback" />
						</td>
					</tr>
				</tfoot>
			</table><br/>
			
						
			<t:dataTable var="f" value="#{questionarioTurma.modelFeedbacks}" id="dataTableFeedbacks" 
				width="95%" rowClasses="linhaPar, linhaImpar">
				<t:column style="text-align:center;width:100px;">
					<f:facet name="header">
						<f:verbatim><p align="left"><h:outputText value="Porcentagens"/></p></f:verbatim>
					</f:facet>
				
					<f:verbatim><p align="left"><h:outputText value="#{ f.porcentagemMinima }% - #{ f.porcentagemMaxima }%" /></p></f:verbatim>
				</t:column>
				
				<t:column>
					<f:facet name="header">
						<f:verbatim><p align="left"><h:outputText value="Feedback"/></p></f:verbatim>
					</f:facet>
				
					<f:verbatim><p align="left"><h:outputText value="#{ f.texto }" /></p></f:verbatim>
				</t:column>
				
				<t:column style="width:20px">
					<a4j:commandButton id="removerItem" image="/img/garbage.png" title="Remover feedback" 
						actionListener="#{questionarioTurma.removerFeedback}"  
						reRender="dataTableFeedbacks" styleClass="noborder" />
				</t:column>
			</t:dataTable>
		</fieldset>
		
		<div class="botoes">
			<div class="form-actions" style="width:250px;text-align:left;">
				<h:commandButton action="#{ questionarioTurma.gerenciarPerguntasDoQuestionario }" value="Salvar e continuar >>" />
				<h:commandButton action="#{ questionarioTurma.salvarQuestionario }" value="Salvar e sair" /> 
			</div>
			<div class="other-actions" style="width:300px;">
				<h:commandButton action="#{ questionarioTurma.listarQuestionariosDocente }" value="<< Voltar aos Questionários" /> 
			</div>
			<div class="required-items">
				<span class="required">&nbsp;</span>
				Itens de Preenchimento Obrigatório.
			</div>
		</div>
		</a4j:region>
	</h:form>
	
	<%--Se o questionario possui avaliações --%>
	<input type="hidden" id="possuiAvaliacoes" value="${ questionarioTurma.possuiAvaliacoes }"/>
	
	<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>

	<script type="text/javascript">

		function exibeMedias (tentativa) {
			var tentativas = parseFloat(tentativa.value);
			if (tentativas > 1)
				document.getElementById("medias").style.display = "";
			else 
				document.getElementById("medias").style.display = "none";
		}
	
		<c:if test="${turmaVirtual.acessoMobile == false}">
			tinyMCE.init({
				mode : "textareas", editor_selector : "mceEditor", theme : "advanced", width : "460", height : "150", language : "pt",
				theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
				theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
				theme_advanced_buttons3 : "", plugins : "searchreplace,contextmenu,advimage", theme_advanced_toolbar_location : "top", theme_advanced_toolbar_align : "left"
			});
		</c:if>
		
		jQuery(document).ready(function () {
			exibeMedias(document.getElementById("tentativas"));

		});
	</script>
	
</f:view>
<%@include file="/ava/rodape.jsp" %>
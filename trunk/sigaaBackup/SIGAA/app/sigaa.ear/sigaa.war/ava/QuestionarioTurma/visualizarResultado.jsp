<%@include file="/ava/cabecalho.jsp" %>
<f:view>
	<a4j:keepAlive beanName="questionarioTurma" />

	<%@include file="/ava/menu.jsp" %>
	<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
	<link href="/sigaa/css/ensino/questionarios.css" rel="stylesheet" type="text/css" />

	<style>
		#resultado {
		
		}
		
		#resultado tr th.campos {
			text-align:right;
			width:100px;
			vertical-align:top;
			font-weight:normal;
			width:160px;
		}
		
		#resultado tr td.valor {
			font-weight:bold;
			text-align:left;
		}
		
		#resultado tfoot tr td{
			background:#C8D5EC;
			text-align:center;
			padding-top:3px;
			padding-bottom:3px;
		}
	</style>

	<h:form id="formQuestionario">
	
		<div style="text-align:center;font-weight:bold;font-size:12pt;margin-bottom:10px;">${ questionarioTurma.questionario.titulo }</div>
		
		<h:outputText value="<div class='descricaoOperacao'>O docente está avaliando suas respostas dissertativas.</div>" rendered="#{ questionarioTurma.resposta.dissertativasPendentes }" escape="false" />
		
		
		<h:outputText escape="false" value="<div style='margin:0px 30px 30px 30px;border:1px solid #CCC;padding:10px;'>#{ questionarioTurma.questionario.descricao }</div>" rendered="#{not empty questionarioTurma.questionario.descricao}" />
		
		<table id="resultado" style="width:98%;margin:0px auto 0px auto;">
		
			<tr>
				<th class="campos">Acerto: </th>
				<td class="valor" style="text-align:left;padding-left:10px;">
					<a4j:outputPanel rendered="#{ (questionarioTurma.prazoFinalizado && !questionarioTurma.questionario.naoVisualizarNota ) || questionarioTurma.questionario.visualizarNotaAposResponder }">
						<h:outputText value="#{ questionarioTurma.resposta.porcentagemString }" />
						<h:outputText value="(Há dissertativas com pendência de correção)" rendered="#{questionarioTurma.resposta.dissertativasPendentes }" />
					</a4j:outputPanel>
					<a4j:outputPanel rendered="#{ !questionarioTurma.prazoFinalizado && questionarioTurma.questionario.visualizarNotaAposFinalizar }">
						<h:outputText value="Disponível dia " />
						<ufrn:format valor="${ questionarioTurma.questionario.fim }" type="dataHora" />
					</a4j:outputPanel>
					<a4j:outputPanel rendered="#{ questionarioTurma.questionario.naoVisualizarNota }">
						<h:outputText value="Este questionário foi configurado para não exibir a nota." rendered="#{ questionarioTurma.questionario.naoVisualizarNota }" />
					</a4j:outputPanel>
				</td>
			</tr>
			<tr>
				<th class="campos">Feedback geral: </th>
				<td class="valor" style="text-align:left;padding-left:10px;">
					<h:outputText value="#{ questionarioTurma.feedbackGeral }" rendered="#{ ((questionarioTurma.prazoFinalizado && !questionarioTurma.questionario.naoVisualizarFeedbackGeral) || questionarioTurma.questionario.visualizarFeedbackGeralAposResponder) }"  />
					<a4j:outputPanel rendered="#{ !questionarioTurma.prazoFinalizado && questionarioTurma.questionario.visualizarFeedbackGeralAposFinalizar }">
						<h:outputText value="Disponível dia " />
						<ufrn:format valor="${ questionarioTurma.questionario.fim }" type="dataHora" />
					</a4j:outputPanel>
					<a4j:outputPanel rendered="#{ questionarioTurma.questionario.naoVisualizarFeedbackGeral }">
						<h:outputText value="Este questionário foi configurado para não exibir o Feedback Geral." rendered="#{ questionarioTurma.questionario.naoVisualizarFeedbackGeral }"/>
					</a4j:outputPanel>
				</td>
			</tr>
			<tr>
				<th class="campos">Horário de Envio: </th>
				<td class="valor" style="text-align:left;padding-left:10px;">
					<h:outputText value="#{ questionarioTurma.resposta.horaEnvio }" />
				</td>
			</tr>
			<tr>
				<a4j:outputPanel rendered="#{ !questionarioTurma.prazoFinalizado && questionarioTurma.questionario.visualizarRespostasAposFinalizar }">
					<h:outputText value="<th class='campos'>Visualização das respostas: </th><td class='valor' style='text-align:left;padding-left:10px;'>" escape="false" />
					<h:outputText value="Disponível dia " />
					<ufrn:format valor="${ questionarioTurma.questionario.fim }" type="dataHora" />
				</a4j:outputPanel>
				<h:outputText value="<td colspan='2'>" escape="false" rendered="#{ (questionarioTurma.prazoFinalizado && !questionarioTurma.questionario.naoVisualizarRespostas ) || questionarioTurma.questionario.visualizarRespostasAposResponder }" />
			</tr>
		</table><br/>
				<rich:dataTable var="resposta" value="#{questionarioTurma.respostasModel}" width="100%" id="dataTableQuestionario" style="margin-top:20px;" rowKeyVar="row" rowClasses="linhaPar, linhaImpar" rendered="#{ ((questionarioTurma.prazoFinalizado && !questionarioTurma.questionario.naoVisualizarRespostas ) || questionarioTurma.questionario.visualizarRespostasAposResponder) }" >
					<f:facet name="header"><h:outputText value="Respostas enviadas" /></f:facet>
					
					<rich:column rendered="#{resposta.pergunta.ativo}">
						<h:outputText value="#{row + 1}. #{resposta.pergunta.nome}" styleClass="pergunta" />
						<h:outputText value="#{resposta.pergunta.pergunta}" styleClass="descricaoPergunta" escape="false" />
						 		
						<h:panelGroup>
							<rich:panel>
								<h:selectOneRadio disabled="true" value="#{resposta.respostaVF}" 
									rendered="#{resposta.pergunta.vf}" id="respostaVf">
									<f:selectItem itemValue="true" itemLabel="Verdadeiro"/>
									<f:selectItem itemValue="false" itemLabel="Falso"/>
								</h:selectOneRadio>
								
								<h:inputTextarea disabled="true" value="#{resposta.respostaDissertativa}" rows="4" style="width: 98%;" rendered="#{resposta.pergunta.dissertativa}" id="respostaDissertativa"/>
								
								<h:inputText disabled="true" value="#{resposta.respostaNumericaString}" size="5" onkeyup="return formatarInteiro(this);" rendered="#{resposta.pergunta.numerica}" id="respostaNumerica"/>
								
								<h:selectOneRadio disabled="true" value="#{resposta.alternativaEscolhida}" layout="pageDirection" converter="convertAlternativaPerguntaQuestionarioTurma" rendered="#{resposta.pergunta.unicaEscolha}" id="unicaEscolha"> 
									<t:selectItems value="#{resposta.pergunta.alternativasValidas}" var="a" itemLabel="#{a.alternativa}#{ ((questionarioTurma.prazoFinalizado && !questionarioTurma.questionario.naoVisualizarFeedback) || questionarioTurma.questionario.visualizarFeedbackAposResponder) && resposta.alternativaEscolhida.id == a.id && not empty a.feedback ? ' - Feedback: ' : ''}#{ ((questionarioTurma.prazoFinalizado && !questionarioTurma.questionario.naoVisualizarFeedback) || questionarioTurma.questionario.visualizarFeedbackAposResponder) && resposta.alternativaEscolhida.id == a.id ? a.feedback : ''}" itemValue="#{a}"/>
								</h:selectOneRadio>
								
								<h:selectManyCheckbox disabled="true" value="#{resposta.alternativasEscolhidas}" layout="pageDirection" converter="convertAlternativaPerguntaQuestionarioTurma" rendered="#{resposta.pergunta.multiplaEscolha}" id="multiplaEscolha">
									<t:selectItems value="#{resposta.pergunta.alternativasValidas}" var="a" itemLabel="#{a.alternativa}#{ ((questionarioTurma.prazoFinalizado && !questionarioTurma.questionario.naoVisualizarFeedback) || questionarioTurma.questionario.visualizarFeedbackAposResponder) && a.selecionado && not empty a.feedback ? ' - Feedback: ' : ''}#{ ((questionarioTurma.prazoFinalizado && !questionarioTurma.questionario.naoVisualizarFeedback) || questionarioTurma.questionario.visualizarFeedbackAposResponder) && a.selecionado ? a.feedback : ''}" itemValue="#{a}"/>
								</h:selectManyCheckbox>
							</rich:panel>
						</h:panelGroup>
						
						<h:outputText value="<strong style='font-weight:bold;'>Correção: </strong> #{resposta.correcaoDissertativa}" rendered="#{resposta.pergunta.dissertativa && resposta.correcaoDissertativa != null && resposta.correcaoDissertativa != ''}" escape="false" />
						<h:outputText value="<strong style='font-weight:bold;'>Acerto: </strong> #{resposta.porcentagemNota}%" rendered="#{resposta.pergunta.dissertativa && resposta.porcentagemNota != null}" escape="false" />
						<h:outputText value="Resposta aguardando correção pelo docente" rendered="#{resposta.pergunta.dissertativa && resposta.porcentagemNota == null}" escape="false" style="display:block;color:#CC0000;margin:10px;padding:10px;" />
						<h:outputText value="O docente poderá recorrigir esta questão" rendered="#{resposta.pergunta.dissertativa && !resposta.corretaDissertativa && resposta.porcentagemNota != null && resposta.correcaoDissertativa != ''}" escape="false" style="display:block;color:#CC0000;margin:10px;padding:10px;" />
						
						<a4j:outputPanel rendered="#{!resposta.pergunta.dissertativa || resposta.porcentagemNota != null }">
							<h:outputText value="#{ resposta.pergunta.feedbackAcerto }" rendered="#{ ((questionarioTurma.prazoFinalizado && !questionarioTurma.questionario.naoVisualizarFeedback) || questionarioTurma.questionario.visualizarFeedbackAposResponder) &&  resposta.correta }" style="display:block;color:#00CC00;margin:10px;padding:10px;" />
							<h:outputText value="#{ resposta.pergunta.feedbackErro }" rendered="#{ ((questionarioTurma.prazoFinalizado && !questionarioTurma.questionario.naoVisualizarFeedback)|| questionarioTurma.questionario.visualizarFeedbackAposResponder) && !resposta.correta }" style="display:block;color:#CC0000;margin:10px;padding:10px;" />
						</a4j:outputPanel>
				
					</rich:column>
					
				</rich:dataTable>
				
				<h:outputText value="</td>" escape="false" />
			</tr>
			<tr><td colspan="2">&nbsp;</td></tr>
		</table>
		
		<div align="center" style="margin-top:5px;">
		<h:commandButton value="<< Voltar aos Questionários" action="#{questionarioTurma.listarQuestionariosDiscente}" />
		</div>	
	</h:form>

</f:view>
<%@include file="/ava/rodape.jsp" %>
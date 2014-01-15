<%@include file="/ava/cabecalho.jsp" %>
<f:view>
	<a4j:keepAlive beanName="questionarioTurma" />


	<%@include file="/ava/menu.jsp" %>
	<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
	<link href="/sigaa/css/ensino/questionarios.css" rel="stylesheet" type="text/css" />

	<h:form id="formQuestionario">
	
	<div style="text-align:center;font-weight:bold;font-size:12pt;margin-bottom:10px;">${ questionarioTurma.questionario.titulo }</div>
	<h:outputText escape="false" value="<div style='margin:0px 30px 30px 30px;border:1px solid #CCC;padding:10px;'>#{ questionarioTurma.questionario.descricao }</div>" rendered="#{not empty questionarioTurma.questionario.descricao}" />
	
	<span style="width:100px;display:inline-block;text-align:right;">Acerto:</span><h:outputText value="#{ questionarioTurma.resposta.porcentagemString }" style="font-weight:bold;" /><br/>
	
	<span style="width:100px;display:inline-block;text-align:right;">Feedback Geral:</span><h:outputText value="#{ questionarioTurma.feedbackGeral }" style="font-weight:bold;" escape="false" />
	
	<style>label { display:inline-block; margin-left:5px; margin-right:20px; }</style>
	
	<rich:dataTable var="resposta" value="#{questionarioTurma.respostasModel}" width="100%" id="dataTableQuestionario" style="margin-top:20px;" rowKeyVar="row" rowClasses="linhaPar, linhaImpar" >
		
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
					
					<a4j:outputPanel rendered="#{resposta.pergunta.dissertativa}">
						<span><strong style="font-weight:bold;">Resposta:</strong></span><br/>
						<h:outputText value="#{resposta.respostaDissertativa}" id="respostaDissertativa" style="border:1px solid #CCC;width:98%;padding:5px;display:block;margin-top:5px;" />
						
						<a4j:outputPanel rendered="#{empty resposta.pergunta.gabaritoDissertativa || (not empty resposta.pergunta.gabaritoDissertativa) }">
							<br/><br/>
							<span><strong style="font-weight:bold;">Correção:</strong></span>
							<h:inputTextarea value="#{resposta.correcaoDissertativa}" rows="4" style="width: 98%;" id="correcaoDissertativa"/><br/>
							
							<span><strong style="font-weight:bold;">A resposta está correta?</strong></span>
							<h:selectOneMenu value="#{ resposta.porcentagemNota }" style="display:inline-block;margin-top:10px;">
								<f:selectItems value="#{ questionarioTurma.notas }" />
							</h:selectOneMenu>
						</a4j:outputPanel>
					</a4j:outputPanel>
					
					<h:inputText disabled="true" value="#{resposta.respostaNumericaString}" size="5" onkeyup="return formatarInteiro(this);" rendered="#{resposta.pergunta.numerica}" id="respostaNumerica"/>
					
					<h:selectOneRadio disabled="true" value="#{resposta.alternativaEscolhida}" layout="pageDirection" converter="convertAlternativaPerguntaQuestionarioTurma" rendered="#{resposta.pergunta.unicaEscolha}" id="unicaEscolha"> 
						<t:selectItems value="#{resposta.pergunta.alternativasValidas}" var="a" itemLabel="#{a.alternativa}" itemValue="#{a}"/>
					</h:selectOneRadio>
					
					<h:selectManyCheckbox disabled="true" value="#{resposta.alternativasEscolhidas}" layout="pageDirection" converter="convertAlternativaPerguntaQuestionarioTurma" rendered="#{resposta.pergunta.multiplaEscolha}" id="multiplaEscolha">
						<t:selectItems value="#{resposta.pergunta.alternativasValidas}" var="a" itemLabel="#{a.alternativa}" itemValue="#{a}"/>
					</h:selectManyCheckbox>
				</rich:panel>
			</h:panelGroup>
			
			<h:outputText value="#{ resposta.pergunta.feedbackAcerto }" rendered="#{ resposta.correta }" style="display:block;color:#00CC00;margin:10px;padding:10px;font-weight:bold;" />
			<h:outputText value="#{ resposta.pergunta.feedbackErro }" rendered="#{ !resposta.correta }" style="display:block;color:#CC0000;margin:10px;padding:10px;font-weight:bold;" />
	
		</rich:column>
		
	</rich:dataTable>
	
	<div style="width:100%;padding-top:3px;padding-bottom:3px;text-align:center;background:#C8D5EC;">
		<h:commandButton action="#{ questionarioTurma.corrigirDissertativas }" rendered="#{ questionarioTurma.resposta.possuiPerguntasDissertativas }" value="Corrigir Dissertativas" />
		<h:commandButton action="#{ questionarioTurma.visualizarRespostas }" value="<< Voltar às respostas" />
		<h:commandButton action="#{ turmaVirtual.entrar }" value="Cancelar" onclick="#{confirm}" />
	</div>
	
	</h:form>
	
</f:view>
<%@include file="/ava/rodape.jsp" %>

<script type="text/javascript" src="/shared/javascript/websnapr.js"></script>
<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
tinyMCE.init({
	mode : "textareas", theme : "advanced", width : "800", height : "250", language : "pt",
	theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
	theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
	theme_advanced_buttons3 : "",
	plugins : "searchreplace,contextmenu,advimage",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left"
});
</script>
<%@include file="/ava/cabecalho.jsp" %>
<f:view>
	<a4j:keepAlive beanName="questionarioTurma" />
	<a4j:keepAlive beanName="categoriaPerguntaQuestionarioTurma" />

	<style>
		.linhaEscura td {
			background:#EEE;
		}
	</style>

	<%@include file="/ava/menu.jsp" %>
	<h2><ufrn:subSistema /> &gt; Questionário</h2>
	<h:form>
		<div class="descricaoOperacao">
			<p style="text-align:justify;">
			Ao clicar no botão <b>'Acessar Questionário'</b> sua tentativa já será <b>contabilizada</b>. Para que ela seja válida, você deve responder às questões e clicar em 'Enviar'. 
			Caso aconteça algum problema como perca da conexão com a internet ou falta de energia, você poderá voltar ao questionário contando a mesma tentativa, 
			caso ainda esteja dentro do tempo para a conclusão de sua tentativa anterior. 
			Caso contrário, uma nova tentativa será contabilizada, se o questionário permitir.
			</p>
		
		</div>
		<div style="text-align:center;font-weight:bold;font-size:12pt;margin-bottom:10px;">${ questionarioTurma.questionario.titulo }</div>
		<h:outputText escape="false" value="<div style='margin:0px 30px 30px 30px;border:1px solid #CCC;padding:10px;'>#{ questionarioTurma.questionario.descricao }</div>" rendered="#{not empty questionarioTurma.questionario.descricao}" />

		<div style="text-align: center; border: 1px solid #DEDEDE;margin:10px auto 10px auto;padding:10px;">
			O questionário está configurado para que a nota do aluno 
			<c:if test="${ questionarioTurma.questionario.ultimaNota}">
				seja a nota de sua <b>última tentativa</b>.
			</c:if>	
			<c:if test="${ questionarioTurma.questionario.notaMaisAlta}">
				seja a nota de sua <b>melhor tentativa</b>.
			</c:if>	
			<c:if test="${ questionarioTurma.questionario.mediasDasNotas}">
				seja a <b>média de suas tentativas</b>.
			</c:if>		
			</b>
		</div>

		<div style="text-align:center;">
			<p>Inicia em <h:outputText value="#{questionarioTurma.questionario.inicio}" /> às <h:outputText value="#{ questionarioTurma.questionario.horaInicio }" />h<h:outputText value="#{ questionarioTurma.questionario.minutoInicio }" /> e finaliza em <h:outputText value="#{questionarioTurma.questionario.fim}" /> às <h:outputText value="#{ questionarioTurma.questionario.horaFim }" />h<h:outputText value="#{ questionarioTurma.questionario.minutoFim }" /></p>
			<p>
				<h:outputText value="Você iniciou uma resposta mas não a finalizou. Ainda há tempo de enviá-la." rendered="#{ questionarioTurma.resposta != null && questionarioTurma.resposta.id > 0 && questionarioTurma.permiteNovaTentativa }" />
				<h:outputText value="Você não pode responder este questionário." rendered="#{ !questionarioTurma.permiteNovaTentativa }" />
				<h:outputText value="Este questionário permite #{ questionarioTurma.questionario.tentativas } tentativa(s) e você ainda tem #{ questionarioTurma.questionario.tentativas - questionarioTurma.tentativas } tentativa(s) restante(s)." rendered="#{ (questionarioTurma.resposta == null || questionarioTurma.resposta.id == 0) && questionarioTurma.permiteNovaTentativa }" />
			</p>
			<p style="margin-top:10px;">
				<h:commandButton value="Acessar Questionário" action="#{ questionarioTurma.responderQuestionario }" />
				
				<h:commandButton value="<< Voltar aos Questionários" action="#{ questionarioTurma.listarQuestionariosDiscente }" />
				
				<h:commandButton value="Visualizar Resultado" action="#{ questionarioTurma.visualizarTentativas }" rendered="#{ questionarioTurma.tentativas > 0 }" />
			</p>
		</div>
	</h:form>

</f:view>

<%@include file="/ava/rodape.jsp" %>
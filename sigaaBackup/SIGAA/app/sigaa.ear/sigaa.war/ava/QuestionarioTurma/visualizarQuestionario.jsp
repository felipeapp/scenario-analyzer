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
	<h2><ufrn:subSistema /> &gt; Question�rio</h2>
	<h:form>
		<div class="descricaoOperacao">
			<p style="text-align:justify;">
			Ao clicar no bot�o <b>'Acessar Question�rio'</b> sua tentativa j� ser� <b>contabilizada</b>. Para que ela seja v�lida, voc� deve responder �s quest�es e clicar em 'Enviar'. 
			Caso aconte�a algum problema como perca da conex�o com a internet ou falta de energia, voc� poder� voltar ao question�rio contando a mesma tentativa, 
			caso ainda esteja dentro do tempo para a conclus�o de sua tentativa anterior. 
			Caso contr�rio, uma nova tentativa ser� contabilizada, se o question�rio permitir.
			</p>
		
		</div>
		<div style="text-align:center;font-weight:bold;font-size:12pt;margin-bottom:10px;">${ questionarioTurma.questionario.titulo }</div>
		<h:outputText escape="false" value="<div style='margin:0px 30px 30px 30px;border:1px solid #CCC;padding:10px;'>#{ questionarioTurma.questionario.descricao }</div>" rendered="#{not empty questionarioTurma.questionario.descricao}" />

		<div style="text-align: center; border: 1px solid #DEDEDE;margin:10px auto 10px auto;padding:10px;">
			O question�rio est� configurado para que a nota do aluno 
			<c:if test="${ questionarioTurma.questionario.ultimaNota}">
				seja a nota de sua <b>�ltima tentativa</b>.
			</c:if>	
			<c:if test="${ questionarioTurma.questionario.notaMaisAlta}">
				seja a nota de sua <b>melhor tentativa</b>.
			</c:if>	
			<c:if test="${ questionarioTurma.questionario.mediasDasNotas}">
				seja a <b>m�dia de suas tentativas</b>.
			</c:if>		
			</b>
		</div>

		<div style="text-align:center;">
			<p>Inicia em <h:outputText value="#{questionarioTurma.questionario.inicio}" /> �s <h:outputText value="#{ questionarioTurma.questionario.horaInicio }" />h<h:outputText value="#{ questionarioTurma.questionario.minutoInicio }" /> e finaliza em <h:outputText value="#{questionarioTurma.questionario.fim}" /> �s <h:outputText value="#{ questionarioTurma.questionario.horaFim }" />h<h:outputText value="#{ questionarioTurma.questionario.minutoFim }" /></p>
			<p>
				<h:outputText value="Voc� iniciou uma resposta mas n�o a finalizou. Ainda h� tempo de envi�-la." rendered="#{ questionarioTurma.resposta != null && questionarioTurma.resposta.id > 0 && questionarioTurma.permiteNovaTentativa }" />
				<h:outputText value="Voc� n�o pode responder este question�rio." rendered="#{ !questionarioTurma.permiteNovaTentativa }" />
				<h:outputText value="Este question�rio permite #{ questionarioTurma.questionario.tentativas } tentativa(s) e voc� ainda tem #{ questionarioTurma.questionario.tentativas - questionarioTurma.tentativas } tentativa(s) restante(s)." rendered="#{ (questionarioTurma.resposta == null || questionarioTurma.resposta.id == 0) && questionarioTurma.permiteNovaTentativa }" />
			</p>
			<p style="margin-top:10px;">
				<h:commandButton value="Acessar Question�rio" action="#{ questionarioTurma.responderQuestionario }" />
				
				<h:commandButton value="<< Voltar aos Question�rios" action="#{ questionarioTurma.listarQuestionariosDiscente }" />
				
				<h:commandButton value="Visualizar Resultado" action="#{ questionarioTurma.visualizarTentativas }" rendered="#{ questionarioTurma.tentativas > 0 }" />
			</p>
		</div>
	</h:form>

</f:view>

<%@include file="/ava/rodape.jsp" %>
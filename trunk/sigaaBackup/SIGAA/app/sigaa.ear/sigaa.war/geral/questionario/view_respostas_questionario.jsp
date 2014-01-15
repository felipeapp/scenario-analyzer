<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%--
Página padrão para visualização dos questionários do sigaa.

Essa págian foi feitas para ser chamada de outros casos de uso do sistema. 

Para isso utilize os comandos abaixo do Mbean do caso de uso:

getCurrentRequest().setAttribute("_visualizaRespostasQuestionarioMBean", this);
return forward("/geral/questionario/view_respostas_questionarios.jsp");

O MBean deve ser um objeto QuestionarioRespostas preenchido.

@See InscricaoParticipantesMBean

 --%>



<h2> <ufrn:subSistema/> > Respostas do Questionário </h2>

<f:view>

<a4j:keepAlive beanName="_visualizaRespostasQuestionarioMBean" />

<div class="descricaoOperacao">
		<p> Caro Usuário, </p>
		<p> Abaixo estão listadas as respostas para o questionário selecionado. </p>
</div>

<table class="formulario" style="width: 90%;">
	
	<caption> ${_visualizaRespostasQuestionarioMBean.questionarioRespondido.questionario.titulo} </caption>
	
	<c:if test="${! _visualizaRespostasQuestionarioMBean.questionarioRespondido.questionario.ativo}">
		<tr> <td style="color: red;"> O questionário não está mais ativo </td> </tr>
	</c:if>
	
	 
	<c:if test="${_visualizaRespostasQuestionarioMBean.questionarioRespondido.questionario.ativo 
			&& fn:length(_visualizaRespostasQuestionarioMBean.questionarioRespondido.respostas) > 0 }">
		
		<c:forEach var="resposta" items="#{_visualizaRespostasQuestionarioMBean.questionarioRespondido.respostas}" varStatus="status">
			
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
			   <%-- Imprime as perguntas --%>
				<th style="text-align: left; font-weight: bold;">
					${status.index + 1}. ${resposta.pergunta.pergunta}
				</th>
			</tr>
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
				 <%-- Imprime as repostas --%>
				<td style="padding-left: 50px;;">
					<h:outputText  value="#{resposta.respostaVfString}" rendered="#{resposta.pergunta.vf}" />
				
					<h:outputText  value="#{resposta.respostaDissertativa}" rendered="#{resposta.pergunta.dissertativa}" />
					
					<h:outputText  value="#{resposta.respostaNumericaString}" rendered="#{resposta.pergunta.numerica}" />
					
					<h:outputText  value="#{resposta.alternativa.alternativa}" rendered="#{resposta.pergunta.unicaEscolha}" />
					
					<h:outputText  value="#{resposta.alternativa.alternativaComPeso}" rendered="#{resposta.pergunta.unicaEscolhaAlternativaPeso}" />
					
					<c:if test="${fn:length(resposta.alternativas) > 0}"> 
						<c:forEach var="alternativa" items="#{resposta.alternativas}" varStatus="status2">
							${status2.index + 1}) <h:outputText  value="#{alternativa.alternativa}" /> <br/>
						</c:forEach>
					</c:if>
					
					<c:if test="${resposta.pergunta.arquivo && resposta.respostaArquivo != null}">
						<a target="_blank" href="${ configSistema['linkSigaa'] }/sigaa/verProducao?idProducao=${resposta.respostaArquivo}&key=${ sf:generateArquivoKey(resposta.respostaArquivo) }">
							<h:graphicImage url="/img/pdf.png" style="border:none" title="Arquivo de Respostas" />
							Arquivo de Respostas
						</a>
					</c:if>
				</td>
			</tr>
		</c:forEach>
	</c:if>
	
</table>

<br />
<div class="voltar" style="text-align: center;">
	<a href="javascript: history.go(-1);"> Voltar </a>
</div>	

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>


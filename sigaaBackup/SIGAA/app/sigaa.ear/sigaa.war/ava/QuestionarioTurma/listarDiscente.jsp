<%@include file="/ava/cabecalho.jsp" %>
<f:view>
	<a4j:keepAlive beanName="questionarioTurma" />

	<%@include file="/ava/menu.jsp" %>
	<h:form id="formAva">
		<fieldset>
			<legend>Questionários</legend>
			
			<c:if test="${ questionarioTurma.possuiQuestionarios }">
				<div class="infoAltRem">
					<h:graphicImage value="/ava/img/corrigir.png" title="Responder" />: Responder
					<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar resposta submetida
				</div>
			
				<table class="listing" style="width:80%;">
					<thead>
						<tr>
							<th style="text-align:left;">Título</th>
							<th style="text-align:center;">Início</th>
							<th style="text-align:center;">Fim</th>
							<th style="width:20px;">&nbsp;</th>
							<th style="width:20px;">&nbsp;</th>
						</tr>
					</thead>
					
					<c:forEach items="#{questionarioTurma.questionarios}" var="q" varStatus="s">
						<c:if test="${ q.finalizado }">
							<tr class='${ s.index % 2 == 0 ? "even" : "odd" }'>
								<td  class="first" style="text-align:left;">${ q.titulo }</td>
								<td style="text-align:center;"><ufrn:format type="data" valor="${ q.inicio }" /> ${ q.horaInicio < 10 ? "0" : "" }${q.horaInicio }:${ q.minutoInicio < 10 ? "0" : "" }${ q.minutoInicio }</td>
								<td style="text-align:center;"><ufrn:format type="data" valor="${ q.fim }" /> ${ q.horaFim < 10 ? "0" : "" }${ q.horaFim }:${ q.minutoFim < 10 ? "0" : "" }${ q.minutoFim }</td>
								<td>
									<h:commandLink id="idEnviarMaterialQuestionario" action="#{ questionarioTurma.iniciarResponderQuestionario }" rendered="#{ turmaVirtual.discente && q.dentroPeriodoEntrega }">
										<h:graphicImage value="/ava/img/corrigir.png" title="Responder" />
										<f:param name="id" value="#{ q.id }"/>
									</h:commandLink>
								</td>
								<td>
									<h:commandLink action="#{ questionarioTurma.visualizarTentativas }">
										<h:graphicImage value="/img/view.gif" title="Visualizar resposta submetida" />
										<f:param name="id" value="#{ q.id }" />
									</h:commandLink>
								</td>
							</tr>
						</c:if>
					</c:forEach>
				</table>
			</c:if>
			<c:if test="${ !questionarioTurma.possuiQuestionarios }">
				<p class="empty-listing">Nenhum item foi encontrado.</p>
			</c:if>
			
		</fieldset>
	</h:form>
</f:view>
<%@include file="/ava/rodape.jsp" %>
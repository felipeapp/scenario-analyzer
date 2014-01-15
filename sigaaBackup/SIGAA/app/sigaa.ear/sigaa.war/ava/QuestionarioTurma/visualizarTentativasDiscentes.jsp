<%@include file="/ava/cabecalho.jsp" %>
<f:view>
	<a4j:keepAlive beanName="questionarioTurma" />

	<%@include file="/ava/menu.jsp" %>
	<h:form id="formAva">
		<fieldset>
			<legend>Questionários</legend>
			
			<c:if test="${ questionarioTurma.questionario.finalizado }">
				<div class="infoAltRem">
					<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar resposta submetida
				</div>
			
			<div style="text-align: center; border: 1px solid #DEDEDE;margin:10px auto 10px auto;padding:10px;">
				O questionário está configurado para que o acerto do aluno 
				<c:if test="${ questionarioTurma.questionario.ultimaNota}">
					seja sua <b>última tentativa</b>.
				</c:if>	
				<c:if test="${ questionarioTurma.questionario.notaMaisAlta}">
					seja sua <b>melhor tentativa</b>.
				</c:if>	
				<c:if test="${ questionarioTurma.questionario.mediasDasNotas}">
					seja a <b>média de suas tentativas</b>.
				</c:if>		
				</b>
			</div>
			
				<table class="listing" style="width:80%;">
					<thead>
						<tr>
							<th style="text-align:left;">Tentativas</th>
							<th style="width:20px;">&nbsp;</th>
						</tr>
					</thead>
					
					<c:forEach items="#{questionarioTurma.conjunto.respostas}" var="r" varStatus="loop">
							<tr class='${ loop.index % 2 == 0 ? "even" : "odd" }'>
								<td  class="first" style="text-align:left;">Tentativa ${ loop.index+1 }</td>
								<td>
									<h:commandLink action="#{ questionarioTurma.visualizarResultado }">
										<h:graphicImage value="/img/view.gif" title="Visualizar resposta submetida" />
										<f:param name="id" value="#{ r.id }" />
									</h:commandLink>
								</td>
							</tr>
					</c:forEach>
				</table>
			</c:if>
			
			
		<div class="botoes">
			<div style="margin-right:40%;" class="other-actions">
				<h:commandButton id="voltar" action="#{ questionarioTurma.listarQuestionariosDiscente }" value="<< Voltar aos Questionários"/> 
			</div>
		</div>
		</fieldset>

	</h:form>
</f:view>
<%@include file="/ava/rodape.jsp" %>
<%@include file="/ava/cabecalho.jsp" %>

<f:view>
	<%@include file="/ava/menu.jsp" %>
	
	<a4j:keepAlive beanName="respostaTarefaTurma" />
	
	<h:form id="form">
	
		
		<fieldset>
			<legend>Visualizar Resposta</legend>
			
			<strong style="font-weight:bold;">Tarefa:</strong> ${respostaTarefaTurma.obj.tarefa.titulo}<br />
			
			<br />
					
			<c:if test="${ not respostaTarefaTurma.obj.tarefa.emGrupo}">	
						<label>Aluno:</label>
						<span style="margin-left:3px;font-weight:bold;">${respostaTarefaTurma.obj.usuarioEnvio.pessoa.nome}</span>
					</c:if>
			<c:if test="${respostaTarefaTurma.obj.tarefa.emGrupo}">	
					<label>Grupo:</label>
					<span style="margin-left:3px;font-weight:bold;">${respostaTarefaTurma.obj.grupoDiscentes.nome}</span>
					<c:forEach items="#{ respostaTarefaTurma.obj.grupoDiscentes.discentes }" var="d" varStatus="loop">
						<li style="margin-left:70px;font-weight:bold;">
						<h:outputText value="#{ d.pessoa.nome }"/>								
						</li>
					</c:forEach>
			</c:if>
					
			<br />
					
			<strong style="font-weight:bold;">Resposta:</strong><br />
			${respostaTarefaTurma.obj.textoResposta}<br />
			
			<br />
			<strong style="font-weight:bold;">Comentários do Aluno:</strong><br />${respostaTarefaTurma.obj.comentarios}
		
			<div class="botoes">
				<div class="other-actions">
					<h:commandButton action="#{respostaTarefaTurma.avaliarTarefas}" value="<< Voltar">
						<f:param name="id" value="#{respostaTarefaTurma.obj.tarefa.id}"></f:param>
					</h:commandButton>
				</div>
			</div>
			
		</fieldset>
	
	</h:form>
	
</f:view>
<%@include file="/ava/rodape.jsp" %>
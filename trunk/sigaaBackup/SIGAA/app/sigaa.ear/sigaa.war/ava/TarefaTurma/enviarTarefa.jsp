<%@include file="/ava/cabecalho.jsp" %>

<f:view>

<a4j:keepAlive beanName="respostaTarefaTurma" />

<%@include file="/ava/menu.jsp" %>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<h:form enctype="multipart/form-data">


<fieldset>
<legend>Responder tarefa</legend>

	<h:inputHidden value="#{ respostaTarefaTurma.obj.tarefa.id }" />
	<h:inputHidden value="#{ respostaTarefaTurma.obj.tarefa.envioArquivo }" />
	<h:inputHidden value="#{ respostaTarefaTurma.obj.tarefa.respostaOnline }" />
	
	<h:inputHidden value="#{ respostaTarefaTurma.obj.tarefa.titulo }" />
	<h:inputHidden value="#{ respostaTarefaTurma.obj.tarefa.conteudo }" />
	<h:inputHidden value="#{respostaTarefaTurma.obj.tarefa.dataEntrega}" />

		<c:if test="${respostaTarefaTurma.usuarioJaEnviou == true}">
		
			<center>Olá. Você já enviou sua resposta e essa tarefa <b>não</b> aceita mais de um envio.</center>
		
			<div class="botoes-show" align="center">
						<h:commandButton id="idVisualizar" value="Visualizar Resposta" action="#{respostaTarefaTurma.verificarTarefasJaEnviadas}"/> |
						<h:commandButton id="idVoltar" value="<< Voltar" action="#{tarefaTurma.listar}" immediate="true"/>
			</div>
		
		</c:if>

		<c:if test="${not respostaTarefaTurma.usuarioJaEnviou}">

					<ul class="form">
		
			<li>
				<label>Nome da Tarefa: </label> 
				<div class="campo"><h:outputText value="#{ respostaTarefaTurma.obj.tarefa.titulo }" /></div>
			</li>

			<li>
				<label>Descrição: </label> 
				<div class="campo">${ respostaTarefaTurma.obj.tarefa.conteudo }</div>
			</li>
			
			<c:if test="${respostaTarefaTurma.obj.tarefa.emGrupo && not empty respostaTarefaTurma.obj.grupoDiscentes.discentes }">
				<li>
					<label>Grupo: </label> 
					<c:forEach items="#{ respostaTarefaTurma.obj.grupoDiscentes.discentes }" var="d" varStatus="loop">
						<div class="campo">${ d.pessoa.nome }</div>
					</c:forEach>	
				</li>
			</c:if>
			
			<c:if test="${respostaTarefaTurma.obj.tarefa.idArquivo != null && respostaTarefaTurma.obj.tarefa.idArquivo > 0}">
				<li>
					<label>Arquivo do Professor:</label>
					<div class="campo">
						<a href="/sigaa/verFoto?idArquivo=${ respostaTarefaTurma.obj.tarefa.idArquivo }&key=${ sf:generateArquivoKey(respostaTarefaTurma.obj.tarefa.idArquivo) }">
						Baixar arquivo enviado pelo professor</a>
					</div>
				</li>
			</c:if>
			
			<li>
				<label>Período: </label>
				<div class="campo">
					Inicia em <ufrn:format type="data" valor="${respostaTarefaTurma.obj.tarefa.dataInicio}" /> às <fmt:formatNumber value="${ respostaTarefaTurma.obj.tarefa.horaInicio }" minIntegerDigits="2" maxIntegerDigits="2"/>h<fmt:formatNumber value="${ respostaTarefaTurma.obj.tarefa.minutoInicio }"  minIntegerDigits="2" maxIntegerDigits="2"/> e finaliza em <ufrn:format type="data" valor="${respostaTarefaTurma.obj.tarefa.dataEntrega}" /> às <fmt:formatNumber value="${ respostaTarefaTurma.obj.tarefa.horaEntrega }" minIntegerDigits="2" maxIntegerDigits="2"/>h<fmt:formatNumber value="${ respostaTarefaTurma.obj.tarefa.minutoEntrega }"  minIntegerDigits="2" maxIntegerDigits="2"/>
				</div>
			</li>

			<c:if test="${respostaTarefaTurma.obj.tarefa.envioArquivo}">
				<li>
					<label>Arquivo:<span class="required">&nbsp;</span></label>
					<t:inputFileUpload id="idArquivo" value="#{ respostaTarefaTurma.arquivo }" size="60"/> 
					<span class="descricao-campo">(Selecione o arquivo a ser enviado como resposta. Tamanho máximo: ${respostaTarefaTurma.tamanhoUploadAluno} MB)</span>
				</li>
			</c:if>
			
			<c:if test="${respostaTarefaTurma.obj.tarefa.respostaOnline}">
				<li>
					<label>Resposta:<span class="required">&nbsp;</span></label>
					<t:inputTextarea id="idTexto" value="#{ respostaTarefaTurma.textoResposta }" cols="70" rows="5" />
				</li>
			</c:if>

			<li>
				<label>Comentários que podem ser visualizados pelo professor: </label> 
				<h:inputTextarea id="idComentarios" value="#{ respostaTarefaTurma.comentarios }" rows="5" cols="70" />
			</li>
		</ul>	
				<div class="botoes">
					<div class="form-actions">
						<h:commandButton id="idEnviar" value="Enviar" action="#{respostaTarefaTurma.cadastrar}" />
					</div>
					<div class="other-actions">
						<h:commandButton id="idCancelar" value="Cancelar" action="#{tarefaTurma.listar}" immediate="true"/>
					</div>
					<div class="required-items">
						<span class="required"/>
						Itens de Preenchimento Obrigatório
						</span>
					</div>
				</div>

		</c:if>
		
	</fieldset>
		
</h:form>

</f:view>
<%@include file="/ava/rodape.jsp"%>

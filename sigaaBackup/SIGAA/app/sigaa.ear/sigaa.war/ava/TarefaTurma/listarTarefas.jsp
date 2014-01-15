
<%@include file="/ava/cabecalho.jsp"%>

<a4j:keepAlive beanName="respostaTarefaTurma" />

<f:view>
<%@include file="/ava/menu.jsp" %>

<h:form>

<c:if test="${turmaVirtual.discente}">
	<fieldset>
		<c:if test="${respostaTarefaTurma.listaTarefaEnviada.dataCorrecao == null }">
		<div class="descricaoOperacao">
			Se a tarefa estiver marcada como corrigida, os comentários do professor poderão ser visualizados nesta tela.
		</div>
		</c:if>
	
		<legend>Resposta enviada</legend>
	
	    <c:if test="${respostaTarefaTurma.listaTarefaEnviada.tarefa.envioArquivo || respostaTarefaTurma.listaTarefaEnviada.idArquivoCorrecao > 0}">
			<div class="infoAltRem">	
					<h:graphicImage value="/ava/img/page_white_put.png" />: Baixar Arquivo
			</div>
		</c:if>
	
		<ul class="form">
		
			<c:if test="${respostaTarefaTurma.listaTarefaEnviada.tarefa.emGrupo}">
				<li><label>Enviada Por:</label><div class="campo"><h:outputText value="#{respostaTarefaTurma.listaTarefaEnviada.usuarioEnvio.pessoa.nome  }"/></div></li>
			</c:if>
			
			<c:if test="${respostaTarefaTurma.listaTarefaEnviada.tarefa.envioArquivo}">
				<c:if test="${respostaTarefaTurma.listaTarefaEnviada.id > 0}">
					<li><label>Arquivo:</label><div class="campo">
						<a href="/sigaa/verProducao?idProducao=${respostaTarefaTurma.listaTarefaEnviada.idArquivo}&key=${sf:generateArquivoKey(respostaTarefaTurma.listaTarefaEnviada.idArquivo)}" title="Baixar Arquivo Enviado">
							<img src="/sigaa/ava/img/page_white_put.png" alt="Baixar Arquivo Enviado" />
							${respostaTarefaTurma.listaTarefaEnviada.nomeArquivo}
						</a>
					</div></li>
				</c:if>
			</c:if>
			<c:if test="${respostaTarefaTurma.listaTarefaEnviada.tarefa.respostaOnline}">
				<li><label>Resposta:</label><div class="campo">${respostaTarefaTurma.listaTarefaEnviada.textoResposta}</</div></li>
			</c:if>
				
			<li><label>Comentários:</label><div class="campo"><h:outputText value="#{respostaTarefaTurma.listaTarefaEnviada.comentarios  }"/></div></li>
		</ul>
	</fieldset>	
</c:if>


<c:if test="${respostaTarefaTurma.listaTarefaEnviada.dataCorrecao != null || turmaVirtual.docente}">
	<fieldset>
		<c:if test="${respostaTarefaTurma.listaTarefaEnviada.textoCorrecao == null && turmaVirtual.docente }">
			<div style="line-height:200px;text-align:center;font-size:1.3em;font-weight:bold;color: #FF0000;">Esta tarefa ainda não foi corrigida.</div>
		</c:if>
		
		<c:if test="${ (respostaTarefaTurma.listaTarefaEnviada.textoCorrecao != null && turmaVirtual.docente) || turmaVirtual.discente}">
			<legend>Correção</legend>
			<ul class="form">
					<c:if test="${respostaTarefaTurma.listaTarefaEnviada.tarefa.possuiNota}">
						<li><label>Nota:</label><div class="campo"><h:outputText value="#{respostaTarefaTurma.nota  }"/></div></li>
					</c:if>
			
					<li><label>Comentários:</label><div class="campo"><h:outputText value="#{respostaTarefaTurma.listaTarefaEnviada.textoCorrecao }" escape="false"/></div></li>
					<c:if test="${respostaTarefaTurma.listaTarefaEnviada.idArquivoCorrecao > 0}">
						<li><label>Arquivo:</label><div class="campo">
							<a href="/sigaa/verProducao?idProducao=${respostaTarefaTurma.listaTarefaEnviada.idArquivoCorrecao}&key=${sf:generateArquivoKey(respostaTarefaTurma.listaTarefaEnviada.idArquivoCorrecao)}" title="Baixar Arquivo Enviado na Correção">
								<img src="/sigaa/ava/img/page_white_put.png" alt="Baixar Arquivo Enviado Correção" />
								${respostaTarefaTurma.listaTarefaEnviada.nomeArquivoCorrecao}
							</a>
						</div></li>
					</c:if>
			</ul>
		</c:if>	
	</fieldset>		
</c:if>
	
<c:if test="${turmaVirtual.docente}">		
	<div class="botoes-show" align="center">
			<h:commandButton action="#{respostaTarefaTurma.avaliarTarefas}" value="<< Voltar"/> 
	</div>
</c:if>

<c:if test="${turmaVirtual.discente}">		
	<div class="botoes-show" align="center">
			<h:commandButton action="#{ tarefaTurma.listar }" value="<< Voltar"/> 
	</div>
</c:if>

</h:form>
</f:view>
<%@include file="/ava/rodape.jsp"%>

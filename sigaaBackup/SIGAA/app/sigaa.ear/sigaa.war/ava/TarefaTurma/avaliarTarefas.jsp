<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>

<a4j:keepAlive beanName="respostaTarefaTurma" />

<h:form id="form">

<fieldset>
<legend>Tarefas Submetidas</legend>

<div class="infoAltRem">
	<c:if test="${turmaVirtual.docente }">
		<img src="/sigaa/ava/img/selecionado.gif">: Tarefa Corrigida
		<c:if test="${respostaTarefaTurma.obj.tarefa.emGrupo}">	
			<img src="/sigaa/img/user.png"> Aluno que Enviou a Tarefa em Grupo
		</c:if>	
		<br />
		<img src="/sigaa/img/email_go.png">: Enviar Mensagem
		<h:graphicImage value="/ava/img/corrigir.png" />: Corrigir
		<img src="/sigaa/img/note.png" width="16">: Visualizar Correção
		<c:if test="${respostaTarefaTurma.obj.tarefa.envioArquivo}">
			<img src="/sigaa/ava/img/page_white_put.png">: Baixar Arquivo Enviado pelo Aluno
		</c:if>
		<c:if test="${respostaTarefaTurma.obj.tarefa.respostaOnline}">
			<h:graphicImage value="/ava/img/zoom.png" />: Visualizar Resposta
		</c:if>
		
		<br />
		<img src="/sigaa/ava/img/marcar.png">: Marcar resposta como Lida
		<img src="/sigaa/ava/img/desmarcar.png">: Marcar resposta como não Lida
		<img src="/sigaa/ava/img/bin.png">: Apagar resposta
	</c:if>
	
	<c:if test="${turmaVirtual.discente }">
		<c:if test="${respostaTarefaTurma.obj.tarefa.emGrupo}">	
			<img src="/sigaa/img/user.png"> Aluno que Enviou a Tarefa em Grupo
		</c:if>	
	</c:if>
</div>

<c:set var="respostas" value="null"/>
<c:if test="${ not empty respostaTarefaTurma.respostas }">
		
	<div style="text-align: center; border: 1px solid #DEDEDE; border-width: 1px 0; margin: 5px; 5px">
		<c:set var="tarefaVisualizadaMomento" value="${respostaTarefaTurma.obj.tarefa.titulo}"/>
		<b>Você está avaliando a tarefa: </b> <c:out value="${tarefaVisualizadaMomento}"></c:out>
	</div>
		
	<table class="listing">

		<thead>
			<tr>
				<th></th>
				<th>#</th>
				<th style="text-align:left;width:250px;">Aluno</th>
				<th style="text-align:left;">Comentários</th>
				<th style="text-align:center;width:200px;">Data/Hora de envio</th>
				<th></th>
				<c:if test="${respostaTarefaTurma.obj.tarefa.envioArquivo || respostaTarefaTurma.obj.tarefa.respostaOnline}">
					<th></th>
				</c:if>
				<th></th>
				<th></th>
				<th></th>
				<th></th>
			</tr>
		</thead>

		<tbody>
				<c:forEach items="#{ respostaTarefaTurma.respostas }" var="r" varStatus="loop">
				<tr class='${ loop.index % 2 == 0 ? "even" : "odd" }' ${ r.lida ? "style='background-color: #FFFFAA'" : "" }>
						
								<td style="border-left: 1px solid #666666;width:0px;">
									<h:graphicImage value="/ava/img/selecionado.gif" rendered="#{ r.lida }" title="Tarefa Corrigida" />
								</td>
								<td style="text-align:right;">
									${loop.index+1}
								</td>
						
								<td>
									<c:if test="${respostaTarefaTurma.obj.tarefa.emGrupo}">	
										<c:if test="${!r.existeGrupo}">	
											<h:outputText value="#{ r.usuarioEnvio.pessoa.nome }"/>
											<ufrn:help>Esta tarefa foi criada antes do SIGAA possuir a opção de Gerenciar Grupos de Alunos.</ufrn:help>
										</c:if>
										<c:if test="${r.existeGrupo}">			
											<ul>
												<li>
													<b><h:outputText value="#{ r.grupoDiscentes.nome }"/></b>
												</li>	
												<c:forEach items="#{ r.grupoDiscentes.discentes }" var="d" varStatus="loop">
													<li>
														<c:if test="${!d.removidoGrupo}">
															<h:outputText value="#{ d.pessoa.nome }"/>
														</c:if>
														<c:if test="${d.removidoGrupo}">
															<span style="text-decoration: line-through;"><h:outputText value="#{ d.pessoa.nome }" title="Aluno não se encontra mas neste grupo."/></span>
														</c:if>
														<c:if test="${r.usuarioEnvio.pessoa.id == d.pessoa.id}">
															<img src="/sigaa/img/user.png" title="Aluno que Enviou a Tarefa em Grupo">
														</c:if>
													</li>
												</c:forEach>
											</ul>
									</c:if>		
									</c:if>	
									
									<c:if test="${ not respostaTarefaTurma.obj.tarefa.emGrupo}">
										<h:outputText value="#{ r.usuarioEnvio.pessoa.nome }"/>
									</c:if>	
								</td>
								
								<td>
									<h:outputText value="#{ r.comentarios }" />
								</td>
								<td style="text-align:center;">
									<ufrn:format type="dataHora" valor="${ r.dataEnvio }"/>
								</td>
								<td class="icon">
									<c:if test="${ r.usuarioEnvio != null }">
											<a href="javascript://nop/" onclick="Mensagem.show(<%=""+br.ufrn.arq.caixa_postal.Mensagem.MENSAGEM%>, '${ r.usuarioEnvio.login }');"><img src="${ctx}/img/email_go.png" alt="Enviar Mensagem" title="Enviar Mensagem"/></a>
									</c:if> 
								</td>
										
								<c:if test="${respostaTarefaTurma.obj.tarefa.respostaOnline}">		
									<td class="icon">
										<h:commandLink id="visualizarRespostas" action="#{respostaTarefaTurma.visualizarResposta}" title="Visualizar Resposta">
											<f:param name="id" value="#{r.id}" />
											<h:graphicImage value="/ava/img/zoom.png" alt="Visualizar Resposta" />
				            			</h:commandLink>
				            		</td>
				            	</c:if>

								<td class="icon">
									<h:commandLink id="corrigir" action="#{respostaTarefaTurma.preCorrigir}" title="Corrigir">
										<f:param name="id" value="#{r.id}" />
										<h:graphicImage value="/ava/img/corrigir.png" alt="Corrigir" />
			            			</h:commandLink>
			            		</td>

								<td class="icon" rowspan="${rowspan}">
									<c:if test="${not empty r.textoCorrecao}">
									<h:commandLink action="#{respostaTarefaTurma.visualizarCorrecao}" title="Visualizar Correção">
										<f:param name="id" value="#{r.id}"></f:param>
										<h:graphicImage value="/img/note.png" width="16" />
									</h:commandLink>
									</c:if>
								</td>

								<c:if test="${respostaTarefaTurma.obj.tarefa.envioArquivo}">		
								<td class="icon">
									<h:commandLink id="idVisualizarArquivo" action="#{respostaTarefaTurma.visualizarArquivo}" title="Arquivo">
										<f:param name="idArquivo" value="#{ r.idArquivo }"/>
										<h:graphicImage value="/ava/img/page_white_put.png" alt="Baixar Arquivo Enviado pelo Aluno" title="Baixar Arquivo Enviado pelo Aluno" />
									</h:commandLink>
								</td>
								</c:if>
								
								<td class="icon">
									<c:if test="${!r.lida}">
										<h:commandLink id="marcarResposta" action="#{ respostaTarefaTurma.marcarLida }">
											<f:param name="id" value="#{ r.id }"/>
											<h:graphicImage value="/ava/img/marcar.png" title="Marcar resposta como lida" />
										</h:commandLink>
									</c:if>	
									<c:if test="${r.lida}">
										<c:if test="${empty r.textoCorrecao}">
											<h:commandLink id="marcarRespostaNaoLida" action="#{ respostaTarefaTurma.marcarLida }">
												<f:param name="id" value="#{ r.id }"/>
												<h:graphicImage value="/ava/img/desmarcar.png"  title="Marcar resposta como não lida" />
											</h:commandLink>
										</c:if>
										<c:if test="${not empty r.textoCorrecao}" >
											<h:commandLink id="marcarRespostaNaoCorrigida" action="#{ respostaTarefaTurma.marcarLida }" onclick="return(confirm('Esta resposta já foi corrigida. Deseja realmente marca-la como não lida?'))">
												<f:param name="id" value="#{ r.id }"/>
												<h:graphicImage value="/ava/img/desmarcar.png"  title="Marcar resposta como não lida" />
											</h:commandLink>
										</c:if>
									</c:if>
								</td>
								
								<td class="icon">
									<h:commandLink id="apagar" action="#{ respostaTarefaTurma.remover }" onclick="return(confirm('Se esta resposta possuir nota a nota será anulada. Deseja realmente excluir este item?'))">
										<f:param name="id" value="#{ r.id }"/>
										<h:graphicImage value="/ava/img/bin.png" title="Apagar resposta" />
									</h:commandLink>
								</td>
					</tr>
					</c:forEach>
			
			</tbody>
	</table>
</c:if>
			<c:if test="${ empty respostaTarefaTurma.respostas }">
				<br>
				<center><i>Nenhum aluno respondeu a esta tarefa.</i></center>
			</c:if>
			
				<c:if test="${ not empty respostaTarefaTurma.respostas && respostaTarefaTurma.obj.tarefa.envioArquivo }">	
						<p align="center">
							<a href="${ pageContext.request.contextPath }/baixarTarefas.do?idTarefa=${ respostaTarefaTurma.obj.tarefa.id }&idTurma=${ turmaVirtual.turma.id }"
							 onclick='<c:if test="${ empty respostaTarefaTurma.respostas }">alert("Nenhuma resposta foi enviada para esta tarefa.");</c:if>' target="_BLANK">
							 Baixar todos os arquivos</a>
						</p>
						<br/>	
				</c:if>	
		
	<div class="botoes">
		<div class="other-actions">
			<h:commandButton id="voltar" action="#{ tarefaTurma.listar }" value="<< Voltar"/> 
		</div>
	</div>
	
</fieldset>

</h:form>

</f:view>
<%@include file="/ava/rodape.jsp" %>
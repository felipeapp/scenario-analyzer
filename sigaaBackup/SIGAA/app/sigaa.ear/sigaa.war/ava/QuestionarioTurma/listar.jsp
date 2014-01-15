<%@include file="/ava/cabecalho.jsp" %>
<f:view>
	<a4j:keepAlive beanName="questionarioTurma" />

	<style>
		.botao-medio {
				margin-bottom:0px !important;
				height:60px !important;
		}
	</style>

	<%@include file="/ava/menu.jsp" %>
	<h:form id="formAva">
		<fieldset>
			<legend>Questionários</legend>
			
			<c:if test="${turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }">
				<div class="menu-botoes" style="text-align:center;width:210px;margin: 0 auto;">
					<ul class="menu-interno">
							<li class="botao-medio novaTarefa;">
								<h:commandLink action="#{ questionarioTurma.novoQuestionario }">
									<p style="margin-left:-10px;text-indent:20px;font-variant:small-caps;font-size:1.3em;font-weight:bold;">Cadastrar Questionário</p> 
								</h:commandLink>
							</li>
					</ul>	
					<div style="clear:both;"></div>	
				</div>
			</c:if>
			
			<c:if test="${fn:length(questionarioTurma.questionarios) > 0 }">
				<div class="infoAltRem">
					<img src="/sigaa/ava/img/selecionado.gif">: Notas Publicadas
					<h:graphicImage value="/img/portal_turma/avaliar_tarefa.png" />: Visualizar respostas
					<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar questionário<br/>
					<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar / Alterar perguntas do questionário
					<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover questionário
					<h:graphicImage value="/ava/img/accept.png" style="overflow: visible;" />: Publicar as notas do questionário
				</div>
				
			
				<table class="listing" style="width:80%;">
					<thead>
						<tr>
							<th style="width:20px;">&nbsp;</th>
							<th style="text-align:left;">Título</th>
							<th style="text-align:center;">Início</th>
							<th style="text-align:center;">Fim</th>
							<th style="text-align:center;">Respostas</th>
							<th style="text-align:center;">Publicado</th>
							<th style="width:20px;">&nbsp;</th>
							<th style="width:20px;">&nbsp;</th>
							<th style="width:20px;">&nbsp;</th>
							<th style="width:20px;">&nbsp;</th>
							<th style="width:20px;">&nbsp;</th>
						</tr>
					</thead>
					
					<c:forEach items="#{questionarioTurma.questionarios}" var="q" varStatus="s">
						<tr class='${ loop.index % 2 == 0 ? "even" : "odd" }' ${ q.notasPublicadas ? "style='background-color: #FFFFAA'" : "" }>
						
							<td style="border-left: 1px solid #666666;">
								<h:graphicImage value="/ava/img/selecionado.gif" rendered="#{ q.notasPublicadas }" title="Notas Publicadas" />
							</td>
						
							<td style="text-align:left;">${ q.titulo }</td>
							<td style="text-align:center;"><ufrn:format type="data" valor="${ q.inicio }" /> ${ q.horaInicio < 10 ? "0" : "" }${q.horaInicio }:${ q.minutoInicio < 10 ? "0" : "" }${ q.minutoInicio } </td>
							<td style="text-align:center;"><ufrn:format type="data" valor="${ q.fim }" /> ${ q.horaFim < 10 ? "0" : "" }${ q.horaFim }:${ q.minutoFim < 10 ? "0" : "" }${ q.minutoFim }</td>
							<td style="text-align:right;">${ q.qtdRespostas } </td>
							<td style="text-align:center;">
								<c:if test="${q.finalizado}">
									Sim
								</c:if>
								<c:if test="${ not q.finalizado}">
									Não
								</c:if>
							
							</td>
							<td>
								<h:commandLink action="#{ questionarioTurma.visualizarRespostas }" >
									<f:param name="id" value="#{ q.id }" />
									<h:graphicImage value="/img/portal_turma/avaliar_tarefa.png" title="Visualizar respostas" />
								</h:commandLink>
							</td>
							<td>
								<h:commandLink action="#{ questionarioTurma.gerenciarPerguntasDoQuestionario }">
									<f:param name="id" value="#{ q.id }" />
									<f:param name="naoSalvar" value="true" />
									<h:graphicImage value="/img/view.gif" title="Visualizar / Alterar perguntas do questionário" />
								</h:commandLink>
							</td>
							<td>
								<h:commandLink action="#{ questionarioTurma.alterarDadosDoQuestionario }">
									<f:param name="id" value="#{ q.id }" />
									<h:graphicImage value="/img/alterar.gif" title="Alterar Questionário" />
								</h:commandLink>
							</td>
							<td>
								<c:if test="${ !q.possuiNota }">
									<h:commandLink action="#{ questionarioTurma.remover }" onclick="if (!confirm('Confirma a remoção deste questionário? Esta operação não poderá ser desfeita.')) return false;">
										<f:param name="id" value="#{ q.id }" />
										<h:graphicImage value="/img/delete.gif" title="Remover Questionário" />
									</h:commandLink>
								
								</c:if>
								<c:if test="${ q.possuiNota }">
								
									<h:commandLink action="#{ questionarioTurma.remover }" 
									onclick="return(confirm('Este questionário possui nota, caso ele seja removido, as notas deste questionário na planilha de notas também serão. Deseja realmente excluir este questionário?'))">
										<f:param name="id" value="#{ q.id }" />
										<h:graphicImage value="/img/delete.gif" title="Remover Questionário" />
									</h:commandLink>
								</c:if>
							</td>
							
							<td>
								<h:commandLink action="#{ questionarioTurma.publicarNotas }" rendered="#{ q.qtdRespostas > 0 && q.possuiNota }">
									<f:param name="idQuestionario" value="#{ q.id }" />
									<h:graphicImage value="/ava/img/accept.png" title="Publicar as notas do questionário" />
								</h:commandLink>
							</td>
						</tr>
					</c:forEach>
				</table>
			</c:if>
			<c:if test="${fn:length(questionarioTurma.questionarios) == 0 }">
				<p class="empty-listing">Nenhum item foi encontrado.</p>
			</c:if>
			
		</fieldset>
	</h:form>
</f:view>
<%@include file="/ava/rodape.jsp" %>
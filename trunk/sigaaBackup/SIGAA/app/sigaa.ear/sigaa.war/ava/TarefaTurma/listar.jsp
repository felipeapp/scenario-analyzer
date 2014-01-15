
<%@include file="/ava/cabecalho.jsp"%>

<f:view>

<%@include file="/ava/menu.jsp" %>
<h:form>

<c:set var="tarefas" value="#{ tarefaTurma.listagem }"/>
<c:set var="tarefasIndividuais" value="#{ tarefaTurma.tarefasIndividuais }"/>
<c:set var="tarefasEmGrupo" value="#{ tarefaTurma.tarefasEmGrupo }"/>

<fieldset>
<legend>Tarefas</legend>

<c:if test="${turmaVirtual.docente || permissaoAva.permissaoUsuario.tarefa || permissaoAva.permissaoUsuario.docente }">
	<div class="menu-botoes" style="text-align:center;width:210px;margin: 0 auto;">
		<ul class="menu-interno">
				<li class="botao-medio novaTarefa;" style="margin-bottom:0px;height:60px;">
					<h:commandLink action="#{ tarefaTurma.novo }">
						<p style="margin-left:30px;font-variant:small-caps;font-size:1.3em;font-weight:bold;">Cadastrar Tarefa</p> 
					</h:commandLink>
				</li>
		</ul>	
		<div style="clear:both;"></div>	
	</div>
</c:if>

<c:if test="${ empty tarefas }">
<p class="empty-listing">Nenhum item foi encontrado</p>
</c:if>
<c:if test="${ not empty tarefas }">

<div class="infoAltRem">
	<c:if test="${turmaVirtual.discente }">
		<img src="/sigaa/img/portal_turma/enviar_tarefa.png">: Enviar Tarefa
		<img src="/sigaa/img/note.png" width="16">: Visualizar Tarefa Enviada/Corrigida
		<img src="/sigaa/ava/img/accept.png">: Tarefa Corrigida
	</c:if>
	<c:if test="${turmaVirtual.docente || permissaoAva.permissaoUsuario.corrigirTarefa}">
		<img src="/sigaa/img/portal_turma/avaliar_tarefa.png">: Avaliar Tarefas Enviadas
	</c:if>
	<c:if test="${turmaVirtual.docente }">
	<img src="/sigaa/ava/img/page_edit.png">: Alterar Tarefa
	<img src="/sigaa/ava/img/bin.png">: Remover Tarefa
	</c:if>
</div>

<table width="100%">
<tbody>
<tr><td>
	<c:if test="${not empty tarefas && tarefaTurma.possuiTarefaIndividual }">
	<fieldset>
	<legend>Tarefas Individuais</legend>
	
	<table class="listing" width="100%">
		<thead>
		<tr>
			<c:if test="${turmaVirtual.discente }">
				<th></th>
			</c:if>	
			<th><p align="left">Título</p></th>
			<th>Período de Entrega</th>
			<th><p align="right">Possui Nota</p></th>
			<th><p align="right">Envios</p></th>
			<c:if test="${turmaVirtual.discente }">
				<th></th>
				<th></th>
			</c:if>
			<c:if test="${turmaVirtual.docente || permissaoAva.permissaoUsuario.corrigirTarefa }">
				<th></th>
			</c:if>
			<c:if test="${turmaVirtual.docente || permissaoAva.permissaoUsuario.tarefa }">
				<th></th>
				<th></th>
			</c:if>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="#{ tarefasIndividuais }" var="item" varStatus="loop">
		
				<c:set var="rowspan" value="2" />
			
				<tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }">
				
					<c:if test="${turmaVirtual.discente }">
						<td style="border-right: 1px solid #D9D9D9; border-left: 1px solid #666666;" class="icon" rowspan="${rowspan}">
							<c:if test="${item.alunoJaEnviou && item.professorJaCorrigiu}">
								<img src="/sigaa/ava/img/accept.png">
							</c:if>
						</td>
					</c:if>
				
					<td class="first" style="font-weight: bold;">${item.titulo}</td>
		
					<td style="width:150px;text-align:center;" rowspan="${rowspan}">
					de <fmt:formatDate value="${item.dataInicio}" pattern="dd/MM/yyyy"/> às <fmt:formatNumber value="${ item.horaInicio }" minIntegerDigits="2" maxIntegerDigits="2"/>h<fmt:formatNumber value="${ item.minutoInicio }"  minIntegerDigits="2" maxIntegerDigits="2"/>
					a  <fmt:formatDate value="${item.dataEntrega}" pattern="dd/MM/yyyy"/> às <fmt:formatNumber value="${ item.horaEntrega }" minIntegerDigits="2" maxIntegerDigits="2"/>h<fmt:formatNumber value="${ item.minutoEntrega }"  minIntegerDigits="2" maxIntegerDigits="2"/></td>
				
					<c:if test="${item.possuiNota}">
						<td style="text-align:right;" rowspan="${rowspan}">Sim</td>
					</c:if>
					<c:if test="${!item.possuiNota}">
						<td style="text-align:right;" rowspan="${rowspan}">Não</td>
					</c:if>
					
					<td rowspan="2" align="right">${ item.qtdRespostas }</td>
					
					<c:if test="${turmaVirtual.discente }">
										
						<td class="icon" rowspan="${rowspan}">
							<c:if test="${ !item.alunoJaEnviou || item.permiteNovoEnvio  }">
								<h:commandLink action="#{respostaTarefaTurma.enviarTarefa}" title="Enviar tarefa">
									<f:param name="id" value="#{item.id}"></f:param>
									<h:graphicImage value="/img/portal_turma/enviar_tarefa.png"/>
								</h:commandLink>
							</c:if>
						</td>
			
						<td class="icon" rowspan="${rowspan}">
							<c:if test="${item.alunoJaEnviou}">
							<h:commandLink action="#{respostaTarefaTurma.verificarTarefasJaEnviadas}" title="Visualizar Tarefa Enviada/Corrigida">
								<f:param name="id" value="#{item.id}"></f:param>
								<h:graphicImage value="/img/note.png" width="16" />
							</h:commandLink>
							</c:if>
						</td>
						
					</c:if>
					
					<c:if test="${turmaVirtual.docente || permissaoAva.permissaoUsuario.corrigirTarefa }">
						<td class="icon" rowspan="${rowspan}">
							<h:commandLink action="#{respostaTarefaTurma.avaliarTarefas}" title="Avaliar Tarefas Enviadas">
								<f:param name="id" value="#{item.id}"></f:param>
								<h:graphicImage value="/img/portal_turma/avaliar_tarefa.png"/>
							</h:commandLink>
						</td>
					</c:if>
		
					<c:if test="${turmaVirtual.docente || permissaoAva.permissaoUsuario.tarefa }">
						<td class="icon" rowspan="${rowspan}">
							<h:commandLink title="Alterar Tarefa" action="#{tarefaTurma.editar}">
								<h:graphicImage value="/ava/img/page_edit.png"/>
								<f:param name="id" value="#{item.id}"></f:param>
							</h:commandLink>
						</td>
		
						<td class="icon" rowspan="${rowspan}">
							<c:if test="${ item.possuiNota }">
								<h:commandLink title="Remover Tarefa" action="#{tarefaTurma.remover}" 
								onclick="return(confirm('Esta tarefa possui nota, caso ela seja removida, as notas desta tarefa na planilha de notas também serão. Deseja realmente excluir este item?'))">
									<h:graphicImage value="/ava/img/bin.png" />
									<f:param name="id" value="#{item.id}"></f:param>
								</h:commandLink>
							</c:if>
							<c:if test="${ !item.possuiNota }">
								<h:commandLink title="Remover Tarefa" action="#{tarefaTurma.remover}" onclick="return(confirm('Deseja realmente excluir este item?'))">
									<h:graphicImage value="/ava/img/bin.png" />
									<f:param name="id" value="#{item.id}"></f:param>
								</h:commandLink>
							</c:if>
						</td>
					</c:if>
				</tr>
				<tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }">
					<td class="first">${item.conteudo} 
						<c:if test="${ not empty item.idArquivo and item.idArquivo != 0}">
							<a href="/sigaa/verProducao?idArquivo=${ item.idArquivo }&key=${ sf:generateArquivoKey(item.idArquivo) }">Baixar arquivo</a>
						</c:if>
					</td>
				</tr>
	
		</c:forEach>
		</tbody>
	</table>
	</fieldset>
	</c:if>
</td></tr>
<tr><td>
	<c:if test="${not empty tarefas && tarefaTurma.possuiTarefaEmGrupo }">
	<fieldset>
	<legend>Tarefas Em Grupo</legend>
	
	<table class="listing" width="100%">
		<thead>
		<tr>
			<c:if test="${turmaVirtual.discente }">
				<th></th>
			</c:if>	
			<th><p align="left">Título</p></th>
			<th>Período de Entrega</th>
			<th><p align="right">Possui Nota</p></th>
			<th><p align="right">Envios</p></th>
			<c:if test="${turmaVirtual.discente }">
				<th></th>
				<th></th>
			</c:if>
			<c:if test="${turmaVirtual.docente || permissaoAva.permissaoUsuario.corrigirTarefa }">
				<th></th>
			</c:if>
			<c:if test="${turmaVirtual.docente || permissaoAva.permissaoUsuario.tarefa }">
				<th></th>
				<th></th>
			</c:if>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="#{ tarefasEmGrupo }" var="item" varStatus="loop">
		
			<c:set var="rowspan" value="2" />
		
			<tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }">
			
				<c:if test="${turmaVirtual.discente }">
					<td style="border-right: 1px solid #D9D9D9; border-left: 1px solid #666666;" class="icon" rowspan="${rowspan}">
						<c:if test="${item.alunoJaEnviou && item.professorJaCorrigiu}">
							<img src="/sigaa/ava/img/accept.png">
						</c:if>
					</td>
				</c:if>
			
				<td class="first" style="font-weight: bold;">${item.titulo}</td>
	
				<td style="width:150px;text-align:center;" rowspan="${rowspan}">
				de <fmt:formatDate value="${item.dataInicio}" pattern="dd/MM/yyyy"/> às <fmt:formatNumber value="${ item.horaInicio }" minIntegerDigits="2" maxIntegerDigits="2"/>h<fmt:formatNumber value="${ item.minutoInicio }"  minIntegerDigits="2" maxIntegerDigits="2"/>
				a  <fmt:formatDate value="${item.dataEntrega}" pattern="dd/MM/yyyy"/> às <fmt:formatNumber value="${ item.horaEntrega }" minIntegerDigits="2" maxIntegerDigits="2"/>h<fmt:formatNumber value="${ item.minutoEntrega }"  minIntegerDigits="2" maxIntegerDigits="2"/></td>
			
				<c:if test="${ item.possuiNota }">
					<td style="text-align:right;" rowspan="${rowspan}">Sim</td>
				</c:if>
				<c:if test="${ !item.possuiNota }">
					<td style="text-align:right;" rowspan="${rowspan}">Não</td>
				</c:if>
				
				<td rowspan="2" align="right">${ item.qtdRespostas }</td>
				
				<c:if test="${turmaVirtual.discente }">
				
					<td class="icon" rowspan="${rowspan}">
						<c:if test="${ !item.alunoJaEnviou || item.permiteNovoEnvio }">
							<h:commandLink action="#{respostaTarefaTurma.enviarTarefa}" title="Enviar tarefa">
								<f:param name="id" value="#{item.id}"></f:param>
								<h:graphicImage value="/img/portal_turma/enviar_tarefa.png"/>
							</h:commandLink>
						</c:if>
					</td>
		
					<td class="icon" rowspan="${rowspan}">
						<c:if test="${item.alunoJaEnviou}">
						<h:commandLink action="#{respostaTarefaTurma.verificarTarefasJaEnviadas}" title="Visualizar Tarefa Enviada/Corrigida">
							<f:param name="id" value="#{item.id}"></f:param>
							<h:graphicImage value="/img/note.png" width="16" />
						</h:commandLink>
						</c:if>
					</td>
					
				</c:if>
				
				<c:if test="${turmaVirtual.docente || permissaoAva.permissaoUsuario.corrigirTarefa }">
					<td class="icon" rowspan="${rowspan}">
						<h:commandLink action="#{respostaTarefaTurma.avaliarTarefas}" title="Avaliar Tarefas Enviadas">
							<f:param name="id" value="#{item.id}"></f:param>
							<h:graphicImage value="/img/portal_turma/avaliar_tarefa.png"/>
						</h:commandLink>
					</td>
				</c:if>
	
				<c:if test="${turmaVirtual.docente || permissaoAva.permissaoUsuario.tarefa }">
					<td class="icon" rowspan="${rowspan}">
						<h:commandLink title="Alterar Tarefa" action="#{tarefaTurma.editar}">
							<h:graphicImage value="/ava/img/page_edit.png"/>
							<f:param name="id" value="#{item.id}"></f:param>
						</h:commandLink>
					</td>
					<td class="icon" rowspan="${rowspan}">	
					<c:if test="${ item.possuiNota }">
						<h:commandLink title="Remover Tarefa" action="#{tarefaTurma.remover}" 
						onclick="return(confirm('Esta tarefa possui nota, caso ela seja removida, as notas desta tarefa na planilha de notas também serão. Deseja realmente excluir este item?'))">
							<h:graphicImage value="/ava/img/bin.png" />
							<f:param name="id" value="#{item.id}"></f:param>
						</h:commandLink>
					</c:if>
					<c:if test="${ !item.possuiNota }">
						<h:commandLink title="Remover Tarefa" action="#{tarefaTurma.remover}" onclick="return(confirm('Deseja realmente excluir este item?'))">
							<h:graphicImage value="/ava/img/bin.png" />
							<f:param name="id" value="#{item.id}"></f:param>
						</h:commandLink>
					</c:if>
					</td>	
				</c:if>
			</tr>
			<tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }">
					<td class="first">${item.conteudo} 
						<c:if test="${ not empty item.idArquivo and item.idArquivo != 0}">
							<a href="/sigaa/verProducao?idArquivo=${ item.idArquivo }&key=${ sf:generateArquivoKey(item.idArquivo) }">Baixar arquivo</a>
						</c:if>
					</td>
			</tr>
	
		</c:forEach>
		</tbody>
	</table>
	</fieldset>
	</c:if>
</td></tr>
</tbody>
</table>
</c:if>
</fieldset>
</h:form>
</f:view>
<%@include file="/ava/rodape.jsp"%>

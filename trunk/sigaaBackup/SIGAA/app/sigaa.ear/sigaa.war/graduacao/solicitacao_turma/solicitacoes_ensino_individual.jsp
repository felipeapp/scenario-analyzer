<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/sigaa/javascript/graduacao/solicitacao_matricula.js"></script>

<style>
	table.listagem tr.curso td{
		background: #C8D5EC;
		font-weight: bold;
	}
</style>

<f:view>
	<%@include file="/graduacao/menu_coordenador.jsp" %>
	<h:outputText value="#{solicitacaoTurma.create}"/>
	<c:set var="confirmDelete" value="if (!confirm('Tem certeza que deseja negar esta solicitação de ${solicitacaoTurma.obj.tipoString}?')) return false" scope="request"/>
	<c:set var="confirmRetorno" value="if (!confirm('Tem certeza que deseja retornar esta solicitação de ${solicitacaoTurma.obj.tipoString}?')) return false" scope="request"/>

	<h2> Solicitação de Abertura de Turma &gt; Discentes Solicitantes de ${solicitacaoTurma.obj.tipoString}</h2>
	
	<div class="descricaoOperacao">
		
		<p>Caro Coordenador,</p>
		<p>
			<c:if test="${solicitacaoTurma.obj.turmaEnsinoIndividual}">
				Abaixo estão listadas as solicitações de ensino individual realizadas pelos alunos. Uma turma de ensino individual pode 
				conter no máximo 4 alunos. Caso mais de 4 alunos tenham solicitado ensino individualizado em uma disciplina você 
				deverá selecionar quais alunos irão cursar. A informação de sugestão de horário é uma sugestão de horário preferencial dele para a turma.
			</c:if>
			<c:if test="${solicitacaoTurma.obj.turmaFerias}">
				Abaixo estão listadas as solicitações de férias realizadas pelos alunos. Uma turma de férias pode 
				conter no mínimo 5 alunos. 
			</c:if>
		</p>
		<p>
			Na opção <b><h:graphicImage value="/img/seta.gif" style="overflow: visible;"/> Solicitar Abertura de Turma</b> você 
			inicia a solicitação da ${solicitacaoTurma.obj.tipoString} ao departamento.
			<c:if test="${solicitacaoTurma.obj.turmaEnsinoIndividual}"> 
			Observe que os alunos da solicitação serão automaticamente matriculados quando a turma for criada.
			</c:if> 
		</p>
		<p>
			Na opção <b><h:graphicImage value="/img/cronograma/remover.gif" style="overflow: visible;"/>Negar Solicitação</b> você 
			nega a solicitação de ${solicitacaoTurma.obj.tipoString} do aluno. 
		</p>
		<p>
			Na opção <b><h:graphicImage value="/img/graduacao/table_delete.png" style="overflow: visible;"/>Negar Todas as Solicitações do Componente</b> você 
			nega as solicitações de ${solicitacaoTurma.obj.tipoString} de todos os alunos do componente curricular selecionado.   
		</p>
		<p>
			Na opção <b><h:graphicImage value="/img/alterar_old.gif" style="overflow: visible;"/> Retornar solicitação negada</b> você 
			pode retornar para <b>pendente</b> uma solicitação que foi negada, para que possa ser atendida.
		</p>
		<c:if test="${solicitacaoTurma.obj.turmaEnsinoIndividual}">
		<p>
			Na opção <b><h:graphicImage value="/img/view.gif" style="overflow: visible;"/> Consultar Solicitações de Matrícula</b> você 
			ver o plano de matrícula do aluno no período corrente. Utilize estas informações para saber qual horário da turma solicitar 
			de acordo com os horários disponíveis dos alunos que irão cursar a turma de ensino individual.
		</p>
		</c:if>
		<p>
			Na opção <b><h:graphicImage value="/img/view2.gif" style="overflow: visible;"/> Ver histórico</b> você 
			visualiza o histórico do aluno. 
		</p>

		<c:if test="${ solicitacaoTurma.obj.permiteSolicitarTurmaSemSolicitacao && solicitacaoTurma.obj.turmaFerias }">
			<p>
				Na opção <b><h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/> Solicitar Turma</b> você 
				pode solicitar uma turma mesmo que nenhum discente tenha solicitado o componente. 
			</p>
		</c:if>
		
	</div>	
	
	<h:form id="form">
	<center>
		<div class="infoAltRem">
			<h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Solicitar Abertura de Turma
			<h:graphicImage value="/img/alterar_old.gif" style="overflow: visible;"/>: Retornar Solicitação Negada
			<br/>
			<h:graphicImage value="/img/cronograma/remover.gif" style="overflow: visible;"/>: Negar Solicitação
			<h:graphicImage value="/img/graduacao/table_delete.png" style="overflow: visible;"/>: Negar Todas as Solicitações do Componente
			<c:if test="${solicitacaoTurma.obj.turmaEnsinoIndividual}">
				<br/>
				<h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Ver Plano de Matrícula
			</c:if>
			<h:graphicImage value="/img/view2.gif"style="overflow: visible;"/>: Visualizar Histórico
	   		<br/>
			<c:if test="${ solicitacaoTurma.obj.permiteSolicitarTurmaSemSolicitacao && solicitacaoTurma.obj.turmaFerias }">
				<p>
					<h:commandLink 	action="#{solicitacaoTurma.iniciarSolicitacaoFeriasSemSolicitacao}">
						<h:graphicImage url="/img/adicionar.gif" /> Solicitação de Turma
					</h:commandLink> <br />
				</p>
			</c:if>
		</div>
	</center>

	<table class="listagem">
		<caption> Solicitações de ${solicitacaoTurma.obj.tipoString} </caption>
		<thead>
		<tr>
			<th colspan="3"> Componente </th>
			<th width="13%"> <c:if test="${solicitacaoTurma.obj.turmaEnsinoIndividual}">Sugestão de Horário </c:if></th>
			<th width="13%"> Situação </th>
			<th width="15%" style="text-align: center"> Data Solicitação </th>
			<th width="5px"> </th>
			<th width="5px"> </th>
			<th width="5px"> </th>
			<th width="5px"> </th>
		</tr>
		<tbody>
			<c:set var="idFiltro" value="-1" />
			<c:forEach items="#{solicitacaoTurma.solicitacoesEnsinoIndividual}" var="solicitacao" varStatus="status">
				<c:set var="idLoop" value="${solicitacao.componente.id}" />
				<c:if test="${ idFiltro != idLoop}">
					<c:set var="idFiltro" value="${solicitacao.componente.id}" />
					<tr class="curso">
						<td></td>
						<td colspan="7">
							${solicitacao.componente}
						</td>
							<c:choose>
							<c:when test="${solicitacaoTurma.mapaAtendimentos[solicitacao.componente.id] 
								&& ( (solicitacaoTurma.calendarioVigente.periodoSolicitacaoTurmaEnsinoIndiv && solicitacaoTurma.obj.turmaEnsinoIndividual)
									|| (solicitacaoTurma.calendarioVigente.periodoSolicitacaoTurmaFerias && solicitacaoTurma.obj.turmaFerias) ) }">
								
								<td align="center">
									<h:commandLink action="#{solicitacaoEnsinoIndividual.iniciarNegaSolicitacao}"
										title="Negar Todas as Solicitações do Componente" id="Negar_Todas_as_Solicitacoes_do_Componente">
										<f:param name="id_solicitacao" value="#{solicitacao.id}"/>
										<f:param name="todas" value="true"/>
										<h:graphicImage value="/img/graduacao/table_delete.png"/>
									</h:commandLink>
								</td>
								<td align="center">
									<h:commandLink action="#{solicitacaoTurma.selecionarSolicitacaoEnsinoIndividual}"
										title="Solicitar Abertura de Turma" id="Solicitar_Abertura_de_Turma">
										<f:param name="id_solicitacao" value="#{solicitacao.id}"/>
										<h:graphicImage value="/img/seta.gif"/>
									</h:commandLink>
								</td>
							</c:when>
							<c:otherwise>
								<td>
								</td>
								<td>
								</td>
							</c:otherwise>
							</c:choose>
					</tr>
				</c:if>
		
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td></td>
					<td>${solicitacao.discente.matricula}</td>
					<td>${solicitacao.discente.nome}</td>
					<td align="left">
						<c:if test="${solicitacaoTurma.obj.turmaEnsinoIndividual}">
						${solicitacao.sugestaoHorario}
						</c:if>
					</td>
					<td>${solicitacao.situacaoString}</td>
					<td align="center"><ufrn:format type="data" valor="${solicitacao.dataSolicitacao}" /></td>
				
					<td align="center">
						<c:if test="${solicitacao.negada 
							&& ( (solicitacaoTurma.calendarioVigente.periodoSolicitacaoTurmaEnsinoIndiv && solicitacaoTurma.obj.turmaEnsinoIndividual)
								|| (solicitacaoTurma.calendarioVigente.periodoSolicitacaoTurmaFerias && solicitacaoTurma.obj.turmaFerias) ) }">
							<h:commandLink action="#{solicitacaoEnsinoIndividual.retornarSolicitacao}"
								title="Retornar Solicitação Negada" onclick="#{confirmRetorno}" id="Retornar_Solicitacao_Negada">
								<f:param name="id_solicitacao" value="#{solicitacao.id}"/>
								<h:graphicImage value="/img/alterar_old.gif"/>
							</h:commandLink>
						</c:if>
					</td>
							
					<td align="center">
						<c:if test="${solicitacao.solicitada 
							&& ( (solicitacaoTurma.calendarioVigente.periodoSolicitacaoTurmaEnsinoIndiv && solicitacaoTurma.obj.turmaEnsinoIndividual)
								|| (solicitacaoTurma.calendarioVigente.periodoSolicitacaoTurmaFerias && solicitacaoTurma.obj.turmaFerias) ) }">
							<h:commandLink action="#{solicitacaoEnsinoIndividual.iniciarNegaSolicitacao}"
								title="Negar Solicitação" id="Negar_Solicitacao">
								<f:param name="id_solicitacao" value="#{solicitacao.id}"/>
								<h:graphicImage value="/img/cronograma/remover.gif"/>
							</h:commandLink>
						</c:if>
					</td>
					<td align="center">
						<h:commandLink action="#{historico.selecionaDiscenteForm}"
							title="Visualizar Histórico" id="Visualizar_Historico">
							<f:param name="id" value="#{solicitacao.discente.id}"/>
							<h:graphicImage value="/img/view2.gif"/>
						</h:commandLink>
					</td>
					<td align="center">
						<c:if test="${solicitacaoTurma.obj.turmaEnsinoIndividual}">
							<a href="javascript:void(0);" onclick="PainelSolicitacoesMatricula.show(${solicitacao.discente.id});">
							<img src="/sigaa/img/view.gif" alt="" class="noborder" title="Ver Plano de Matrícula"/> 
							</a>
						</c:if>
					</td>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="10" align="center">
					<h:commandButton value="Cancelar" action="#{solicitacaoTurma.cancelar}" id="cancelar" onclick="#{ confirm }"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
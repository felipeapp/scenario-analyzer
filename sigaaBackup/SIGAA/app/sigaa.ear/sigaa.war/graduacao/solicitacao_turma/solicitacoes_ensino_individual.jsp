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
	<c:set var="confirmDelete" value="if (!confirm('Tem certeza que deseja negar esta solicita��o de ${solicitacaoTurma.obj.tipoString}?')) return false" scope="request"/>
	<c:set var="confirmRetorno" value="if (!confirm('Tem certeza que deseja retornar esta solicita��o de ${solicitacaoTurma.obj.tipoString}?')) return false" scope="request"/>

	<h2> Solicita��o de Abertura de Turma &gt; Discentes Solicitantes de ${solicitacaoTurma.obj.tipoString}</h2>
	
	<div class="descricaoOperacao">
		
		<p>Caro Coordenador,</p>
		<p>
			<c:if test="${solicitacaoTurma.obj.turmaEnsinoIndividual}">
				Abaixo est�o listadas as solicita��es de ensino individual realizadas pelos alunos. Uma turma de ensino individual pode 
				conter no m�ximo 4 alunos. Caso mais de 4 alunos tenham solicitado ensino individualizado em uma disciplina voc� 
				dever� selecionar quais alunos ir�o cursar. A informa��o de sugest�o de hor�rio � uma sugest�o de hor�rio preferencial dele para a turma.
			</c:if>
			<c:if test="${solicitacaoTurma.obj.turmaFerias}">
				Abaixo est�o listadas as solicita��es de f�rias realizadas pelos alunos. Uma turma de f�rias pode 
				conter no m�nimo 5 alunos. 
			</c:if>
		</p>
		<p>
			Na op��o <b><h:graphicImage value="/img/seta.gif" style="overflow: visible;"/> Solicitar Abertura de Turma</b> voc� 
			inicia a solicita��o da ${solicitacaoTurma.obj.tipoString} ao departamento.
			<c:if test="${solicitacaoTurma.obj.turmaEnsinoIndividual}"> 
			Observe que os alunos da solicita��o ser�o automaticamente matriculados quando a turma for criada.
			</c:if> 
		</p>
		<p>
			Na op��o <b><h:graphicImage value="/img/cronograma/remover.gif" style="overflow: visible;"/>Negar Solicita��o</b> voc� 
			nega a solicita��o de ${solicitacaoTurma.obj.tipoString} do aluno. 
		</p>
		<p>
			Na op��o <b><h:graphicImage value="/img/graduacao/table_delete.png" style="overflow: visible;"/>Negar Todas as Solicita��es do Componente</b> voc� 
			nega as solicita��es de ${solicitacaoTurma.obj.tipoString} de todos os alunos do componente curricular selecionado.   
		</p>
		<p>
			Na op��o <b><h:graphicImage value="/img/alterar_old.gif" style="overflow: visible;"/> Retornar solicita��o negada</b> voc� 
			pode retornar para <b>pendente</b> uma solicita��o que foi negada, para que possa ser atendida.
		</p>
		<c:if test="${solicitacaoTurma.obj.turmaEnsinoIndividual}">
		<p>
			Na op��o <b><h:graphicImage value="/img/view.gif" style="overflow: visible;"/> Consultar Solicita��es de Matr�cula</b> voc� 
			ver o plano de matr�cula do aluno no per�odo corrente. Utilize estas informa��es para saber qual hor�rio da turma solicitar 
			de acordo com os hor�rios dispon�veis dos alunos que ir�o cursar a turma de ensino individual.
		</p>
		</c:if>
		<p>
			Na op��o <b><h:graphicImage value="/img/view2.gif" style="overflow: visible;"/> Ver hist�rico</b> voc� 
			visualiza o hist�rico do aluno. 
		</p>

		<c:if test="${ solicitacaoTurma.obj.permiteSolicitarTurmaSemSolicitacao && solicitacaoTurma.obj.turmaFerias }">
			<p>
				Na op��o <b><h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/> Solicitar Turma</b> voc� 
				pode solicitar uma turma mesmo que nenhum discente tenha solicitado o componente. 
			</p>
		</c:if>
		
	</div>	
	
	<h:form id="form">
	<center>
		<div class="infoAltRem">
			<h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Solicitar Abertura de Turma
			<h:graphicImage value="/img/alterar_old.gif" style="overflow: visible;"/>: Retornar Solicita��o Negada
			<br/>
			<h:graphicImage value="/img/cronograma/remover.gif" style="overflow: visible;"/>: Negar Solicita��o
			<h:graphicImage value="/img/graduacao/table_delete.png" style="overflow: visible;"/>: Negar Todas as Solicita��es do Componente
			<c:if test="${solicitacaoTurma.obj.turmaEnsinoIndividual}">
				<br/>
				<h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Ver Plano de Matr�cula
			</c:if>
			<h:graphicImage value="/img/view2.gif"style="overflow: visible;"/>: Visualizar Hist�rico
	   		<br/>
			<c:if test="${ solicitacaoTurma.obj.permiteSolicitarTurmaSemSolicitacao && solicitacaoTurma.obj.turmaFerias }">
				<p>
					<h:commandLink 	action="#{solicitacaoTurma.iniciarSolicitacaoFeriasSemSolicitacao}">
						<h:graphicImage url="/img/adicionar.gif" /> Solicita��o de Turma
					</h:commandLink> <br />
				</p>
			</c:if>
		</div>
	</center>

	<table class="listagem">
		<caption> Solicita��es de ${solicitacaoTurma.obj.tipoString} </caption>
		<thead>
		<tr>
			<th colspan="3"> Componente </th>
			<th width="13%"> <c:if test="${solicitacaoTurma.obj.turmaEnsinoIndividual}">Sugest�o de Hor�rio </c:if></th>
			<th width="13%"> Situa��o </th>
			<th width="15%" style="text-align: center"> Data Solicita��o </th>
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
										title="Negar Todas as Solicita��es do Componente" id="Negar_Todas_as_Solicitacoes_do_Componente">
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
								title="Retornar Solicita��o Negada" onclick="#{confirmRetorno}" id="Retornar_Solicitacao_Negada">
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
								title="Negar Solicita��o" id="Negar_Solicitacao">
								<f:param name="id_solicitacao" value="#{solicitacao.id}"/>
								<h:graphicImage value="/img/cronograma/remover.gif"/>
							</h:commandLink>
						</c:if>
					</td>
					<td align="center">
						<h:commandLink action="#{historico.selecionaDiscenteForm}"
							title="Visualizar Hist�rico" id="Visualizar_Historico">
							<f:param name="id" value="#{solicitacao.discente.id}"/>
							<h:graphicImage value="/img/view2.gif"/>
						</h:commandLink>
					</td>
					<td align="center">
						<c:if test="${solicitacaoTurma.obj.turmaEnsinoIndividual}">
							<a href="javascript:void(0);" onclick="PainelSolicitacoesMatricula.show(${solicitacao.discente.id});">
							<img src="/sigaa/img/view.gif" alt="" class="noborder" title="Ver Plano de Matr�cula"/> 
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
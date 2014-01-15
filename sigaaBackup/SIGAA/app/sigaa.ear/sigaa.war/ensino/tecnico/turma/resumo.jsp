<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:if test="${not empty param.ajaxRequest }">
<%@include file="/WEB-INF/jsp/include/head_dialog.jsp"%>
</c:if>
<style>
<!--
table.subFormulario td {text-align: left;}

table.subFormulario  tr.turmas td{
	background: #C4D2EB;
	padding-left: 10px;
	font-weight: bold;
}

table.subFormulario tr th{
	font-weight: bold; 
}

-->
</style>
<f:view>
	<c:if test="${empty param.ajaxRequest }">
	<h2 ><ufrn:subSistema/> > Resumo da Turma</h2>
	</c:if>
	<c:if test="${not empty param.ajaxRequest }">
		<h:outputText value="#{ turmaTecnicoBean.carregarTurma}"/>
	</c:if>

	<table class="formulario" width="90%">
	<caption>Dados da Turma</caption>
	<tr><td>
		<table class="subFormulario" width="100%" style="">
		<caption>Dados Básicos</caption>
			<tr>
				<th width="30%">Componente Curricular:</th>
				<td>
					<h:outputText value="#{turmaTecnicoBean.obj.disciplina.codigo} - #{turmaTecnicoBean.obj.disciplina.nome}"/>
				</td>
			</tr>
			<tr>
				<th> Tipo do Componente: </th>
				<td> ${turmaTecnicoBean.obj.disciplina.tipoComponente.descricao } </td>
			</tr>
			<tr>
				<th>CH / Créditos:</th>
				<td>
				<h:outputText value="#{turmaTecnicoBean.obj.disciplina.chTotal} h / #{turmaTecnicoBean.obj.disciplina.crTotal} crs"/>
				</td>
			</tr>
			<c:if test="${turmaTecnicoBean.obj.curso.id > 0}">
				<tr>
					<th>Curso:</th>
					<td>${turmaTecnicoBean.obj.curso.descricao}</td>
				</tr>
			</c:if>
			<c:if test="${ not empty turmaTecnicoBean.solicitacao}">
				<tr>
					<th> Tipo da Turma: </th>
					<td> ${turmaTecnicoBean.obj.tipoString} </td>
				</tr>
				<c:if test="${turmaTecnicoBean.obj.turmaFerias}">
					<tr>
						<th>Motivo da Solicitação:</th>
						<td>
							<h:outputText id="motivo" value="#{ solicitacao.motivo }"/>
						</td>
					</tr>
				</c:if>
			</c:if>
			<tr>
				<th>Docente(s):</th>
				<td>${turmaTecnicoBean.obj.docentesNomes }</td>
			</tr>
			<c:if test="${ turmaTecnicoBean.editarCodigoTurma}">
				<tr>
					<th>Código da Turma:</th>
					<td>
						<h:outputText value="#{ turmaTecnicoBean.obj.codigo }"/>
					</td>
				</tr>
			</c:if>
			<c:if test="${ !turmaTecnicoBean.obj.distancia }">
			<tr>
				<th>Local:</th>
				<td>
					<h:outputText value="#{ turmaTecnicoBean.obj.local }"/>
				</td>
			</tr>
			<tr>
				<th>Horário:</th>
				<td>
					<h:outputText value="#{ turmaTecnicoBean.obj.descricaoHorario }"/>
				</td>
			</tr>
			</c:if>
			<tr>
				<th>Ano-Período:</th>
				<td>
					<h:outputText value="#{ turmaTecnicoBean.obj.anoPeriodo }"/>
				</td>
			</tr>
			<tr>
				<th>Período de Aulas:</th>
				<td>
				<h:outputText value="#{ turmaTecnicoBean.obj.dataInicio}"/> - <h:outputText value="#{ turmaTecnicoBean.obj.dataFim}"/>
				</td>
			</tr>
			<tr>
				<th>Modalidade:</th>
				<td>
					<c:choose>
						<c:when test="${ !turmaTecnicoBean.obj.distancia }">Presencial</c:when>
						<c:otherwise>A Distância</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<c:if test="${ !turmaTecnicoBean.obj.distancia }">
			<tr>
				<th>Capacidade de Alunos:</th>
				<td>
				<h:outputText value="#{ turmaTecnicoBean.obj.capacidadeAluno }"/>
				</td>
			</tr>
			</c:if>
			<tr>
				<th>Total de Matriculados:</th>
				<td>${turmaTecnicoBean.obj.totalMatriculados }</td>
			</tr>
			<c:if test="${turmaTecnicoBean.solicitacao.turmaEnsinoIndividual || turmaTecnicoBean.solicitacao.turmaFerias}">
				<tr>
					<td colspan="2">
						<table class="subFormulario" width="100%">
						<caption>Discentes Solicitantes da Turma de ${ turmaTecnicoBean.solicitacao.turmaEnsinoIndividual ? "Ensino Individual" : "Férias" }</caption>
							<thead>
							<tr>
								<td>Curso</td>
								<td>Discentes</td>
							</tr>
							</thead>
							<c:forEach  items="#{turmaTecnicoBean.discentes }" var="discenteLoop" varStatus="status">
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
									<td> ${discenteLoop.discenteGraduacao.curso.descricao} </td>
									<td>${discenteLoop.discenteGraduacao}</td>
								</tr>
							</c:forEach>
						</table>
					</td>
				</tr>
			</c:if>
			<c:if test="${not empty turmaTecnicoBean.obj.totalSolicitacoes}">
			<tr>
				<th>Total de Solicitações de Matrícula:</th>
				<td>${turmaTecnicoBean.obj.totalSolicitacoes}</td>
			</tr>
			</c:if>
		</table>
	</td></tr>

	<c:if test="${!turmaTecnicoBean.apenasVisualizacao }">
		<tfoot>
			<tr>
				<td colspan="2">
				<h:form id="resumo">
					<c:if test="${!turmaTecnicoBean.remover }">
						<h:commandButton value="#{ turmaTecnicoBean.confirmButton }" action="#{ turmaTecnicoBean.cadastrar }" id="btCadastrar" />
						<h:commandButton value="<< Dados Gerais" action="#{ turmaTecnicoBean.formDadosGerais }" id="btDadosGerais" />
						<h:commandButton value="<< Horário da Turma" action="#{ turmaTecnicoBean.formHorarios }" id="btHorarioTurma"/>
						<h:commandButton value="<< Docentes" action="#{ turmaTecnicoBean.formDocentes }" id="btDocentes"/>
					</c:if>
					<c:if test="${turmaTecnicoBean.remover }">
						<h:commandButton value="#{ turmaTecnicoBean.confirmButton }" action="#{ turmaTecnicoBean.remover }" id="btRemover"/>
					</c:if>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ turmaTecnicoBean.cancelar }" id="btCancelar"/>
				</h:form>
				</td>
			</tr>
		</tfoot>
	</c:if>
	<c:if test="${turmaTecnicoBean.apenasVisualizacao}">
	<tfoot>
		<tr><td colspan="2">
		<h:commandButton value="<< Voltar" action="#{ turmaTecnicoBean.popularBuscaGeral}" id="btVoltar"/>
		</td></tr>
	</tfoot>
	</c:if>

	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

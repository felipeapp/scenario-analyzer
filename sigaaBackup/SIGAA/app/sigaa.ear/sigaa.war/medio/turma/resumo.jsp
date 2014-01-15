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
		<h:outputText value="#{ turmaMedio.carregarTurma}"/>
	</c:if>

	<table class="formulario" width="90%">
	<caption>Dados da Disciplina</caption>
	<tr><td>
		<table class="subFormulario" width="100%" style="">
		<caption>Dados Básicos</caption>
			<tr>
				<th width="30%">Componente Curricular:</th>
				<td>
					<h:outputText value="#{turmaMedio.obj.disciplina.codigo} - #{turmaMedio.obj.disciplina.nome}"/>
				</td>
			</tr>
			<tr>
				<th> Tipo do Componente: </th>
				<td> ${turmaMedio.obj.disciplina.tipoComponente.descricao } </td>
			</tr>
			<tr>
				<th>CH / Créditos:</th>
				<td>
				<h:outputText value="#{turmaMedio.obj.disciplina.chTotal} h / #{turmaMedio.obj.disciplina.crTotal} crs"/>
				</td>
			</tr>
			<c:if test="${turmaMedio.obj.curso.id > 0}">
				<tr>
					<th>Curso:</th>
					<td>${turmaMedio.obj.curso.descricao}</td>
				</tr>
			</c:if>
			<c:if test="${ not empty turmaMedio.solicitacao}">
				<tr>
					<th> Tipo da Turma: </th>
					<td> ${turmaMedio.obj.tipoString} </td>
				</tr>
				<c:if test="${turmaMedio.obj.turmaFerias}">
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
				<td>${turmaMedio.obj.docentesNomes }</td>
			</tr>
			<c:if test="${ turmaMedio.editarCodigoTurma}">
				<tr>
					<th>Código da Turma:</th>
					<td>
						<h:outputText value="#{ turmaMedio.obj.codigo }"/>
					</td>
				</tr>
			</c:if>
			<c:if test="${ !turmaMedio.obj.distancia }">
			<tr>
				<th>Local:</th>
				<td>
					<h:outputText value="#{ turmaMedio.obj.local }"/>
				</td>
			</tr>
			<tr>
				<th>Horário:</th>
				<td>
					<h:outputText value="#{ turmaMedio.obj.descricaoHorario }"/>
				</td>
			</tr>
			</c:if>
			<tr>
				<th>Ano-Período:</th>
				<td>
					<h:outputText value="#{ turmaMedio.obj.anoPeriodo }"/>
				</td>
			</tr>
			<tr>
				<th>Período de Aulas:</th>
				<td>
				<h:outputText value="#{ turmaMedio.obj.dataInicio}"/> - <h:outputText value="#{ turmaMedio.obj.dataFim}"/>
				</td>
			</tr>
			<tr>
				<th>Modalidade:</th>
				<td>
					<c:choose>
						<c:when test="${ !turmaMedio.obj.distancia }">Presencial</c:when>
						<c:otherwise>A Distância</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<c:if test="${ !turmaMedio.obj.distancia }">
			<tr>
				<th>Capacidade de Alunos:</th>
				<td>
				<h:outputText value="#{ turmaMedio.obj.capacidadeAluno }"/>
				</td>
			</tr>
			</c:if>
			<tr>
				<th>Total de Matriculados:</th>
				<td>${turmaMedio.obj.totalMatriculados }</td>
			</tr>
			<c:if test="${turmaMedio.solicitacao.turmaEnsinoIndividual || turmaMedio.solicitacao.turmaFerias}">
				<tr>
					<td colspan="2">
						<table class="subFormulario" width="100%">
						<caption>Discentes Solicitantes da Turma de ${ turmaMedio.solicitacao.turmaEnsinoIndividual ? "Ensino Individual" : "Férias" }</caption>
							<thead>
							<tr>
								<td>Curso</td>
								<td>Discentes</td>
							</tr>
							</thead>
							<c:forEach  items="#{turmaMedio.discentes }" var="discenteLoop" varStatus="status">
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
									<td> ${discenteLoop.discenteGraduacao.curso.descricao} </td>
									<td>${discenteLoop.discenteGraduacao}</td>
								</tr>
							</c:forEach>
						</table>
					</td>
				</tr>
			</c:if>
			<c:if test="${not empty turmaMedio.obj.totalSolicitacoes}">
			<tr>
				<th>Total de Solicitações de Matrícula:</th>
				<td>${turmaMedio.obj.totalSolicitacoes}</td>
			</tr>
			</c:if>
		</table>
	</td></tr>

	<c:if test="${ !(turmaMedio.obj.distancia || turmaMedio.solicitacao.turmaEnsinoIndividual) && !turmaMedio.remover}">
		<tr><td>
		<table class="subFormulario" width="100%">
		<caption>Reservas</caption>
			<c:if test="${ empty turmaMedio.obj.reservas }">
				<tr><td>
					<center><font color="red"><i><strong>Não há reservas para esta turma</strong></i></font></center>
				</td></tr>
			</c:if>
			<c:if test="${ not empty turmaMedio.obj.reservas }">
				<thead>
				<tr>
					<td>
						<c:if test="${turmaMedio.solicitacao.turmaFerias}"> Curso </c:if>
						<c:if test="${!turmaMedio.solicitacao.turmaFerias}"> Matriz Curricular </c:if>
					</td>
					<td>Vagas Reservadas</td>
				</tr>
				</thead>
				<c:forEach  items="${turmaMedio.obj.reservas }" var="reserva" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td>${ reserva.descricao }</td>
					<td width="20%" style="text-align: center">${reserva.vagasReservadas }</td>
				</tr>
				</c:forEach>
			</c:if>
		</table>
		</td></tr>
	</c:if>
	
	<c:if test="${!turmaMedio.apenasVisualizacao }">
		<tfoot>
			<tr>
				<td colspan="2">
				<h:form id="resumo">
					<c:if test="${!turmaMedio.remover }">
						<h:commandButton value="#{ turmaMedio.confirmButton }" action="#{ turmaMedio.cadastrar }" id="btCadastrar" />
						<h:commandButton value="<< Dados Gerais" action="#{ turmaMedio.formDadosGerais }" id="btDadosGerais" />
						<h:commandButton value="<< Horário da Turma" action="#{ turmaMedio.formHorarios }" id="btHorarioTurma"/>
						<h:commandButton value="<< Docentes" action="#{ turmaMedio.formDocentes }" id="btDocentes"/>
					</c:if>
					<c:if test="${turmaMedio.remover }">
						<h:commandButton value="#{ turmaMedio.confirmButton }" action="#{ turmaMedio.remover }" id="btRemover"/>
					</c:if>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ turmaMedio.cancelar }" id="btCancelar"/>
				</h:form>
				</td>
			</tr>
		</tfoot>
	</c:if>
	<c:if test="${turmaMedio.apenasVisualizacao}">
	<tfoot>
		<tr><td colspan="2">
		<h:commandButton value="<< Voltar" action="#{ turmaMedio.popularBuscaGeral}" id="btVoltar"/>
		</td></tr>
	</tfoot>
	</c:if>

	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

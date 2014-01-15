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

<a4j:keepAlive beanName="buscaTurmaBean"/>
<a4j:keepAlive beanName="turmaStrictoSensuBean"/>

<f:view>
	<c:if test="${acesso.chefeDepartamento}">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</c:if>
	<c:if test="${acesso.coordenadorCursoGrad or acesso.coordenacaoProbasica}">
		<%@include file="/graduacao/menu_coordenador.jsp" %>
	</c:if>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<c:if test="${empty param.ajaxRequest }">
	<h2 ><ufrn:subSistema/> > Resumo da Turma</h2>
	</c:if>
	<c:if test="${not empty param.ajaxRequest }">
		<h:outputText value="#{ turmaStrictoSensuBean.carregarTurma}"/>
	</c:if>

	<table class="formulario" width="90%">
	<caption>Dados da Turma</caption>
	<tr><td>
		<table class="subFormulario" width="100%" style="">
		<caption>Dados Básicos</caption>
			<tr>
				<th width="30%">Componente Curricular:</th>
				<td>
					<h:outputText value="#{turmaStrictoSensuBean.obj.disciplina.codigo} - #{turmaStrictoSensuBean.obj.disciplina.nome}"/>
				</td>
			</tr>
			<tr>
				<th> Tipo do Componente: </th>
				<td> ${turmaStrictoSensuBean.obj.disciplina.tipoComponente.descricao } </td>
			</tr>
			<tr>
				<th>CH / Créditos:</th>
				<td>
				<h:outputText value="#{turmaStrictoSensuBean.obj.disciplina.chTotal} h / #{turmaStrictoSensuBean.obj.disciplina.crTotal} crs"/>
				</td>
			</tr>
			<c:if test="${turmaStrictoSensuBean.obj.curso.id > 0}">
				<tr>
					<th>Curso</th>
					<td>${turmaStrictoSensuBean.obj.curso.descricao}</td>
				</tr>
			</c:if>
			<c:if test="${ not empty turmaStrictoSensuBean.solicitacao}">
				<tr>
					<th> Tipo da Turma: </th>
					<td> ${turmaStrictoSensuBean.obj.tipoString} </td>
				</tr>
				<c:if test="${turmaStrictoSensuBean.obj.turmaFerias}">
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
				<td>${turmaStrictoSensuBean.obj.docentesNomes }</td>
			</tr>
			<c:if test="${ turmaStrictoSensuBean.editarCodigoTurma}">
				<tr>
					<th>Código da Turma:</th>
					<td>
						<h:outputText value="#{ turmaStrictoSensuBean.obj.codigo }"/>
					</td>
				</tr>
			</c:if>
			<c:if test="${ !turmaStrictoSensuBean.obj.distancia }">
			<tr>
				<th>Local:</th>
				<td>
					<h:outputText value="#{ turmaStrictoSensuBean.obj.local }"/>
				</td>
			</tr>
			<tr>
				<th>Horário:</th>
				<td>
					<h:outputText value="#{ turmaStrictoSensuBean.obj.descricaoHorario }"/>
				</td>
			</tr>
			</c:if>
			<tr>
				<th>Ano-Período:</th>
				<td>
					<h:outputText value="#{ turmaStrictoSensuBean.obj.anoPeriodo }"/>
				</td>
			</tr>
			<tr>
				<th>Período de Aulas:</th>
				<td>
				<h:outputText value="#{ turmaStrictoSensuBean.obj.dataInicio}"/> - <h:outputText value="#{ turmaStrictoSensuBean.obj.dataFim}"/>
				</td>
			</tr>
			<tr>
				<th>Modalidade:</th>
				<td>
					<c:choose>
						<c:when test="${ !turmaStrictoSensuBean.obj.distancia }">Presencial</c:when>
						<c:otherwise>A Distância</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<c:if test="${ !turmaStrictoSensuBean.obj.distancia }">
			<tr>
				<th>Capacidade de Alunos:</th>
				<td>
				<h:outputText value="#{ turmaStrictoSensuBean.obj.capacidadeAluno }"/>
				</td>
			</tr>
			</c:if>
			<tr>
				<th>Total de Matriculados:</th>
				<td>${turmaStrictoSensuBean.obj.qtdMatriculados }</td>
			</tr>
			<c:if test="${turmaStrictoSensuBean.solicitacao.turmaEnsinoIndividual || turmaStrictoSensuBean.solicitacao.turmaFerias}">
				<tr>
					<td colspan="2">
						<table class="subFormulario" width="100%">
						<caption>Discentes Solicitantes da Turma de ${ turmaStrictoSensuBean.solicitacao.turmaEnsinoIndividual ? "Ensino Individual" : "Férias" }</caption>
							<thead>
							<tr>
								<td>Curso</td>
								<td>Discentes</td>
							</tr>
							</thead>
							<c:forEach  items="#{turmaStrictoSensuBean.discentes }" var="discenteLoop" varStatus="status">
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
									<td> ${discenteLoop.discenteGraduacao.curso.descricao} </td>
									<td>${discenteLoop.discenteGraduacao}</td>
								</tr>
							</c:forEach>
						</table>
					</td>
				</tr>
			</c:if>
			<c:if test="${not empty turmaStrictoSensuBean.obj.totalSolicitacoes}">
			<tr>
				<th>Total de Solicitações de Matrícula:</th>
				<td>${turmaStrictoSensuBean.obj.totalSolicitacoes}</td>
			</tr>
			</c:if>
		</table>
	</td></tr>

	<c:if test="${ turmaStrictoSensuBean.obj.graduacao && !(turmaStrictoSensuBean.obj.distancia || turmaStrictoSensuBean.solicitacao.turmaEnsinoIndividual) }">
		<tr><td>
		<table class="subFormulario" width="100%">
		<caption>Reservas</caption>
			<c:if test="${ empty turmaStrictoSensuBean.obj.reservas }">
				<tr><td>
					<center><font color="red"><i><strong>Não há reservas para esta turma</strong></i></font></center>
				</td></tr>
			</c:if>
			<c:if test="${ not empty turmaStrictoSensuBean.obj.reservas }">
				<thead>
				<tr>
					<td>
						<c:if test="${turmaStrictoSensuBean.solicitacao.turmaFerias}"> Curso </c:if>
						<c:if test="${!turmaStrictoSensuBean.solicitacao.turmaFerias}"> Matriz Curricular </c:if>
					</td>
					<td>Vagas Reservadas</td>
				</tr>
				</thead>
				<c:forEach  items="${turmaStrictoSensuBean.obj.reservas }" var="reserva" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td>${ reserva.descricao }</td>
					<td width="20%" style="text-align: center">${reserva.vagasReservadas }</td>
				</tr>
				</c:forEach>
			</c:if>
		</table>
		</td></tr>
	</c:if>
	
	<c:if test="${!turmaStrictoSensuBean.apenasVisualizacao }">
		<tfoot>
			<tr>
				<td colspan="2">
				<h:form id="resumo">
					<h:commandButton value="#{ turmaStrictoSensuBean.confirmButton }" action="#{ turmaStrictoSensuBean.cadastrar }" id="btaoCadastro"/>
					<h:commandButton value="<< Dados Gerais" action="#{ turmaStrictoSensuBean.formDadosGerais }" id="btDadosGerais"/>
					<h:commandButton value="<< Horário da Turma" action="#{ turmaStrictoSensuBean.formHorarios }" id="horaiosTurma"/>
					<h:commandButton value="<< Docentes" action="#{ turmaStrictoSensuBean.formDocentes }" id="btaoDocentes"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ turmaStrictoSensuBean.cancelar }" id="cancelarOperacao"/>
				</h:form>
				</td>
			</tr>
		</tfoot>
	</c:if>
	<c:if test="${turmaStrictoSensuBean.apenasVisualizacao}">
	<tfoot>
		<tr><td colspan="2">
		<h:commandButton value="<< Voltar" action="#{ turmaStrictoSensuBean.popularBuscaGeral}" id="botaoDevoltar"/>
		</td></tr>
	</tfoot>
	</c:if>

	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

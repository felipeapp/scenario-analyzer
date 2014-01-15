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
		<h:outputText value="#{ turmaLatoSensuBean.carregarTurma}"/>
	</c:if>

	<table class="formulario" width="90%">
	<caption>Dados da Turma</caption>
	<tr><td>
		<table class="subFormulario" width="100%" style="">
		<caption>Dados Básicos</caption>
			<tr>
				<th width="30%">Componente Curricular:</th>
				<td>
					<h:outputText value="#{turmaLatoSensuBean.obj.disciplina.codigo} - #{turmaLatoSensuBean.obj.disciplina.nome}"/>
				</td>
			</tr>
			<tr>
				<th> Tipo do Componente: </th>
				<td> ${turmaLatoSensuBean.obj.disciplina.tipoComponente.descricao } </td>
			</tr>
			<tr>
				<th>CH / Créditos:</th>
				<td>
				<h:outputText value="#{turmaLatoSensuBean.obj.disciplina.chTotal} h / #{turmaLatoSensuBean.obj.disciplina.crTotal} crs"/>
				</td>
			</tr>
			<c:if test="${turmaLatoSensuBean.obj.curso.id > 0}">
				<tr>
					<th>Curso</th>
					<td>${turmaLatoSensuBean.obj.curso.descricao}</td>
				</tr>
			</c:if>
			<c:if test="${ not empty turmaLatoSensuBean.solicitacao}">
				<tr>
					<th> Tipo da Turma: </th>
					<td> ${turmaLatoSensuBean.obj.tipoString} </td>
				</tr>
				<c:if test="${turmaLatoSensuBean.obj.turmaFerias}">
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
				<td>${turmaLatoSensuBean.obj.docentesNomes }</td>
			</tr>
			<c:if test="${ turmaLatoSensuBean.editarCodigoTurma}">
				<tr>
					<th>Código da Turma:</th>
					<td>
						<h:outputText value="#{ turmaLatoSensuBean.obj.codigo }"/>
					</td>
				</tr>
			</c:if>
			<c:if test="${ !turmaLatoSensuBean.obj.distancia }">
			<tr>
				<th>Local:</th>
				<td>
					<h:outputText value="#{ turmaLatoSensuBean.obj.local }"/>
				</td>
			</tr>
			<tr>
				<th>Horário:</th>
				<td>
					<h:outputText value="#{ turmaLatoSensuBean.obj.descricaoHorario }"/>
				</td>
			</tr>
			</c:if>
			<tr>
				<th>Ano-Período:</th>
				<td>
					<h:outputText value="#{ turmaLatoSensuBean.obj.anoPeriodo }"/>
				</td>
			</tr>
			<tr>
				<th>Período de Aulas:</th>
				<td>
				<h:outputText value="#{ turmaLatoSensuBean.obj.dataInicio}"/> - <h:outputText value="#{ turmaLatoSensuBean.obj.dataFim}"/>
				</td>
			</tr>
			<tr>
				<th>Modalidade:</th>
				<td>
					<c:choose>
						<c:when test="${ !turmaLatoSensuBean.obj.distancia }">Presencial</c:when>
						<c:otherwise>A Distância</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<c:if test="${ !turmaLatoSensuBean.obj.distancia }">
			<tr>
				<th>Capacidade de Alunos:</th>
				<td>
				<h:outputText value="#{ turmaLatoSensuBean.obj.capacidadeAluno }"/>
				</td>
			</tr>
			</c:if>
			<c:if test="${turmaLatoSensuBean.obj.totalMatriculados > 0}">
				<tr>
					<th>Total de Matriculados:</th>
					<td>${turmaLatoSensuBean.obj.totalMatriculados }</td>
				</tr>
				<tr>
					<th valign="top">Discentes Matriculados:</th>
					<td>
						<c:forEach var="matricula" items="${turmaLatoSensuBean.obj.matriculasDisciplina}" varStatus="status">
							${matricula.discente.matricula} - ${matricula.discente.nome} (${matricula.situacaoMatricula.descricao})<br/> 
						</c:forEach>
					</td>	
				</tr>
			</c:if>
			<c:if test="${turmaLatoSensuBean.solicitacao.turmaEnsinoIndividual || turmaLatoSensuBean.solicitacao.turmaFerias}">
				<tr>
					<td colspan="2">
						<table class="subFormulario" width="100%">
						<caption>Discentes Solicitantes da Turma de ${ turmaLatoSensuBean.solicitacao.turmaEnsinoIndividual ? "Ensino Individual" : "Férias" }</caption>
							<thead>
							<tr>
								<td>Curso</td>
								<td>Discentes</td>
							</tr>
							</thead>
							<c:forEach  items="#{turmaLatoSensuBean.discentes }" var="discenteLoop" varStatus="status">
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
									<td> ${discenteLoop.discenteGraduacao.curso.descricao} </td>
									<td>${discenteLoop.discenteGraduacao}</td>
								</tr>
							</c:forEach>
						</table>
					</td>
				</tr>
			</c:if>
			<c:if test="${not empty turmaLatoSensuBean.obj.totalSolicitacoes}">
			<tr>
				<th>Total de Solicitações de Matrícula:</th>
				<td>${turmaLatoSensuBean.obj.totalSolicitacoes}</td>
			</tr>
			</c:if>
		</table>
	</td></tr>

	<c:if test="${!turmaLatoSensuBean.apenasVisualizacao }">
		<tfoot>
			<tr>
				<td colspan="2">
				<h:form id="resumo">
					<h:commandButton value="#{ turmaLatoSensuBean.confirmButton }" action="#{ turmaLatoSensuBean.cadastrar }" id="btCadastrar"/>
					<c:if test="${not turmaLatoSensuBean.remover}">
						<h:commandButton value="<< Dados Gerais" action="#{ turmaLatoSensuBean.formDadosGerais }" id="btDadosGerais"/>
						<h:commandButton value="<< Horário da Turma" action="#{ turmaLatoSensuBean.formHorarios }" id="btHorarioTurma"/>
						<h:commandButton value="<< Docentes" action="#{ turmaLatoSensuBean.formDocentes }" id="btDocentes"/>
					</c:if>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ turmaLatoSensuBean.cancelar }" id="btCancelar"/>
				</h:form>
				</td>
			</tr>
		</tfoot>
	</c:if>
	<c:if test="${turmaLatoSensuBean.apenasVisualizacao}">
	<tfoot>
		<tr><td colspan="2">
		<h:commandButton value="<< Voltar" action="#{ turmaLatoSensuBean.popularBuscaGeral}" id="btVoltar"/>
		</td></tr>
	</tfoot>
	</c:if>

	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

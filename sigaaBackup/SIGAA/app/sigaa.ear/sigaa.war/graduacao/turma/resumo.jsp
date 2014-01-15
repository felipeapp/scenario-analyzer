<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:if test="${not empty param.ajaxRequest }">
<%@include file="/WEB-INF/jsp/include/head_dialog.jsp"%>
</c:if>

<style>
table.subFormulario td {text-align: left;}

table.subFormulario  tr.turmas td{
	background: #C4D2EB;
	padding-left: 10px;
	font-weight: bold;
}

table.subFormulario tr th{
	font-weight: bold; 
}

</style>

<f:view>
<a4j:keepAlive beanName="horarioTurmaBean"></a4j:keepAlive>
<a4j:keepAlive beanName="cursoGrad"></a4j:keepAlive>
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
		<h:outputText value="#{ turmaGraduacaoBean.carregarTurma}"/>
	</c:if>

	<table class="formulario" width="90%">
	<caption>Dados da Turma</caption>
	<tr><td>
		<table class="subFormulario" width="100%" style="">
		<caption>Dados Básicos</caption>
			<tr>
				<th width="20%">Componente Curricular:</th>
				<td>
					<h:outputText value="#{turmaGraduacaoBean.obj.disciplina.codigo} - #{turmaGraduacaoBean.obj.disciplina.nome}"/>
				</td>
			</tr>
			<tr>
				<th> Tipo do Componente: </th>
				<td> ${turmaGraduacaoBean.obj.disciplina.tipoComponente.descricao } </td>
			</tr>
			<tr>
				<th>CH / Créditos:</th>
				<td>
				<h:outputText value="#{turmaGraduacaoBean.obj.disciplina.chTotal} h / #{turmaGraduacaoBean.obj.disciplina.crTotal} crs"/>
				</td>
			</tr>
			<c:if test="${turmaGraduacaoBean.obj.curso.id > 0}">
				<tr>
					<th>Curso</th>
					<td>${turmaGraduacaoBean.obj.curso.descricao}</td>
				</tr>
			</c:if>
			<c:if test="${ not empty turmaGraduacaoBean.solicitacao}">
				<tr>
					<th> Tipo da Turma: </th>
					<td> ${turmaGraduacaoBean.obj.tipoString} </td>
				</tr>
				<c:if test="${turmaGraduacaoBean.obj.turmaFerias and not empty solicitacao.motivo}">
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
				<td>
					<c:forEach items="#{turmaGraduacaoBean.obj.docentesNomesHorarios}" var="nomeHorario">
						${nomeHorario}<br/>
					</c:forEach>
				</td>
			</tr>
			<c:if test="${ turmaGraduacaoBean.editarCodigoTurma}">
				<tr>
					<th>Código da Turma:</th>
					<td>
						<c:if test="${turmaGraduacaoBean.obj.turmaAgrupadora.id > 0}">
							Será atribuído automaticamente.
						</c:if>
						<c:if test="${turmaGraduacaoBean.obj.turmaAgrupadora == null || turmaGraduacaoBean.obj.turmaAgrupadora.id == 0}">
							<c:if test="${not empty turmaGraduacaoBean.obj.codigo}">
								<h:outputText value="#{ turmaGraduacaoBean.obj.codigo }"/>
							</c:if>
							<c:if test="${empty turmaGraduacaoBean.obj.codigo}">
								Será atribuído automaticamente
							</c:if>
						</c:if>
					</td>
				</tr>
			</c:if>
			<c:if test="${ !turmaGraduacaoBean.obj.distancia }">
			<tr>
				<th>Local:</th>
				<td>
					<h:outputText value="#{ turmaGraduacaoBean.obj.local }"/>
				</td>
			</tr>
			<c:if test="${ not turmaGraduacaoBean.obj.turmaEnsinoIndividual }">			
			<tr>
				<th>Horário:</th>
				<td>
					<h:outputText value="#{ turmaGraduacaoBean.obj.descricaoHorario }"/>
				</td>
			</tr>
			</c:if>
			</c:if>
			<tr>
				<th>Ano-Período:</th>
				<td>
					<h:outputText value="#{ turmaGraduacaoBean.obj.anoPeriodo }"/>
				</td>
			</tr>
			<tr>
				<th>Período de Aulas:</th>
				<td>
				<h:outputText value="#{ turmaGraduacaoBean.obj.dataInicio}"/> - <h:outputText value="#{ turmaGraduacaoBean.obj.dataFim}"/>
				</td>
			</tr>
			<tr>
				<th>Modalidade:</th>
				<td>
					<c:choose>
						<c:when test="${ !turmaGraduacaoBean.obj.distancia }">Presencial</c:when>
						<c:otherwise>A Distância</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<c:if test="${ !turmaGraduacaoBean.obj.distancia }">
			<tr>
				<th>Capacidade de Alunos:</th>
				<td>
				<h:outputText value="#{ turmaGraduacaoBean.obj.capacidadeAluno }"/>
				</td>
			</tr>
			</c:if>
			<c:if test="${not empty turmaGraduacaoBean.obj.totalMatriculados }">
				<tr>
					<th>Total de Matriculados:</th>
					<td>${turmaGraduacaoBean.obj.totalMatriculados }</td>
				</tr>
			</c:if>
			<c:if test="${ turmaGraduacaoBean.exibeDiscentesSolicitantes }">
				<tr>
					<td colspan="2">
						<table class="subFormulario" width="100%">
						<caption>Discentes Solicitantes da Turma de ${ turmaGraduacaoBean.solicitacao.turmaEnsinoIndividual ? "Ensino Individual" : "Férias" }</caption>
							<thead>
							<tr>
								<td>Curso</td>
								<td>Discentes</td>
							</tr>
							</thead>
							<c:forEach  items="#{turmaGraduacaoBean.discentes }" var="discenteLoop" varStatus="status">
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
									<td> ${discenteLoop.discenteGraduacao.curso.descricao} </td>
									<td>${discenteLoop.discenteGraduacao}</td>
								</tr>
							</c:forEach>
						</table>
					</td>
				</tr>
			</c:if>
		</table>
	</td></tr>
	<c:if test="${ not empty turmaGraduacaoBean.obj.reservas }">
		<tr>
			<td colspan="2">
			<table class="subFormulario" width="100%">
			<caption>Reservas</caption>
					<thead>
					<tr>
						<td width="80%">
							<c:choose>
								<c:when test="${turmaGraduacaoBean.solicitacao.turmaFerias}"> Curso </c:when>
								<c:otherwise> Matriz Curricular </c:otherwise>
							</c:choose>
						</td>
						<td style="text-align:right" width="10%">Vagas Reservadas</td>
						<td style="text-align:right" width="10%">Vagas para Ingressantes</td>
					</tr>
					</thead>
					<c:forEach  items="${turmaGraduacaoBean.obj.reservas }" var="reserva" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
						<td>${ reserva.descricao }</td>
						<td style="text-align: right;">${reserva.vagasReservadas }</td>
						<td style="text-align: right;">${reserva.vagasReservadasIngressantes }</td>
					</tr>
					</c:forEach>
			</table>
			</td>
		</tr>
	</c:if>
	<c:if test="${!turmaGraduacaoBean.apenasVisualizacao }">
		<tfoot>
			<tr>
				<td colspan="3">
				<h:form id="resumo">
					<h:commandButton value="#{ turmaGraduacaoBean.confirmButton }" action="#{ turmaGraduacaoBean.cadastrar }" id="btCadastrar"/>
					<h:commandButton value="<< Dados Gerais" action="#{ turmaGraduacaoBean.formDadosGerais }" id="btDadosGerais"/>
					<h:commandButton value="<< Horário da Turma" action="#{ turmaGraduacaoBean.formHorarios }" rendered="#{turmaGraduacaoBean.defineHorario}" id="btHorarioTurma"/>
					<h:commandButton value="<< Docentes" action="#{ turmaGraduacaoBean.formDocentes }" rendered="#{turmaGraduacaoBean.defineDocentes}" id="btDocentes"/>
					<ufrn:checkRole papeis="<%= new int[] {SigaaPapeis.DAE, SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SEDIS} %>">
						<h:commandButton value="<< Reservas de Curso" action="#{ turmaGraduacaoBean.formReservaCurso }" id="btReservaCurso" rendered="#{ !turmaGraduacaoBean.obj.turmaFerias }"/>
					</ufrn:checkRole>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ turmaGraduacaoBean.cancelar }" id="btCancelar"/>
				</h:form>
				</td>
			</tr>
		</tfoot>
	</c:if>
	<c:if test="${turmaGraduacaoBean.apenasVisualizacao}">
	<tfoot>
		<tr><td colspan="2">
		<h:commandButton value="<< Voltar" action="#{ turmaGraduacaoBean.popularBuscaGeral}" id="btVoltar"/>
		</td></tr>
	</tfoot>
	</c:if>

	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

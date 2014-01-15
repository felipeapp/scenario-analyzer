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
		<h:outputText value="#{ turmaResidenciaMedica.carregarTurma}"/>
	</c:if>

	<table class="formulario" width="90%">
	<caption>Dados da Turma</caption>
	<tr><td>
		<table class="subFormulario" width="100%" style="">
		<caption>Dados Básicos</caption>
			<tr>
				<th width="20%">Componente Curricular:</th>
				<td>
					<h:outputText value="#{turmaResidenciaMedica.obj.disciplina.codigo} - #{turmaResidenciaMedica.obj.disciplina.nome}"/>
				</td>
			</tr>
			<tr>
				<th> Tipo do Componente: </th>
				<td> ${turmaResidenciaMedica.obj.disciplina.tipoComponente.descricao } </td>
			</tr>
			<tr>
				<th>CH / Créditos:</th>
				<td>
				<h:outputText value="#{turmaResidenciaMedica.obj.disciplina.chTotal} h / #{turmaResidenciaMedica.obj.disciplina.crTotal} crs"/>
				</td>
			</tr>
			<c:if test="${turmaResidenciaMedica.obj.curso.id > 0}">
				<tr>
					<th>Curso</th>
					<td>${turmaResidenciaMedica.obj.curso.descricao}</td>
				</tr>
			</c:if>
			<c:if test="${ not empty turmaResidenciaMedica.solicitacao}">
				<tr>
					<th> Tipo da Turma: </th>
					<td> ${turmaResidenciaMedica.obj.tipoString} </td>
				</tr>
				<c:if test="${turmaResidenciaMedica.obj.turmaFerias and not empty solicitacao.motivo}">
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
					<c:forEach items="#{turmaResidenciaMedica.obj.docentesNomesHorarios}" var="nomeHorario">
						${nomeHorario}<br/>
					</c:forEach>
				</td>
			</tr>
			<c:if test="${ turmaResidenciaMedica.editarCodigoTurma}">
				<tr>
					<th>Código da Turma:</th>
					<td>
						<c:if test="${turmaResidenciaMedica.obj.turmaAgrupadora.id > 0}">
							Será atribuído automaticamente.
						</c:if>
						<c:if test="${turmaResidenciaMedica.obj.turmaAgrupadora == null || turmaResidenciaMedica.obj.turmaAgrupadora.id == 0}">
							<c:if test="${not empty turmaResidenciaMedica.obj.codigo}">
								<h:outputText value="#{ turmaResidenciaMedica.obj.codigo }"/>
							</c:if>
							<c:if test="${empty turmaResidenciaMedica.obj.codigo}">
								Será atribuído automaticamente
							</c:if>
						</c:if>
					</td>
				</tr>
			</c:if>
			<c:if test="${ !turmaResidenciaMedica.obj.distancia }">
			<tr>
				<th>Local:</th>
				<td>
					<h:outputText value="#{ turmaResidenciaMedica.obj.local }"/>
				</td>
			</tr>
			
			<tr>
				<th>Horário:</th>
				<td>
					<h:outputText value="#{ turmaResidenciaMedica.obj.descricaoHorario }"/>
				</td>
			</tr>
			</c:if>
			<tr>
				<th>Ano-Período:</th>
				<td>
					<h:outputText value="#{ turmaResidenciaMedica.obj.anoPeriodo }"/>
				</td>
			</tr>
			<tr>
				<th>Período de Aulas:</th>
				<td>
				<h:outputText value="#{ turmaResidenciaMedica.obj.dataInicio}"/> - <h:outputText value="#{ turmaResidenciaMedica.obj.dataFim}"/>
				</td>
			</tr>
			<tr>
				<th>Modalidade:</th>
				<td>
					<c:choose>
						<c:when test="${ !turmaResidenciaMedica.obj.distancia }">Presencial</c:when>
						<c:otherwise>A Distância</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<c:if test="${ !turmaResidenciaMedica.obj.distancia }">
			<tr>
				<th>Capacidade de Alunos:</th>
				<td>
				<h:outputText value="#{ turmaResidenciaMedica.obj.capacidadeAluno }"/>
				</td>
			</tr>
			</c:if>
			<c:if test="${not empty turmaResidenciaMedica.obj.totalMatriculados }">
				<tr>
					<th>Total de Matriculados:</th>
					<td>${turmaResidenciaMedica.obj.totalMatriculados }</td>
				</tr>
			</c:if>
		</table>
	</td></tr>
	
	<c:if test="${!turmaResidenciaMedica.apenasVisualizacao }">
		<tfoot>
			<tr>
				<td colspan="2">
				<h:form id="resumo">
					<h:commandButton value="#{ turmaResidenciaMedica.confirmButton }" action="#{ turmaResidenciaMedica.cadastrar }" id="btCadastrar"/>
					<h:commandButton value="<< Dados Gerais" action="#{ turmaResidenciaMedica.formDadosGerais }" id="btDadosGerais"/>
					<h:commandButton value="<< Horário da Turma" action="#{ turmaResidenciaMedica.formHorarios }" rendered="#{turmaResidenciaMedica.defineHorario}" id="btHorarioTurma"/>					
					<h:commandButton value="<< Docentes" action="#{ turmaResidenciaMedica.formDocentes }" rendered="#{turmaResidenciaMedica.defineDocentes}" id="btDocentes"/>
					<ufrn:checkRole papeis="<%= new int[] {SigaaPapeis.DAE} %>">
						<h:commandButton value="<< Reservas de Curso" action="#{ turmaResidenciaMedica.formReservaCurso }" id="btReservaCurso" rendered="#{!turmaResidenciaMedica.turmaEad}" />
					</ufrn:checkRole>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ turmaResidenciaMedica.cancelar }" id="btCancelar"/>
				</h:form>
				</td>
			</tr>
		</tfoot>
	</c:if>
	<c:if test="${turmaResidenciaMedica.apenasVisualizacao}">
	<tfoot>
		<tr><td colspan="2">
		<h:commandButton value="<< Voltar" action="#{ turmaResidenciaMedica.popularBuscaGeral}" id="btVoltar"/>
		</td></tr>
	</tfoot>
	</c:if>

	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

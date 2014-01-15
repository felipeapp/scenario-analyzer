<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:if test="${not empty para.ajaxRequest }">
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
	<h2 ><ufrn:subSistema/> > Remover Turma</h2>
	</c:if>
	<c:if test="${not empty param.ajaxRequest }">
		<h:outputText value="#{ turmaBean.carregarTurma}"/>
	</c:if>

	<table class="visualizacao" width="90%">
		<caption>Dados da Turma</caption>
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
		<tr>
			<th> Tipo da Turma: </th>
			<td> ${turmaStrictoSensuBean.obj.tipoString} </td>
		</tr>
		<tr>
			<th>Docente(s):</th>
			<td>${turmaStrictoSensuBean.obj.docentesNomes }</td>
		</tr>
		<tr>
			<th>Código da Turma:</th>
			<td>
				<h:outputText value="#{ turmaStrictoSensuBean.obj.codigo }"/>
			</td>
		</tr>
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
		<c:if test="${turmaStrictoSensuBean.obj.totalMatriculados > 0}">
			<tr>
				<th>Total de Matriculados:</th>
				<td>${turmaStrictoSensuBean.obj.totalMatriculados }</td>
			</tr>
			<tr>
				<th>Discentes Matriculados:</th>
				<td>
					<c:forEach var="matricula" items="${turmaStrictoSensuBean.obj.matriculasDisciplina}">
						${matricula.discente.matricula} - $(matricula.discente.nome} (${matricula.situacaoMatricula.descricao})<br/> 
					</c:forEach>
				</td>	
			</tr>
		</c:if>
		<tfoot>
			<tr>
				<td colspan="2">
				<h:form id="resumo">
					<h:commandButton value="#{ turmaStrictoSensuBean.confirmButton }" action="#{ turmaStrictoSensuBean.remover }" id="tbnRemovTurma"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ turmaStrictoSensuBean.cancelar }" id="cancelarbt"/>
				</h:form>
				</td>
			</tr>
		<tfoot>
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

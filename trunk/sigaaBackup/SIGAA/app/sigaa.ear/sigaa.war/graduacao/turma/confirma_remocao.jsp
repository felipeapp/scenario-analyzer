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
<f:view>
	<c:if test="${not empty turmaGraduacaoBean.obj.matriculasDisciplina}">
		<c:set var="alertaDiscentesMatriculados" value="return confirm('ATENÇÃO! Ao confirmar esta operação, as matrículas em componentes dos discentes também serão excluídas!');"
		scope="application"/>
	</c:if>
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
		<tr>
			<th> Tipo da Turma: </th>
			<td> ${turmaGraduacaoBean.obj.tipoString} </td>
		</tr>
		<tr>
			<th>Docente(s):</th>
			<td>${turmaGraduacaoBean.obj.docentesNomes }</td>
		</tr>
		<tr>
			<th>Código da Turma:</th>
			<td>
				<h:outputText value="#{ turmaGraduacaoBean.obj.codigo }"/>
			</td>
		</tr>
		<c:if test="${ !turmaGraduacaoBean.obj.distancia }">
		<tr>
			<th>Local:</th>
			<td>
				<h:outputText value="#{ turmaGraduacaoBean.obj.local }"/>
			</td>
		</tr>
		<tr>
			<th>Horário:</th>
			<td>
				<h:outputText value="#{ turmaGraduacaoBean.obj.descricaoHorario }"/>
			</td>
		</tr>
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
		<c:if test="${turmaGraduacaoBean.obj.totalMatriculados > 0}">
			<tr>
				<th>Total de Matriculados:</th>
				<td>${turmaGraduacaoBean.obj.totalMatriculados }</td>
			</tr>
			<tr>
				<th valign="top">Discentes Matriculados:</th>
				<td>
					<c:forEach var="matricula" items="${turmaGraduacaoBean.obj.matriculasDisciplina}" varStatus="status">
						${matricula.discente.matricula} - ${matricula.discente.nome} (${matricula.situacaoMatricula.descricao})<br/> 
					</c:forEach>
				</td>	
			</tr>
		</c:if>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:form id="resumo">				
						<input type="button" value="<< Voltar" onclick="javascript:history.go(-1)" />						
						<h:commandButton value="#{ turmaGraduacaoBean.confirmButton }" action="#{ turmaGraduacaoBean.remover }" id="btRemover"
						onclick="#{alertaDiscentesMatriculados}"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ turmaGraduacaoBean.cancelar }" id="btCancelar"/>
					</h:form>
				</td>
			</tr>
		<tfoot>
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>


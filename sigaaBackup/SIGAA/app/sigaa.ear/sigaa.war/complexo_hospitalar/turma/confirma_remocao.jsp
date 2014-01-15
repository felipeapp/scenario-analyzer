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
	<h2 ><ufrn:subSistema/> > Remover Turma</h2>

	<table class="visualizacao" width="90%">
		<caption>Dados da Turma</caption>
		<tr>
			<th width="30%">Componente Curricular:</th>
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
		<tr>
			<th> Tipo da Turma: </th>
			<td> ${turmaResidenciaMedica.obj.tipoString} </td>
		</tr>
		<tr>
			<th>Docente(s):</th>
			<td>${turmaResidenciaMedica.obj.docentesNomes }</td>
		</tr>
		<tr>
			<th>Código da Turma:</th>
			<td>
				<h:outputText value="#{ turmaResidenciaMedica.obj.codigo }"/>
			</td>
		</tr>
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
		<c:if test="${turmaResidenciaMedica.obj.totalMatriculados > 0}">
			<tr>
				<th>Total de Matriculados:</th>
				<td>${turmaResidenciaMedica.obj.totalMatriculados }</td>
			</tr>
			<tr>
				<th>Discentes Matriculados:</th>
				<td>
					<c:forEach var="matricula" items="${turmaResidenciaMedica.obj.matriculasDisciplina}">
						${matricula.discente.matricula} - $(matricula.discente.nome} (${matricula.situacaoMatricula.descricao})<br/> 
					</c:forEach>
				</td>	
			</tr>
		</c:if>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:form id="resumo">
						<h:commandButton value="#{ turmaResidenciaMedica.confirmButton }" action="#{ turmaResidenciaMedica.remover }" id="remover"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ turmaResidenciaMedica.cancelar }" id="cancelar"/>
					</h:form>
				</td>
			</tr>
		<tfoot>
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<f:view>
<%@include file="/portais/turma/cabecalho.jsp"%>
<style type="text/css">
fieldset { padding: 10px; }
span.ementa { padding: 5px; }
span.ementa h5 { text-align: center; padding: 10px; font-size: 1em; }
</style>

<h3>Plano de Ensino</h3>

<h:outputText value="#{ portalTurma.create }"/>

<fieldset>
<legend>Dados do Componente Curricular</legend>

<table cellpadding="3">
	<tr>
		<td><strong>Código:</strong> </td>
		<td>${ portalTurma.obj.turma.disciplina.codigo }</td>
	</tr>
	<tr>
		<td><strong>Disciplina:</strong> </td>
		<td>${ portalTurma.obj.turma.disciplina.detalhes.nome }</td>
	</tr>
	<tr>
		<td><strong>Turma:</strong> </td>
		<td>${ portalTurma.obj.turma.codigo }</td>
	</tr>
	<tr>
		<td><strong>Período:</strong> </td>
		<td>${ portalTurma.obj.turma.anoPeriodo }</td>
	</tr>
	<tr>
		<td><strong>Horário:</strong> </td>
		<td>${ portalTurma.obj.turma.descricaoHorario }</td>
	</tr>
	<tr>
		<td><strong>Professor(es):</strong> </td>
		<td>
			<c:forEach var="docente" items="${ portalTurma.obj.turma.docentes }">
				${ docente.pessoa.nome }<br/>
			</c:forEach>
		</td>
	</tr>
	<tr>
		<td><strong>Carga Horária Total:</strong> </td>
		<td>${ portalTurma.obj.turma.disciplina.detalhes.chTotal } horas</td>
	</tr>
	<tr>
		<td><strong>Carga Horária de Aulas:</strong> </td>
		<td>${ portalTurma.obj.turma.disciplina.detalhes.chAula } horas</td>
	</tr>
	<tr>
		<td><strong>Carga Horária de Laboratório:</strong> </td>
		<td>${ portalTurma.obj.turma.disciplina.detalhes.chLaboratorio } horas</td>
	</tr>
	<tr>
		<td><strong>Carga Horária de Estágio:</strong> </td>
		<td>${ portalTurma.obj.turma.disciplina.detalhes.chEstagio } horas</td>
	</tr>
</table>
</fieldset>

<fieldset>
<legend>Objetivos</legend>
${ portalTurma.obj.objetivos }
</fieldset>

<fieldset>
<legend>Ementa</legend>
<span class="ementa">
	<c:if test="${ not empty portalTurma.obj.turma.disciplina.detalhes.ementa }">
	${ portalTurma.obj.turma.disciplina.detalhes.ementa }
	</c:if>
	<c:if test="${ empty portalTurma.obj.turma.disciplina.detalhes.ementa }">
	<h5>Não há ementa cadastrada para este componente curricular</h5>
	</c:if>
</span>
</fieldset>

<fieldset>
<legend>Metodologia</legend>
${ portalTurma.obj.metodologia }
</fieldset>

<fieldset>
<legend>Bibliografia</legend>
${ portalTurma.obj.bibliografia }
</fieldset>

	
</f:view>
<%@include file="/portais/turma/rodape.jsp"%>

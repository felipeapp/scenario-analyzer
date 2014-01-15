
<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form>


<fieldset>
<legend>Plano de Ensino</legend>

<table cellpadding="3">
	<tr>
		<td><strong>C�digo:</strong> </td>
		<td>${ planoEnsino.object.turma.disciplina.codigo }</td>
	</tr>
	<tr>
		<td><strong>Disciplina:</strong> </td>
		<td>${ planoEnsino.object.turma.disciplina.detalhes.nome }</td>
	</tr>
	<tr>
		<td><strong>Turma:</strong> </td>
		<td>${ planoEnsino.object.turma.codigo }</td>
	</tr>
	<tr>
		<td><strong>Per�odo:</strong> </td>
		<td>${ planoEnsino.object.turma.anoPeriodo }</td>
	</tr>
	<tr>
		<td><strong>Hor�rio:</strong> </td>
		<td>${ planoEnsino.object.turma.descricaoHorario }</td>
	</tr>
	<tr>
		<td><strong>Professor(es):</strong> </td>
		<td>
			<c:forEach var="docente" items="${ planoEnsino.object.turma.docentes }">
				${ docente.pessoa.nome }<br/>
			</c:forEach>
		</td>
	</tr>
	<tr>
		<td><strong>Carga Hor�ria Total:</strong> </td>
		<td>${ planoEnsino.object.turma.disciplina.detalhes.chTotal } horas</td>
	</tr>
	<tr>
		<td><strong>Carga Hor�ria de Aulas:</strong> </td>
		<td>${ planoEnsino.object.turma.disciplina.detalhes.chAula } horas</td>
	</tr>
	<tr>
		<td><strong>Carga Hor�ria de Laborat�rio:</strong> </td>
		<td>${ planoEnsino.object.turma.disciplina.detalhes.chLaboratorio } horas</td>
	</tr>
	<tr>
		<td><strong>Carga Hor�ria de Est�gio:</strong> </td>
		<td>${ planoEnsino.object.turma.disciplina.detalhes.chEstagio } horas</td>
	</tr>
</table>
</fieldset>

<fieldset>
<legend>Objetivos</legend>
${ planoEnsino.object.objetivos }
</fieldset>

<fieldset>
<legend>Ementa</legend>
<span class="ementa">
	<c:if test="${ not empty planoEnsino.object.turma.disciplina.detalhes.ementa }">
	${ planoEnsino.object.turma.disciplina.detalhes.ementa }
	</c:if>
	<c:if test="${ empty planoEnsino.object.turma.disciplina.detalhes.ementa }">
	<h5>N�o h� ementa cadastrada para este componente curricular</h5>
	</c:if>
</span>
</fieldset>

<fieldset>
<legend>Metodologia</legend>
${ planoEnsino.object.metodologia }
</fieldset>

<fieldset>
<legend>Bibliografia</legend>
${ planoEnsino.object.bibliografia }
</fieldset>

</fieldset>

</h:form>
</f:view>

<%@include file="/ava/rodape.jsp" %>
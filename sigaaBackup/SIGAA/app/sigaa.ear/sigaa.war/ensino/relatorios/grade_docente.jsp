<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<%@page import="br.ufrn.academico.dominio.NivelEnsino"%>
<%@page import="java.util.Date"%><link rel="stylesheet" media="all" href="/shared/css/grade_docente.css" type="text/css" />
<jwr:style src="/css/agenda.css" media="all" />

<f:view>
<h2>Grade de Horários do Docente</h2>

<div id="parametrosRelatorio">
	<table>
		<tr><th>Ano Semestre:</th><td colspan="3">${ gradeDocente.anoSemestre }</td></tr>
		<tr><th>Siape:</th><td>${ gradeDocente.docente.siape }</td><td></td><td></td></tr>
		<tr><th>Nome: </th><td>${ gradeDocente.docente.pessoa.nome }</td></tr>
		<tr><th>Unidade: </th><td>${ gradeDocente.docente.unidade.nome }</td></tr>
	</table>
</div>

<c:set var="turmas" value="#{ gradeDocente.turmas }"/>
<c:set var="horariosTurma" value="#{ gradeDocente.horariosTurma }"/>

<br />

<c:set var="nivelGrupo" value="" />
<c:forEach var="item" items="#{ turmas }">
	<c:if test="${nivelGrupo != item.disciplina.nivel}">
		<c:if test="${fechaTabela}">
			</tbody>
			</table>
			<br/>
		</c:if>
		<c:set var="count" value="0" />
		<c:forEach var="turmaCount" items="#{turmas}">
			<c:if test="${turmaCount.disciplina.nivelDesc == item.disciplina.nivelDesc}"><c:set var="count" value="${count + 1}" /></c:if>
		</c:forEach>
		<table id="matriculas" class="tabelaRelatorioBorda">
		<caption>Turmas de ${ item.disciplina.nivelDesc } (${count})</caption>
		<thead>
			<tr>
				<th width="10%" style="text-align: left">Cód.</th>
				<th width="45%">Disciplinas/Docentes</th>
				<th width="5%" style="text-align: left">Turma</th>
				<th width="20%" style="text-align: left">Local</th>
				<th width="20%" style="text-align: left">Horário</th>
			</tr>
		</thead>
		<tbody>
		<c:set var="nivelGrupo" value="${item.disciplina.nivel}" />
		<c:set var="fechaTabela" value="true" />
	</c:if>
			<tr>
				<td class="codigo" style="text-align: left">${ item.disciplina.codigo }<br/>
					  	(${ item.disciplina.tipoComponente.descricao })</td>
				<td valign="top">
					<span class="componente">
						${ item.disciplina.nome } 
						<span class="nivelComponente"> (${item.anoPeriodo}) </span>
					</span>
					<span class="docente">${ item.docentesNomes }</span>
				</td>
				<td class="turma" style="text-align: left">${ item.codigo }</td>
				<td class="status" style="font-variant: small-caps; text-align: left;">${ item.local }</td>
				<td class="horario" style="text-align: left">${ item.descricaoHorario }</td>
			</tr>
</c:forEach>
		</tbody>
		</table>

<c:if test="${not empty horariosTurma}">
<br />
	<c:set var="nivelGrupo" value="" />
	<c:forEach var="nivel" items="#{gradeDocente.niveisEnsinoTurmas}">
		<c:set var="possuiHorario" value="false" />
		<c:forEach items="${gradeDocente.horarios}" var="_horario" varStatus="s">
			<c:if test="${_horario.nivel == nivel}">
				<c:set var="possuiHorario" value="true" />
			</c:if>
		</c:forEach>
		<c:if test="${possuiHorario }">
			<h4>Tabela de Horários para <h:outputText value="#{nivel}" converter="convertNivelEnsino" />:</h4>
		
			<table width="80%" id="horario" align="center">
				<tr class="titulo" style="background-color: #333366; color: white; font-weight: bold">
					<td align="center">Horários</td>
					<td width="13%" align="center">Seg</td>
					<td width="13%" align="center">Ter</td>
					<td width="13%" align="center">Qua</td>
					<td width="13%" align="center">Qui</td>
					<td width="13%" align="center">Sex</td>
					<td width="13%" align="center">Sab</td>
				</tr>
				<c:set var="ordemAnt" value="0" />
				<c:forEach items="${gradeDocente.horarios}" var="_horario" varStatus="s">
					<c:if test="${_horario.nivel == nivel}">
						<c:set var="dia" value="1" />
						<c:if test="${_horario.ordem < ordemAnt}">
							<tr><td colspan="7">&nbsp;</td></tr>
						</c:if>
						<c:set var="ordemAnt" value="${_horario.ordem}"/>
						<tr>
							<td align="center">${_horario.hoursDesc}</td>
							<c:set var="dia" value="${dia + 1}" />
							<td align="center"><span id="${nivel}_${dia}_${_horario.id}">---</span></td>
							<c:set var="dia" value="${dia + 1 }" />
							<td align="center"><span id="${nivel}_${dia}_${_horario.id}">---</span></td>
							<c:set var="dia" value="${dia + 1 }" />
							<td align="center"><span id="${nivel}_${dia}_${_horario.id}">---</span></td>
							<c:set var="dia" value="${dia + 1 }" />
							<td align="center"><span id="${nivel}_${dia}_${_horario.id}">---</span></td>
							<c:set var="dia" value="${dia + 1 }" />
							<td align="center"><span id="${nivel}_${dia}_${_horario.id}">---</span></td>
							<c:set var="dia" value="${dia + 1 }" />
							<td align="center"><span id="${nivel}_${dia}_${_horario.id}">---</span></td>
							<c:set var="dia" value="${dia + 1 }" />
						</tr>
					</c:if>
				</c:forEach>
			</table>
			<c:set var="permiteHorarioFlexivel" value="false" />
			<c:forEach items="#{horariosTurma}" var="hor">
				<c:if test="${hor.turma.disciplina.nivel == nivel && hor.turma.disciplina.permiteHorarioFlexivel}">
					<c:set var="permiteHorarioFlexivel" value="true" />
				</c:if>
			</c:forEach>
			<c:if test="${permiteHorarioFlexivel}">
				* A turma possui horário flexível e a tabela mostra o horário no mês atual.<br/>
			</c:if>
			<br/>
		</c:if>
	</c:forEach>

	<script type="text/javascript">
	var elem;
	<c:forEach var="nivel" items="#{gradeDocente.niveisEnsinoTurmas}">
		<c:forEach items="#{horariosTurma}" var="hor">
			<c:if test="${hor.turma.disciplina.nivel == nivel}">
				elem = document.getElementById('${nivel}_${hor.dia}_${hor.horario.id}');
				if (elem.innerHTML != '---')  {
					elem.innerHTML += "<br>${hor.turma.disciplina.codigo}${hor.turma.disciplina.permiteHorarioFlexivel ? '*' : ''}";
				} else {
					elem.innerHTML = "${hor.turma.disciplina.codigo}${hor.turma.disciplina.permiteHorarioFlexivel ? '*' : ''}";
				}
			</c:if>
		</c:forEach>
	</c:forEach>
	</script>
</c:if>

</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
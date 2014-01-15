<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
hr {
	background: #C1C1C1;
}

table {
	margin-top: 10px;
	margin-bottom: 10px;
}

table thead {
	background: #FFF;
}

table.matriculas td {
	border-bottom: 1px solid #BDBDBD;
	border-left: 1px solid #BDBDBD;
}

table.matriculas .direita {
	border-right: 1px solid #BDBDBD;
}

table.matriculas th {
	border-left: 1px solid #BDBDBD;
	border-bottom: 1px solid #BDBDBD;
	border-top: 1px solid #BDBDBD;
}
</style>

<f:view>
<h3>Atestado de Matrícula - Emitido em <fmt:formatDate value="${ dataAtual }" pattern="dd/MM/yyyy"/> às <fmt:formatDate value="${ dataAtual }" pattern="HH:mm"/>h</h3>
<h:outputText value="#{ atestadoMatricula.create }"/>

<hr/>
<table>
<tr><td>Ano Semestre:</td><td colspan="3"><strong>${ atestadoMatricula.anoSemestre }</strong></td></tr>
<tr><td>Matrícula:</td><td> <strong>${ atestadoMatricula.discente.matricula }</strong></td><td>Tipo: </td><td></td></tr>
<tr><td>Nome: </td><td colspan="3"><strong>${ atestadoMatricula.discente.pessoa.nome }</strong></td></tr>
<tr><td>Curso: </td><td colspan="3"><strong>${ atestadoMatricula.discente.curso.descricao }</strong></td></tr>
</table>
<hr/>

<c:set var="matriculas" value="${ atestadoMatricula.disciplinasMatriculadas }"/>

<h4>Turmas Matriculadas: (${ fn:length(matriculas) })</h4>

<table class="matriculas" width="100%" cellspacing="0">
<thead>
	<tr><th align="center">Cód.</th><th>Disciplinas/Docentes</th><th align="center">Turma</th>
	<th align="center">Status</th><th align="center" class="direita">Horário</th></tr>
</thead>
<tbody>
<c:forEach var="item" items="${ matriculas }">
	<tr>
		<td align="center" valign="top">${ item.turma.disciplina.codigo }</td>
		<td valign="top">${ item.turma.descricaoSemCodigo }</td>
		<td align="center" valign="top">${ item.turma.codigo }</td>
		<td align="center" valign="top">${ item.situacaoMatricula.descricao }</td>
		<td align="center" valign="top" class="direita">${ item.turma.descricaoHorario }</td>
	</tr>
</c:forEach>
</tbody>
</table>

<hr/>

<c:if test="${not empty atestadoMatricula.horariosTurma}">
	<h4>Tabela de Horário:</h4>

	<table width="80%" class="formulario" align="center">
		<tr class="titulo" style="background-color: #333366; color: white; font-weight: bold">
			<td align="center">Horários</td>
			<td width="10%" align="center">Dom</td>
			<td width="10%" align="center">Seg</td>
			<td width="10%" align="center">Ter</td>
			<td width="10%" align="center">Qua</td>
			<td width="10%" align="center">Qui</td>
			<td width="10%" align="center">Sex</td>
			<td width="10%" align="center">Sab</td>
		</tr>
		<c:forEach items="${atestadoMatricula.horarios}" var="horario" varStatus="s">
			<c:set var="dia" value="1" />
			<c:if test="${horario.ordem == 1 and s.count > 1}">
			<tr><td colspan="7">&nbsp;</td></tr>
			</c:if>
			<tr>
				<td align="center">${horario.hoursDesc}</td>
				<td align="center"><span id="${dia}_${horario.id}">---</span></td>
				<c:set var="dia" value="${dia + 1}" />
				<td align="center"><span id="${dia}_${horario.id}">---</span></td>
				<c:set var="dia" value="${dia + 1 }" />
				<td align="center"><span id="${dia}_${horario.id}">---</span></td>
				<c:set var="dia" value="${dia + 1 }" />
				<td align="center"><span id="${dia}_${horario.id}">---</span></td>
				<c:set var="dia" value="${dia + 1 }" />
				<td align="center"><span id="${dia}_${horario.id}">---</span></td>
				<c:set var="dia" value="${dia + 1 }" />
				<td align="center"><span id="${dia}_${horario.id}">---</span></td>
				<c:set var="dia" value="${dia + 1 }" />
				<td align="center"><span id="${dia}_${horario.id}">---</span></td>
				<c:set var="dia" value="${dia + 1 }" />
			</tr>
		</c:forEach>
	</table>

		<script type="text/javascript">
	<c:forEach items="${atestadoMatricula.horariosTurma}" var="hor">
			var elem = document.getElementById('${hor.dia}_${hor.horario.id}');
			elem.innerHTML = '${hor.turma.disciplina.codigo}';
	</c:forEach>
		</script>
</c:if>

<p align="center" style="font-size: x-small; padding-bottom: 20px; padding-top: 30px;"><strong>ATENÇÃO</strong></p>

<p align="center" style="font-size: x-small;">ESTE DOCUMENTO SÓ SERÁ CONSIDERADO OFICIAL COM A CHANCELA DA</p>
<p align="center" style="font-size: x-small; padding-bottom: 45px;">COORDENAÇÃO DO PROGRAMA OU DA PRÓ-REITORIA DE PÓS-GRADUAÇÃO - PPg</p>

<p align="center" style="font-size: x-small;">________________________________________</p>
<p align="center" style="font-size: x-small;">Coordenador do Programa ou PPg</p>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
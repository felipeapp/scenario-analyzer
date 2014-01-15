<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	<br>
	<table class="visualizacao" style="width: 100%">
	<tr>
		<th width="25%"> Discente: </th>
		<td> ${matriculaGraduacao.discente.matriculaNome} </td>
	</tr>
	<c:if test="${matriculaGraduacao.discente.graduacao}">
	<tr>
		<th> Matriz Curricular: </th>
		<td> ${matriculaGraduacao.discente.matrizCurricular.descricao} </td>
	</tr>
	</c:if>
	<tr>
		<th> Currículo: </th>
		<td> ${matriculaGraduacao.discente.curriculo.codigo} </td>
	</tr>
	</table>
	<c:if test="${not empty matriculaGraduacao.turmas}">
	<br>
	<table class="listagem" style="width: 100%">
		<caption>Turmas</caption>
		<tbody>
			<c:forEach items="${matriculaGraduacao.turmas}" var="turma" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
				<td>${turma.disciplina.descricao}</td>
				<td width="12%">Turma ${turma.codigo}</td>
				<td width="12%">${turma.local}</td>
			</tr>
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
				<td colspan="3">
					Docente(s): ${turma.docentesNomes }
				</td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
	</c:if>
	<br>
	<table width="80%" class="formulario" align="center"  >
		<tr class="titulo" style="background-color: #333366; color: white; font-weight: bold; font-size: x-small;">
			<td width="14%" align="center">Horários</td>
			<td width="10%" align="center">Seg</td>
			<td width="10%" align="center">Ter</td>
			<td width="10%" align="center">Qua</td>
			<td width="10%" align="center">Qui</td>
			<td width="10%" align="center">Sex</td>
			<td width="10%" align="center">Sáb</td>
		</tr>
		<c:forEach items="${matriculaGraduacao.horarios}" var="horario" varStatus="s">
			<c:set var="dia" value="1" />
			<c:if test="${horario.obj.ordem == 1 and s.count > 1}">
			<tr><td colspan="7" style="height: 1px">&nbsp;</td></tr>
			</c:if>
			<tr>
				<td align="center">${horario.obj.hoursDesc}</td>
				<td align="center"><span id="${dia}_${horario.obj.id}">---</span></td>
				<c:set var="dia" value="${dia + 1 }" />
				<td align="center"><span id="${dia}_${horario.obj.id}">---</span></td>
				<c:set var="dia" value="${dia + 1 }" />
				<td align="center"><span id="${dia}_${horario.obj.id}">---</span></td>
				<c:set var="dia" value="${dia + 1 }" />
				<td align="center"><span id="${dia}_${horario.obj.id}">---</span></td>
				<c:set var="dia" value="${dia + 1 }" />
				<td align="center"><span id="${dia}_${horario.obj.id}">---</span></td>
				<c:set var="dia" value="${dia + 1 }" />
				<td align="center"><span id="${dia}_${horario.obj.id}">---</span></td>
				<c:set var="dia" value="${dia + 1 }" />
			</tr>
		</c:forEach>
	</table>
	<c:if test="${not empty matriculaGraduacao.horariosTurma}">
		<script type="text/javascript">
			<c:forEach items="${matriculaGraduacao.horariosTurma}" var="hor">
					var elem = document.getElementById('${hor.dia}_${hor.horario.id}');
					elem.innerHTML = '${hor.turma.disciplina.codigo}';
			</c:forEach>
		</script>
	</c:if>
 </div>
  <br/>
  <div>
	<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
  </div>
</f:view>
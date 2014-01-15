<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2> Lista de Alunos da Turma</h2>

	<table class="visualizacao" >
		<tr>
			<th width="20%"> Componente: </th>
			<td> ${turmaMedio.obj.disciplina.descricao}</td>
		</tr>
		<tr>
			<th>Turma:</th>
			<td> ${turmaMedio.turmaSerie.descricaoCompleta}</td> 
		</tr>
		<tr>
			<th> Docente(s): </th>
			<td> ${turmaMedio.obj.docentesNomes} </td>
		</tr>
		<tr>
			<th> Horário: </th>
			<td> ${turmaMedio.obj.descricaoHorario} </td>
		</tr>
	</table>
	
	<br />
	<table class="listagem" id="lista-turmas" style="width: 90%;">
		<caption>${fn:length(turmaMedio.matriculados) } discentes foram matriculados nessa disciplina</caption>

		<c:if test="${empty turmaMedio.matriculados}">
			<tr><td>Nenhum aluno está matriculado nessa turma</td></tr>
		</c:if>
		<c:if test="${not empty turmaMedio.matriculados}">
			<thead>
				<tr>
					<td width="10%" style="text-align: center;">Matrícula</td>
					<td style="text-align: left;">Nome</td>
					<td style="text-align: left;">Série</td>
					<td style="text-align: left;">Tipo de Matrícula</td>
					<td  style="text-align: left;" width="13%">Situação</td>
				</tr>
			</thead>
		<c:forEach items="${turmaMedio.matriculados}" var="mat" varStatus="s">
			<tbody>
				<tr class="${s.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td style="text-align: center;"> ${mat.matriculaComponente.discente.matricula } </td>
					<td style="text-align: left;"> ${mat.matriculaComponente.discente.nome} </td>
					<td  style="text-align: left;">	${mat.matriculaComponente.serie.descricaoCompleta} </td>
					<td  style="text-align: left;">	${mat.matriculaDiscenteSerie.tipoMatricula} </td>
					<td style="text-align: left;"> ${mat.matriculaComponente.situacaoMatricula.descricao} </td>
				</tr>
			</tbody>
		</c:forEach>
		</c:if>
	</table>
	<br>
	<center>
		<h:form>
			<input type="button" value="<< Selecionar Outra Disciplina"  onclick="javascript: history.back();" id="voltar" />
		</h:form>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

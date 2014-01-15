<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
<ufrn:subSistema /> &gt; ${tipoAlteracao }
</h2>

<table class="formulario" width="75%">
		<caption>Dados do Aluno</caption>

		<tbody>
			<tr>
				<td></td>
				<td></td>
				<th nowrap="nowrap">Aluno:</th>
				<td><b>${discente.matricula } - ${discente.pessoa.nome}</b>
				</td>
			</tr>
			<tr>
				<td></td>
				<td></td>
				<th nowrap="nowrap">Curso:</th>
				<td><b>${discente.curso.descricao }</b></td>
			</tr>

		</tbody>
	</table>
	<br>
	<c:if test="${not empty listaMatriculados }">
	<div class="infoAltRem">
    <html:img page="/img/delete.gif"/> : ${tipoAlteracao }
	</div>
	</c:if>

	<ufrn:table collection="${listaMatriculados}"
		properties="turma.disciplina.codigo,turma.disciplina.nome,turma.codigo,turma.local"
		headers="Código, Disciplina, Cod. Turma, Local"
		title="Turmas Matriculadas" crud="false"
		links="src='${ctx}/img/delete.gif',?id={id}&dispatch=confirmaDados"/>

<br>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>

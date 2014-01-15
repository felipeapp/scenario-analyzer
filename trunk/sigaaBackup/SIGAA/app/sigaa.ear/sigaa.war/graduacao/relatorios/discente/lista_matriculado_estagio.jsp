o <%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<%@include file="/graduacao/relatorios/cabecalho_curso.jsp"%>

	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<caption><b>Total de Registros: ${ fn:length(resultado) }</b></caption>

	<c:set var="curso2"/>
	<c:set var="turno2"/>
	<c:set var="disciplina2"/>
	<c:forEach items="${resultado}" var="linha">
		<c:set var="cursoAtual" value="${linha.id_curso}"/>
		<c:set var="turnoAtual" value="${linha.id_turno}"/>
		<c:if test="${curso2 != cursoAtual || turno2 != turnoAtual}">
			<c:set var="curso2" value="${cursoAtual}"/>
			<c:set var="turno2" value="${turnoAtual}"/>
			<c:set var="disciplina2"/>

			<tr>
				<td colspan="3">
					<br>
					<b>${linha.centro}	- ${linha.curso_nome} /	${linha.sigla_turno}</b>
					<hr>
				</td>
			</tr>
		</c:if>
		<c:set var="disciplinaAtual" value="${linha.disciplina_codigo}"/>
		<c:if test="${disciplina != disciplinaAtual}">
			<c:set var="disciplina2" value="${disciplinaAtual}"/>
			<tr>
				<td colspan="3">
					<br>
					<b>${linha.disciplina_codigo} - ${linha.disciplina_nome}</b>
					<hr>
				</td>
			</tr>
			<tr>
				<td>Discente</td>
				<td>CPF</td>
				<td>Data de Nascimento</td>
			<tr>
		</c:if>
		<tr>
			<td>
				${linha.matricula} - ${linha.nome_aluno}
				<c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
			</td>
			<td>
				${linha.cpf}
			</td>
			<td>
				${linha.data_nascimento}
			</td>

		</tr>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

	<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<%@include file="/graduacao/relatorios/cabecalho_curso.jsp"%>

	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<caption><b>Total de Registros: ${ fn:length(resultado) }</b></caption>
	<c:set var="cursoLoop"/>
	<c:set var="turnoLoop"/>
	<c:forEach items="${resultado}" var="linha">
			<c:set var="cursoAtual" value="${linha.id_curso}"/>
			<c:set var="turnoAtual" value="${linha.id_turno}"/>
		<c:if test="${cursoLoop != cursoAtual || turnoLoop != turnoAtual}">
			<c:set var="cursoLoop" value="${cursoAtual}"/>
			<c:set var="turnoLoop" value="${turnoAtual}"/>
			<tr>
				<td colspan="4">
					<br>
					<b>${linha.centro}	- ${linha.curso_aluno} /	${linha.turno_sigla}</b>
					<hr>
				</td>
			</tr>
			<tr>
				<td colspan="2">Discente</td>
				<td>Status</td>
				<td>Prazo de Conclusão</td>
			<tr>
		</c:if>
		<tr>
			<td>
				${linha.centro} - ${linha.curso_nome}
			</td>
			<td>
				${linha.ano_ingresso}-${linha.periodo_ingresso}/${linha.matricula}
			-
				${linha.aluno_nome}
				<c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
			</td>
			<td>
				${linha.status_aluno}
			</td>
			<td>
				${linha.prazo_conclusao}
			</td>

		</tr>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

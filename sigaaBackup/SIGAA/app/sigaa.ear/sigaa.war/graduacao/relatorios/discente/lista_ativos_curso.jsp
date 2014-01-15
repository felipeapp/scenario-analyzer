	<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<%@include file="/graduacao/relatorios/cabecalho_curso.jsp"%>

	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<caption><b>Total de Registros: ${ fn:length(resultado) }</b></caption>
	<c:set var="curso_"/>
	<c:set var="turno_"/>
	<c:forEach items="${resultado}" var="linha">
			<c:set var="cursoAtual" value="${linha.id_curso}"/>
			<c:set var="turnoAtual" value="${linha.id_turno}"/>
		<c:if test="${curso_ != cursoAtual || turno_ != turnoAtual}">
			<c:set var="curso_" value="${cursoAtual}"/>
			<c:set var="turno_" value="${turnoAtual}"/>
			<tr>
				<td colspan="4">
					<br>
					<b>${linha.centro}	- ${linha.curso_aluno} /	${linha.turno_sigla}</b>
					<hr>
				</td>
			</tr>
			<tr>
				<td>Ingresso</td>
				<td>Matrícula</td>
				<td>Nome</td>
				<td>Status</td>
			<tr>
		</c:if>
		<tr>
			<td>
				${linha.ano_ingresso}.${linha.periodo_ingresso}
			</td>
			<td>
				${linha.matricula}
			</td>
			<td>
				${linha.aluno_nome}
				<c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
			</td>
			<td>
				${linha.status_aluno}
			</td>
		</tr>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

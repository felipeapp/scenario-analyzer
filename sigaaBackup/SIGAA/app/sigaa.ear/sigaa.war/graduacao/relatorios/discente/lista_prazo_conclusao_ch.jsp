<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<%@include file="/graduacao/relatorios/cabecalho_curso.jsp"%>

	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<caption><b>Total de Registros: ${ fn:length(resultado) }</b></caption>
	<c:set var="cursoLoop"/>
	<c:set var="turnoLoop"/>
    <c:set var="curriculoLoop"/>
	<c:forEach items="${resultado}" var="linha">
			<c:set var="cursoAtual" value="${linha.id_curso}"/>
			<c:set var="turnoAtual" value="${linha.id_turno}"/>
			<c:set var="curriculoAtual" value="${linha.codigo}"/>
		<c:if test="${cursoLoop != cursoAtual || turnoLoop != turnoAtual}">
			<c:set var="cursoLoop" value="${cursoAtual}"/>
			<c:set var="turnoLoop" value="${turnoAtual}"/>

			<tr>
				<td colspan="5">
					<br>
					<b>${linha.centro}	- ${linha.curso_nome} /	${linha.sigla_turno} </b>
					<hr>
				</td>
			</tr>

		</c:if>
		<c:if test="${curriculoLoop != curriculoAtual}">
			<c:set var="curriculoLoop" value="${curriculoAtual}"/>
			<tr>
				<td colspan="4">
					<br>
					<b>Currículo: ${linha.codigo} - Carga Horária: ${linha.total_curso }</b>
					<hr>
				</td>
			</tr>
			<tr>
				<tr>
					<td>Discente</td>
					<td>CH. Integ.</td>
					<td>CH. Pedente</td>
					<td>Prazo de Conclusão</td>
				<tr>
			<tr>
		</c:if>
		<tr>
			<td>
				${linha.matricula} -${linha.aluno_nome}
				<c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
			</td>
			<td>
				${linha.ch_total_integralizada}
			</td>
			<td>
				${linha.ch_total_pendente}
			</td>
			<td>
				${linha.prazo_conclusao}
			</td>

		</tr>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<%@include file="/graduacao/relatorios/cabecalho_curso.jsp"%>

	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<caption><b>Total de Registros: ${ fn:length(resultado) }</b></caption>
	<c:set var="cursoLoop"/>
	<c:set var="turnoLoop"/>
	<c:set var="grauAcademicoLoop"/>
    <c:set var="habilitacaoLoop"/>
	<c:forEach items="${resultado}" var="linha">
			<c:set var="cursoAtual" value="${linha.id_curso}"/>
			<c:set var="turnoAtual" value="${linha.id_turno}"/>
			<c:set var="grauAcademicoAtual" value="${linha.id_grau_academico}"/>
			<c:set var="habilitacaoAtual" value="${linha.id_habilitacao}"/>
		<c:if test="${cursoLoop != cursoAtual || turnoLoop != turnoAtual || habilitacaoLoop != habilitacaoAtual || grauAcademicoLoop != grauAcademicoAtual}">
			<c:set var="cursoLoop" value="${cursoAtual}"/>
			<c:set var="turnLoopo" value="${turnoAtual}"/>
			<c:set var="habilitacaoLoop" value="${habilitacaoAtual}"/>
			<c:set var="grauAcademicoLoop" value="${grauAcademicoAtual}"/>
			<tr>
				<td colspan="3">
					<br>
					<b>${linha.centro}	- ${linha.curso_nome} - ${linha.modalidade_nome} - ${linha.habilitacao_nome} - ${linha.municipio}  /	${linha.sigla_turno} </b>
					<hr>
				</td>
			</tr>
			<tr>
				<td><b>Discente</b></td>
				<td><b>CH Obrig Pendente</b></td>
				<td><b>CH Optat Pendente</b></td>
				<td><b>Status Atual</b></td>
			<tr>
		</c:if>
		<tr>
			<td>
				${linha.matricula} - ${linha.discente_nome}
				<c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
			</td>
			<td>
				${linha.ch_obrig_pendente}
			</td>
			<td>
				${linha.ch_optativa_pendente}
			</td>
			<td>
				${linha.status_discente}
			</td>
		</tr>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

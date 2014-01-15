<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<%@include file="/graduacao/relatorios/cabecalho_curso.jsp"%>

	<c:set var="curso2" />
	<c:set var="grauAcademico2" />
	<c:set var="habilitacao2" />
	
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<caption><b>Total de Registros: ${ fn:length(resultado) }</b></caption>

	<c:forEach items="${resultado}" var="linha">
		<c:set var="cursoAtual" value="${linha.id_curso}"/>
		<c:set var="grauAcademicoAtual" value="${linha.id_grau_academico}"/>
		<c:set var="habilitacaoAtual" value="${linha.id_habilitacao}"/>
		
		<c:if test="${curso2 != cursoAtual || grauAcademico2 != grauAcademicoAtual || habilitacao2 != habilitacaoAtual}">
			<c:set var="curso2" value="${cursoAtual}"/>
			<c:set var="grauAcademico2" value="${grauAcademicoAtual}"/>
			<c:set var="habilitacao2" value="${habilitacaoAtual}"/>
			<tr>
				<td colspan="7">
					<br>
					<b>${linha.unidade_sigla} - ${linha.curso_nome} - ${linha.modalidade_aluno} - ${linha.habilitacao_aluno}</b>
					<hr>
				</td>
			</tr>
			<tr>
				<td align="center"><b>Ingresso</b></td>
				<td align="center"><b>Matrícula</b></td>
				<td><b>Nome</b></td>
				<td align="center"><b>Status</b>
				<td align="center"><b>CH. Integralizada</b></td>
				<td align="center"><b>CH. Total</b></td>
				<td align="center"><b>% Cumprido</b></td>
			<tr>
		</c:if>
			<tr>
			<td align="center">${linha.ano_ingresso}-${linha.periodo_ingresso}</td>
			<td align="right" style="padding-right: 7px;">${linha.matricula}</td>
			<td>
				${linha.aluno_nome}
				<c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
			</td>
			<td align="center">${linha.status_aluno}</td>
			<td align="right">${linha.ch_total_integralizada}</td>
			<td align="right">${linha.ch_total_minima}</td>
			<td align="right">${(linha.percentualcargacumprida == null?'0.0':linha.percentualcargacumprida)}%</td>
		</tr>
		
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

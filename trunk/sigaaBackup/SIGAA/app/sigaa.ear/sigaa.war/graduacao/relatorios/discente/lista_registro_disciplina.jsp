o <%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<hr>
	<table width="100%">
		<caption><b>Relatório de Alunos com registro em um determinado Componente Curricular</b></caption>
			<tr>
				<th>Componente Curricular:</th>
				<td colspan="3"><b><h:outputText value="#{relatorioDiscente.disciplina.codigo }" /> - <h:outputText
					value="#{relatorioDiscente.disciplina.detalhes.nome }" /></b></td>
			</tr>
	</table>
	<hr>
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<caption><b>Total de Registros: <h:outputText value="#{relatorioDiscente.numeroRegistosEncontrados}"/></b></caption>
	<c:set var="cursoVar"/>
	<c:set var="turnoVar"/>
	<c:set var="ano"/>
    <c:set var="periodo"/>
    <c:set var="componenteVar"/>
	<c:forEach items="${relatorioDiscente.listaDiscente}" var="linha">
			<c:set var="cursoAtual" value="${linha.id_curso}"/>
			<c:set var="turnoAtual" value="${linha.id_turno}"/>
			<c:set var="anoAtual" value="${linha.ano}"/>
			<c:set var="periodoAtual" value="${linha.periodo}"/>
			<c:set var="componenteAtual" value="${linha.disciplina_codigo}"/>
		<c:if test="${ano != anoAtual || periodo != periodoAtual}">
			<c:set var="ano" value="${anoAtual}"/>
			<c:set var="periodo" value="${periodoAtual}"/>
			<c:set var="cursoVar"/>
			<c:set var="turnoVar"/>
		    <c:set var="componenteVar"/>
			<tr>
				<td colspan="5">
					<br>
					<b>Ano Semestre: ${linha.ano}-${linha.periodo}</b>
					<hr>
				</td>
			</tr>
		</c:if>
		<c:if test="${componenteVar != componenteAtual}">
			<c:set var="cursoVar"/>
			<c:set var="turnoVar"/>
		    <c:set var="componenteVar" value="${componenteAtual}"/>
			<tr>
				<td colspan="5">
					<br>
					<b>${linha.disciplina_codigo} - ${linha.disciplina_nome}</b>
					<hr>
				</td>
			</tr>
		</c:if>
		<c:if test="${cursoVar != cursoAtual || turnoVar != turnoAtual}">
			<c:set var="cursoVar" value="${cursoAtual}"/>
			<c:set var="turnoVar" value="${turnoAtual}"/>

			<tr>
				<td colspan="5">
					<br>
					<b>${linha.centro}	- ${linha.curso_aluno} /	${linha.turno_sigla} </b>
					<hr>
				</td>
			</tr>
			<tr>
				<td>Ingresso</td>
				<td>Discente</td>
				<td>M. Final</td>
				<td>Resultado</td>
				<td>Status Atual</td>
			<tr>
		</c:if>
		<tr>
			<td>
				${linha.ano_ingresso}-${linha.periodo_ingresso}
			</td>
			<td>
				${linha.matricula} - ${linha.aluno_nome}
				<c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
			</td>
			<td>
				${linha.media_final}
			</td>
			<td>
				${linha.status_matricula}
			</td>
			<td>
				${linha.status_aluno}
			</td>

		</tr>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

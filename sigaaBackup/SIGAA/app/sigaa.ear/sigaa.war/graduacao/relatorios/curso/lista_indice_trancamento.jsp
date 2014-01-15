<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<hr>
	<table width="100%">
		<caption><b>Relatório do Índice de trancamentos e cancelamentos de matrícula em componentes curriculares</b></caption>
			<tr>
				<th>Ano Período Ocorrência:</th>
				<td><b><h:outputText
					value="#{relatorioCurso.ano == 0? 'TODOS' : (relatorioCurso.ano*10)+relatorioCurso.periodo}" /></b></td>
			</tr>
	</table>
	<hr>
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<caption><b>Total de Registros: <h:outputText value="#{relatorioCurso.numeroRegistosEncontrados}"/></b></caption>
	<c:set var="cursoLoop"/>
	<c:set var="matrizCurricularLoop"/>
    <c:set var="totalTrancamento" value="0"/>
    <c:set var="totalTrancamentoGrupo" value="0"/>
    <c:set var="totalTrancamentoCurso" value="0"/>
    <c:set var="totalCancelamento" value="0"/>
    <c:set var="totalCancelamentoGrupo" value="0"/>
    <c:set var="totalCancelamentoCurso" value="0"/>
	<c:forEach items="${relatorioCurso.listaCurso}" var="linha">
			<c:set var="cursoAtual" value="${linha.id_curso}"/>
			<c:set var="matrizCurricularAtual" value="${linha.id_matriz_curricular}"/>
		<c:if test="${cursoLoop != cursoAtual || matrizCurricularLoop != matrizCurricularAtual }">
			<c:if test="${not empty cursoLoop}">
				<tr>
					<td colspan="5" align="center"><hr></td>
				</tr>
				<tr>
					<td colspan="5" align="center">
					    Total de Trancamentos: ${totalTrancamentoGrupo}
					    Total de Cancelamentos: ${totalCancelamentoGrupo}
					</td>
				</tr>

				<c:if test="${cursoLoop != cursoAtual}">
					<tr>
						<td colspan="5" align="center"><hr></td>
					</tr>
					<tr>
						<td colspan="5" align="center"><b>Total de Trancamentos do Curso-Cidade: ${totalTrancamentoCurso} /
						Total de Cancelamentos do Curso-Cidade: ${totalCancelamentoCurso} </b>
						</td>
					</tr>
					<tr>
						<td colspan="5" align="center"><br></td>
					</tr>
					<c:set var="totalTrancamentoCurso" value="0"/>
					<c:set var="totalCancelamentoCurso" value="0"/>
				</c:if>

				<c:set var="totalTrancamentoGrupo" value="0"/>
				<c:set var="totalCancelamentoGrupo" value="0"/>
			</c:if>
			<c:set var="cursoLoop" value="${cursoAtual}"/>
			<c:set var="matrizCurricularLoop" value="${matrizCurricularAtual}"/>
			<tr>
				<td colspan="5">
					<br>
					<b>${linha.centro}	- ${linha.curso_nome} - ${linha.municipio_nome} </b><br>
					<b><i>${linha.turno_descricao}	- ${linha.modalidade_nome}</i></b><br>
					<hr>
				</td>
			</tr>
			<tr>
				<td><b>Habilitação</b></td>
				<td><b>Semestre </b></td>
				<td><b>Trancamento</b></td>
				<td><b>Cancelamento</b></td>
				<td><b>Matrículas</b></td>
			</tr>
		</c:if>
		<c:set var="totalTrancamento" value="${ totalGraduando + linha.trancamentos }"/>
		<c:set var="totalTrancamentoGrupo" value="${ totalGraduandoGrupo + linha.trancamentos }"/>
		<c:set var="totalTrancamentoCurso" value="${ totalGraduandoCurso + linha.trancamentos }"/>
		<c:set var="totalCancelamento" value="${ totalCancelamento + linha.cancelamentos }"/>
		<c:set var="totalCancelamentoGrupo" value="${ totalCancelamentoGrupo + linha.cancelamentos }"/>
		<c:set var="totalCancelamentoCurso" value="${ totalCancelamentoCurso + linha.cancelamentos }"/>
		<tr>
			<td>
				${empty linha.habilitacao_nome ? '<i>MODALIDADE SEM HABILITAÇÃO</i>': linha.habilitacao_nome}
			</td>
			<td>
				${linha.ano}-${linha.periodo}
			</td>
			<td>
				${linha.trancamentos}
			</td>
			<td>
				${linha.cancelamentos}
			</td>
			<td>
				${linha.total_matriculas}
			</td>
		</tr>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

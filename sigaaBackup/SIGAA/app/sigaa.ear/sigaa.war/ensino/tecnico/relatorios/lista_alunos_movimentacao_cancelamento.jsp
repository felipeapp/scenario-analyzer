<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
tr.componentes td {padding: 2px ; border-bottom: 1px dashed #888}
</style>

<f:view>
	<hr>
	<table width="100%">
		<caption><b>Lista de alunos Com Movimentação de Cancelamento</b></caption>
			<tr>
				<th>Unidade:</th>
				<td> <b>${usuario.vinculoAtivo.unidade.nome}</b></td>
			</tr>
	</table>
	<hr>
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<caption><b>Total de Registros: <h:outputText value="#{relatoriosTecnico.numeroRegistrosEncontrados}"/></b></caption>
	<c:set var="curso_"/>
	<c:set var="especializacao_"/>
	<c:forEach items="${relatoriosTecnico.lista}" var="linha">
			<c:set var="cursoAtual" value="${linha.id_curso}"/>
			<c:set var="especializacaoAtual" value="${linha.id_especializacao_turma_entrada}"/>
		<c:if test="${curso_ != cursoAtual || especializacao_ != especializacaoAtual}">
			<c:set var="curso_" value="${cursoAtual}"/>
			<c:set var="especializacao_" value="${especializacaoAtual}"/>
			<tr>
				<td colspan="3">
					<br>
					<b>${linha.nome_curso} - <i>${empty linha.nome_especializacao? "Curso sem especialização" : linha.nome_especializacao }</i></b>
				</td>
			</tr>
			<tr class="header">
				<td style="text-align: center;">Matrícula</td>
				<td>Nome</td>
				<td>Status</td>
				<td style="text-align: right;">CH Cumprida</td>
			</tr>
		</c:if>
		<tr class="componentes">
			<td style="text-align: center;">${linha.matricula}</td>
			<td>${linha.nome_discente}</td>
			<td>
				<c:choose>
					<c:when test="${linha.status == 1}">ATIVO</c:when>
					<c:when test="${linha.status == 2}">CADASTRADO</c:when>
					<c:when test="${linha.status == 3}">CONCLUIDO</c:when>
					<c:when test="${linha.status == 4}">AFASTADO</c:when>
					<c:when test="${linha.status == 5}">TRANCADO</c:when>
					<c:when test="${linha.status == 6}">CANCELADO</c:when>
					<c:when test="${linha.status == 7}">JUBILADO</c:when>
					<c:when test="${linha.status == 8}">FORMANDO</c:when>
					<c:when test="${linha.status == 9}">GRADUANDO</c:when>
					<c:when test="${linha.status == 10}">EXCLUIDO</c:when>
					<c:otherwise>Desconhecido</c:otherwise>
				</c:choose>
			</td>
			<td style="text-align: right;">${linha.ch_cumprida} h</td>
		</tr>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

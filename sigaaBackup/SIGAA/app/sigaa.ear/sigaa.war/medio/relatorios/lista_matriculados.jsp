<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
<!--
tr.componentes td {padding: 2px ; border-bottom: 1px dashed #888}
tr.header td {padding: 2px ; border-bottom: 1px solid #888; font-weight: bold;}
-->
</style>

<f:view>
	<h2>Lista de alunos ativos e matriculados por ano</h2>
	
	<div id="parametrosRelatorio">
	<table>
		<tr>
			<th>Unidade:</th>
			<td>${relatoriosMedio.unidade.nome}</td>
		</tr>
		<tr>
			<th>Ano:</th>
			<td><h:outputText value="#{relatoriosMedio.ano}"/></td>
		</tr>
		
		</table>
	</div>

	<br />
	<table class="tabelaRelatorioBorda" cellspacing="1" width="100%" style="font-size: 10px;">
	<c:set var="_curso"/>
	<c:set var="_serie"/>
	<c:set var="_turma"/>
	<c:forEach items="${relatoriosMedio.lista}" var="linha">
		<c:set var="cursoAtual" value="${linha.id_curso}"/>
		<c:set var="serieAtual" value="${linha.id_serie}"/>
		<c:set var="turmaAtual" value="${linha.id_turma_serie}"/>
		<thead>
		<c:if test="${_curso != cursoAtual}">
			<c:set var="_curso" value="${cursoAtual}"/>
			<tr>
				<td colspan="2" valign="middle" >
					<br>
					<b>${linha.nome_curso}</b>
					<br>
				</td>
			</tr>
		</c:if>
		<c:if test="${_serie != serieAtual || _turma != turmaAtual}">
			<c:set var="_serie" value="${serieAtual}"/>
			<c:set var="_turma" value="${turmaAtual}"/>
			<tr>
				<th colspan="2">
					<br>
					<b>${linha.numero_serie}ª ${linha.descricao_serie} - Turma ${linha.nome_turma} </b>
					<br>
				</th>
			</tr>
			<tr class="header">
				<td style="text-align: center;">Matrícula</td>
				<td>Discente</td>
			</tr>
		</c:if>
		</thead>
		<tr class="componentes">
			<td style="text-align: center;">${linha.matricula}</td>
			<td>${linha.nome_discente} ${linha.dependencia ? '(DEPENDÊNCIA)' : '' }</td>
		</tr>
	</c:forEach>
	</table>
	<br />
	<table align="center">
		<tr align="center">
			<td colspan="2"><b>Total de Registros: <h:outputText value="#{relatoriosMedio.numeroRegistrosEncontrados}"/></b></td>
		</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
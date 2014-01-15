<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
<!--
tr.componentes td {padding: 2px ; border-bottom: 1px dashed #888}
tr.header td {padding: 2px ; border-bottom: 1px solid #888; font-weight: bold;}
-->
</style>

<f:view>
	<h2>Lista de alunos ativos e matriculados num determinado período</h2>
	
	<div id="parametrosRelatorio">
	<table>
		<tr>
			<th>Unidade:</th>
			<td>${relatoriosTecnico.unidade.nome}</td>
		</tr>
		<tr>
			<th>Ano-Período:</th>
			<td><h:outputText value="#{relatoriosTecnico.ano}"/> - <h:outputText value="#{relatoriosTecnico.periodo}"/></td>
		</tr>
		
		</table>
	</div>

	<br />
	<table cellspacing="1" width="100%" style="font-size: 10px;">
	<c:set var="curso_"/>
	<c:set var="especializacao_"/>
	<c:forEach items="${relatoriosTecnico.lista}" var="linha">
			<c:set var="cursoAtual" value="${linha.id_curso}"/>
			<c:set var="especializacaoAtual" value="${linha.id_especializacao_turma_entrada}"/>
		<c:if test="${curso_ != cursoAtual || especializacao_ != especializacaoAtual}">
			<c:set var="curso_" value="${cursoAtual}"/>
			<c:set var="especializacao_" value="${especializacaoAtual}"/>
			<tr>
				<td colspan="2">
					<br>
					<b>${linha.nome_curso} - <i>${empty linha.nome_especializacao? "Curso sem especialização" : linha.nome_especializacao }</i></b>
					<hr>
				</td>
			</tr>
			<tr class="header">
				<td style="text-align: center;">Matrícula</td>
				<td>Discente</td>
			</tr>
		</c:if>
		<tr class="componentes">
			<td style="text-align: center;">${linha.matricula}</td>
			<td>${linha.nome_discente}</td>
		</tr>
	</c:forEach>
	</table>
	<br />
	<table align="center">
		<tr align="center">
			<td colspan="2"><b>Total de Registros: <h:outputText value="#{relatoriosTecnico.numeroRegistrosEncontrados}"/></b></td>
		</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
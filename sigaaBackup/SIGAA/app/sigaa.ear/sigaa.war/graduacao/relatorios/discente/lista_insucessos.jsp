<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.foot td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee; font-weight: bold; font-size: 13px; }
	tr.foot2 td {padding: 3px ; border-bottom: 1px solid #555; border-top: 2px solid #555; background-color: #eee; font-weight: bold; font-size: 14px; }
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
	tr.componentes td.assinatura { padding:2px; border-bottom: 1px solid #888;  width: 40%;}
</style>
<f:view>
	<hr>
	<table width="100%">
		<caption><b>Relatório de Insucessos de Alunos</b></caption>
			<c:if test="${ not empty relatorioDiscente.curso.descricao }">
				<tr>
					<th>Curso:</th>
					<td colspan="3" width="80%">
						<b>${ relatorioDiscente.curso.descricao }</b>
					</td>
				</tr>
			</c:if>
			<tr>
				<th>Ano-Período:</th>
				<td colspan="3" width="80%">
					<b>${relatorioDiscente.ano}.${relatorioDiscente.periodo}</b>
				</td>
			</tr>
	</table>
	<hr>
	<table cellspacing="1" width="100%" style="font-size: 10px;">
	<c:set var="disciplinaLoop"/>
	<c:set var="turmaLoop"/>
	<c:set var="total" value="0"/>
	<c:set var="totalGeral" value="0"/>
	<c:forEach items="${relatorioDiscente.listaDiscente}" var="linha">
		<c:set var="disciplinaAtual" value="${linha.codigo_disciplina}"/>
		<c:set var="turmaAtual" value="${linha.codigo_turma}"/>
		
		<c:if test="${disciplinaLoop != disciplinaAtual || turmaLoop != turmaAtual}">
			<c:if test="${!empty disciplinaLoop}">
				<c:set var="totalGeral" value="${totalGeral+total}"/>
				<tr class="foot">
					<td colspan="2">Total: ${total} </td>
				<tr>
			</c:if>
			<c:set var="disciplinaLoop" value="${disciplinaAtual}"/>
			<c:set var="turmaLoop" value="${turmaAtual}"/>
			<c:set var="total" value="0"/>
			<tr class="curso">
				<td colspan="2">
					<b>${linha.codigo_disciplina} - ${linha.nome_disciplina} - <i>Turma ${linha.codigo_turma}</i></b>
				</td>
			</tr>
			<tr class="header">
				<td><b>Discente</b></td>
				<td><b>Situação</b></td>
			<tr>
		</c:if>
		
		<c:set var="total" value="${total+1}"/>
		<tr class="componentes">
			<td>
				${linha.matricula_discente} -  ${linha.nome_discente }
				<c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
			</td>
			<td>
				${linha.situacao}
			</td>
		</tr>
	</c:forEach>
		<c:set var="totalGeral" value="${totalGeral+total}"/>
		<tr class="foot">
			<td colspan="2">Total: ${total} </td>
		<tr>
		<tr class="foot2">
			<td colspan="2">Total Geral: ${totalGeral} </td>
		<tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

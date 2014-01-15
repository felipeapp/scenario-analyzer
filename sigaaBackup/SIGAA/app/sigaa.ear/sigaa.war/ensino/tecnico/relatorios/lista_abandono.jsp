<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
	tr.componentes td.assinatura { padding:2px; border-bottom: 1px solid #888;  width: 40%;}
</style>
<f:view>
	<hr>
	<table width="100%">
		<caption><b>Lista de Alunos Não Matriculados (Abandono) - ${usuario.vinculoAtivo.unidade.nome}</b></caption>
		<tr>
			<td></td>
		</tr>
	</table>
	<hr>
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<caption><b>Total de Registros: <h:outputText value="#{relatoriosTecnico.numeroRegistrosEncontrados}"/></b></caption>
	<c:set var="curso_"/>
	<c:forEach items="${relatoriosTecnico.lista}" var="linha">
			<c:set var="cursoAtual" value="${linha.id_curso}"/>
		<c:if test="${curso_ != cursoAtual}">
			<c:set var="curso_" value="${cursoAtual}"/>
			<tr class="curso">
				<td colspan="3">
					<br>
					<b>${linha.curso_nome}</b>
					<hr>
				</td>
			</tr>
			<tr class="header">
				<td style="text-align: center;">Ingresso</td>
				<td style="text-align: center;">Matrícula</td>
				<td>Nome</td>
			<tr>
		</c:if>
		<tr class="componentes">
			<td style="text-align: center;">
				${linha.ano_ingresso}.${linha.periodo_ingresso}
			</td>
			<td style="text-align: center;">
				${linha.matricula}
			</td>
			<td>
				${linha.aluno_nome}
			</td>
		</tr>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
	tr.componentes td.assinatura { padding:2px; border-bottom: 1px solid #888;  width: 40%;}
</style>
<f:view>
<h2>Lista de Alunos Cadastrados</h2>

<div id="parametrosRelatorio">
	<table>
			<tr>
				<th>Unidade:</th>
				<td>${usuario.vinculoAtivo.unidade.nome}</td>
			</tr>
	</table>
</div>	
<br />
	<table cellspacing="1" width="100%" style="font-size: 10px;">

		
		<c:set var="anoPeriodo_"/>
		
		<c:forEach items="${relatoriosTecnico.lista}" var="d" varStatus="s">
			<c:set var='anoPeriodoAtual_' value="${ (d.ano_ingresso * 10) + d.periodo_ingresso }"/>
			
			<c:if test="${ anoPeriodo_ != anoPeriodoAtual_ }">
				<c:set var="anoPeriodo_" value="${anoPeriodoAtual_}"/>
				<tr class="curso">
					<td colspan="3">
						<br>
						<b>${d.ano_ingresso}.${d.periodo_ingresso}</b>
						<hr>
					</td>
				</tr>
				<tr class="header">
					<td width="10%" style="text-align: center;">Matrícula</td>
					<td>Nome</td>
					<td width="15%">Situação</td>
				</tr>
			</c:if>
			<tr class="componentes">
				<td style="text-align: center;"> ${d.matricula } </td>
				<td> ${d.aluno_nome} </td>
				<td> ${d.descricao_status} </td>
			</tr>
		</c:forEach>
	</table>
	
	<br /><br />
	
	<table width="100%">
		<tr align="center">
			<td colspan="3"><b>Total de Registros: <h:outputText value="#{relatoriosTecnico.numeroRegistrosEncontrados}"/></b></td>
		</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

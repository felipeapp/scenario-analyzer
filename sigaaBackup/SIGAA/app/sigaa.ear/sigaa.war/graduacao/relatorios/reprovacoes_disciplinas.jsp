<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555; font-weight: bold; font-size: 14px;}
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.foot td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee; font-weight: bold; font-size: 13px; }
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
	tr.componentes td.assinatura { padding:2px; border-bottom: 1px solid #888;  width: 40%;}
</style>
<f:view>
	<h:outputText value="#{ relatoriosCoordenador.create }" />
	<c:set var="relatorio_" value="#{ relatoriosCoordenador.relatorio }"/>
	<c:set var="anoPeriodo" value="#{ relatoriosCoordenador.ano }.#{ relatoriosCoordenador.periodo }"/>
	<c:set var="anoPeriodoIngresso" value="#{ relatoriosCoordenador.anoIngresso }.#{ relatoriosCoordenador.periodoIngresso}"/>
	<hr>
	<table width="100%">
		<caption><b>Relatório de Reprovações em Disciplinas</b></caption>
			<c:if test="${relatoriosCoordenador.exibeCurso}">
				<tr>
					<th>Curso:</th>
					<td colspan="3" width="80%">
						<b>${ relatoriosCoordenador.curso.descricao }</b>
					</td>
				</tr>
			</c:if>
			<tr>
				<th>Ano-Período:</th>
				<td colspan="3" width="80%">
					<b>${ anoPeriodo }</b>
				</td>
			</tr>
			<c:if test="${relatoriosCoordenador.anoIngresso > 0}">
				<tr>
					<th>Ano-Período Ingresso:</th>
					<td colspan="3" width="80%">
					<b>${ anoPeriodoIngresso }</b>
				</td>
				</tr>
			</c:if>
	</table>
	<hr>
	<table cellspacing="1" width="100%" style="font-size: 10px;">
	<caption><b>Reprovações encontradas</b></caption>
		<c:set var="totalGeral" value="0"/>
		<c:forEach var="linha" items="#{ relatorio_ }" varStatus="status">
			<c:set var="total" value="#{ fn:length(linha.value.discentes) }"/>
			<c:set var="totalPresencial" value="#{ totalPresencial + linha.value.totalPresencial }"/>
			<c:set var="totalEAD" value="#{ totalEAD + linha.value.totalEad }"/>
			<c:set var="totalGeral" value="#{ totalGeral + total }"/>
			<tr class="curso">		
				<td> <b>${ linha.value.codigo }</b> </td>
				<td> <b>${ linha.value.nome }</b> </td>
			</tr>
			<c:forEach var="discente" items="#{ linha.value.discentes }">
				<tr class="componentes">
					<td> ${ discente.value } </td>
					<td> ${ discente.key } </td>
				</tr>
			</c:forEach>
			<tr class="foot">
				<td>Total:</td> <td style="text-align: right;">${ total }</td>
			</tr>
		</c:forEach>
		<tr>
			<td>&nbsp</td>
		</tr>
		<tr class="foot">
			<td>Total Presencial:</td> <td style="text-align: right;">${ totalPresencial }</td>
		</tr>
		<c:if test="${relatoriosCoordenador.ead}">
			<tr class="foot">
				<td>Total EAD:</td> <td style="text-align: right;">${ totalEAD }</td>
			</tr>
		</c:if>
		<tr class="foot">
			<td>Total Geral:</td> <td style="text-align: right;">${ totalGeral }</td>
		</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

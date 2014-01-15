<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555; font-size: 14px;}
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.foot td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee; font-weight: bold; font-size: 13px; }
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
	tr.componentes td.assinatura { padding:2px; border-bottom: 1px solid #888;  width: 40%;}
</style>
<f:view>
	<h:outputText value="#{ relatoriosCoordenador.create }" />
	<c:set var="relatorio_" value="${ relatoriosCoordenador.relatorio }"/>
	<c:set var="anoPeriodo" value="${ relatoriosCoordenador.ano }.${ relatoriosCoordenador.periodo }"/>
	
	<h2>Relatório de Trancamentos por semestre</h2>
	
	<div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Curso:</th>
				<td>${ relatoriosCoordenador.curso.descricao }</td>
			</tr>
			<tr>
				<th>Ano-Período:</th>
				<td> ${ anoPeriodo }</td>
			</tr>
		</table>
	</div>
	
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<c:set var="totalGeral" value="0"/>
		<c:forEach var="linha" items="${ relatorio_ }" varStatus="status">
			<c:set var="total" value="${ fn:length(linha.value.discentes) }"/>
			<c:set var="totalGeral" value="${ totalGeral + total }"/>
			<tr class="curso">		
				<td> ${ linha.key.codigo } </td>
				<td colspan="2"> ${ linha.key.detalhes.nome } </td>
			</tr>
			<tr class="foot">		
				<td> Matrícula </td>
				<td> Nome </td>
				<td> Matriz Curricular</td>
			</tr>
			<c:forEach var="discente" items="#{ linha.value.discentes }">
				<tr class="componentes">
					<td> ${ discente.value.matricula } </td>
					<td> ${ discente.value.nome } </td>
					<td> ${ discente.value.matrizCurricular.descricaoMin} </td>
				</tr>
			</c:forEach>
			<tr class="foot">
				<td colspan="3" style="text-align: right;">Total: ${ total }</td>
			</tr>
		</c:forEach>
		<tr class="foot">
			<td colspan="3" style="text-align: right;">Total Geral: ${ totalGeral }</td>
		</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

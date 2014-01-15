<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
<!--
tr.componentes td {padding: 2px ; border-bottom: 1px dashed #888}
-->
</style>
<f:view>
	<hr>
	<table width="100%">
		<caption><b>Relatório Quantitativo de Alunos Ativos</b></caption>
		<tr><td>&nbsp;</td></tr>
	</table>
	<hr>
	<table cellspacing="1" width="100%" style="font-size: 10px;">
	
	<c:set var="total" value="0"/>
	<c:set var="totalGrupo" value="0"/>
	
	<c:forEach items="${relatoriosPlanejamento.relatorio}" var="linha" varStatus="row">
	<tr>
		<td colspan="2"><hr> ${linha.key} <hr></td>
	</tr>
		<c:forEach items="${linha.value}" var="sublinha" varStatus="row2">
			<tr class="componentes">
				<td>${sublinha.key}</td>
				<td align="right">${sublinha.value}</td>
			</tr>
			<c:set var="totalGrupo" value="${ totalGrupo + sublinha.value }"/>
		</c:forEach>
		<tr>
   			<td colspan="2" align="right"><b>Total: ${totalGrupo}</b></td>
   		</tr>
   		<c:set var="total" value="${ total + totalGrupo }"/>
   		<c:set var="totalGrupo" value="0"/>
	</c:forEach>
   		<tr>
   			<td colspan="2"><br><hr></td>
   		</tr>
   		<tr>
   			<td colspan="2" align="center"><b>Total de Alunos: ${total}</b></td>
   		</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

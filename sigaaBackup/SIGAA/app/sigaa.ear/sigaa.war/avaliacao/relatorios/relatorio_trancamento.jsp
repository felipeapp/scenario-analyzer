<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
</style>

<f:view>
	<br />
	<table width="100%">
			<tr>
				<td align="center" style="font-size: 11px;"><b>RELATÓRIO QUANTITATIVO DO TOTAL DE ALUNOS E TOTAL DE TRANCAMENTOS</b></td>
			</tr>
	</table>
	<br />
	<table width="100%">
		<td><b>Ano-Período:</b> 
				<h:outputText value="#{relatorioAvaliacaoMBean.ano}"/>.<h:outputText value="#{relatorioAvaliacaoMBean.periodo}"/>
		</td>
	</table>
	<hr>
	<br/>
	<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
			<tr class="header">
				<td align="left"><b>Descrição</b></td>
				<td align="right"><b>Total</b></td>
			</tr>
		<c:set var="total" value="0"/>
		<c:forEach items="#{relatorioAvaliacaoMBean.result}" var="item">
		<tr class="componentes">
			<td align="left"> ${item.key }</td>
			<td align="right"> ${item.value}</td>
			<c:set var="total"  value="${total + item.value}"/>
		</tr>
		</c:forEach>
		<tr>
		<td align="left"><b>Total Geral:</b></td>
		<td align="right"><b>${total}</b></td>
		</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
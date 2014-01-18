<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	tr.header td {padding: 0px; background-color: #eee; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
</style>

<f:view>
	<h2> RELATÓRIO DOS DISCENTE PAGANTES DO RU </h2>

		<table style="width:100%" border="1">
			<c:set value="0" var="_total" />
			<tr class="header">
				<c:forEach var="item" items="${ relatoriosSaeMBean.discentesPagantes }" varStatus="status">
					<td width="20%" style="text-align: center;"> ${item.key} </td>
				</c:forEach>
			</tr>										
			<tr>
				<c:forEach var="item" items="${ relatoriosSaeMBean.discentesPagantes }" varStatus="status">
					<td style="text-align: center;"> ${item.value} </td>
					<c:set var="_total" value="${ _total + item.value }"/>
				</c:forEach>
			</tr>										
		</table>
		
		<br />

		<table width="100%" style="font-size: 10px">
		<caption><b>Legenda</b></caption>
			<tr>
				<td><b>A:</b><i> Renda per capita acima de 20 SM</i> </td>
				<td><b>B:</b><i> Renda per capita entre 10 e 20 SM</i> </td>
			</tr>
			<tr>
				<td><b>C:</b><i> Renda per capita entre 4 e 10 SM</i> </td>
				<td><b>D:</b><i> Renda per capita entre 2 e 4 SM</i> </td>
			</tr>
			<tr>
				<td><b>E:</b><i> Renda per capita até 2 SM</i> </td>
			</tr>
		</table>

		<br />
		
		<table align="center">
			<tfoot>
				<tr>
					<td colspan="6" style="text-align: center; font-weight: bold;">
						${_total} Total de Discente(s) pagantes encontrado(s)
					</td>
				</tr>
			</tfoot>
		</table>
		
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
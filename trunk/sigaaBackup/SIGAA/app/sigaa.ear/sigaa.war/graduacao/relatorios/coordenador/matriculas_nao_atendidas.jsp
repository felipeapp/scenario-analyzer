<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 3px ; background-color: #eee;}
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
	tr.componentes td.assinatura { padding:2px; border-bottom: 1px solid #888;  width: 40%;}
</style>
<f:view>
	<h:outputText value="#{ relatoriosCoordenador.create }" />
	<c:set var="relatorio" value="#{ relatoriosCoordenador.relatorio }"/>

	<table width="100%">
			<tr align="center">
				<td colspan="4"><h2>Relatório de Matrículas Online não orientadas</h2></td>
			</tr>
			<tr>
				<th style="font-weight: bold; text-align: left;">Curso:</th>
				<td colspan="3" width="90%">
					${ relatoriosCoordenador.curso }
				</td>
			</tr>
	</table>
	
	<table cellspacing="1" width="100%" style="font-size: 10px;">
	<caption><b>Pré-matrículas encontradas</b></caption>
		<c:set var="totalGeral" value="0"/>
		<c:forEach var="linha" items="#{ relatorio }" varStatus="status">
			<c:set var="totalGeral" value="${ totalGeral + linha.value.total }"/>
			<tr class="curso">		
				<td colspan="2" style="font-weight: bold;"> ${ linha.key } </td>
			</tr>
			<c:forEach var="discente" items="#{ linha.value.discentes }">
				<tr class="componentes">
					<td> ${ discente.value } </td>
					<td> ${ discente.key } </td>
				</tr>
			</c:forEach>
			<tr>
				<td colspan="2" style="text-align: right;"><b>Total: </b> ${ linha.value.total } </td>
			</tr>
		</c:forEach>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<hr>
			<td style="text-align: right;"></td> <td style="text-align: right;"><b>Total Geral:</b> ${ totalGeral }</td>
		</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

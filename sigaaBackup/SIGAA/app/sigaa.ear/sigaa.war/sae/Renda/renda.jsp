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
			<caption><b>Lista do Espectro de Renda</b></caption>
	</table>
	<hr>
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<caption><b>Total de Registros: <h:outputText value="#{rendaEspectro.numeroRegistrosEncontrados}"/></b></caption>
			<tr class="header">
				<td align="center">Número de Salários</td>
				<td align="center">Número de Alunos</td>
			<tr>
		<c:forEach items="#{rendaEspectro.lista}" var="linha">
		<tr class="componentes">
			<td align="center"> ${linha.numero_salarios} </td>
			<td align="center"> ${linha.qtd_salario}	</td>
		</tr>
		</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
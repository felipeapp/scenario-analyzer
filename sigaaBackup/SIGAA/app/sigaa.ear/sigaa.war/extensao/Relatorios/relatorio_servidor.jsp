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
			<caption><b>Relatório Servidor Detalhado</b></caption>
	</table>
	<hr>
	<table cellspacing="1" width="100%" style="font-size: 10px;">
			<tr class="header">
				<td align="justify">Tipo da Ação</td> 	
				<td align="justify">Categoria</td> 		
				<td align="justify">Situação</td> 		
				<td align="justify">Função Equipe</td>  
				<td align="justify">Total</td>			
			<tr>
		<c:set var="totalServidor" value="0"/>
		<c:forEach items="#{relatorioPlanejamentoMBean.lista}" var="linha">
		<tr class="componentes">
			<td align="justify"> ${linha.tipo_acao }</td>
			<td align="justify"> ${linha.categoria}</td>
			<td align="justify"> ${linha.situacao_acao}</td>
			<td align="justify"> ${linha.funcao}</td>
			<td align="justify"> ${linha.total}	</td>
			<c:set var="totalServidor"  value="${totalServidor + linha.total}"/>

		</tr>
		</c:forEach>
				<table cellspacing="1" width="100%" style="font-size: 11px;">
					<tr class="header">
						<td>Número de Docentes encontrados: </td>
						<td align="center">${totalServidor}</td>
					</tr>
				</table>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
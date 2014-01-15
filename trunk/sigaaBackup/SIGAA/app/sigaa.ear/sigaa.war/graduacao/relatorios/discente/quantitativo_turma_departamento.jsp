<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.parametro td {padding: 0px; border-bottom: 1px solid #555;}
	tr.header td {padding: 0px ; background-color: #eee; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #000;}
	tr.espaco td {padding: 12px; font-weight: bold;}
	tr.titulo td {border-bottom: 2.5px solid #555; }
</style>
<f:view>

	<table width="100%" style="font-size: 11px;">
		<caption><td align="center"><b>RELATÓRIO QUANTITATIVO DE TURMAS NÂO CONSOLIDADAS POR DEPARTAMENTO</b></td></b>
	</table>
	<br />
	<br />
	<table width="100%">
		<tr>
		<td>	
			<b>Ano - Período:</b> 
			<h:outputText value="#{relatorioDiscente.ano}"/>.<h:outputText value="#{relatorioDiscente.periodo}"/>
		</td>
		<tr class="parametro">
			<td>
				<b>Incluir Ead:</b> <h:outputText value="#{relatorioDiscente.todos ? 'Sim':'Não'}"/>
			</td>
		</tr>
	</table>
	<br />
	<br />

	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<tr class="titulo">
			<td colspan="4"></td>
		</tr>
		<tr class="titulo">
			<td align="center" colspan="4"><b>Itens do Relatório</b></td>
		</tr>
	
		<tr class="header">
			<td align="left"> Departamento</td>
			<td align="right" width="10%"> Total</td>
		</tr>

	<c:forEach items="#{relatorioDiscente.listaDiscente}" var="linha" varStatus="indice">
			
		<tr class="componentes">
			<td align="left"><b> ${linha.nome}</b></td>
			<td align="right"> ${linha.count}</td>
		<c:set var="_total" value="${_total + linha.count}"/>
		</tr>
		</c:forEach>
		
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<tr class="espaco">
			<td align="center">TOTAL DE TURMAS NÃO CONSOLIDADAS: &nbsp; ${_total}</td>
		</tr>
	</table>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 20px 0 0; border-bottom: 1px solid #555}
	tr.total td {padding: 20px 0 0; font-weight: bold;}
	tr.header td {padding: 0px ; background-color: #eee; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componente td {border-bottom: 0.2mm dashed black;}
</style>
<f:view>

	<table width="100%" style="font-size: 11px;">
		<tr>
			<td align="center">
				<b>RELATÓRIO DOS PROGRAMAS QUE NÃO FIZERAM MATRÍCULA ON-LINE</b>
			</td>
		</tr>
	</table>
	<br />
	<table width="100%">
		<tr class="curso">
		<td><b>Ano - Período:</b> 
		<h:outputText value="#{relatorioPrograma.ano}"/>.<h:outputText value="#{relatorioPrograma.periodo}"/>	
		</td>
	</table>
	<br />

    <table cellspacing="1" width="100%" style="font-size: 10px;">
			<tr class="header">
				<td align="center" style="font-size: 11px;">PROGRAMA</td>
			</tr>
		<c:set var="_total" value="0"/>	
		
		<c:forEach items="#{relatorioPrograma.listaPrograma}" var="linha" varStatus="indice">
			<tr class="componente">
				<td align="left">${linha.nome_programa}</td>
				<c:set var="_total" value="${_total  + 1}"/>
			</tr>
		</c:forEach>
	</table>
	
	<table cellspacing="1" width="100%" style="font-size: 11px;">
		<tr class="total">
			<td align="center">Total de programas que não fizeram Matrícula On-line: ${_total}</td>
		</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
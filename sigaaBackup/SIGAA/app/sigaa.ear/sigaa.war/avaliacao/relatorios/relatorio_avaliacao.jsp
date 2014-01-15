<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
	tr.componentes td.assinatura { padding:2px; border-bottom: 1px solid #888;  width: 40%;}
</style>
<f:view>
	<table width="100%" style="font-size: 12px;">
		<caption><td align="center"><b>RELATÓRIO QUANTITATIVO DE TRANCAMENTOS POR CENTRO / DEPARTAMENTO</b></td></b>
	</table>
	<br />	<br />
	<table width="100%">
		<td><b>Ano - Período:</b> 
		<h:outputText value="#{relatorioAvaliacaoMBean.ano}"/>.<h:outputText value="#{relatorioAvaliacaoMBean.periodo}"/>	
		</td>
	</table>
	<hr>
	
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		
		<c:set var="total" value="0"/>
		<c:set var="totalCentro" value="0"/>
		<c:set var="_centro" />
		
		<c:forEach items="#{relatorioAvaliacaoMBean.lista}" var="linha">
			<c:set var="centroAtual" value="${linha.nome_centro}"/>
		
			<c:if test="${_centro != centroAtual}">
				<c:if test="${not empty _centro}">
					<tr>
						<td></td>
					</tr>
					<tr>
						<td align="right"><b>Total do Centro:</b></td>
						<td><b>${totalCentro}</b></td>
					</tr>
				</c:if>
				
				<tr class="curso">
					<td colspan="2"><b>${linha.nome_centro}</b></td>
				</tr>
				
				<c:set var="totalCentro" value="0"/>
				<c:set var="_centro" value="${centroAtual}"/>
			</c:if>
			
			<c:set var="totalCentro" value="${totalCentro + linha.count}"/>
			<c:set var="total" value="${total + linha.count}"/>
		
			<tr class="componentes">
				<td align="justify"> ${linha.nome_unidade}</td>
				<td align="right"> ${linha.count}</td>
			</tr>
		</c:forEach>
		<tr>
			<td align="right"><b>Total do Centro:</b></td>
			<td align="right"><b>${totalCentro}</b></td>
		</tr>
		<tr><td></td></tr>
		<table align="center">
		<tr>
			<td align="center"><b>Total de Trancamentos:</b></td>
			<td><b>${total}</b></td>
		</tr>
		</table>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
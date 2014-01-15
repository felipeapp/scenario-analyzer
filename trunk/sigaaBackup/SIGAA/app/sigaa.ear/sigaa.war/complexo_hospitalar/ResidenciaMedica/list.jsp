<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555}
	tr.linha td {padding: 3px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
</style>
<f:view>
	<table width="100%" style="font-size: 12px;">
		<caption><td align="center"><b>RELATÓRIO DAS RESIDÊNCIAS MÉDICAS</b></td></b>
	</table>
	<br />
	<c:set var="ano" value="${residenciaMedica.checkBuscaAno}"/>
	<c:set var="programa" value="${residenciaMedica.checkBuscaPrograma}"/>
	
	<table width="100%">
		
	<c:if test="${ano == programa}">
	<tr>
		<td><b>Ano:</b> 
		<h:outputText value="#{residenciaMedica.ano}"/>.<h:outputText value="#{residenciaMedica.periodo}"/></td></td>
	</tr>
	<tr class="linha">
		<td><b>Programa:</b> 
		<h:outputText value="#{residenciaMedica.nome}"/></td>
	</tr>
	</c:if>

	<c:if test="${programa == false}">
	<tr class="linha">
		<td><b>Ano:</b> 
		<h:outputText value="#{residenciaMedica.ano}"/>.<h:outputText value="#{residenciaMedica.periodo}"/></td></td>
	</tr>
	</c:if>
	<c:if test="${ano == false}">
	<tr class="linha">
		<td><b>Programa:</b> 
		<h:outputText value="#{residenciaMedica.nome}"/></td>
	</tr>
	</c:if>
	</table>
	<br/>
	
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<c:set var="total" value="0"/>
		<c:set var="_hospital" />
		
		<c:forEach items="#{residenciaMedica.lista}" var="linha">
			<c:set var="hospitalAtual" value="${linha.hospital}"/>
		
			<c:if test="${_hospital != hospitalAtual}">
				<c:if test="${not empty _hospital}">
					<tr>
						<td></td>
					</tr>
				</c:if>
				
				<tr class="curso">
					<td colspan="4"><b>${linha.hospital}</b></td>
				</tr>
				<tr>
					<td><b>Programa</b></td>
					<td><b>Servidor</b></td>
					<td align="center"><b>Ano/Período</b></td>
					<td align="right"><b>Carga Horária</b></td>
				</tr>
				
				<c:set var="_hospital" value="${hospitalAtual}"/>
			</c:if>
			
			<tr class="componentes">
				<td align="left"> ${linha.nome}</td>
				<td align="left"> ${linha.nome_servidor}</td>
				<td align="center"> ${linha.ano}.${linha.semestre}</td>
				<td align="right"> ${linha.ch_semanal}</td>
			</tr>
		</c:forEach>
		<tr><td></td></tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 20px 0 0; border-bottom: 1px solid #555; }
	tr.header td {padding: 0px ; border-bottom: #555; background-color: #eee; font-weight: bold;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px dashed #888;}
</style>
<f:view>
	<table width="100%" style="font-size: 12px;">
		<caption><td align="center"><b>PROFESSORES QUE NÃO UTILIZAM AS TURMAS VIRTUAIS</b></td></b>
	</table>
	<br />
	<table width="100%">
		<tr class="curso">
		<td><b>Ano - Período:</b> 
		<h:outputText value="#{relatoriosPlanejamento.ano}"/>.<h:outputText value="#{relatoriosPlanejamento.periodo}"/>	
		</td>
	</table>
	<br />
	
	<table cellspacing="1" width="100%" style="font-size: 10px;">
	   <c:set var="_departamento" />
		<c:forEach items="#{relatoriosPlanejamento.lista}" var="linha">
			<c:set var="departamentoAtual" value="${linha.departamento}"/>
		
			<c:if test="${_departamento != departamentoAtual}">
				<c:if test="${not empty _departamento}">
					<tr>
						<td></td>
					</tr>
				</c:if>
				
				<tr class="curso">
					<td colspan="10"><b>${linha.departamento}</b></td>
				</tr>
				<tr class="header">
					<td align="left"> Nome</td>
				</tr>
					<c:set var="_departamento" value="${departamentoAtual}"/>
			</c:if>
				<tr class="componentes">
					<td align="left"> ${linha.nome}</td>
				</tr>
		  </c:forEach>
		</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
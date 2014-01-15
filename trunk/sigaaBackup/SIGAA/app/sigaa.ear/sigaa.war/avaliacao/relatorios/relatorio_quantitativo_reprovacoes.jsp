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
		<td><b>Ano - Período:</b> 
			<h:outputText value="#{relatorioAvaliacaoMBean.ano}"/>.<h:outputText value="#{relatorioAvaliacaoMBean.periodo}"/>
		</td>
	</table>
	<hr>
	<br/>
	<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
			<tr class="header">
				<td align="left"><b>Curso</b></th> 	
				<td align="right"><b>Reprovações por Nota</b></td> 		
				<td align="right"><b>Reprovações por Falta</b></td> 		
				<td align="right"><b>Total de Reprovações</b></td>  
			</tr>
	
		<c:set var="total_nota" value="0"/>
		<c:set var="total_falta" value="0"/>
		<c:set var="total" value="0"/>
		<c:forEach items="#{relatorioAvaliacaoMBean.lista}" var="linha">
		<tr class="componentes">
			<td align="left"> ${linha.curso }</td>
			<td align="right">  ${linha.reprovacao_por_nota}</td>
			<td align="right">  ${linha.reprovacao_por_falta}</td>
			<td align="right">  ${linha.total_de_reprovacoes}</td>
			<c:set var="total_nota"  value="${total_nota + linha.reprovacao_por_nota}"/>
			<c:set var="total_falta"  value="${total_falta + linha.reprovacao_por_falta}"/>
			<c:set var="total"  value="${total + linha.total_de_reprovacoes}"/>
		</tr>
		</c:forEach>
				<tr class="header">
					<td align="left"><b>Total de Reprovados: </b></td>
					<td align="right"><b>${total_nota}</b></td>
					<td align="right"><b>${total_falta}</b></td>
					<td align="right"><b>${total}</b></td>
				</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
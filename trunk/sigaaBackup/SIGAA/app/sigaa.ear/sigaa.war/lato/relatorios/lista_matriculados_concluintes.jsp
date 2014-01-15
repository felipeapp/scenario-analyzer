<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>	
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
</style>

<f:view>
	<hr>
	<table width="100%">
		<caption><b>RELATÓRIO QUANTITATIVO DOS ALUNOS MATRICULADOS E CONCLUÍDOS POR CENTRO</b></caption>
	</table>
	<br />
	<table width="100%">
		<td><b>Ano-período:</b> 
			<h:outputText value="#{relatoriosLato.ano}"/>.<h:outputText value="#{relatoriosLato.periodo}"/>
		</td>
	</table>
	<hr>
	<br />
	<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
		<tr	class="header">
			<td align="left"><b>Unidade</b></td>
			<td align="right"><b>Matriculados</b></td>
			<td align="right"><b>Concluídos</b></td>
			<td align="right"><b>Total</b></td>
		</tr>
		<c:set var="matriculados" value="0"/>
		<c:set var="concluintes" value="0"/>
		<c:set var="total" value="0"/>
		<c:forEach items="${relatoriosLato.lista}" var="linha">
			<tr class="componentes">
				<td align="left">${linha.nome}</td>
				<td align="right">${linha.alunos_matriculados}</td>
				<td align="right">${linha.alunos_concluintes}</td>
				<td align="right">${linha.alunos_matriculados + linha.alunos_concluintes}</td>
				<c:set var="matriculados"  value="${matriculados + linha.alunos_matriculados}"/>
				<c:set var="concluintes"  value="${concluintes + linha.alunos_concluintes}"/>
				<c:set var="total"  value="${total + linha.alunos_matriculados + linha.alunos_concluintes}"/>
			</tr>
		</c:forEach>
			<tr class="header"> 
			<td><b>Total Geral:</b></td>
			<td align="right"><b>${matriculados}</b></td>
			<td align="right"><b>${concluintes}</b></td>
			<td align="right"><b>${total}</b></td>
			</tr>
  </table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

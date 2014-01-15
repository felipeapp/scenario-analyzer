<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 1px 0 0 ;}
	tr.discentes td {padding: 2px ; border-bottom: 1px dashed #888}
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.ano-Qtd td {padding: 15px 0 0; border-bottom: 1px solid #555}
</style>
<f:view>
	<h:outputText value="#{ relatoriosCoordenador.create }" />
	<c:set var="relatorio" value="#{ relatoriosCoordenador.relatorio }"/>

	<h2><b>Lista de Alunos Ativos no Curso</b></h2>
	<table>
		<tr class="curso">
			<td><b>Curso: </b> <h:outputText value="#{opCoordenadorGeralEad.curso}"/></td>
		</tr>
	</table>
	<hr>
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<c:set var="totalGeral" value="0"/>
		<c:forEach items="#{ relatorio }" var="linha" varStatus="status">
			<c:set var="totalGeral" value="#{ totalGeral + linha.value.total }"/>
			<tr class="ano-Qtd" >
				<td> <b>Ano: ${ linha.key }</b> </td>
				<td> <b>Total discentes: ${ linha.value.total }</b> </td>
			</tr>
			<tr class="header">
				<td align="center"><b>Matrícula</b></td>
				<td align="left"><b>Discente</b></td>
			</tr>
			<c:forEach var="discente" items="#{ linha.value.discentes }">
				<tr class="discentes">
					<td align="center"> ${ discente.value } </td>
					<td> ${ discente.key } </td>
				</tr>
			</c:forEach>
		</c:forEach>		
	</table>
	<br>
	<h3 align="right"><b>Total de Registros: <h:outputText value="#{totalGeral}"/></b></h3>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

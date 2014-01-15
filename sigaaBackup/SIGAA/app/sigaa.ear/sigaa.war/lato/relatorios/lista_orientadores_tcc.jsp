<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
<!--
tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
tr.orientadores td {padding: 2px ; border-bottom: 1px dashed #888}
-->
</style>

<f:view>
	<h2>Orientadores de Trabalho Final de Curso</h2>
	
	<b>Curso: </b><h:outputText value="#{relatoriosLato.curso}"/>
	<hr>
	
	<br/>
	
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<tr class="header">
				<td><b>Orientador</b></td>
				<td><b>Discente</b></td>
				<td><b>Título do Trabalho</b></td>
			</tr>
		<c:forEach items="${relatoriosLato.lista}" var="linha">
			<tr class="orientadores">
				<td>${linha.docente}</td>
				<td>${linha.orientando}</td>
				<td>${linha.titulo}</td>
			</tr>
		</c:forEach>
	</table>
	<center><b>Total de Registros: <h:outputText value="#{relatoriosLato.numeroRegistrosEncontrados}"/></b></center>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
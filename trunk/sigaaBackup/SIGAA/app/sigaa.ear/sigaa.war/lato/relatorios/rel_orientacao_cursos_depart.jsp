<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 20px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 0px ; background-color: #eee; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #000;}
</style>

<f:view>
	<h2>Orientadores de Trabalho Final de Curso</h2>
	
	<table cellspacing="1" width="100%" style="font-size: 10px;">
	
	    <c:set var="_departamento" />
		<c:forEach items="${ relatoriosLato.lista }" var="linha">
	
			<c:set var="departamentoAtual" value="${ linha.unidade }"/>
			<c:if test="${_departamento != departamentoAtual}">
				<tr class="curso">
					<td colspan="10"><b> ${ linha.unidade } </b></td>
				</tr>
				
				<tr class="header">
					<td style="text-align: left;"> Orientador</td>
					<td style="text-align: left;"> Discente</td>
					<td style="text-align: left;"> Título do Trabalho</td>
				</tr>
					<c:set var="_departamento" value="${departamentoAtual}"/>
			</c:if>
				

				<tr class="componentes">
					<td style="text-align: left;"> ${ linha.docente } </td>
					<td style="text-align: left;"> ${ linha.orientando } </td>
					<td style="text-align: left;"> ${ linha.titulo } </td>
				</tr>
			
		</c:forEach>
	
	</table>
	
	<center><b>Total de Registros: <h:outputText value="#{relatoriosLato.numeroRegistrosEncontrados}"/></b></center>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<style>
<!--
	table.subFormulario td {text-align: left;}
	
	table.subFormulario  tr.turmas td{
		background: #C4D2EB;
		padding-left: 10px;
		font-weight: bold;
	}
-->
</style>

<f:view>
	${faltaDocente.create}
	${faltaDocente.observacaoAluno}
	
	<h:form>
		<table width="100%">
			<thead>
				<tr>
					<th width="3%" style="text-align:center">#</th>
					<th style="text-align:left" width="97%">Observação</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="denuncia" items="#{faltaDocente.denuncias}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
						<td width="3%" style="text-align:left">${status.index + 1}</td>
						<td width="97%" style="text-align:left">${denuncia.observacao }</td>
					</tr>
				</c:forEach>
			</tbody>
				
		
		</table>
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

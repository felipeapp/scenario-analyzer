<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%-- Página que mostra as informações de todas as assinaturas de um Título, as assinaturas devem ser passadas na variável de request _assinaturas--%>

<table class="visualizacao">
	
	<tr> 
	 <th style="text-align: center; border:1px solid #DEDFE3;" colspan="8"> Assinaturas do Título ( ${fn:length(_assinaturas)} )</th>
	</tr>
	
	<c:set var="idFiltroUnidadeDestinoAssinatura" value="-1" scope="request"/>
	<c:forEach var="assinatura" items="${_assinaturas}">
		
		<c:if test="${ idFiltroUnidadeDestinoAssinatura != assinatura.unidadeDestino.id}">
			<c:set var="idFiltroUnidadeDestinoAssinatura" value="${assinatura.unidadeDestino.id}" scope="request"/>
			<tr>
				<th style="text-align: left; border:1px solid #DEDFE3; background-color:#EBEBEB;" colspan="8">${assinatura.unidadeDestino.descricao}</th>
			</tr>
		</c:if>
		
		<tr>
			<th width="10%"> Código : </th>
			<td width="10%"> ${assinatura.codigo} </td>
			<th width="10%"> Título: </th>
			<td width="30%"> ${assinatura.titulo} </td>
			<th width="10%"> ISSN: </th>
			<td width="10%"> ${assinatura.issn} </td>
			<th width="20%">Modalidade Aquisição:</th>
			<td width="10%">
				<c:if test="${assinatura.assinaturaDeCompra}">
					COMPRA
				</c:if>
				<c:if test="${assinatura.assinaturaDeDoacao}">
					DOAÇÃO
				</c:if>
				<c:if test="${! assinatura.assinaturaDeDoacao && ! assinatura.assinaturaDeCompra}">
					INDEFINIDO
				</c:if>
			</td>
		</tr>
	
	</c:forEach>
	
</table>
<br />
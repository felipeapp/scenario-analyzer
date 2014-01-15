<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%-- Página que mostra as informações de uma assinatura, a assinatura devem ser passadas na variável de request _assinatura--%>

<table class="visualizacao">

	<caption>Dados da Assinatura</caption>
	
	<tr>
		<th width="25%"> Código : </th>
		<td width="25%"> ${_assinatura.codigo} </td>
		<th width="25%"> Título: </th>
		<td width="25%"> ${_assinatura.titulo} </td>
	</tr>
	
	<tr>
		<th>Modalidade de Aquisição:</th>
		<td>
			<c:if test="${_assinatura.assinaturaDeCompra}">
				COMPRA
			</c:if>
			<c:if test="${_assinatura.assinaturaDeDoacao}">
				DOAÇÃO
			</c:if>
			<c:if test="${! _assinatura.assinaturaDeDoacao && ! _assinatura.assinaturaDeCompra}">
				INDEFINIDO
			</c:if>
		</td>
		<th>Unidade Destino: </th>
		<td>${_assinatura.unidadeDestino.descricao}</td>
	</tr>
		
</table>
<br />
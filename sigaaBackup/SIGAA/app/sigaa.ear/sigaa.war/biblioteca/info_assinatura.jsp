<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%-- P�gina que mostra as informa��es de uma assinatura, a assinatura devem ser passadas na vari�vel de request _assinatura--%>

<table class="visualizacao">

	<caption>Dados da Assinatura</caption>
	
	<tr>
		<th width="25%"> C�digo : </th>
		<td width="25%"> ${_assinatura.codigo} </td>
		<th width="25%"> T�tulo: </th>
		<td width="25%"> ${_assinatura.titulo} </td>
	</tr>
	
	<tr>
		<th>Modalidade de Aquisi��o:</th>
		<td>
			<c:if test="${_assinatura.assinaturaDeCompra}">
				COMPRA
			</c:if>
			<c:if test="${_assinatura.assinaturaDeDoacao}">
				DOA��O
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
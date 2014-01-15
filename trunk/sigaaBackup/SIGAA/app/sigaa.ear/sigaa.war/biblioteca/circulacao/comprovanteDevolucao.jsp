<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style type="text/css">

h3{
	margin-top:20px;
	margin-bottom:30px;
	text-align: center;
}

#divDadosUsuario {
	margin-bottom:30px;
	width: 70%;
	margin-left: auto;
	margin-right: auto;
}

#divDadosUsuario span{
	font-weight: bold;
}


#divMensagemDeclaracao{
	margin-bottom:30px;
}

#dadosMaterial{
	margin-bottom:30px;
}

#dadosMaterial table tr th{ 
	width: 30%;
	text-align: left;
	font-weight: bold;
	padding-left: 20px;
}


#divCodigoVerificacao{
	text-align: center;
	margin-right: auto;
	margin-left: auto;
	font-style: italic;
	color: gray;
}

</style>

<c:if test="${requestScope.liberaEmissaoComprovante == true}">

	<h3>COMPROVANTE DE DEVOLUÇÃO</h3>
	
	<div id="divDadosUsuario">
	
			<c:if test="${_mBeanRealizouDevolucao.infoUsuario != null }">
				<f:view>	
				<c:set var="_infoUsuarioCirculacao" value="${_mBeanRealizouDevolucao.infoUsuario}" scope="request" />
				<c:set var="_transparente" value="true" scope="request" />
				<c:set var="_mostrarFoto" value="false" scope="request" />			
							
				<%@include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%>
				</f:view>
			</c:if>
	
	</div>
	
	
	
	
	
	
	
	<div id="dadosMaterial">

		<table style="width: 100%;"> 
		
			<tr>
				<th style="width: 30%;">Código de Barras: </th>
				<td>${_mBeanRealizouDevolucao.emprestimoDevolvido.materialDto.codigoBarras}</td>
			</tr>
			<tr>
				<th>Título: </th>
				<td>${_mBeanRealizouDevolucao.emprestimoDevolvido.materialDto.tituloDto.titulo}</td>
			</tr>
			<tr>
				<th>Autor: </th>
				<td>${_mBeanRealizouDevolucao.emprestimoDevolvido.materialDto.tituloDto.autor}</td>
			</tr>
			
			<tr>
				<td colspan="2" style="height: 20px;"></td>
			</tr>
			
			<tr>
				<th>Devolução Prevista: </th>
				<td> ${_mBeanRealizouDevolucao.emprestimoDevolvido.prazoFormatado}  </td>
			</tr>
			<tr>
				<th>Data da Devolução: </th>
				<td> ${_mBeanRealizouDevolucao.emprestimoDevolvido.dataDevolucaoFormatado} </td>
			</tr>
			
			<tr>
				<td colspan="2" style="height: 20px;"></td>
			</tr>
			
			<c:forEach var="mensagem" items="${_mBeanRealizouDevolucao.mensagensComprovanteDevolucao}">
				<tr>
				<td  colspan="2" style="text-align: center"> ${mensagem} </td>
				</tr>
			</c:forEach>
			
			<tr>
				<td colspan="2" style="height: 20px;"></td>
			</tr>
			
			<tr>
				<th>Operador(a): </th>
				<td> ${sessionScope.usuario.nome}</td>
			</tr>
			
		</table>
	
	</div>
	
	
	<div id="divCodigoVerificacao">
			Código de Autenticação : ${_mBeanRealizouDevolucao.emprestimoDevolvido.numeroAutenticacao}
	</div>	
	
</c:if>

<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style type="text/css">

h3{
	margin-top:20px;
	margin-bottom:30px;
	text-align: center;
}

#divDadosUsuario {
	margin-bottom:30px;
}

#divDadosUsuario span{
	font-weight: bold;
}


#divMensagemDeclaracao{
	margin-bottom:30px;
	text-align: center;
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

	<h3>COMPROVANTE DE RENOVAÇÃO</h3>
	
	<div id="divDadosUsuario">
	
			<c:if test="${_mBeanRealizouRenovacao.infoUsuario != null }">
				<f:view>	
				<c:set var="_infoUsuarioCirculacao" value="${_mBeanRealizouRenovacao.infoUsuario}" scope="request" />
				<c:set var="_transparente" value="true" scope="request" />
				<c:set var="_mostrarFoto" value="false" scope="request" />			
							
				<%@include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%>
				</f:view>
			</c:if>
	
	</div>
	
	
	
	<div id="divMensagemDeclaracao">
			&nbsp&nbsp&nbsp&nbsp As renovações dos materiais abaixo foram realizadas com sucesso:
	</div>	
	
	
	
	<div id="dadosMaterial">

		<table style="width: 100%;"> 
		
			<thead>
				<tr>
					<td style="width: 20%; font-weight: bold; background-color: none; text-align: left;">Data da Renovação</td>
					<td style="width: 60%; font-weight: bold; background-color: none; text-align: center;">Informações do Material</td>
					<td style="width: 20%; font-weight: bold; background-color: none; text-align: center;">Prazo para Devolução</td>
				</tr>
			</thead>
		
			<c:if test="${not empty _mBeanRealizouRenovacao.emprestimosRenovadosOp}">
				<c:forEach var="operacaoRenovacao" items="#{_mBeanRealizouRenovacao.emprestimosRenovadosOp}" varStatus="status">
					<tr>
						<td style="text-align: left;"> ${operacaoRenovacao.dataRealizacaoFormatada} </td>
						<td style="text-align: left; padding-left: 10px;"> ${operacaoRenovacao.infoMaterial} </td>
						<td style="text-align: left;"> ${operacaoRenovacao.prazoFormatado} </td>
					</tr>
					
				</c:forEach>
			</c:if>
		</table>
	
	</div>
	
	
	<div id="divCodigoVerificacao">
			Código de Autenticação : ${_mBeanRealizouRenovacao.codigoAutenticacaoRenovacao}
	</div>	
	
</c:if>

<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
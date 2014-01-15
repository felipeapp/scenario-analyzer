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

<%-- para não permitir o usuário acessar essa página diretamente  --%>

<f:view>

<h3>COMPROVANTE DE COMUNICAÇÃO DE PERDA DE MATERIAL</h3>

<div id="divDadosUsuario">

		<c:if test="${comunicarMaterialPerdidoMBean.informacaoUsuario != null }">
		
			<c:set var="_infoUsuarioCirculacao" value="${comunicarMaterialPerdidoMBean.informacaoUsuario}" scope="request"/>
			<c:set var="_mostrarFoto" value="${false}" scope="request"/>
			<c:set var="_transparente" value="${false}" scope="request"/>
			<%@ include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%><br>
	
		</c:if>

</div>



<div id="divMensagemDeclaracao">
		&nbsp&nbsp&nbsp&nbsp O usuário(a) supracitado(a), comunicou a perda do seguinte material que estava sobre sua responsabilidade:
</div>	





<div id="dadosMaterial">

	<table style="width: 100%;"> 
	
		<tr>
			<th>Código de Barras:</th>
			<td> ${comunicarMaterialPerdidoMBean.emprestimo.material.codigoBarras} </td>
		</tr>
		
		<tr style="height: 20px;  ">
		</tr>
	
		<tr>
			<th>Título:</th>
			<td> ${comunicarMaterialPerdidoMBean.infoMaterial.titulo} </td>
		</tr>
		
		<c:if test="${not empty comunicarMaterialPerdidoMBean.infoMaterial.subTitulo}">
			<tr>
				<th>SubTítulo:</th>
				<td> ${comunicarMaterialPerdidoMBean.infoMaterial.subTitulo} </td>
			</tr>
		</c:if>
		
		
		<tr>
			<th>Autores:</th>
			<td> ${comunicarMaterialPerdidoMBean.infoMaterial.autor} </td>
		</tr>
		<tr>
		<tr>
			<th> </th>
			<td>
				<table width="100%" id="tabelaInterna">
					<c:forEach items="${comunicarMaterialPerdidoMBean.infoMaterial.autoresSecundariosFormatados}" var="autorSecundario">
					<tr>
						<td>
							${autorSecundario}
						</td>
					</tr>
					</c:forEach>
				</table>
			</td>
		</tr>
		
		<tr>	
			<th>Ano:</th>
			<td>
				<table width="100%" id="tabelaInterna">
					<c:forEach items="#{comunicarMaterialPerdidoMBean.infoMaterial.anosFormatados}" var="ano">
					<tr>
						<td>
							${ano}
						</td>
					</tr>
					</c:forEach>
				</table>
			</td>
		</tr>
		
		<tr>	
			<th>Edição:</th>
			<td>
				${comunicarMaterialPerdidoMBean.infoMaterial.edicao}
			</td>
		</tr>
		
		<tr>	
			<th>Local Publicação:</th>
			<td>
				<table width="100%" id="tabelaInterna">
					<c:forEach items="#{comunicarMaterialPerdidoMBean.infoMaterial.locaisPublicacaoFormatados}" var="local">
					<tr>
						<td>
							${local}
						</td>
					</tr>
					</c:forEach>
				</table>
			</td>
		</tr>
			
		<tr>	
			<th>Editora:</th>
			<td>
				<table width="100%" id="tabelaInterna">
					<c:forEach items="#{comunicarMaterialPerdidoMBean.infoMaterial.editorasFormatadas}" var="editora">
					<tr>
						<td>
							${editora}
						</td>
					</tr>
					</c:forEach>
				</table>
			</td>
		</tr>
		
		<c:if test="${fn:length(comunicarMaterialPerdidoMBean.infoMaterial.ISBNFormatados) > 0 }">
			<tr>
				<th> ISBN: </th>
				<td>
				<table width="100%" id="tabelaInterna">
					<c:forEach items="${comunicarMaterialPerdidoMBean.infoMaterial.ISBNFormatados}" var="isbn">
					<tr>
						<td>
							${isbn}
						</td>
					</tr>
					</c:forEach>
				</table>
			</td>
			</tr>
		</c:if>
		
		<c:if test="${fn:length(comunicarMaterialPerdidoMBean.infoMaterial.ISSNFormatados) > 0 }">
			<tr>
				<th> ISSN: </th>
				<td>
				<table width="100%" id="tabelaInterna">
					<c:forEach items="${comunicarMaterialPerdidoMBean.infoMaterial.ISSNFormatados}" var="issn">
					<tr>
						<td>
							${issn}
						</td>
					</tr>
					</c:forEach>
				</table>
			</td>
			</tr>
		</c:if>
	
	</table>



</div>







<div id="divMensagemDeclaracao">
		&nbsp&nbsp&nbsp&nbsp Assim, o mesmo se compromete a fazer sua reposição até a data de:  <strong><ufrn:format type="data" valor="${comunicarMaterialPerdidoMBean.emprestimo.prazo}" /></strong>
		sem sofrer penalidades, caso esta comunicação tenha sido feita dentro do tempo hábil.
</div>	


<div id="divCodigoVerificacao">
		Código de Autenticação : ${comunicarMaterialPerdidoMBean.codigoAutenticacaoProrrogacao}
</div>	


</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
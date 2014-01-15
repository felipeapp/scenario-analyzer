<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2 class="tituloPagina"> <ufrn:subSistema/> > Dados Acadêmicos Consolidados</h2>
	<br>

	<h:form id="form">
	
		<table class="formulario" width="100%">
			<caption class="listagem">Consolidação dos Dados Acadêmicos</caption>
			
			<c:set var="municipioAtual" value="0" />
			<c:forEach items="#{ dadosIndiceAcaMBean.all }" var="linha" varStatus="status">
				
				<c:if test="${ linha.matriz.curso.municipio.id != municipioAtual}">
					<c:if test="${ municipioAtual > 0 }">
						<tr class="linhaPar">
							<td colspan="3" style="padding-top: 15px;"></td>
						</tr>
					</c:if>
					
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
						<td class="subFormulario" colspan="3">
							${ linha.matriz.curso.municipio.nome }
						</td>
					</tr>

					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
						<td class="subFormulario"> Curso </td>
						<td class="subFormulario" style="text-align: center;"> IECH </td>
						<td class="subFormulario" style="text-align: center;"> IEPL </td>
					</tr>
						
				</c:if>
				
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td width="80%"> ${ linha.matriz.curso.nome } </td>
					<td style="text-align: center;"> ${ linha.iech } </td>
					<td style="text-align: center;"> ${ linha.iepl } </td>
				</tr>
				
				<c:set var="municipioAtual" value="${ linha.matriz.curso.municipio.id }" />
			</c:forEach>
		</table>
		
		<br />
		<center>
			<h:commandLink value="<< Voltar" action="#{ dadosIndiceAcaMBean.listar }"/>
		</center>

	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
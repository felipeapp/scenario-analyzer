<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="traducaoElementoMBean" />
	<h2><ufrn:subSistema /> > Tradução de ${traducaoElementoMBean.tituloOperacao}</h2> 
	
	<div class="descricaoOperacao">
		Utilize esta operação para realizar a tradução dos elementos utilizados na emissão de documentos oficiais da instituição. 
		Para realizar a internacionalização dos elementos, informe a tradução nos valores destinados a cada idioma. 
	</div>
	
	<h:form>
		<table class="visualizacao" style="width: 80%">
			<tr>
				<th width="22%" style="text-align: right;">${traducaoElementoMBean.tituloOperacao}:</th>
				<td style="text-align: left;">${traducaoElementoMBean.descricaoObjeto }</td>
			</tr>
		</table>
		<br/><br/>	
	
		<table class="formulario" width="80%">
			<caption>Internacionalização de ${traducaoElementoMBean.tituloOperacao}</caption>
			<thead>
				<tr>
					<th width="10%" style="text-align:left; padding-left: 5px; font-weight: bold">Campo</th>
					<th style="text-align:left; padding-left: 5px;">Tradução</th>
				</tr>	
			</thead>
			<tbody>		
				<c:forEach items="#{traducaoElementoMBean.listaTraducaoElemento}" var="item" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<th style="text-align:left; padding-left: 5px; font-weight: bold" valign="center">${item.itemTraducao.nome}:</th>
						<td style="text-align:left;">
							<table>
							<c:forEach items="#{item.elementos}" var="elemento" varStatus="loop">
								<tr>
									<td><h:outputText value="#{elemento.descricaoIdioma}:"/></td>
								</tr>
								<tr>
									<td><h:inputTextarea value="#{elemento.valor}" disabled="#{elemento.idiomaLocal or elemento.inputDisabled}"  cols="#{item.itemTraducao.tipoAreaTexto ? '80' : '60'}" rows="#{item.itemTraducao.tipoAreaTexto ? '5' : '1'}"/></td>
								</tr>	
							</c:forEach>
							</table>
						</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
			<tr>
				<td colspan=2>
					<h:commandButton value="#{traducaoElementoMBean.confirmButton}" action="#{traducaoElementoMBean.cadastrar}" /> 
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{traducaoElementoMBean.cancelar}" />
				</td>
			</tr>
		</table>	
		
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
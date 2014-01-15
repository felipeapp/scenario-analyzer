<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="componenteTraducaoMBean" />
	<h2><ufrn:subSistema /> > Tradução de Componente Curricular</h2> 
	<h:form>
		<table class="visualizacao" style="width: 80%">
			<tr>
				<td width="5%"></td>
				<th width="25%" style="text-align: right;">Componente Curricular:</th>
				<td style="text-align: left;">${componenteTraducaoMBean.obj.descricaoResumida }</td>
			</tr>
			<tr>
				<td></td>
				<th style="text-align: right;"> Tipo do Componente: </th>
				<td style="text-align: left;"> ${componenteTraducaoMBean.obj.tipoComponente.descricao } </td>
			</tr>
			<c:if test="${componenteTraducaoMBean.obj.atividade}">
			<tr>
				<td></td>
				<th style="text-align: right;"> Tipo de Atividade: </th>
				<td style="text-align: left;"> ${componenteTraducaoMBean.obj.tipoAtividade.descricao } </td>
			</tr>
			</c:if>
			<c:choose>
			<c:when test="${ componenteTraducaoMBean.obj.passivelTipoAtividade }">
				<tr>
					<td></td>
					<th style="text-align: right;"> CH Total: </th>
					<td style="text-align: left;"> ${componenteTraducaoMBean.obj.chTotal} h </td>
				</tr>
			</c:when>
			<c:otherwise>
				<tr>
					<td></td>
					<th style="text-align: right;"> Total de Créditos: </th>
					<td style="text-align: left;"> ${componenteTraducaoMBean.obj.crTotal} cr </td>
				</tr>
			</c:otherwise>
			</c:choose>
		</table>	
		<br/><br/>
		<table class="formulario" width="80%">
			<caption>Internacionalização de Componente Curricular</caption>
			<thead>
				<tr>
					<th style="text-align:left; padding-left: 5px; font-weight: bold">Campo</th>
					<th style="text-align:left; padding-left: 5px;">Tradução</th>
				</tr>	
			</thead>
			<tbody>		
				<c:forEach items="#{componenteTraducaoMBean.listaTraducaoElemento}" var="item" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<th style="text-align:left; padding-left: 5px; font-weight: bold" valign="center" width="15%">${item.itemTraducao.nome}:</th>
						<td style="text-align:left;">
							<table>
							<c:forEach items="#{item.elementos}" var="elemento" varStatus="loop">
								<tr>
									<td><h:outputText value="#{elemento.descricaoIdioma}:"/></td>
								</tr>
								<tr>
									<td><h:inputTextarea value="#{elemento.valor}" onkeyup="CAPS(this)" disabled="#{elemento.idiomaLocal or elemento.inputDisabled}" cols="#{item.itemTraducao.tipoAreaTexto ? '80' : '60'}" rows="#{item.itemTraducao.tipoAreaTexto ? '5' : '1'}"/></td>
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
					<h:commandButton value="#{componenteTraducaoMBean.confirmButton}" action="#{componenteTraducaoMBean.cadastrar}" /> 
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{componenteTraducaoMBean.cancelar}" />
				</td>
			</tr>
		</table>	
		
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
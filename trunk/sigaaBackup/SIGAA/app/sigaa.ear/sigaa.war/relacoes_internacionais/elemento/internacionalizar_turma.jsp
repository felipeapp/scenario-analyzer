<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="turmaTraducaoMBean" />
	<h2><ufrn:subSistema /> > Tradução de Turma</h2> 
	
	<c:set var="turma" value="${turmaTraducaoMBean.obj}"/>
	<div style="width: 80.8%; margin-left: 9.5%" align="center">
		<%@include file="/ensino/turma/info_turma.jsp"%>
	</div>
	<h:form>
		<table class="formulario" width="80%">
			<caption>Internacionalização de Turma</caption>
			<thead>
				<tr>
					<th style="text-align:left; padding-left: 5px; font-weight: bold">Campo</th>
					<th style="text-align:left; padding-left: 5px;">Tradução</th>
				</tr>	
			</thead>
			<tbody>		
				<c:forEach items="#{turmaTraducaoMBean.listaTraducaoElemento}" var="item" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<th style="text-align:left; padding-left: 5px; font-weight: bold" valign="center" width="15%">${item.itemTraducao.nome}:</th>
						<td style="text-align:left;">
							<table>
							<c:forEach items="#{item.elementos}" var="elemento" varStatus="loop">
								<tr>
									<td><h:outputText value="#{elemento.descricaoIdioma}:"/></td>
								</tr>
								<tr>
									<td><h:inputTextarea value="#{elemento.valor}" disabled="#{elemento.idiomaLocal or elemento.inputDisabled}" cols="#{item.itemTraducao.tipoAreaTexto ? '80' : '60'}" rows="#{item.itemTraducao.tipoAreaTexto ? '5' : '1'}"/></td>
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
					<h:commandButton value="#{turmaTraducaoMBean.confirmButton}" action="#{turmaTraducaoMBean.cadastrar}" /> 
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{turmaTraducaoMBean.cancelar}" />
				</td>
			</tr>
		</table>	
		
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<a4j:keepAlive beanName="historicoTraducaoMBean" />

<h:form id="form_matriz">

	<table class="formulario" width="80%">
		<caption>Internacionalização de Dados da Matriz Curricular do Histórico</caption>
		<thead>
			<tr>
				<th style="text-align:left; padding-left: 5px; font-weight: bold">Campo</th>
				<th style="text-align:left; padding-left: 5px;">Tradução</th>
			</tr>	
		</thead>
		<tbody>		
			<tr><td colspan="2">
			<c:forEach items="#{historicoTraducaoMBean.mapaMatriz}" var="map" varStatus="loop">
			<table class="subFormulario" width="100%">
				<caption>${map.key.nome}</caption>
				<c:forEach items="#{map.value}" var="item" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<th style="text-align:left; padding-left: 20px; font-weight: bold" valign="center" width="15%">${item.itemTraducao.nome}:</th>
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
			</table>
			<br/><br/>
			</c:forEach>
			</td></tr>		
		</tbody>
		<tfoot>
		<tr>
			<td colspan=2>
				<h:commandButton value="#{historicoTraducaoMBean.confirmButton}" action="#{historicoTraducaoMBean.cadastrar}" onclick="setAbaHistorico('matriz')"/> 
				<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{historicoTraducaoMBean.cancelar}" />
			</td>
		</tr>
	</table>	
		
</h:form>
		<c:if test="${not empty carregarDadosProducoesMBean.obj.dataValidacao}">
			<tr class="view">
				<c:if test="${carregarDadosProducoesMBean.obj.validado}">
					<td colspan="4" style="color: green;">Produção Válida</td>
				</c:if>
				<c:if test="${not carregarDadosProducoesMBean.obj.validado}">
					<td colspan="4"  style="color: red;">Produção Inválida</td>
				</c:if>
			</tr>
		</c:if>
		<tr class="view">
			<th>Informações Complementares:</th>
			<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.informacao}" /></td>
		</tr>
		<tr class="view">
			<th>Instituição:</th>
			<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.instituicao.nome}" /></td>
		</tr>
		<tr class="view">
			<th>Prêmio:</th>
			<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.premio}" /></td>
		</tr>
		<tr class="view">
			<th>Abrangência:</th>
			<td><h:outputText value="#{carregarDadosProducoesMBean.obj.tipoRegiao.descricao}" /></td>
	    </tr>
		<tr class="view">
			<th>Ano de Referência:</th>
			<td><h:outputText value="#{carregarDadosProducoesMBean.obj.anoReferencia}" /></td>
		</tr>
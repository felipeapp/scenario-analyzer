		<c:if test="${not empty carregarDadosProducoesMBean.obj.dataValidacao}">
			<tr class="view">
				<c:if test="${carregarDadosProducoesMBean.obj.validado}">
					<td colspan="4" style="color: green;">Produ��o V�lida</td>
				</c:if>
				<c:if test="${not carregarDadosProducoesMBean.obj.validado}">
					<td colspan="4"  style="color: red;">Produ��o Inv�lida</td>
				</c:if>
			</tr>
		</c:if>
		<tr class="view">
			<th>Informa��es Complementares:</th>
			<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.informacao}" /></td>
		</tr>
		<tr class="view">
			<th>Institui��o:</th>
			<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.instituicao.nome}" /></td>
		</tr>
		<tr class="view">
			<th>Pr�mio:</th>
			<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.premio}" /></td>
		</tr>
		<tr class="view">
			<th>Abrang�ncia:</th>
			<td><h:outputText value="#{carregarDadosProducoesMBean.obj.tipoRegiao.descricao}" /></td>
	    </tr>
		<tr class="view">
			<th>Ano de Refer�ncia:</th>
			<td><h:outputText value="#{carregarDadosProducoesMBean.obj.anoReferencia}" /></td>
		</tr>
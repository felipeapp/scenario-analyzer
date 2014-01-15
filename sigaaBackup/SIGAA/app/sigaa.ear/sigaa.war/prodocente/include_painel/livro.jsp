			<c:if test="${not empty carregarDadosProducoesMBean.obj.dataValidacao}">
				<tr class="view">
					<c:if test="${carregarDadosProducoesMBean.obj.validado}">
						<td colspan="4" class="prodValida">Produção Válida</td>
					</c:if>
					<c:if test="${not carregarDadosProducoesMBean.obj.validado}">
						<td colspan="4" class="prodInvalida">Produção Inválida</td>
					</c:if>
				</tr>
			</c:if>
			<tr class="view">
				<th>Título:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.titulo}</td>
			</tr>
			<tr class="view">
				<th>Autores:</th>
				<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.autores}" /></td>
			</tr>
			<tr class="view">
				<th>Organizadores/Editores:</th>
				<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.organizadores}" /></td>
			</tr>
			<tr class="view">
				<th>Área:</th>
				<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.area.nome}" /></td>
			</tr>
			<tr class="view">
				<th>Sub-Área:</th>
				<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.subArea.nome}" /></td>
			</tr>
			<tr class="view">
				<th>Observações:</th>
				<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.informacao}" /></td>
			</tr>
			<tr class="view">
				<td colspan="4"><hr /></td>
			</tr>
			<tr class="view">
				<th>Publicação:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.tipoRegiao.descricao}" /></td>
				<th>Tipo de Participação:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.tipoParticipacao.descricao}" /></td>
			</tr>
			<tr class="view">
				<th>Ano de Referência:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.anoReferencia}" /></td>
				<th>Local de Publicação:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.localPublicacao}" /></td>
			</tr>

			<tr class="view">
				<th>Nº Páginas:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.quantidadePaginas}" /></td>
				<td></td>
				<td></td>
			</tr>

			<tr class="view">
				<th>Editora:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.editora}" /></td>
				<th>Destaque:</th>
				<td><h:selectBooleanCheckbox value="#{carregarDadosProducoesMBean.obj.destaque}" readonly="true" /></td>
			</tr>
			<tr class="view">
				<th>Título:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.titulo}</td>
			</tr>
			<tr class="view">
				<th>Autores:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.autores}</td>
			</tr>
			<tr class="view">
				<th>Título do Livro:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.tituloLivro}</td>
			</tr>
			<tr class="view">
				<th>Área:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.area.nome}</td>
			</tr>
			<tr class="view">
				<th>Sub-Área:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.subArea.nome}</td>
			</tr>
			<tr class="view">
				<th>Observações:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.informacao}</td>
			</tr>
			<tr>
				<td colspan="4"><hr /></td>
			</tr>
			<tr class="view">
				<th>Ano de Referência:</th>
				<td>${carregarDadosProducoesMBean.obj.anoReferencia}</td>
				<th>Local de Publicação:</th>
				<td>${carregarDadosProducoesMBean.obj.localPublicacao}</td>
			</tr>
			<tr class="view">
				<th>Publicação:</th>
				<td>${carregarDadosProducoesMBean.obj.tipoRegiao.descricao}</td>
				<th>Tipo de Participação:</th>
				<td>${carregarDadosProducoesMBean.obj.tipoParticipacao.descricao}</td>
			</tr>
			<tr class="view">
				<th>Editor:</th>
				<td>${carregarDadosProducoesMBean.obj.editor}</td>
				<th>Capítulos do Livro:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.capitulosLivro}</td>
			</tr>
			<tr class="view">
				<th>Página Inicial:</th>
				<td>${carregarDadosProducoesMBean.obj.paginaInicial}</td>
				<th>Página Final:</th>
				<td>${carregarDadosProducoesMBean.obj.paginaFinal}</td>
			</tr>
			<tr class="view">
				<th>Premiada:</th>
				<td><h:selectBooleanCheckbox value="#{carregarDadosProducoesMBean.obj.premiada}" readonly="true" /></td>
			</tr>
			<tr class="view">
				<th>T�tulo:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.titulo}</td>
			</tr>
			<tr class="view">
				<th>Autores:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.autores}</td>
			</tr>
			<tr class="view">
				<th>T�tulo do Livro:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.tituloLivro}</td>
			</tr>
			<tr class="view">
				<th>�rea:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.area.nome}</td>
			</tr>
			<tr class="view">
				<th>Sub-�rea:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.subArea.nome}</td>
			</tr>
			<tr class="view">
				<th>Observa��es:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.informacao}</td>
			</tr>
			<tr>
				<td colspan="4"><hr /></td>
			</tr>
			<tr class="view">
				<th>Ano de Refer�ncia:</th>
				<td>${carregarDadosProducoesMBean.obj.anoReferencia}</td>
				<th>Local de Publica��o:</th>
				<td>${carregarDadosProducoesMBean.obj.localPublicacao}</td>
			</tr>
			<tr class="view">
				<th>Publica��o:</th>
				<td>${carregarDadosProducoesMBean.obj.tipoRegiao.descricao}</td>
				<th>Tipo de Participa��o:</th>
				<td>${carregarDadosProducoesMBean.obj.tipoParticipacao.descricao}</td>
			</tr>
			<tr class="view">
				<th>Editor:</th>
				<td>${carregarDadosProducoesMBean.obj.editor}</td>
				<th>Cap�tulos do Livro:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.capitulosLivro}</td>
			</tr>
			<tr class="view">
				<th>P�gina Inicial:</th>
				<td>${carregarDadosProducoesMBean.obj.paginaInicial}</td>
				<th>P�gina Final:</th>
				<td>${carregarDadosProducoesMBean.obj.paginaFinal}</td>
			</tr>
			<tr class="view">
				<th>Premiada:</th>
				<td><h:selectBooleanCheckbox value="#{carregarDadosProducoesMBean.obj.premiada}" readonly="true" /></td>
			</tr>
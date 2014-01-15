			<tr class="view">
				<th>Título:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.titulo}</td>
				<th >Ano de Referência:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.anoReferencia}" /></td>
			</tr>

			<tr class="view">
				<th>Examinado(s):</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.autor}</td>
			</tr>

			<tr class="view">
				<th>Natureza do Exame:</th>
				<td>${carregarDadosProducoesMBean.obj.naturezaExame.descricao}</td>
				
				<th>País:</th>
				<td>${carregarDadosProducoesMBean.obj.pais.nome}</td>
			</tr>

			<tr class="view">
				<th>Área:</th>
				<td>${carregarDadosProducoesMBean.obj.area.nome}</td>
				<th>Instituição:</th>
				<td>${carregarDadosProducoesMBean.obj.instituicao.nome}</td>
			</tr>

			<tr class="view">
				<th>Sub-Área:</th>
				<td>${carregarDadosProducoesMBean.obj.subArea.nome}</td>
				<th>Município:</th>
				<td>${carregarDadosProducoesMBean.obj.municipio.nome}</td>
			</tr>

			<tr class="view">
				<th>Departamento:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.departamento.nome}</td>
			</tr>
		
			<tr class="view">
				<th>Informações complementares:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.informacao}</td>
			</tr>
			<tr class="view">
				<th>T�tulo:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.titulo}</td>
				<th >Ano de Refer�ncia:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.anoReferencia}" /></td>
			</tr>

			<tr class="view">
				<th>Examinado(s):</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.autor}</td>
			</tr>

			<tr class="view">
				<th>Natureza do Exame:</th>
				<td>${carregarDadosProducoesMBean.obj.naturezaExame.descricao}</td>
				
				<th>Pa�s:</th>
				<td>${carregarDadosProducoesMBean.obj.pais.nome}</td>
			</tr>

			<tr class="view">
				<th>�rea:</th>
				<td>${carregarDadosProducoesMBean.obj.area.nome}</td>
				<th>Institui��o:</th>
				<td>${carregarDadosProducoesMBean.obj.instituicao.nome}</td>
			</tr>

			<tr class="view">
				<th>Sub-�rea:</th>
				<td>${carregarDadosProducoesMBean.obj.subArea.nome}</td>
				<th>Munic�pio:</th>
				<td>${carregarDadosProducoesMBean.obj.municipio.nome}</td>
			</tr>

			<tr class="view">
				<th>Departamento:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.departamento.nome}</td>
			</tr>
		
			<tr class="view">
				<th>Informa��es complementares:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.informacao}</td>
			</tr>
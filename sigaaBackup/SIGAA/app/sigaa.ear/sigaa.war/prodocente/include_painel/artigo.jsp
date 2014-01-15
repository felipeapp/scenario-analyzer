			<tr class="view">
				<th>Título:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.titulo}</td>
			</tr>
			<tr class="view">
				<th>Autores:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.autores}</td>
			</tr>
			<tr class="view">
				<th>Editora:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.editora}</td>
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
       			<td colspan="4"><hr></td>
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
				<th>Título do Periódico:</th>
				<td>${carregarDadosProducoesMBean.obj.tituloPeriodico}</td>
				<th>Volume:</th>
				<td>${carregarDadosProducoesMBean.obj.volume}</td>
			</tr>
			<tr class="view">
				<th>Página Inicial:</th>
				<td>${carregarDadosProducoesMBean.obj.paginaInicial}</td>
				<th>Página Final:</th>
				<td>${carregarDadosProducoesMBean.obj.paginaFinal}</td>
			</tr>
			<tr class="view">
				<th>Número:</th>
				<td>${carregarDadosProducoesMBean.obj.numero}</td>
				<th>Destaque:</th>
				<td><h:selectBooleanCheckbox value="#{carregarDadosProducoesMBean.obj.destaque}" disabled="true" /></td>
			</tr>
			<tr class="view">
				<th>ISSN/ISBN:</th>
				<td>${carregarDadosProducoesMBean.obj.issn}</td>
				<th>Tipo do Periódico:</th>
				<td>${carregarDadosProducoesMBean.obj.tipoPeriodico.descricao}</td>
			</tr>			
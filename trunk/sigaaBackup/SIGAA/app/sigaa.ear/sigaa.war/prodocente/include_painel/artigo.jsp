			<tr class="view">
				<th>T�tulo:</th>
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
       			<td colspan="4"><hr></td>
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
				<th>T�tulo do Peri�dico:</th>
				<td>${carregarDadosProducoesMBean.obj.tituloPeriodico}</td>
				<th>Volume:</th>
				<td>${carregarDadosProducoesMBean.obj.volume}</td>
			</tr>
			<tr class="view">
				<th>P�gina Inicial:</th>
				<td>${carregarDadosProducoesMBean.obj.paginaInicial}</td>
				<th>P�gina Final:</th>
				<td>${carregarDadosProducoesMBean.obj.paginaFinal}</td>
			</tr>
			<tr class="view">
				<th>N�mero:</th>
				<td>${carregarDadosProducoesMBean.obj.numero}</td>
				<th>Destaque:</th>
				<td><h:selectBooleanCheckbox value="#{carregarDadosProducoesMBean.obj.destaque}" disabled="true" /></td>
			</tr>
			<tr class="view">
				<th>ISSN/ISBN:</th>
				<td>${carregarDadosProducoesMBean.obj.issn}</td>
				<th>Tipo do Peri�dico:</th>
				<td>${carregarDadosProducoesMBean.obj.tipoPeriodico.descricao}</td>
			</tr>			
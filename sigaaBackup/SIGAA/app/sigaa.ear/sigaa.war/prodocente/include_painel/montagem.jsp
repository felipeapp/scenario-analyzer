			<c:if test="${not empty carregarDadosProducoesMBean.obj.dataValidacao}">
				<tr class="validada">
					<c:if test="${carregarDadosProducoesMBean.obj.validado}">
						<td colspan="4" style="color: green;">Produção Válida</td>
					</c:if>
					<c:if test="${not carregarDadosProducoesMBean.obj.validado}">
						<td colspan="4" style="color: red;">Produção Inválida</td>
					</c:if>
				</tr>
			</c:if>
			<tr class="view">
				<td width="20%"><b>Título:</b></td>
				<td>${carregarDadosProducoesMBean.obj.titulo}</td>
			</tr>
			<tr class="view">
				<td><b>Tipo de Participação:</b></td>
				<td>${carregarDadosProducoesMBean.obj.tipoParticipacao.descricao}</td>
			</tr>
			<tr class="view">
				<td><b>Area:</b></td>
				<td>${carregarDadosProducoesMBean.obj.area.nome}</td>
			</tr>
			<tr class="view">
				<td><b>Sub-Area:</b></td>
				<td>${carregarDadosProducoesMBean.obj.subArea.nome}</td>
			</tr>
			<tr class="view">
				<td><b>Tipo Artístico:</b></td>

				<td>${carregarDadosProducoesMBean.obj.tipoArtistico.descricao}</td>
			</tr>
			<tr class="view">
				<td><b>Sub-Tipo Artístico:</b></td>
				<td>${carregarDadosProducoesMBean.obj.subTipoArtistico.descricao}</td>
			</tr>
			<tr class="view">
				<td><b>Informação:</b></td>
				<td>${carregarDadosProducoesMBean.obj.informacao}</td>
			</tr>
			<tr class="view">
				<td><b>Data Início:</b></td>
				<td>${carregarDadosProducoesMBean.obj.periodoInicio}</td>
			</tr>
			<tr class="view">
				<td><b>Data Fim:</b></td>
				<td>${carregarDadosProducoesMBean.obj.periodoFim}</td>
			</tr>
			<tr class="view">
				<td><b>Local:</b></td>

				<td>${carregarDadosProducoesMBean.obj.local}</td>
			</tr>
			<tr class="view">
				<td><b><font color="red">Autor:</font></b></td>
				<td>${carregarDadosProducoesMBean.obj.autores}</td>
			</tr>
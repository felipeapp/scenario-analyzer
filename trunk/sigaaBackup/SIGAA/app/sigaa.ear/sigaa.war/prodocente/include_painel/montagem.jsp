			<c:if test="${not empty carregarDadosProducoesMBean.obj.dataValidacao}">
				<tr class="validada">
					<c:if test="${carregarDadosProducoesMBean.obj.validado}">
						<td colspan="4" style="color: green;">Produ��o V�lida</td>
					</c:if>
					<c:if test="${not carregarDadosProducoesMBean.obj.validado}">
						<td colspan="4" style="color: red;">Produ��o Inv�lida</td>
					</c:if>
				</tr>
			</c:if>
			<tr class="view">
				<td width="20%"><b>T�tulo:</b></td>
				<td>${carregarDadosProducoesMBean.obj.titulo}</td>
			</tr>
			<tr class="view">
				<td><b>Tipo de Participa��o:</b></td>
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
				<td><b>Tipo Art�stico:</b></td>

				<td>${carregarDadosProducoesMBean.obj.tipoArtistico.descricao}</td>
			</tr>
			<tr class="view">
				<td><b>Sub-Tipo Art�stico:</b></td>
				<td>${carregarDadosProducoesMBean.obj.subTipoArtistico.descricao}</td>
			</tr>
			<tr class="view">
				<td><b>Informa��o:</b></td>
				<td>${carregarDadosProducoesMBean.obj.informacao}</td>
			</tr>
			<tr class="view">
				<td><b>Data In�cio:</b></td>
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
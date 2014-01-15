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
				<th>T�tulo:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.titulo}</td>
			</tr>
			<tr class="view">
				<th>Tipo de Participa��o:</th>
				<td>${carregarDadosProducoesMBean.obj.tipoParticipacao.descricao}</td>
				<th>�mbito:</th>
				<td>${carregarDadosProducoesMBean.obj.tipoRegiao.descricao}</td>
			</tr>

			<tr class="view">
				<th>Area:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.area.nome}</td>
			</tr>
			<tr class="view">
				<th>Sub-Area:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.subArea.nome}</td>
			</tr>
			<tr class="view">
				<th>Informa��o:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.informacao}</td>
			</tr>
			<tr class="view">
				<th>Autores:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.autores}</td>
			</tr>
			<tr class="view">
				<th>Ano de Refer�ncia:</th>
				<td>${carregarDadosProducoesMBean.obj.anoReferencia}</td>
			</tr>
			<tr class="view">
				<th>Tipo de Produ��o Tecnol�gica:</th>
				<td>${carregarDadosProducoesMBean.obj.tipoProducaoTecnologica.descricao}</td>
				<th>Premiada:</th>
				<td><h:selectBooleanCheckbox value="#{carregarDadosProducoesMBean.obj.premiada}" readonly="true" /></td>
			</tr>
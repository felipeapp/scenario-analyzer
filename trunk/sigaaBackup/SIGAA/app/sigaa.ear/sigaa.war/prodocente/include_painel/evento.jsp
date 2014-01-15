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
				<th>Autores:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.autores}</td>
			</tr>
			<tr class="view">
				<th>Organizadores:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.organizadores}</td>
			</tr>
			<tr class="view">
				<th>Nome do Evento:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.nomeEvento}</td>
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
				<th>Informa��o:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.informacao}</td>
			</tr>
			<tr class="view">
				<td colspan="4"><hr /></td>
			</tr>
			<tr class="view">
				<th>Tipo de Evento:</th>
				<td>${carregarDadosProducoesMBean.obj.tipoEvento.descricao}</td>
				<th>�mbito:</th>
				<td>${carregarDadosProducoesMBean.obj.tipoRegiao.descricao}</td>
			</tr>
			<tr class="view">
				<th>Tipo de Participa��o:</th>
				<td>${carregarDadosProducoesMBean.obj.tipoParticipacao.descricao}</td>
				<th>Natureza:</th>
				<td><h:selectOneMenu value="#{carregarDadosProducoesMBean.obj.natureza}" disabled="true"
							disabledClass="#{carregarDadosProducoesMBean.disableClass}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{publicacaoEvento.allNatureza}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr class="view">
				<th>Ano de Refer�ncia:</th>
				<td>${carregarDadosProducoesMBean.obj.anoReferencia}</td>
				<th>Local de Participa��o:</th>
				<td>${carregarDadosProducoesMBean.obj.localPublicacao}</td>
			</tr>

			<tr class="view">
				<th>P�gina Inicial:</th>
				<td>${carregarDadosProducoesMBean.obj.paginaInicial}</td>
				<th>P�gina Final:</th>
				<td>${carregarDadosProducoesMBean.obj.paginaFinal}</td>
			</tr>
			<tr class="view">
				<th>Destaque:</th>
				<td><h:selectBooleanCheckbox value="#{carregarDadosProducoesMBean.obj.destaque}" disabled="true" /></td>
				<th>Apresentado:</th>
				<td><h:selectBooleanCheckbox value="#{carregarDadosProducoesMBean.obj.apresentado}" disabled="true" /></td>
			</tr>

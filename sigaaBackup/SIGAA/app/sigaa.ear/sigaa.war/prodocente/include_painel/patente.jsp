			<c:if test="${not empty carregarDadosProducoesMBean.obj.dataValidacao}">
				<tr>
					<c:if test="${carregarDadosProducoesMBean.obj.validado}">
						<td colspan="4" class="prodValida">Produção Válida</td>
					</c:if>
					<c:if test="${not carregarDadosProducoesMBean.obj.validado}">
						<td colspan="4" class="prodInvalida">Produção Inválida</td>
					</c:if>
				</tr>
			</c:if>
			<tr class="view">
				<th>Título:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.titulo}</td>
			</tr>
			<tr class="view">
				<th>Tipo de Participação:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.tipoParticipacao.descricao}</td>
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
				<th>Informações Complementares:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.informacao}</td>
			</tr>
			<tr class="view">
				<th>Autores:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.autores}</td>
			</tr>
			<tr class="view">
				<th>Premiada:</th>
				<td><h:selectBooleanCheckbox value="#{carregarDadosProducoesMBean.obj.premiada}" disabled="true" /></td>
				<th>Nº de Registro:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.numeroRegistro}</td>
			</tr>
			<tr class="view">				
				<th>Instituição Patrocinadora:</th>
				<td colspan="3"><c:forEach items="#{carregarDadosProducoesMBean.obj.patrocinadora}" var="item" varStatus="status">
				    	${item.nome} <br />
					</c:forEach>
				</td>
			</tr>
			<tr class="view">
				<th>Nº da Patente:</th>
				<td>${carregarDadosProducoesMBean.obj.numeroPatente}</td>
				<th>Título da Publicação:</th>
				<td>${carregarDadosProducoesMBean.obj.registroTitulo}</td>
			</tr>
			<tr class="view">
				<th>Local de Registro:</th>
				<td>${carregarDadosProducoesMBean.obj.registroLocal}</td>
				<th>Ano de Referência:</th>
				<td>${carregarDadosProducoesMBean.obj.anoReferencia}</td>
			</tr>
			<tr class="view">
				<th>Página de Registro:</th>
				<td>${carregarDadosProducoesMBean.obj.registroPagina}</td>
				<th>Volume do Registro:</th>
				<td>${carregarDadosProducoesMBean.obj.registroVolume}</td>
			</tr>
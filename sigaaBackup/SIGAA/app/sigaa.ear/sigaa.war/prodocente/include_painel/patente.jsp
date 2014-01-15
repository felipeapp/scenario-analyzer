			<c:if test="${not empty carregarDadosProducoesMBean.obj.dataValidacao}">
				<tr>
					<c:if test="${carregarDadosProducoesMBean.obj.validado}">
						<td colspan="4" class="prodValida">Produ��o V�lida</td>
					</c:if>
					<c:if test="${not carregarDadosProducoesMBean.obj.validado}">
						<td colspan="4" class="prodInvalida">Produ��o Inv�lida</td>
					</c:if>
				</tr>
			</c:if>
			<tr class="view">
				<th>T�tulo:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.titulo}</td>
			</tr>
			<tr class="view">
				<th>Tipo de Participa��o:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.tipoParticipacao.descricao}</td>
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
				<th>Informa��es Complementares:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.informacao}</td>
			</tr>
			<tr class="view">
				<th>Autores:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.autores}</td>
			</tr>
			<tr class="view">
				<th>Premiada:</th>
				<td><h:selectBooleanCheckbox value="#{carregarDadosProducoesMBean.obj.premiada}" disabled="true" /></td>
				<th>N� de Registro:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.numeroRegistro}</td>
			</tr>
			<tr class="view">				
				<th>Institui��o Patrocinadora:</th>
				<td colspan="3"><c:forEach items="#{carregarDadosProducoesMBean.obj.patrocinadora}" var="item" varStatus="status">
				    	${item.nome} <br />
					</c:forEach>
				</td>
			</tr>
			<tr class="view">
				<th>N� da Patente:</th>
				<td>${carregarDadosProducoesMBean.obj.numeroPatente}</td>
				<th>T�tulo da Publica��o:</th>
				<td>${carregarDadosProducoesMBean.obj.registroTitulo}</td>
			</tr>
			<tr class="view">
				<th>Local de Registro:</th>
				<td>${carregarDadosProducoesMBean.obj.registroLocal}</td>
				<th>Ano de Refer�ncia:</th>
				<td>${carregarDadosProducoesMBean.obj.anoReferencia}</td>
			</tr>
			<tr class="view">
				<th>P�gina de Registro:</th>
				<td>${carregarDadosProducoesMBean.obj.registroPagina}</td>
				<th>Volume do Registro:</th>
				<td>${carregarDadosProducoesMBean.obj.registroVolume}</td>
			</tr>
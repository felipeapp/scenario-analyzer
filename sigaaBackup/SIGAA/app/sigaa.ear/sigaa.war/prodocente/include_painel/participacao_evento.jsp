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
			<th>Âmbito:</th>
			<td>${carregarDadosProducoesMBean.obj.ambito.descricao}</td>
			<th></th>
			<td></td>
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
			<th>Veículo:</th>
			<td colspan="3">${carregarDadosProducoesMBean.obj.veiculo}</td>
		</tr>
		<tr class="view">
			<th>Local:</th>
			<td>${carregarDadosProducoesMBean.obj.local}</td>
			<th>Tipo de Participação da Organização:&nbsp;&nbsp;&nbsp;</th>
			<td>&nbsp;&nbsp;&nbsp;${carregarDadosProducoesMBean.obj.tipoParticipacaoOrganizacao.descricao}</td>
		</tr>
		<tr class="view">
			<th>Ano de Referência:</th>
			<td>
				${carregarDadosProducoesMBean.obj.anoReferencia}
			</td>
		</tr>
		<tr class="view">
			<th>Período Início:</th>
			<td><fmt:formatDate value="${carregarDadosProducoesMBean.obj.periodoInicio}" pattern="MM/yyyy" /></td>
			<th>Período Fim:</th>
			<td><fmt:formatDate value="${carregarDadosProducoesMBean.obj.periodoFim}" pattern="MM/yyyy" /></td>
		</tr>

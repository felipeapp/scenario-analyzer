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
				<th>Departamento:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.departamento.nome}</td>
			</tr>
			<tr class="view">
				<th>Instituição:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.instituicao.nome}</td>
			</tr>
			<tr class="view">
				<th>Comissão:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.comissao}</td>
			</tr>
			<tr class="view">
				<th>Informação:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.informacao}</td>
			</tr>
			<tr class="view">
				<th>Ano de Referência:</th>
				<td>${carregarDadosProducoesMBean.obj.anoReferencia}</td>
			</tr>
			<tr class="view">
				<th>Período Início:</th>
				<td><fmt:formatDate value="${carregarDadosProducoesMBean.obj.periodoInicio}" pattern="MM/yyyy" /></td>
				<th>Período Fim:</th>
				<td><fmt:formatDate value="${carregarDadosProducoesMBean.obj.periodoFim}" pattern="MM/yyyy" /></td>
			</tr>
	
			<tr class="view">
				<th>Tipo de comissão:</th>
				<td>${carregarDadosProducoesMBean.obj.tipoComissaoColegiado.descricao}</td>
				<th>Número de Reuniões:</th>
				<td>${carregarDadosProducoesMBean.obj.numeroReunioes}</td>
			</tr>
			<tr class="view">
				<th>Membro Nato:</th>
				<td><t:selectBooleanCheckbox value="#{carregarDadosProducoesMBean.obj.nato}" readonly="true" /></td>
			</tr>

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
				<th>Título:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.titulo}</td>
			</tr>
			<tr class="view">
				<th>Âmbito:</th>
				<td>${carregarDadosProducoesMBean.obj.ambito.descricao}</td>
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
				<th>Informação:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.informacao}</td>
			</tr>
			<tr class="view">
				<th>Ies:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.ies.nome}</td>
			</tr>
			<tr class="view">
				<th>Entidade:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.entidade}</td>
			</tr>
			<tr class="view">
				<th>Valor:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.valor}</td>
			</tr>
			<tr class="view">
				<th>Departamento:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.departamento.nome}</td>
			</tr>
			<tr class="view">
				<th>Local:</th>
				<td>${carregarDadosProducoesMBean.obj.local}</td>
				<th>Financiamento Externo:</th>
				<td><t:selectBooleanCheckbox value="#{carregarDadosProducoesMBean.obj.financiamentoExterno}" disabled="true" /></td>
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

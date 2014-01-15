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
				<th>Departamento:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.departamento.nome}</td>
			</tr>
			<tr class="view">
				<th>Institui��o:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.instituicao.nome}</td>
			</tr>
			<tr class="view">
				<th>Comiss�o:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.comissao}</td>
			</tr>
			<tr class="view">
				<th>Informa��o:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.informacao}</td>
			</tr>
			<tr class="view">
				<th>Ano de Refer�ncia:</th>
				<td>${carregarDadosProducoesMBean.obj.anoReferencia}</td>
			</tr>
			<tr class="view">
				<th>Per�odo In�cio:</th>
				<td><fmt:formatDate value="${carregarDadosProducoesMBean.obj.periodoInicio}" pattern="MM/yyyy" /></td>
				<th>Per�odo Fim:</th>
				<td><fmt:formatDate value="${carregarDadosProducoesMBean.obj.periodoFim}" pattern="MM/yyyy" /></td>
			</tr>
	
			<tr class="view">
				<th>Tipo de comiss�o:</th>
				<td>${carregarDadosProducoesMBean.obj.tipoComissaoColegiado.descricao}</td>
				<th>N�mero de Reuni�es:</th>
				<td>${carregarDadosProducoesMBean.obj.numeroReunioes}</td>
			</tr>
			<tr class="view">
				<th>Membro Nato:</th>
				<td><t:selectBooleanCheckbox value="#{carregarDadosProducoesMBean.obj.nato}" readonly="true" /></td>
			</tr>

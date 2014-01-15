			<c:if test="${not empty carregarDadosProducoesMBean.obj.dataValidacao}">
				<tr>
					<c:if test="${carregarDadosProducoesMBean.obj.validado}">
						<td colspan="4" class="prodValida">Produ��o V�lida</c:if>
					<c:if test="${not carregarDadosProducoesMBean.obj.validado}">
						<td colspan="4" class="prodInvalida">Produ��o Inv�lida</td>
					</c:if>
				</tr>
			</c:if>

			<tr class="view">
				<th>Docente:</th>
				<td>${carregarDadosProducoesMBean.obj.servidor.pessoa.nome}</td>
			</tr>

			<tr class="view">
				<th>�rea:</th>
				<td>${carregarDadosProducoesMBean.obj.area.nome}</td>
			</tr>
			<tr class="view">
				<th>Sub-�rea:</th>
				<td>${carregarDadosProducoesMBean.obj.subArea.nome}</td>
			</tr>
			<tr class="view">
				<th>Institui��o Fomento</th>
				<td>${carregarDadosProducoesMBean.obj.instituicaoFomento.nome}</td>
			</tr>
			<tr class="view">
				<th>Tipo de Bolsa:</th>
				<td>${carregarDadosProducoesMBean.obj.tipoBolsa.descricao}</td>
			</tr>
			<tr class="view">
				<th>Ano de Refer�ncia:</th>
				<td>${carregarDadosProducoesMBean.obj.anoReferencia}</td>
			</tr>
			<tr class="view">
				<th>Per�odo In�cio:</th>
				<td><fmt:formatDate  value="${carregarDadosProducoesMBean.obj.periodoInicio}" pattern="MM/yyyy" /></td>
			</tr>
			<tr class="view">
				<th> Per�odo Fim:</th>
				<td><fmt:formatDate  value="${carregarDadosProducoesMBean.obj.periodoFim }" pattern="MM/yyyy" /></td>
			</tr>
			<tr class="view">
				<th>Informa��es Complementares:</th>
				<td>${carregarDadosProducoesMBean.obj.informacao}</td>
			</tr>
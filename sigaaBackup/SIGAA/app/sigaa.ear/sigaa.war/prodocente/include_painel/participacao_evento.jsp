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
			<th>�mbito:</th>
			<td>${carregarDadosProducoesMBean.obj.ambito.descricao}</td>
			<th></th>
			<td></td>
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
			<th>Ve�culo:</th>
			<td colspan="3">${carregarDadosProducoesMBean.obj.veiculo}</td>
		</tr>
		<tr class="view">
			<th>Local:</th>
			<td>${carregarDadosProducoesMBean.obj.local}</td>
			<th>Tipo de Participa��o da Organiza��o:&nbsp;&nbsp;&nbsp;</th>
			<td>&nbsp;&nbsp;&nbsp;${carregarDadosProducoesMBean.obj.tipoParticipacaoOrganizacao.descricao}</td>
		</tr>
		<tr class="view">
			<th>Ano de Refer�ncia:</th>
			<td>
				${carregarDadosProducoesMBean.obj.anoReferencia}
			</td>
		</tr>
		<tr class="view">
			<th>Per�odo In�cio:</th>
			<td><fmt:formatDate value="${carregarDadosProducoesMBean.obj.periodoInicio}" pattern="MM/yyyy" /></td>
			<th>Per�odo Fim:</th>
			<td><fmt:formatDate value="${carregarDadosProducoesMBean.obj.periodoFim}" pattern="MM/yyyy" /></td>
		</tr>

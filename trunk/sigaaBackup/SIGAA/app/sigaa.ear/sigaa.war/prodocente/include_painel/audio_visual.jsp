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
				<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.titulo}" /></td>
			</tr>
			<tr class="view">
				<th>Informação:</th>
				<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.informacao}" /></td>
			</tr>
			<tr class="view">
				<th>Autores:</th>
				<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.autores}" /></td>
			</tr>

			<tr class="view">
				<th>Local:</th>
				<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.local}" /></td>

			</tr>
			<tr class="view">
				<th>Veículo:</th>
				<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.veiculo}" /></td>
			</tr>
			<tr class="view"><td colspan="4"><hr/></td></tr>

			<tr class="view">
				<th>Tipo Artístico:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.tipoArtistico.descricao}" /></td>
				<th>Sub-Tipo Artístico:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.subTipoArtistico.descricao}" /></td>
			</tr>
				<tr class="view">
				<th>Tipo de Participação:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.tipoParticipacao.descricao}" /></td>
				<th>Âmbito:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.tipoRegiao.descricao}" /></td>
			</tr>
			<tr class="view">

				<th>Ano de Referência:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.anoReferencia}" /></td>
				<th>Duração da Divulgação:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.duracaoDivulgacao}" /></td>
			</tr>
			<tr class="view">
				<th>Divulgado:</th>
				<td><h:selectBooleanCheckbox value="#{carregarDadosProducoesMBean.obj.divulgado}" readonly="true" /></td>
				<th>Premiado:</th>
				<td><h:selectBooleanCheckbox value="#{carregarDadosProducoesMBean.obj.premiada}" readonly="true" /></td>
			</tr>

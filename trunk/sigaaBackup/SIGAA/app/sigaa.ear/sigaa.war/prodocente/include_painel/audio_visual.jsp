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
				<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.titulo}" /></td>
			</tr>
			<tr class="view">
				<th>Informa��o:</th>
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
				<th>Ve�culo:</th>
				<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.veiculo}" /></td>
			</tr>
			<tr class="view"><td colspan="4"><hr/></td></tr>

			<tr class="view">
				<th>Tipo Art�stico:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.tipoArtistico.descricao}" /></td>
				<th>Sub-Tipo Art�stico:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.subTipoArtistico.descricao}" /></td>
			</tr>
				<tr class="view">
				<th>Tipo de Participa��o:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.tipoParticipacao.descricao}" /></td>
				<th>�mbito:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.tipoRegiao.descricao}" /></td>
			</tr>
			<tr class="view">

				<th>Ano de Refer�ncia:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.anoReferencia}" /></td>
				<th>Dura��o da Divulga��o:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.duracaoDivulgacao}" /></td>
			</tr>
			<tr class="view">
				<th>Divulgado:</th>
				<td><h:selectBooleanCheckbox value="#{carregarDadosProducoesMBean.obj.divulgado}" readonly="true" /></td>
				<th>Premiado:</th>
				<td><h:selectBooleanCheckbox value="#{carregarDadosProducoesMBean.obj.premiada}" readonly="true" /></td>
			</tr>

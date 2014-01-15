			<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
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
				<td>${carregarDadosProducoesMBean.obj.titulo}</td>
			</tr>
			<tr class="view">
				<th>Autores:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.autores}</td>
			</tr>
			<tr class="view">
				<th>Tipo de Participação:</th>
				<td>${carregarDadosProducoesMBean.obj.tipoParticipacao.descricao}</td>
			</tr>
			<tr class="view">
				<th>Informações Complementares:</th>
				<td>${carregarDadosProducoesMBean.obj.informacao}</td>
			</tr>
			<tr class="view">
			 <th>Premiada:</th>
			    <td>
				<h:selectBooleanCheckbox value="#{carregarDadosProducoesMBean.obj.premiada}"
					readonly="true" />
				</td>
			</tr>
			<tr class="view">
				<th>Âmbito:</th>
				<td>${carregarDadosProducoesMBean.obj.tipoRegiao.descricao}</td>
			</tr>
			<tr class="view">
				<th>Area:</th>
				<td>${carregarDadosProducoesMBean.obj.area.nome}</td>
			</tr>
			<tr class="view">
				<th>Sub-Area:</th>
				<td>${carregarDadosProducoesMBean.obj.subArea.nome}</td>
			</tr>
			<tr class="view">
				<th>Tipo Artístico:</th>
				<td>${carregarDadosProducoesMBean.obj.tipoArtistico.descricao}</td>
			</tr>
			<tr class="view">
				<th>Ano Referência:</th>
				<td>${carregarDadosProducoesMBean.obj.anoReferencia}</td>
			</tr>
       		<tr class="view">
       			<th>Data Início:</th>
       			<td colspan="3">
       				<ufrn:format type="data" valor="${carregarDadosProducoesMBean.obj.periodoInicio}" />
       			</td>
       			<th>Data Fim:</th>
       			<td colspan="3">
	       			<ufrn:format type="data" valor="${carregarDadosProducoesMBean.obj.periodoFim}" />
			    </td>
       		</tr>
			<tr class="view">
       			<th>Local:</th>
       			<td colspan="3">${carregarDadosProducoesMBean.obj.local}</td>
       		</tr>       		
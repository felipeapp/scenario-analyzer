			<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
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
				<td>${carregarDadosProducoesMBean.obj.titulo}</td>
			</tr>
			<tr class="view">
				<th>Autores:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.autores}</td>
			</tr>
			<tr class="view">
				<th>Tipo de Participa��o:</th>
				<td>${carregarDadosProducoesMBean.obj.tipoParticipacao.descricao}</td>
			</tr>
			<tr class="view">
				<th>Informa��es Complementares:</th>
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
				<th>�mbito:</th>
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
				<th>Tipo Art�stico:</th>
				<td>${carregarDadosProducoesMBean.obj.tipoArtistico.descricao}</td>
			</tr>
			<tr class="view">
				<th>Ano Refer�ncia:</th>
				<td>${carregarDadosProducoesMBean.obj.anoReferencia}</td>
			</tr>
       		<tr class="view">
       			<th>Data In�cio:</th>
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
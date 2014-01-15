		<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
			<tr class="view">
				<th>Título:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.titulo}</td>
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
				<td><h:selectBooleanCheckbox value="#{carregarDadosProducoesMBean.obj.premiada}"
					readonly="true" /></td>
			</tr>
			<tr class="view">
				<th>Âmbito:</th>
				<td>${carregarDadosProducoesMBean.obj.tipoRegiao.descricao}</td>
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
       			<th>Tipo Artístico:</th>
       			<td colspan="3">${carregarDadosProducoesMBean.obj.tipoArtistico.descricao}</td>
       		</tr>
       		<tr class="view">
       			<th>Ano Referência:</th>
       			<td colspan="3">${carregarDadosProducoesMBean.obj.anoReferencia}</td>
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
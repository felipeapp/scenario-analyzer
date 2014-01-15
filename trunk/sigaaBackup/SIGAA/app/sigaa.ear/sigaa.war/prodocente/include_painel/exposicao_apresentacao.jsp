		<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
			<tr class="view">
				<th>T�tulo:</th>
				<td colspan="3">${carregarDadosProducoesMBean.obj.titulo}</td>
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
				<td><h:selectBooleanCheckbox value="#{carregarDadosProducoesMBean.obj.premiada}"
					readonly="true" /></td>
			</tr>
			<tr class="view">
				<th>�mbito:</th>
				<td>${carregarDadosProducoesMBean.obj.tipoRegiao.descricao}</td>
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
       			<th>Tipo Art�stico:</th>
       			<td colspan="3">${carregarDadosProducoesMBean.obj.tipoArtistico.descricao}</td>
       		</tr>
       		<tr class="view">
       			<th>Ano Refer�ncia:</th>
       			<td colspan="3">${carregarDadosProducoesMBean.obj.anoReferencia}</td>
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
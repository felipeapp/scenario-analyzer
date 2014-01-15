			<tr class="view">
				<th>Tipo de Texto:</th>
				<td colspan="3">
					<c:if test="${carregarDadosProducoesMBean.obj.textoDiscussao==true}">
				    	Texto de Discussão
				 	</c:if>
				 	<c:if test="${carregarDadosProducoesMBean.obj.textoDiscussao!=true}">
				    	Texto de Didático
				 	</c:if>
				</td>
			</tr>
			<tr class="view">
				<th>Título:</th>
				<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.titulo}" /></td>
			</tr>
			<tr class="view">
				<th>Autores:</th>
				<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.autores}" /></td>
			</tr>
			<tr class="view">
				<th>Tipo de Participação:</th>

				<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.tipoParticipacao.descricao}" /></td>
			</tr>
			<tr class="view">
			<td colspan="4"><hr/> </td>
			</tr>
			<tr class="view">
				<th>Informação:</th>
				<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.informacao}" /></td>
			</tr>
			<tr class="view">
				<th>Área:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.area.nome}" /></td>
				<th>Sub-Área:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.subArea.nome}" /></td>
			</tr>
			<tr class="view">
				<th>Ano de Referência:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.anoReferencia}" /></td>
				<th>Local de Publicação:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.localPublicacao}" /></td>
			</tr>


			<tr class="view">
				<th>Página Inicial:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.paginaInicial}" /></td>
				<th>Página Final:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.paginaFinal}" /></td>
			</tr>

			<tr class="view">
			   	<th><h:outputText value="Premiada:" rendered="#{carregarDadosProducoesMBean.obj.textoDiscussao}" /></th>
				<td><c:if test="${carregarDadosProducoesMBean.obj.premiada}">
			      		<h:outputText value="Sim" rendered="#{carregarDadosProducoesMBean.obj.textoDiscussao}" />
			      	</c:if>
				  	<c:if test="${not carregarDadosProducoesMBean.obj.premiada}">
				  		<h:outputText value="Não" rendered="#{carregarDadosProducoesMBean.obj.textoDiscussao}" />
				  	</c:if>
			   	</td>
			   	<th></th>
			   	<td></td>
		    </tr>
			<tr class="view">
				<th>Destaque:</th>
				<td>
					<c:if test="${carregarDadosProducoesMBean.obj.destaque}">Sim</c:if>
					<c:if test="${not carregarDadosProducoesMBean.obj.destaque}">Não</c:if>
				</td>
				<th>Tipo de Instância:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.tipoInstancia.descricao}" /></td>
			</tr>			
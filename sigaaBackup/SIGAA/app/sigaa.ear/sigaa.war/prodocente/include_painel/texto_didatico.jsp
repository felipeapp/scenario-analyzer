			<tr class="view">
				<th>Tipo de Texto:</th>
				<td colspan="3">
					<c:if test="${carregarDadosProducoesMBean.obj.textoDiscussao==true}">
				    	Texto de Discuss�o
				 	</c:if>
				 	<c:if test="${carregarDadosProducoesMBean.obj.textoDiscussao!=true}">
				    	Texto de Did�tico
				 	</c:if>
				</td>
			</tr>
			<tr class="view">
				<th>T�tulo:</th>
				<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.titulo}" /></td>
			</tr>
			<tr class="view">
				<th>Autores:</th>
				<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.autores}" /></td>
			</tr>
			<tr class="view">
				<th>Tipo de Participa��o:</th>

				<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.tipoParticipacao.descricao}" /></td>
			</tr>
			<tr class="view">
			<td colspan="4"><hr/> </td>
			</tr>
			<tr class="view">
				<th>Informa��o:</th>
				<td colspan="3"><h:outputText value="#{carregarDadosProducoesMBean.obj.informacao}" /></td>
			</tr>
			<tr class="view">
				<th>�rea:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.area.nome}" /></td>
				<th>Sub-�rea:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.subArea.nome}" /></td>
			</tr>
			<tr class="view">
				<th>Ano de Refer�ncia:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.anoReferencia}" /></td>
				<th>Local de Publica��o:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.localPublicacao}" /></td>
			</tr>


			<tr class="view">
				<th>P�gina Inicial:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.paginaInicial}" /></td>
				<th>P�gina Final:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.paginaFinal}" /></td>
			</tr>

			<tr class="view">
			   	<th><h:outputText value="Premiada:" rendered="#{carregarDadosProducoesMBean.obj.textoDiscussao}" /></th>
				<td><c:if test="${carregarDadosProducoesMBean.obj.premiada}">
			      		<h:outputText value="Sim" rendered="#{carregarDadosProducoesMBean.obj.textoDiscussao}" />
			      	</c:if>
				  	<c:if test="${not carregarDadosProducoesMBean.obj.premiada}">
				  		<h:outputText value="N�o" rendered="#{carregarDadosProducoesMBean.obj.textoDiscussao}" />
				  	</c:if>
			   	</td>
			   	<th></th>
			   	<td></td>
		    </tr>
			<tr class="view">
				<th>Destaque:</th>
				<td>
					<c:if test="${carregarDadosProducoesMBean.obj.destaque}">Sim</c:if>
					<c:if test="${not carregarDadosProducoesMBean.obj.destaque}">N�o</c:if>
				</td>
				<th>Tipo de Inst�ncia:</th>
				<td><h:outputText value="#{carregarDadosProducoesMBean.obj.tipoInstancia.descricao}" /></td>
			</tr>			
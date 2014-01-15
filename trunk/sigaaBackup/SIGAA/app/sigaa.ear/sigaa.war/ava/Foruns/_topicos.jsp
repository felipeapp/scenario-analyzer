<fieldset>	
<br/>
	<c:set var="topicos" value="#{ forumBean.topicos }" />

	<c:if test="${ empty topicos }">
		<p class="empty-listing">Nenhum tópico cadastrado para este fórum.</p>
	</c:if>
	 
	<c:if test="${ not empty topicos }">
		<table class="listing">
			<thead>
				<tr>
					<th>${ forumBean.obj.tipo.labelMensagem }</th>
					<th>Autor(a)</th>
					<th>Respostas</th>
					<th>Última Mensagem</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="topico" items="#{ topicos }" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }">	
						<td class="first">
							<h:commandLink action="#{ forumMensagemBean.view }" id="btnView">
								<h:outputText value="#{ topico.titulo }" />
								<f:setPropertyActionListener value="#{ topico }" target="#{ forumMensagemBean.obj }" />
							</h:commandLink>
						</td>

						<td class="width150">
							${ topico.usuario.pessoa.nome }
						</td>

						<td class="width150">
							<h:commandLink action="#{ forumMensagemBean.view }" id="btnRespostas">
								<h:outputText value="#{ topico.totalRespostas }" />
								<f:setPropertyActionListener value="#{ topico }" target="#{ forumMensagemBean.obj }" />
							</h:commandLink>
						</td>
						
						<td class="width150">
							<h:commandLink action="#{ forumMensagemBean.view }" id="btnViewUltima">
								<f:setPropertyActionListener value="#{ topico.ultimaMensagem }" target="#{ forumMensagemBean.obj }" />
								<h:outputText value="#{ topico.ultimaMensagem.usuario.pessoa.nome }" /><br/>
								<fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${ topico.ultimaMensagem.data }" />								
							</h:commandLink>
						</td>
				
						<td class="icon">
							<h:commandLink action="#{ forumMensagemBean.preRemover }" id="btnPreRemover" title="Remover"
								rendered="#{ forumBean.usuarioLogado.id == topico.usuario.id && topico.forum.tipo.permiteRemoverTopico}">										
								<f:setPropertyActionListener value="#{ topico }" target="#{ forumMensagemBean.obj }" />
								<h:graphicImage value="/ava/img/bin.png" />
							</h:commandLink>
						</td>
						
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
			
</fieldset>

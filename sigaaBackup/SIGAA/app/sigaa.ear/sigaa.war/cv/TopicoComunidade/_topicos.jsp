<c:set var="topicos" value="#{topicoComunidadeMBean.topicos}"/>
<c:set var="limiteTopicos" value="999"/>
	
<c:if test="${ empty topicos}">
	<p class="vazio">Nenhum tópico foi cadastrado para esta comunidade</p>
</c:if>

<h:form>

	<c:if test="${ not empty topicos }">
		<table class="listagem">
			<caption>Tópicos cadastrados</caption>
			<thead>
				<tr>
					<th>Tópico</th>
					<th></th>
					<th></th>
					<th></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="#{ topicos }" var="item" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td class="first" style="background:rgb(<h:outputText value='#{item.cor }' />)"><h:outputText value="#{item.descricao}" /></td>
						<td class="icon" style="background:rgb(<h:outputText value='#{item.cor }' />)"><h:commandLink action="#{topicoComunidadeMBean.mostrar}">
							<f:param name="id" value="#{item.id}" />
							<h:graphicImage value="/cv/img/zoom.png" alt="Visualizar"
								title="Visualizar" />
						</h:commandLink></td>
						<td class="icon" style="background:rgb(<h:outputText value='#{item.cor }' />)">
							<c:if test="${ comunidadeVirtualMBean.membro.permitidoModerar || item.usuario.id == comunidadeVirtualMBean.usuarioLogado.id  }">
								<h:commandLink title="Esconder Tópico" action="#{ topicoComunidadeMBean.esconderTopico }" rendered="#{item.visivel}">
									<f:param name="id" value="#{ item.id }" />
									<h:graphicImage value="/img/hide.gif" />
								</h:commandLink>
								
								<h:commandLink title="Exibir Tópico" action="#{ topicoComunidadeMBean.exibirTopico }" rendered="#{!item.visivel}">
									<f:param name="id" value="#{ item.id }" />
									<h:graphicImage value="/img/show.gif" />
								</h:commandLink>
							</c:if>	
						</td>
						<td class="icon" style="background:rgb(<h:outputText value='#{item.cor }' />)">
							<c:if test="${ comunidadeVirtualMBean.membro.permitidoModerar || item.usuario.id == comunidadeVirtualMBean.usuarioLogado.id  }">
								<h:commandLink
									action="#{topicoComunidadeMBean.editar}">
									<f:param name="id" value="#{item.id}" />
									<h:graphicImage value="/ava/img/page_edit.png" alt="Alterar" title="Alterar" />
								</h:commandLink>
							</c:if>
						</td>
						
						<td class="icon" style="background:rgb(<h:outputText value='#{item.cor }' />)">
							<c:if test="${ comunidadeVirtualMBean.membro.permitidoModerar || item.usuario.id == comunidadeVirtualMBean.usuarioLogado.id  }">
								<h:commandLink
									action="#{topicoComunidadeMBean.remover}"
									styleClass="confirm-remover"
									onclick="#{confirmDelete}">
									<f:param name="id" value="#{item.id}" />
									<h:graphicImage value="/ava/img/bin.png" alt="Remover" title="Remover" />
								</h:commandLink>
							</c:if>
						</td>
						
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
	</h:form>
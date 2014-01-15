<%@include file="/cv/include/cabecalho.jsp" %>
<f:view>
	
<%@include file="/cv/include/_menu_comunidade.jsp" %>
<%@include file="/cv/include/_info_comunidade.jsp" %>


<div class="secaoComunidade">
	<rich:panel header="Enquetes da Comunidade" headerClass="headerBloco">

	
	<h:form>
	<div class="infoAltRem">
		<c:if test="${ !comunidadeVirtualMBean.membro.visitante }">
			<h:graphicImage value="/img/adicionar.gif"/> <h:commandLink action="#{ enqueteComunidadeMBean.novo }" value="Cadastrar Enquete" />
        	<h:graphicImage value="/img/alterar.gif"/>: Alterar
        	<h:graphicImage value="/img/garbage.png"/>: Remover
       		<h:graphicImage value="/cv/img/accept.png" alt="Votar" title="Votar"/>: Votar
       		<h:graphicImage value="/img/view.gif"/>: Visualizar
	 	</c:if>
	</div>
	<br/>

	<c:set var="enquetes" value="#{ enqueteComunidadeMBean.listagem }" /> 
	<c:set var="respostaUsuarioEnquete" value="${enqueteComunidadeMBean.respostaUsuarioEnquete}" />

	<c:if test="${ empty enquetes}">
		<p class="vazio">Nenhuma enquete foi cadastrada para esta comunidade</p>
	</c:if>


<c:if test="${ not empty enquetes }">
		<table class="listagem">
			<caption>Enquetes cadastrados</caption>
			<thead>
				<tr>
					<th>Pergunta da Enquete</th>
					<th>Criador da Enquete</th>
					<th>Data de criação</th>
					<th></th>
					<th></th>
					<th></th>
					<th></th>
				</tr>
			</thead>
			<tbody>

					<c:forEach var="a" items="#{ enquetes }" varStatus="loop">
						
						<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							<td class="first">${ a.pergunta }</td>
							<td>${ a.usuario.pessoa.nome }</td>
							
							<td><fmt:formatDate pattern="dd/MM/yyyy" value="${ a.data }" /></td>

							<td class="icon">
								<c:if test="${ !comunidadeVirtualMBean.membro.visitante }">
									<c:if test="${a.publicada}">
										<h:commandLink action="#{ enqueteComunidadeMBean.telaVotacao }">
											<f:param name="id" value="#{ a.id }" />
											<h:graphicImage value="/cv/img/accept.png" alt="Votar" title="Votar" />
										</h:commandLink>
									</c:if>
								</c:if>
							</td>

							<td class="icon">
								<h:commandLink action="#{ enqueteComunidadeMBean.mostrar }">
									<f:param name="id" value="#{ a.id }" />
									<h:graphicImage value="/cv/img/zoom.png" alt="Visualizar" title="Visualizar" />
								</h:commandLink>
							</td>
							
							<td class="icon">
								<c:if test="${ comunidadeVirtualMBean.membro.permitidoModerar || a.usuario.id == comunidadeVirtualMBean.usuarioLogado.id }">
									<h:commandLink action="#{ enqueteComunidadeMBean.editar }">
										<f:param name="id" value="#{ a.id }" />
										<h:graphicImage value="/ava/img/page_edit.png" alt="Alterar" title="Alterar" />
									</h:commandLink>
								</c:if>
							</td>
								
							<td class="icon">
							<c:if test="${ comunidadeVirtualMBean.membro.permitidoModerar || a.usuario.id == comunidadeVirtualMBean.usuarioLogado.id }">
								<h:commandLink action="#{ enqueteComunidadeMBean.removerEnqueteComVotos }" styleClass="confirm-remover" 
										onclick="return(confirm('Deseja realmente remover?'));">
											<f:param name="id" value="#{ a.id }" />
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
</rich:panel>
<%@include file="/cv/include/rodape.jsp" %>
</f:view>

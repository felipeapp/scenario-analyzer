<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>
	
<%@include file="/cv/include/_menu_comunidade.jsp" %>
<%@include file="/cv/include/_info_comunidade.jsp" %>


<div class="secaoComunidade">
	<rich:panel header="Indicações e Referências da Comunidade" headerClass="headerBloco">
	<h:form>
	<div class="infoAltRem">
		<c:if test="${ !comunidadeVirtualMBean.membro.visitante }">
			<h:graphicImage value="/img/adicionar.gif"/> <h:commandLink action="#{ indicacaoReferenciaComunidadeMBean.novo }" value="Cadastrar Referência" />
	       	<h:graphicImage value="/img/alterar.gif"/>: Alterar
	       	<h:graphicImage value="/img/garbage.png"/>: Remover
	      	<h:graphicImage value="/img/view.gif"/>: Visualizar
		</c:if>
	</div>
	<br />

	
	<c:set var="referencias" value="#{indicacaoReferenciaComunidadeMBean.listagem}"/>
	<c:if test="${ empty referencias}">
		<p class="vazio">Nenhum conteúdo foi cadastrado para esta comunidade</p>
	</c:if>
	
	<c:if test="${ not empty referencias }">
		<table class="listagem">
			<caption>Referências cadastradas</caption>
			<thead>
				<tr>
					<th>Nome</th>
					<th>Tipo</th>
					<th>URL</th>
					<th></th>
					<th></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="r" items="#{ referencias }" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${ r.titulo }</td>
						<td class="width10">${ r.tipoDesc }</td>
						
						<td class="width10"><a href="${ r.url }" class="websnapr">${ r.url }</a></td>
						
						<td class="icon">
							<h:commandLink action="#{ indicacaoReferenciaComunidadeMBean.mostrar }">
								<f:param name="id" value="#{ r.id }" />
								<h:graphicImage value="/cv/img/zoom.png" alt="Visualizar" title="Visualizar" />
							</h:commandLink>
						</td>
						
						<td class="icon">
							<c:if test="${ comunidadeVirtualMBean.membro.permitidoModerar || r.usuarioCadastro.id == comunidadeVirtualMBean.usuarioLogado.id }">
								<h:commandLink action="#{ indicacaoReferenciaComunidadeMBean.editar }">
									<f:param name="id" value="#{ r.id }" />
									<h:graphicImage value="/ava/img/page_edit.png" alt="Alterar" title="Alterar" />
								</h:commandLink>
							</c:if>
						</td>
						<c:if test="${ comunidadeVirtualMBean.membro.permitidoModerar || r.usuarioCadastro.id == comunidadeVirtualMBean.usuarioLogado.id }">
							<td class="icon">
								<h:commandLink action="#{ indicacaoReferenciaComunidadeMBean.remover }" onclick="#{confirmDelete}">
									<f:param name="id" value="#{ r.id }" />
									<h:graphicImage value="/ava/img/bin.png" alt="Remover" title="Remover" />
								</h:commandLink>
							</td>
						</c:if>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
	</h:form>
	</rich:panel>
		
</div>
</f:view>
		
<%@include file="/cv/include/rodape.jsp" %>
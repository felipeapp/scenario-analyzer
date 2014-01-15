<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>
	
<%@include file="/cv/include/_menu_comunidade.jsp" %>
<%@include file="/cv/include/_info_comunidade.jsp" %>


<div class="secaoComunidade">
	
	<rich:panel header="Lista de Conteúdos da Comunidade" headerClass="headerBloco">
	
	<h:form>
	<div class="infoAltRem">
		<c:if test="${ !comunidadeVirtualMBean.membro.visitante }">
			<h:graphicImage value="/img/adicionar.gif"/> <h:commandLink action="#{ conteudoComunidadeMBean.novo }" value="Cadastrar Conteúdo" />
        	<h:graphicImage value="/img/alterar.gif"/>: Alterar
        	<h:graphicImage value="/img/garbage.png"/>: Remover
       		<h:graphicImage value="/img/view.gif"/>: Visualizar
       	</c:if>
	</div>
	<br/>
		
	<c:set var="conteudos" value="#{conteudoComunidadeMBean.listagem}"/>
	<c:if test="${ empty conteudos}">
		<p class="vazio">Nenhum conteúdo foi cadastrado para esta comunidade</p>
	</c:if>
	
	<c:if test="${ not empty conteudos }">
		<table class="listagem">
			<caption>Conteúdos cadastrados</caption>
			<thead>
				<tr>
					<th>Título</th>
					<th> Tópico Relacionado </th>
					<th style="text-align: center;">Data de Cadastro</th>
					<th></th>
					<th></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="#{ conteudos }" var="item" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td class="first">${item.titulo}</td>
						<td>${item.topico.descricao}</td>
						
						<td align="center">
							<ufrn:format type="dataHora" valor="${item.dataCadastro}" />
						</td>
						<td class="icon"><h:commandLink action="#{conteudoComunidadeMBean.mostrar}">
							<f:param name="id" value="#{item.id}" />
							<h:graphicImage value="/cv/img/zoom.png" alt="Visualizar"
								title="Visualizar" />
						</h:commandLink></td>
						
						<td class="icon">
						<c:if test="${ comunidadeVirtualMBean.membro.permitidoModerar || item.usuarioCadastro.id == comunidadeVirtualMBean.usuarioLogado.id }">
								<h:commandLink
									action="#{conteudoComunidadeMBean.editar}">
									<f:param name="id" value="#{item.id}" />
									<h:graphicImage value="/ava/img/page_edit.png" alt="Alterar" title="Alterar" />
								</h:commandLink>
						</c:if>
						</td>
						
						<td class="icon">
						<c:if test="${ comunidadeVirtualMBean.membro.permitidoModerar || item.usuarioCadastro.id == comunidadeVirtualMBean.usuarioLogado.id }">
								<h:commandLink
									action="#{conteudoComunidadeMBean.remover}"
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
</div>
</rich:panel>

</f:view>
		
<%@include file="/cv/include/rodape.jsp" %>
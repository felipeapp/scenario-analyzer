<%@include file="/cv/include/cabecalho.jsp" %>
<f:view>
	
<%@include file="/cv/include/_menu_comunidade.jsp" %>
<%@include file="/cv/include/_info_comunidade.jsp" %>


<div class="secaoComunidade">
	<rich:panel header="Fórum da Comunidade" headerClass="headerBloco">
	<h:form>
	<div class="infoAltRem">
		<c:if test="${ !comunidadeVirtualMBean.membro.visitante }">
			<h:graphicImage value="/img/adicionar.gif"/> <h:commandLink action="#{ forumMensagemComunidadeMBean.novo }" value="Criar Tópico no Fórum" />
       		<h:graphicImage value="/img/garbage.png"/>: Remover
       	</c:if>
	</div>
	<br />
	
		<c:set var="foruns" value="#{forumMensagemComunidadeMBean.listagemOrdenada}" />

		<c:if test="${ empty foruns }">
			<p class="vazio">Nenhum tópico foi cadastrado para esse Fórum</p>
		</c:if> 
	
	<c:if test="${ not empty foruns }">
		
		<%-- Exibe a paginação --%>
		<c:set var="itensPaginacao" value="#{ foruns }" />
		<c:set var="beanPaginacao" value="#{ forumMensagemComunidadeMBean }" />
		<c:set var="labelCrescente" value="Mais Antigos Primeiro" />
		<c:set var="labelDecrescente" value="Mais Recentes Primeiro" />
		<%@include file="/cv/include/paginacao.jsp" %>
		
		<table class="listagem">
			<caption>Tópicos Cadastrados</caption>
			<thead>
				<tr>
					<th>Título</th>
					<th>Respostas</th>
					<th>Autor</th>
					<th style="text-align: center;">Última Postagem</th>
					<c:if test="${ comunidadeVirtualMBean.membro.permitidoModerar || n.usuario.id == forumMensagemComunidadeMBean.discenteLogado }"><th></th></c:if>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="n" items="#{ foruns }" varStatus="loop">
				<tr class="linha${ loop.index % 2 == 0 ? 'Imp' : 'P' }ar">
					
					<!-- <td class="first">${ n.titulo }</td> -->
					
					<td class="first"> <h:commandLink action="#{ forumMensagemComunidadeMBean.mostrar }"> 
							<h:outputText value="#{ n.titulo }"/>
							<f:param name="idForumMensagem" value="#{ n.id }"/>
							<f:param name="id" value="#{ n.forum.id }"/>
					</h:commandLink> 
					</td>
					<td class="width90"> ${ n.respostas } </td>
					<td class="width80">${ n.usuario.pessoa.nome }</td>
					
					<td style="text-align: center;">
						<c:if test="${ n.ultimaPostagem != null }">
							<fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${ n.ultimaPostagem }"/>
						</c:if>
						<c:if test="${ n.ultimaPostagem == null }">
							<fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${ n.data }"/>
						</c:if>
					</td>
					
					<c:if test="${ comunidadeVirtualMBean.membro.permitidoModerar || n.usuario.id == forumMensagemComunidadeMBean.discenteLogado }">
						<td class="icon">
							<h:commandLink action="#{ forumMensagemComunidadeMBean.remover }" styleClass="confirm-remover" title="Remover"
							onclick="return(confirm('Se excluir este tópico TODAS as mensagens que ele possui também serão removidas. Tem certeza?'));">
								<f:param name="topico" value="true"/>
								<f:param name="id" value="#{ n.id }"/><h:graphicImage value="/ava/img/bin.png"/>
							</h:commandLink></td>
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
<%@include file="/ava/cabecalho.jsp" %>

<f:view>

<style>
	.botao-medio {
			margin-bottom:0px !important;
			height:60px !important;
	}
</style>

<%@include file="/ava/menu.jsp" %>
<h:form>
<fieldset>
<legend>Conteúdos</legend>
	
<c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }">

	<div class="menu-botoes" style="text-align:center;width:210px;margin: 0 auto;">
		<ul class="menu-interno">
				<li class="botao-medio novoConteudo;">
					<h:commandLink action="#{ conteudoTurma.novo }">
						<p style="margin-left:2px;font-variant:small-caps;font-size:1.3em;font-weight:bold;">Cadastrar Novo Conteúdo</p> 
					</h:commandLink>
				</li>
		</ul>	
		<div style="clear:both;"></div>	
	</div>
</c:if>

<c:set var="conteudos" value="#{conteudoTurma.listagem}"/>

<c:if test="${ empty conteudos }">
<p class="empty-listing">Nenhum item foi encontrado</p>
</c:if>
<c:if test="${ not empty conteudos }">

<div class="infoAltRem">
	<img src="${ctx}/ava/img/zoom.png"/>: Visualizar
	<c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }">
		<img src="${ctx}/ava/img/page_edit.png"/>: Alterar
		<img src="${ctx}/ava/img/bin.png"/>: Remover
	</c:if>
</div>

<table class="listing">
<thead>
<tr><th style="text-align: left">Titulo</th><th>Criado em</th><th></th><c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }"><th></th><th></th></c:if></tr>
</thead>
<tbody>
<c:forEach items="#{ conteudos }" var="item" varStatus="loop">
<tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }">	
	<td class="first">${item.titulo}</td>
	<td class="width90"><fmt:formatDate pattern="dd/MM/yyyy" value="${item.dataCadastro}" /></td>
	<td class="icon"><h:commandLink title="Visualizar" action="#{conteudoTurma.mostrar}"><f:param name="id" value="#{item.id}"/><h:graphicImage value="/ava/img/zoom.png"/></h:commandLink></td>
<c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }">
    <td class="icon"><h:commandLink title="Alterar" action="#{conteudoTurma.editar}"><f:param name="id" value="#{item.id}"/><h:graphicImage value="/ava/img/page_edit.png"/></h:commandLink></td>
    <td class="icon"><h:commandLink title="Remover" action="#{conteudoTurma.remover}" onclick="return(confirm('Deseja realmente excluir este conteúdo?'));" styleClass="confirm-remover"><f:param name="id" value="#{item.id}"/><h:graphicImage value="/ava/img/bin.png"/></h:commandLink></td>
</c:if>
</tr>
</c:forEach>
</tbody>
</table>
</c:if>

</h:form>

</f:view>
<%@include file="/ava/rodape.jsp" %>
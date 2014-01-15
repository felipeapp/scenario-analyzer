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


<c:set var="noticias" value="#{noticiaTurma.listagem}"/>

<fieldset>
<legend>Notícias</legend>
<c:if test="${ noticiaTurma.permiteCadastrarNoticia }">

<div class="menu-botoes" style="text-align:center;width:210px;margin: 0 auto;">
	<ul class="menu-interno">
			<li class="botao-medio novaNoticia;">
				<h:commandLink action="#{ noticiaTurma.novo }">
					<p style="margin-left:30px;font-variant:small-caps;font-size:1.3em;font-weight:bold;">Cadastrar Notícia</p> 
				</h:commandLink>
			</li>
	</ul>	
	<div style="clear:both;"></div>	
</div>
</c:if>
<c:if test="${ empty noticias }">
<p class="empty-listing">Nenhum item foi encontrado</p>
</c:if>
<c:if test="${ not empty noticias }">

<c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }">
<div class="infoAltRem">
	<img src="${ctx}/ava/img/zoom.png"/>: Visualizar
	<img src="${ctx}/ava/img/page_edit.png"/>: Alterar
	<img src="${ctx}/ava/img/bin.png"/>: Remover
</div>
</c:if>

<c:if test="${ turmaVirtual.discente }">
<div class="infoAltRem">
	<img src="${ctx}/ava/img/zoom.png"/>: Visualizar
</div>
</c:if>

<table class="listing">
<thead>
<tr><th><p align="left">Título</p></th><th>Data</th><th></th><c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }"><th></th><th></th></c:if></tr>
</thead>
<tbody>
<c:forEach var="n" items="#{ noticias }" varStatus="loop">
<tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }">
	<td class="first">${ n.descricao }</td>
	<td class="width75"><fmt:formatDate pattern="dd/MM/yyyy" value="${ n.data }"/></td>
	<td class="icon"><h:commandLink action="#{ noticiaTurma.mostrar }"><f:param name="id" value="#{ n.id }"/><h:graphicImage value="/ava/img/zoom.png" title="Visualizar"/></h:commandLink></td>
	<c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }">
    <td class="icon"><h:commandLink action="#{ noticiaTurma.editar }"><f:param name="id" value="#{ n.id }"/><h:graphicImage value="/ava/img/page_edit.png" title="Alterar" /></h:commandLink></td>
    <td class="icon"><h:commandLink action="#{ noticiaTurma.remover }" onclick="return(confirm('Deseja realmente excluir esta notícia?'));" styleClass="confirm-remover"><f:param name="id" value="#{ n.id }"/><h:graphicImage value="/ava/img/bin.png" title="Remover" /></h:commandLink></td>
    </c:if>
</tr>
</c:forEach>
</tbody>
</table>
</c:if>

</fieldset>

</h:form>

</f:view>
<%@include file="/ava/rodape.jsp" %>

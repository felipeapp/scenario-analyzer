
<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form>


<c:set var="avaliacoes" value="#{ dataAvaliacao.listagem }"/>

<fieldset>
<legend>Datas de Avaliações</legend>

<p class="create-new"><h:commandLink action="#{ dataAvaliacao.novo }" value="Cadastrar Data de Avaliação"/></p>

<c:if test="${ empty avaliacoes }">
<p class="empty-listing">Nenhum item foi encontrado</p>
</c:if>
<c:if test="${ not empty avaliacoes }">
<table class="listing">
<thead>
<tr><th>Data</th><th>Hora</th><th>Descrição</th><th></th><th></th><th></th></tr>
</thead>
<tbody>
<c:forEach var="a" items="#{ avaliacoes }" varStatus="loop">
<tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }">
	<td class="first width75"><fmt:formatDate value="${ a.data }" pattern="dd/MM/yyyy"/></td>
	<td class="width120">${ a.hora }</td>
	<td>${ a.descricao }</td>
	<td class="icon"><h:commandLink action="#{ dataAvaliacao.mostrar }"><f:param name="id" value="#{ a.id }"/><h:graphicImage value="/ava/img/zoom.png"/></h:commandLink></td>
    <td class="icon"><h:commandLink action="#{ dataAvaliacao.editar }"><f:param name="id" value="#{ a.id }"/><h:graphicImage value="/ava/img/page_edit.png"/></h:commandLink></td>
    <td class="icon"><h:commandLink action="#{ dataAvaliacao.editar }" styleClass="confirm-remover"><f:param name="id" value="#{ a.id }"/><h:graphicImage value="/ava/img/bin.png"/></h:commandLink></td>
</tr>
</c:forEach>
</tbody>
</table>
</c:if>

</h:form>
</f:view>

<%@include file="/ava/rodape.jsp" %>
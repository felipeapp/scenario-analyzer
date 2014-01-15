<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form>


<c:set var="foruns" value="#{forum.listagem}"/>

<h:messages showDetail="true" />

<fieldset>
<legend>Fórum</legend>
<c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.forum }">
<p class="create-new"><h:commandLink action="#{ forum.novo }" value="Cadastrar Fórum"/></p>
</c:if>
<c:if test="${ empty foruns }">
<p class="empty-listing">Nenhum item foi encontrado</p>
</c:if>
<c:if test="${ not empty foruns }">
<table class="listing">
<thead>
<tr><th>Título</th><th>Autor</th><th>Data</th><c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.forum }"></c:if><th></th></th></tr>
</thead>
<tbody>
<c:forEach var="n" items="#{ foruns }" varStatus="loop">
<tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }">
	
	<td class="first"> 
	<h:commandLink action="#{ forumMensagem.listar }"> 
		<h:outputText value="#{n.titulo}"/> 
		<f:param name="id" value="#{ n.id }"/> 
	</h:commandLink> </td>
	
	<td class="width150">${ n.usuario.pessoa.nome }</td>
	<td class="width90"><fmt:formatDate pattern="dd/MM/yyyy" value="${ n.data }"/></td>
	
	<c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.forum }">
		
	    				<td class="icon">
	    					<h:commandLink action="#{ forum.remover }" onclick="return(confirm('Se remover esse Fórum você irá remover todas as mensagens contidas no mesmo. Tem certeza?'));">
	    						<f:param name="id" value="#{ n.id }"/>
	    						<h:graphicImage value="/ava/img/bin.png"/>
	    				</h:commandLink></td>
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

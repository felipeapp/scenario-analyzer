<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<a4j:keepAlive beanName="forumMensagem" />

<style>
	.botao-medio {
			margin-bottom:0px !important;
			height:60px !important;
	}
</style>

<%@include file="/ava/menu.jsp" %>
<h:form>

<c:set var="foruns" value="#{forumMensagem.listagem}"/>

<fieldset>
<legend>Mensagens do Fórum<h:outputText value="#{ forumMensagem.forum.titulo }"/></legend>

<div class="menu-botoes" style="text-align:center;width:210px;margin: 0 auto;">
	<ul class="menu-interno">
			<li class="botao-medio novoTopico;">
				<h:commandLink action="#{ forumMensagem.novo }">
					<p style="margin-left:15px;font-variant:small-caps;font-size:1.3em;font-weight:bold;">Cadastrar novo tópico</p> 
				</h:commandLink>
			</li>
	</ul>	
	<div style="clear:both;"></div>	
</div>

<c:if test="${ empty foruns }">
<p class="empty-listing">Nenhum item foi encontrado</p>
</c:if>

<c:if test="${ not empty foruns }">
<c:if test="${turmaVirtual.docente}">
<div class="infoAltRem">
	<img src="/sigaa/ava/img/bin.png">: Remover Tópico
</div>
</c:if>
<table class="listing">
<thead>
<tr><th><p align="left">Título</p></th><th><p align="right">Respostas</p></th><th><p align="left">Autor</p></th><th>Última Postagem</th><c:if test="${ turmaVirtual.docente }"><th></th></c:if></tr>
</thead>
<tbody>

 
<c:forEach var="n" items="#{ foruns }" varStatus="loop">
<tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }">
	
	<!-- <td class="first">${ n.titulo }</td> -->
	
	<td class="first"> <h:commandLink action="#{ forumMensagem.mostrar }"> 
			<h:outputText value="#{ n.titulo }"/>
			<f:param name="idForumMensagem" value="#{ n.id }"/>
			<f:param name="id" value="#{ n.forum.id }"/>
	</h:commandLink> 
	</td>
	<td class="width90"> <p align="right">${ n.respostas }</p> </td>
	<td class="width150">
		<p align="left">
			${ n.usuario.pessoa.nome }
		</p>
	</td>
	<td class="width90">
		<c:if test="${ n.ultimaPostagem != null }">
			<fmt:formatDate pattern="dd/MM/yyyy" value="${ n.ultimaPostagem }"/>
		</c:if>
		<c:if test="${ n.ultimaPostagem == null }">
			<fmt:formatDate pattern="dd/MM/yyyy" value="${ n.data }"/>
		</c:if>
	</td>
	
		<c:if test="${ turmaVirtual.docente || n.usuario.id == usuario.id}">
			<td class="icon">
				<h:commandLink action="#{ forumMensagem.remover }" styleClass="confirm-remover" title="Remover Tópico"
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

</fieldset>

</h:form>
</f:view>
<%@include file="/ava/rodape.jsp" %>

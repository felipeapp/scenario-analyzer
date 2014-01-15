
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

<c:set var="enquetes" value="#{ enquete.listagem }"/>

<fieldset>
<legend>Enquetes</legend>

<c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.enquete || permissaoAva.permissaoUsuario.docente || turmaVirtual.config.permiteAlunoCriarEnquete}">
	<div class="menu-botoes" style="text-align:center;width:210px;margin: 0 auto;">
		<ul class="menu-interno">
				<li class="botao-medio novaEnquete;">
					<h:commandLink action="#{ enquete.novo }">
						<p style="margin-left:28px;font-variant:small-caps;font-size:1.3em;font-weight:bold;">Cadastrar Enquete</p> 
					</h:commandLink>
				</li>
		</ul>	
		<div style="clear:both;"></div>	
	</div>
</c:if>

<c:if test="${ empty enquetes }">
<p class="empty-listing">Nenhum item foi encontrado</p>
</c:if>
<c:if test="${ not empty enquetes }">

<div class="infoAltRem">
	<img src="${ctx}/ava/img/accept.png"/>: Votar
	<img src="/sigaa/img/delete.png">: Enquete Encerrada
	<img src="${ctx}/ava/img/zoom.png"/>: Visualizar
	<c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.enquete }">
		<img src="${ctx}/ava/img/page_edit.png"/>: Alterar
		<img src="${ctx}/ava/img/bin.png"/>: Remover
	</c:if>
</div>

<table class="listing" width="100%">
<thead>
<tr>
	<c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente || permissaoAva.permissaoUsuario.enquete }"><th style="width:80px;text-align:left;">Situação</th></c:if>
	<th style="text-align:left;">Pergunta</th>
	<th style="text-align:left;">Criado por</th>
	<th>Criado em</th>
	<th>Prazo para Votação</th>
	<th></th>
	<th></th>
	<c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente || permissaoAva.permissaoUsuario.enquete }"><th></th><th></th></c:if>
</tr>
</thead>
<tbody>
<c:forEach var="a" items="#{ enquetes }" varStatus="loop">
	<c:if test="${ a.publicada || turmaVirtual.docente  || permissaoAva.permissaoUsuario.docente || permissaoAva.permissaoUsuario.enquete }">
		<tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }">
			<c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente || permissaoAva.permissaoUsuario.enquete }">
				<td class="first" style="text-align:left;border-left:0 none;"><c:if test="${not a.publicada}">Não</c:if> Publicada</td>
				<td style="text-align:left;">${ a.pergunta }</td>
			</c:if>
			<c:if test="${ turmaVirtual.discente && !permissaoAva.permissaoUsuario.enquete && !permissaoAva.permissaoUsuario.docente }">
				<td class="first" style="text-align:left;">${ a.pergunta }</td>
			</c:if>
			<td class="width90" style="text-align:left;">${ a.usuario.login }</td>
			<td class="width90"><ufrn:format type="data" valor="${ a.data }"/></td>
			<td class="width90"><ufrn:format type="dataHora" valor="${ a.dataFim }"/></td>
			<c:if test="${ not a.prazoExpirado }">
				<td class="icon"><h:commandLink action="#{ enquete.telaVotacao }"><f:param name="id" value="#{ a.id }"/><h:graphicImage value="/ava/img/accept.png" alt="Votar" title="Votar"/></h:commandLink></td>
			</c:if>	
			<c:if test="${ a.prazoExpirado }">
				<td class="icon"><h:graphicImage value="/img/delete.png" alt="Enquete Encerrada" title="Enquete Encerrada"/></td>
			</c:if>	
			<td class="icon"><h:commandLink action="#{ enquete.mostrar }"><f:param name="id" value="#{ a.id }"/><h:graphicImage value="/ava/img/zoom.png" alt="Visualizar" title="Visualizar" /></h:commandLink></td>
		    <c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente || permissaoAva.permissaoUsuario.enquete }">
			    <td class="icon"><h:commandLink action="#{ enquete.editar }"><f:param name="id" value="#{ a.id }"/><h:graphicImage value="/ava/img/page_edit.png" alt="Alterar" title="Alterar"/></h:commandLink></td>
			    <td class="icon"><h:commandLink action="#{ enquete.removerEnqueteComVotos }" onclick="return(confirm('Deseja realmente excluir esta enquete?'));" styleClass="confirm-remover"><f:param name="id" value="#{ a.id }"/><h:graphicImage value="/ava/img/bin.png" alt="Remover" title="Remover"/></h:commandLink></td>
		    </c:if>        
		</tr>
	</c:if>
</c:forEach>

</tbody>
</table>
</c:if>
</fieldset>

</h:form>
</f:view>
<%@include file="/ava/rodape.jsp" %>

<%@include file="/ava/cabecalho.jsp" %>
<script type="text/javascript" src="http://www.websnapr.com/js/websnapr.js"></script>
<f:view>

<style>
	.botao-medio {
			margin-bottom:0px !important;
			height:60px !important;
	}
</style>

<%@include file="/ava/menu.jsp" %>
<h:form>

<c:set var="referencias" value="#{indicacaoReferencia.listagem}"/>

<fieldset>
<legend>Referências</legend>

<c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }">
	<div class="menu-botoes" style="text-align:center;width:210px;margin: 0 auto;">
		<ul class="menu-interno">
				<li class="botao-medio novaReferencia;">
					<h:commandLink action="#{ indicacaoReferencia.novo }">
						<p style="margin-left:20px;font-variant:small-caps;font-size:1.3em;font-weight:bold;">Cadastrar Referência</p> 
					</h:commandLink>
				</li>
		</ul>	
		<div style="clear:both;"></div>	
	</div>
</c:if>

<c:if test="${ empty referencias }">
<p class="empty-listing">Nenhum item foi encontrado</p>
</c:if>
<c:if test="${ not empty referencias }">

<div class="infoAltRem">
	<img src="${ctx}/ava/img/zoom.png"/>: Visualizar
	<c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }">
		<img src="${ctx}/ava/img/page_edit.png"/>: Alterar
		<img src="${ctx}/ava/img/bin.png"/>: Remover
	</c:if>
</div>

<table class="listing">
<thead>
<tr><th><p align="left">Nome</p></th><th><p align="left">Tipo</p></th><th><p align="left">URL</p></th><th></th><c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }"><th></th><th></th></c:if></tr>
</thead>
<tbody>

<c:forEach var="r" items="#{ referencias }" varStatus="loop">
<tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }">
	<td class="first"><h:outputText value="#{ r.descricao }" escape="false" /></td>
	<td class="width75"><p align="left">${ r.tipoDesc }</p></td>
	<td>
		<div id="imgUrl${r.id}" style="position:absolute;float:left;display:none">
			<script type="text/javascript">wsr_snapshot('${ r.url != "http://" ? r.url : "" }', '80TpQrHyjPI0', 's');</script>
		</div>
		<a href="${ r.url }" target="_blank" onmouseover="mostrarUrl(${r.id},this,event);" onmouseout="esconderUrl(${r.id},this,event);">${ r.url != "http://" ? r.url : "" }</a>
	</td>
	<td class="icon"><h:commandLink action="#{ indicacaoReferencia.mostrar }" title="Visualizar"><f:param name="id" value="#{ r.id }"/><h:graphicImage value="/ava/img/zoom.png"/></h:commandLink></td>
    <c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }">
    <td class="icon"><h:commandLink action="#{ indicacaoReferencia.editar }" title="Alterar"><f:param name="id" value="#{ r.id }"/><h:graphicImage value="/ava/img/page_edit.png"/></h:commandLink></td>
    <td class="icon"><h:commandLink action="#{ indicacaoReferencia.remover }" title="Remover" styleClass="confirm-remover" onclick="if (!confirm(\"Deseja realmente excluir esta referência?\")) return false;"><f:param name="id" value="#{ r.id }"/><h:graphicImage value="/ava/img/bin.png"/></h:commandLink></td>
   	</c:if>
</tr>
</c:forEach>
</tbody>
</table>
</c:if>
</fieldset>

</h:form>
</f:view>
<script type="text/javascript">
 
function mostrarUrl (id,elem,event) {
	var img = J("#imgUrl"+id);
	img.css("display","block");
	img.css("margin-top",20);	
}

function esconderUrl (id,elem,event) {
	var img = J("#imgUrl"+id).css("display","none");			
}

</script>

<%@include file="/ava/rodape.jsp" %>

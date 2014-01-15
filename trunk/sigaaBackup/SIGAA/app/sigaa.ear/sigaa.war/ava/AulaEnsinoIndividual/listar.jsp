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


<c:set var="aulas" value="#{aulaEnsinoIndividual.listagem}"/>

<fieldset>
<legend>Aulas de Ensino Individual</legend>

<div class="menu-botoes" style="text-align:center; width:210px;margin: 0 auto;">
	<ul class="menu-interno">
			<li class="botao-medio novaAula;">
				<h:commandLink action="#{ aulaEnsinoIndividual.novo }">
					<p style="margin-left:9px;padding-top:11px;font-variant:small-caps;font-size:1.3em;font-weight:bold;">Cadastrar<br/>Aula de Ensino Individual</p> 
				</h:commandLink>
			</li>
	</ul>	
	<div style="clear:both;"></div>	
</div>

<c:if test="${ empty aulas }">
<p class="empty-listing">Nenhum item foi encontrado</p>
</c:if>
<c:if test="${ not empty aulas }">

<div class="infoAltRem">
	<img src="${ctx}/ava/img/page_edit.png"/>: Alterar
	<img src="${ctx}/ava/img/bin.png"/>: Remover
</div>
<table class="listing">
<thead>
<tr>
<th>Data</th>
<c:if test="${ not empty turmaVirtual.turma.subturmas }">
<th>Turma</th>
</c:if>
<th><p align="left">&nbsp;Tipo</th><p/><th><p align="left">Descrição</p></th>
<th></th><th></th></tr>
</thead>
<tbody>
<c:forEach var="a" items="#{ aulas }" varStatus="loop">
<tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }">
	<td class="first width75"><fmt:formatDate pattern="dd/MM/yyyy" value="${ a.dataAula }"/></td>
	<c:if test="${ not empty turmaVirtual.turma.subturmas }">
		<td>${ a.turma.descricaoSemDocente }</td>
	</c:if>
	<td class="width90" style="text-align:left;">${ a.tipoDesc }</td>
	<td>${ a.descricao }</td>
    <td class="icon"><h:commandLink title="Alterar" action="#{ aulaEnsinoIndividual.editar }"><f:param name="id" value="#{ a.id }"/><h:graphicImage value="/ava/img/page_edit.png"/></h:commandLink></td>
    <td class="icon"><h:commandLink title="Remover" action="#{ aulaEnsinoIndividual.remover }" styleClass="confirm-remover" onclick="if (!confirm(\"Tem certeza que deseja remover esta aula de ensino individual?\")) return false;"><f:param name="id" value="#{ a.id }" /><h:graphicImage value="/ava/img/bin.png"/></h:commandLink></td>
</tr>
</c:forEach>
</tbody>
</table>
</c:if>

</fieldset>

</h:form>

</f:view>
<%@include file="/ava/rodape.jsp" %>

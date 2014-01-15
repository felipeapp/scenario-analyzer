<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2>Tipo de Avaliação de Organização de Eventos</h2>
<br>
<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>: <h:commandLink action="#{tipoAvaliacaoOrganizacaoEvento.preCadastrar}" value="Cadastrar Novo Tipo de Avaliação de Organização de Eventos"></h:commandLink><br/>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Tipo de Avaliação de Organização de Eventos<br/>
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Tipo de Avaliação de Organização de Eventos<br/>
		</div>
	</h:form>
<h:outputText value="#{tipoAvaliacaoOrganizacaoEvento.create}"/>

<table class=listagem>
<caption class="listagem"> Lista de Tipo de Avaliação de Organização de Eventos</caption>
<thead>

<td>Descrição</td>
<td></td>
<td></td>
</thead>
<c:forEach items="${tipoAvaliacaoOrganizacaoEvento.all}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
<td>${item.descricao}</td>
<h:form>
<td  width=20>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{tipoAvaliacaoOrganizacaoEvento.atualizar}"/>
</td>
</h:form>
<h:form>
<td  width=25>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/delete.gif" alt="Remover" action="#{tipoAvaliacaoOrganizacaoEvento.remover}"/>
</td>
</h:form>
</tr>
</c:forEach>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

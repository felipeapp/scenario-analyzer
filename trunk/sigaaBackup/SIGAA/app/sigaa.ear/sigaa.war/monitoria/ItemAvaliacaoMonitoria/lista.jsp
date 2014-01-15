<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h:messages/>
	<h2><ufrn:subSistema /> > Itens de Avaliação</h2>
	<br>

	<center>
	<h:form>
		Filtrar Por: <h:selectOneMenu value="#{itemAvaliacaoMonitoria.obj.grupo.id}">
			<f:selectItem itemValue="0" itemLabel="TODOS"/> 
			<f:selectItems value="#{grupoItemAvaliacao.allComboMonitoria}"/>
		</h:selectOneMenu>
		<h:commandButton action="#{itemAvaliacaoMonitoria.filtrarPorGrupo}" value="Buscar"/>
	</h:form>
	</center>
	<br>

	<h:outputText value="#{itemAvaliacaoMonitoria.create}"/>

	<div class="infoAltRem">
	    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Item
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Item<br/>
	</div>


	<h:form>
	<table class="listagem">
		<caption class="listagem">Lista de Itens de Avaliação </caption>
	
	<thead>
		<tr>
			<th> Descrição do Item </th>
			<th> Grupo </td>
			<th align="center"> Nota Máxima </th>
			<th></th>
			<th></th>
		</tr>
	</thead>

	<tbody>
		<c:if test="${not empty itemAvaliacaoMonitoria.itens}">
		<c:forEach items="#{itemAvaliacaoMonitoria.itens}" var="itemAvaliacao" varStatus="status">
		       <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td> ${itemAvaliacao.descricao} </td>
				<td> ${itemAvaliacao.grupo.denominacao} </td>
				<td align="center"> ${itemAvaliacao.notaMaxima} </td>
	
				<td width="2%">
						<h:commandLink  title="Alterar" action="#{itemAvaliacaoMonitoria.atualizar}" style="border: 0;">
						      <f:param name="id" value="#{itemAvaliacao.id}"/>
						      <h:graphicImage url="/img/alterar.gif" />
						</h:commandLink>
				</td>		
				<td width="2%">	
						<h:commandLink  title="Remover" action="#{itemAvaliacaoMonitoria.preRemover}" style="border: 0;">
						      <f:param name="id" value="#{itemAvaliacao.id}"/>
						      <h:graphicImage url="/img/delete.gif" />
						</h:commandLink>
				</td>
		</c:forEach>
		</c:if>
		<c:if test="${empty itemAvaliacaoMonitoria.itens}">
		           <tr> <td> <font color="red">Não há itens cadastrados para o grupo selecinado!</font> </td></tr>
		</c:if>	
	</tbody>
	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
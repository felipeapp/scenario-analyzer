<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h:messages/>
	<h2><ufrn:subSistema /> > Itens de Avaliação</h2>
	<br>

	<center>
	<h:form>
		Filtrar Por: <h:selectOneMenu value="#{itemAvaliacaoExtensao.obj.grupo.id}">
			<f:selectItem itemValue="0" itemLabel="TODOS"/>
			<f:selectItems value="#{grupoItemAvaliacaoExtensao.allComboExtensao}"/>
		</h:selectOneMenu>
		<h:commandButton action="#{itemAvaliacaoExtensao.filtrarPorGrupo}" value="Buscar"/>
	</h:form>
	</center>
	<br>

	<h:outputText value="#{itemAvaliacaoExtensao.create}"/>

	<h:form id="CadastrarNovamenteItem">
		<div class="infoAltRem">
			<h:commandLink title="Cadastrar Item" action="#{itemAvaliacaoExtensao.iniciarCadastroItem}">		      
		    	  <h:graphicImage url="/img/adicionar.gif"/> Cadastrar Item
			</h:commandLink>
			
	    	<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Item
	    	<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Item<br/>
		</div>
	</h:form>

	<h:form>
	<table class="listagem">
		<caption class="listagem">Lista de Itens de Avaliação </caption>
	
	<thead>
		<tr>
			<th> Descrição do Item </th>
			<th> Grupo </td>
			<th style="text-align: center;"> Peso </th>
			<th style="text-align: center;"> Nota Máxima </th>
			<th></th>
			<th></th>
		</tr>
	</thead>

	<tbody>
		<c:if test="${not empty itemAvaliacaoExtensao.itens}">
		<c:forEach items="#{itemAvaliacaoExtensao.itens}" var="itemAvaliacao" varStatus="status">
		       <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td> ${itemAvaliacao.descricao} </td>
				<td> ${itemAvaliacao.grupo.denominacao} </td>
				<td style="text-align: center;"> ${itemAvaliacao.peso} </td>
				<td style="text-align: center;"> ${itemAvaliacao.notaMaxima} </td>
	
				<td width="2%">
						<h:commandLink  title="Alterar" action="#{itemAvaliacaoExtensao.atualizar}" style="border: 0;">
						      <f:param name="id" value="#{itemAvaliacao.id}"/>
						      <h:graphicImage url="/img/alterar.gif" />
						</h:commandLink>
				</td>		
				<td width="2%">	
						<h:commandLink  title="Remover" action="#{itemAvaliacaoExtensao.preRemover}" style="border: 0;">
						      <f:param name="id" value="#{itemAvaliacao.id}"/>
						      <h:graphicImage url="/img/delete.gif" />
						</h:commandLink>
				</td>
		</c:forEach>
		</c:if>
		<c:if test="${empty itemAvaliacaoExtensao.itens}">
		           <tr> <td> <font color="red">Não há itens cadastrados para o grupo selecinado!</font> </td></tr>
		</c:if>	
	</tbody>	
	
	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
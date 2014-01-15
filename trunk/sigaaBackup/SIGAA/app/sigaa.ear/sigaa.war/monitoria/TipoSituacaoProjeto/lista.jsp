<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages/>
	<h2><ufrn:subSistema /> > Tipo de Situação do Projeto</h2>

	<h:outputText value="#{tipoSituacaoProjeto.create}"/>

	<div class="infoAltRem">
	    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Tipo de Situação
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Tipo de Situação<br/>
	</div>

	<h:form>
	<table class="listagem">
			<caption class="listagem">Lista de Tipos de Situação </caption>
	<thead>
		<th>Descrição</th>
		<th>Contexto</th>
		<th></th>
		<th></th>
	</thead>

	<c:forEach items="#{tipoSituacaoProjeto.all}" var="situacao" varStatus="status">
        <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td>${situacao.descricao} </td>
			<c:choose>
			 <c:when test="${situacao.contexto == 'M'}"> <td>MONITORIA</td> </c:when>
			 <c:when test="${situacao.contexto == 'E'}"> <td>EXTENSÃO</td> </c:when>
			 <c:when test="${situacao.contexto == 'P'}"> <td>PESQUISA</td> </c:when>
			 <c:when test="${situacao.contexto == 'T'}"> <td>TODOS</td> </c:when>
			 <c:when test="${empty situacao.contexto || situacao.contexto == ' '  }"> <td> TODOS </td> </c:when>
			</c:choose>
			<td width="2%" >
					<h:commandLink  title="Alterar" action="#{tipoSituacaoProjeto.atualizar}" style="border: 0;" >
					   	<f:param name="id" value="#{situacao.id}"/>				    	
						<h:graphicImage url="/img/alterar.gif"   />
					</h:commandLink>
			</td>
			<td  width="2%">	
				<h:commandLink title="Remover" action="#{tipoSituacaoProjeto.preRemover}" style="border: 0;" >
				   	<f:param name="id" value="#{situacao.id}"/>				    	
					<h:graphicImage url="/img/delete.gif"  />
				</h:commandLink>
			</td>
		</tr>
	</c:forEach>
	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
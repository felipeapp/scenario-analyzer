<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages/>
	<h2><ufrn:subSistema /> > Itens de Avaliação</h2>
	<br>

	<h:outputText value="#{itemAvaliacaoEad.create}"/>

	<div class="infoAltRem">
	    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Item de Avaliação
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Item de Avaliação<br/>
	</div>


	<table class="listagem">
	<caption class="listagem">Lista de Itens de Avaliação </caption>
	<thead>
		<tr>
		<th>Nome</th>
		<th width="5%"></th>
		<th width="5%"></th>
		</tr>
	</thead>

	<c:forEach items="${itemAvaliacaoEad.all}" var="item">
		<tr>
			<td> ${item.nome} </td>
			<h:form>
				<td>
					<input type="hidden" value="${item.id}" name="id"/>
					<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{itemAvaliacaoEad.atualizar}" style="border: 0;"/>

				</td>
			</h:form>
			<h:form>
				<td  width="25">
					<input type="hidden" value="${item.id}" name="id"/>
					<h:commandButton image="/img/delete.gif" alt="Remover" action="#{itemAvaliacaoEad.preRemover}" style="border: 0;"/>
				</td>
			</h:form>
	</c:forEach>
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

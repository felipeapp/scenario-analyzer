<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipo de Produto</h2>

	<h:outputText value="#{tipoProduto.create}" />

	<center><h:messages />
	<div class="infoAltRem"><h:graphicImage value="/img/alterar.gif"
		style="overflow: visible;" />: Alterar dados do Tipo de Produto <h:graphicImage
		value="/img/delete.gif" style="overflow: visible;" />: Remover Tipo
	de Produto<br />
	</div>
	</center>

	<h:form>
		<table class=listagem>
			<caption class="listagem">Lista de Tipo de Produto</caption>
			<thead>
				<tr>
					<th>Descrição</th>
					<th></th>
					<th></th>
				</tr>
			</thead>

			<c:forEach items="#{tipoProduto.allAtivos}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${item.descricao}</td>
					<td width="2%"><h:commandLink title="Alterar"
						action="#{tipoProduto.atualizar}" style="border: 0;">
						<f:param name="id" value="#{item.id}" />
						<h:graphicImage url="/img/alterar.gif" />
					</h:commandLink></td>
					<td width="2%"><h:commandLink title="Remover"
						action="#{tipoProduto.preRemover}" style="border: 0;">
						<f:param name="id" value="#{item.id}" />
						<h:graphicImage url="/img/delete.gif" />
					</h:commandLink></td>
				</tr>
			</c:forEach>

		</table>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
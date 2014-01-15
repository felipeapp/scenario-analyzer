<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Local de Aplicação de Prova</h2>

	<center><h:messages />
	<div class="infoAltRem"><h:graphicImage value="/img/alterar.gif"
		style="overflow: visible;" />: Alterar dados do Local de Aplicação de
	Prova <h:graphicImage value="/img/delete.gif"
		style="overflow: visible;" />: Remover Local de Aplicação de Prova <br />
	</div>
	</center>

	<h:form>
		<table class=listagem>
			<caption class="listagem">Lista de Local de Aplicação de
			Prova</caption>
			<thead>
				<tr>
					<th>Nome</th>
					<th>Municipio</th>
					<th colspan="2"></th>
				</tr>
			</thead>

			<c:forEach items="#{localAplicacaoProva.all}" var="item"
				varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${item.nome}</td>
					<td>${item.endereco.municipio.nome}</td>
					<td width="2%"><h:commandLink title="Alterar"
						action="#{localAplicacaoProva.atualizar}" style="border: 0;">
						<f:param name="id" value="#{item.id}" />
						<h:graphicImage url="/img/alterar.gif" />
					</h:commandLink></td>
					<td width="2%"><h:commandLink title="Remover"
						action="#{localAplicacaoProva.preRemover}" style="border: 0;">
						<f:param name="id" value="#{item.id}" />
						<h:graphicImage url="/img/delete.gif" />
					</h:commandLink></td>
				</tr>
			</c:forEach>

		</table>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
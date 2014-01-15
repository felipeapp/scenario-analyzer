<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Língua Estrangeira</h2>

	<h:outputText value="#{regiaoPreferencialProva.create}" />

	<center><h:messages />
	</center>

	<h:form>
		<table class=listagem>
			<caption class="listagem">Lista das Línguas Estrangeiras Cadastradas</caption>
			<thead>
				<tr>
					<th>Descrição</th>
					<th colspan="2"></th>
				</tr>
			</thead>
			<c:forEach items="#{linguaEstrangeira.all}" var="item"
				varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${item.denominacao}</td>
					<td width="5%"></td>
				</tr>
			</c:forEach>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
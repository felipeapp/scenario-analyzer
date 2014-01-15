<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Agência Financiadora</h2>
	<br>
	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>: <h:commandLink action="#{entidadeFinanciadora.preCadastrar}" value="Cadastrar Agência Financiadora"></h:commandLink>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Agência Financiadora
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Agência Financiadora<br/>
		</div>
	</h:form>
	<h:outputText value="#{entidadeFinanciadora.create}" />
	<table class=listagem>
		<caption class="listagem">Lista de Agências Financiadoras</caption>
		<thead>

			<td>Nome</td>

			<td></td>
			<td></td>
		</thead>
		<c:forEach items="${entidadeFinanciadora.all}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.nome}</td>

				<h:form>
					<td width=20><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/alterar.gif" value="Alterar"
						action="#{entidadeFinanciadora.atualizar}" /></td>
				</h:form>
				<h:form>
					<td width=25><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/delete.gif" alt="Remover"
						action="#{entidadeFinanciadora.remover}" /></td>
				</h:form>
			</tr>
		</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

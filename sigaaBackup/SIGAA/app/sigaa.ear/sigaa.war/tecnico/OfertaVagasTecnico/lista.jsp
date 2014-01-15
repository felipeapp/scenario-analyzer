<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Oferta de Vagas</h2>

	<center><h:messages />
	<div class="infoAltRem">
		<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Oferta de Vagas
		<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Oferta de Vagas <br />
	</div>
	</center>

	<h:form>
		<table class=listagem>
			<caption class="listagem">Lista de Ofertas de Vagas</caption>
			<thead>
				<tr>
					<th>Nome</th>
					<th>Pólo</th>
					<th>Grupo</th>
					<th>Quantidade</th>
					<th>Reserva</th>
					<th colspan="2"></th>
				</tr>
			</thead>
			<c:forEach items="#{ofertaVagasTecnico.all}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${item.nome}</td>
					<td>${item.polo.cidade.nome}</td>
					<td>${item.grupoString}</td>
					<td>${item.quantidadeVagas}</td>
					<td>${item.reservaVagas == true ? 'Sim' : 'Não'}</td>
					<td width="2%">
						<h:commandLink title="Alterar" action="#{ofertaVagasTecnico.atualizar}" style="border: 0;">
							<f:param name="id" value="#{item.id}" />
							<h:graphicImage url="/img/alterar.gif" />
						</h:commandLink>
					</td>
					<td width="2%">
						<h:commandLink title="Remover" action="#{ofertaVagasTecnico.prepareRemover}" style="border: 0;">
							<f:param name="id" value="#{item.id}" />
							<h:graphicImage url="/img/delete.gif" />
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
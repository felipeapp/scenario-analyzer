<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Região Preferencial de Prova</h2>

	<h:outputText value="#{regiaoPreferencialProva.create}" />

	<center><h:messages />
	<div class="infoAltRem"><h:graphicImage value="/img/alterar.gif"
		style="overflow: visible;" />: Alterar dados da Região Preferencial
	de Prova <h:graphicImage value="/img/delete.gif"
		style="overflow: visible;" />: Remover Região Preferencial de Prova <br />
	</div>
	</center>

	<h:form>
		<table class=listagem>
			<caption class="listagem">Lista das Regiões Preferenciais
			de Prova</caption>
			<thead>
				<tr>
					<th>Descrição</th>
					<td></td>
					<td></td>
				</tr>
			</thead>
			<c:forEach items="#{regiaoPreferencialProva.all}" var="item"
				varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${item.denominacao}</td>
					<td width="3%"><h:commandLink title="Alterar"
							action="#{regiaoPreferencialProva.atualizar}" style="border: 0;">
							<f:param name="id" value="#{item.id}" />
							<h:graphicImage url="/img/alterar.gif" />
						</h:commandLink> 
					</td>
					<td width="3%"><h:commandLink title="Remover"
							action="#{regiaoPreferencialProva.preRemover}" style="border: 0;">
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
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Área de Conhecimento</h2>

	<h:outputText value="#{areaConhecimentoVestibular.create}" />

	<center><h:messages />
	<div class="infoAltRem"><h:graphicImage value="/img/alterar.gif"
		style="overflow: visible;" />: Alterar dados da Área de Conhecimento
	<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />:
	Remover Área de Conhecimento <br />
	</div>
	</center>

	<h:form>
		<table class=listagem>
			<caption class="listagem">Lista de Áreas de Conhecimento</caption>
			<thead>
				<tr>
					<th>Descrição</th>
					<th colspan="2"></th>
				</tr>
			</thead>
			<c:forEach items="#{areaConhecimentoVestibular.all}" var="item"
				varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${item.descricao}</td>
					<td width="2%"><h:commandLink title="Alterar"
						action="#{areaConhecimentoVestibular.atualizar}"
						style="border: 0;">
						<f:param name="id" value="#{item.id}" />
						<h:graphicImage url="/img/alterar.gif" />
					</h:commandLink></td>
					<td width="2%"><h:commandLink title="Remover"
						action="#{areaConhecimentoVestibular.preRemover}"
						style="border: 0;">
						<f:param name="id" value="#{item.id}" />
						<h:graphicImage url="/img/delete.gif" />
					</h:commandLink></td>
				</tr>
			</c:forEach>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
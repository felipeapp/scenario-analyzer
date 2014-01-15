<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Processo Seletivo</h2>

	<center><h:messages />
	<div class="infoAltRem">
		<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Processo Seletivo
		<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Processo Seletivo <br />
	</div>
	</center>

	<h:form>
		<table class=listagem>
			<caption class="listagem">Lista de Processos Seletivos</caption>
			<thead>
				<tr>
					<th>Nome</th>
					<th>Forma de Ingresso</th>
					<th colspan="2"></th>
				</tr>
			</thead>
			<c:forEach items="#{processoSeletivoTecnico.all}" var="item"
				varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${item.nome}</td>
					<td>${item.formaIngresso.descricao}</td>
					<td width="2%">
						<h:commandLink title="Alterar" action="#{processoSeletivoTecnico.atualizar}" style="border: 0;">
							<f:param name="id" value="#{item.id}" />
							<h:graphicImage url="/img/alterar.gif" />
						</h:commandLink>
					</td>
					<td width="2%">
						<h:commandLink title="Remover" action="#{processoSeletivoTecnico.prepareRemover}" style="border: 0;">
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
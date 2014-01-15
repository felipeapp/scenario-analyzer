<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Lista de Índices Acadêmicos</h2>
	<div class="infoAltRem" style="width: 100%"><img
		src="/shared/img/adicionar.gif" style="overflow: visible;" />: <a
		href="${ctx}/ensino/IndiceAcademico/form.jsf">Cadastrar Novo
	Índice Acadêmico</a> <img src="/shared/img/alterar.gif"
		style="overflow: visible;" />: Alterar <img
		src="/shared/img/delete.gif" style="overflow: visible;" />: Remover</div>
	<h:form>
		<c:if test="${ not empty indiceAcademicoMBean.all }">
			<table class="listagem">
				<caption>Lista de Índices Acadêmicos</caption>
				<thead>
					<tr>
						<th>Nome</th>
						<th>Sigla</th>
						<th>Ordem</th>
						<th>Histórico</th>
						<th>Classe</th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<c:forEach items="#{indiceAcademicoMBean.all}" var="item"
					varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${item.nome}</td>
						<td>${item.sigla}</td>
						<td>${item.ordem}</td>
						<td>${item.exibidoHistorico ? 'Sim' : 'Não' }</td>
						<td>${item.classe}</td>
						<td><h:commandLink
							action="#{ indiceAcademicoMBean.atualizar }">
							<f:verbatim>
								<img src="/shared/img/alterar.gif" alt="Alterar" title="Alterar" />
							</f:verbatim>
							<f:param name="id" value="#{ item.id }" />
						</h:commandLink></td>
						<td><h:commandLink
							action="#{ indiceAcademicoMBean.inativar }"
							onclick="#{confirmDelete}">
							<f:verbatim>
								<img src="/shared/img/delete.gif" alt="Remover" title="Remover" />
							</f:verbatim>
							<f:param name="id" value="#{ item.id }" />
						</h:commandLink></td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

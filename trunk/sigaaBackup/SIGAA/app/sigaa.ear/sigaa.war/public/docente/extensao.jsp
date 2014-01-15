<%@include file="/public/docente/cabecalho.jsp"%>
<f:view>
	<h:form id="formExtensaoDocente">

		<div id="id-docente">
		<h3>${fn:toLowerCase(docente.nome)}</h3>
		<p class="departamento">${docente.unidade.siglaAcademica} -
		${docente.unidade.nome}</p>
		</div>

		<div id="atividade-docente">
		<h4>Atividades de Extensão que Coordena</h4>

		<c:set var="projetos" value="#{portal.projetosExtensaoCoordena}" /> <c:if
			test="${not empty projetos}">
			<table class="listagem">
				<thead>
					<tr>
						<td width="6%">Código</td>
						<td width="80%">Título</td>
						<td>Detalhes</td>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="item" items="#{portal.projetosExtensaoCoordena}"
						varStatus="loop">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td>${item.codigo}</td>
							<td>${item.titulo}</td>
							<td align="center"><h:commandLink
								title="Visualizar Ação de Extensão"
								action="#{ inscricaoParticipanteAtividadeMBean.visualizarDadosCursoEvento }" immediate="true">
								<f:param name="idAtividadeExtensaoSelecionada" value="#{item.id}" />
								<h:graphicImage url="/img/view.gif" />
							</h:commandLink></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if> <c:if test="${empty projetos}">
			<p class="vazio">Nenhum projeto de pesquisa cadastrado</p>
		</c:if></div>

		<div id="atividade-docente">
		<h4>Atividades de Extensão das quais Participo</h4>

		<c:set var="projetos" value="${portal.projetosExtensaoParticipa}" />
		<c:if test="${not empty projetos}">
			<table class="listagem">
				<thead>
					<tr>
						<td width="6%">Código</td>
						<td width="80%">Título</td>
						<td>Detalhes</td>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="item" items="#{portal.projetosExtensaoParticipa}"
						varStatus="loop">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td>${item.codigo}</td>
							<td>${item.titulo}</td>
							<td align="center">
							<h:commandLink title="Visualizar Ação de Extensão"
								action="#{ inscricaoParticipanteAtividadeMBean.visualizarDadosCursoEvento }" immediate="true">
								<f:param name="idAtividadeExtensaoSelecionada" value="#{item.id}" />
								<h:graphicImage url="/img/view.gif" />
							</h:commandLink>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if> <c:if test="${empty projetos}">
			<p class="vazio">Nenhum projeto de extensão cadastrado</p>
		</c:if></div>

	</h:form>
</f:view>
<%@include file="/public/include/rodape.jsp"%>

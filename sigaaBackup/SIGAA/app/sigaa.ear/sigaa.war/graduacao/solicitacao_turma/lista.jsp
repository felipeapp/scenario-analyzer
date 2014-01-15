<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
<!--
/* Tabela com sugestão das disciplinas */
table.listagem  tr.periodo td{
	background: #C4D2EB;
	padding-left: 10px;
	font-weight: bold;
}
-->
</style>


<f:view>
	<%@include file="/graduacao/menu_coordenador.jsp" %>
	<h2>Consulta de Solicitação de Criação de Turma</h2>

	<div class="descricaoOperacao">
		Para buscar por solicitações de turmas, informe um ano-período regular. <b>O resultado da busca listará as turmas de férias do período automaticamente.</b>
	</div>

	<h:form id="busca">
	<table class="formulario" width="60%" align="center">
	<caption>Selecione o período que deseja visualizar as solicitações</caption>
		<tbody>
			<tr>
				<th>Ano-Período:</th>
				<td>
					<h:inputText value="#{solicitacaoTurma.ano}" size="4" maxlength="4" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);" id="ano"/>
					 - <h:inputText value="#{solicitacaoTurma.periodo}" size="1" maxlength="1" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);" id="periodo"/>
				</td>
			</tr>
		</tbody>
		<tfoot>
		<tr>
			<td colspan="2" align="center">
				<h:commandButton value="Filtrar Solicitações" action="#{solicitacaoTurma.listar}"/>
			</td>
		</tr>
		</tfoot>
	</table>
	</h:form>
	<br/>


	<c:if test="${empty solicitacaoTurma.solicitacoes}">
		<br><div style="font-style: italic; text-align:center">Nenhum registro encontrado de acordo com os critérios de busca informados.</div>
	</c:if>
	<c:if test="${not empty solicitacaoTurma.solicitacoes}">
		<h:form id="resultado">
		<center>
		<div class="infoAltRem">
			<c:if test="${!solicitacaoTurma.exibirApenasView}">
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar dados da solicitação
			</c:if>
			<c:if test="${!solicitacaoTurma.exibirApenasView}">
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Solicitação
			</c:if>
			<h:graphicImage value="/img/icones/page_white_magnify.png" style="overflow: visible;" />: Visualizar Solicitação<br />
		</div>
		</center>

		<table class=listagem>
			<caption class="listagem">Lista de Solicitações</caption>
			<thead>
				<tr>
					<td>Ano-Período</td>
					<td>Componente</td>
					<td>Tipo</td>
					<td>Situação</td>
					<td>Horário</td>
					<td style="text-align: right;">Vagas</td>
					<td></td>
					
				</tr>
			</thead>
			<c:set var="depAtual" value="0"/>
			<c:forEach items="#{solicitacaoTurma.solicitacoes}" var="item" varStatus="status">
			
				<c:if test="${ depAtual != item.componenteCurricular.unidade.id}">
					<c:set var="depAtual" value="${item.componenteCurricular.unidade.id}" />
					<tr class="periodo"><td colspan="9">
						${item.componenteCurricular.unidade.sigla} - ${item.componenteCurricular.unidade.nome} 
					</td></tr>
				</c:if>
			
				<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${item.ano}-${item.periodo}</td>
					<td>${item.componenteCurricular.descricaoResumida}</td>
					<td>${item.tipoString}</td>
					<td>${item.situacaoString}</td>
					<td>${item.horario}</td>
					<td style="text-align: right;">${item.vagas}</td>
					<td nowrap="nowrap">
						<h:commandLink rendered="#{item.podeAlterar}" action="#{solicitacaoTurma.atualizar}">
							<h:graphicImage url="/img/alterar.gif" title="Alterar dados da solicitação"/>
							<f:param name="id" value="#{item.id}" />
						</h:commandLink>
						<h:commandLink rendered="#{!solicitacaoTurma.exibirApenasView and item.podeRemover}" action="#{solicitacaoTurma.preRemover}">
							<h:graphicImage url="/img/delete.gif" title="Remover Solicitação"/>
							<f:param name="id" value="#{item.id}" />
						</h:commandLink>
						<h:commandLink action="#{solicitacaoTurma.view}">
							<h:graphicImage url="/img/icones/page_white_magnify.png" title="Visualizar Solicitação" />
							<f:param name="id" value="#{item.id}" />
							<f:param name="isListaSolicitacao" value="true" />
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
		</table>
		</h:form>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

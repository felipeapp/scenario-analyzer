<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<f:view>
<a4j:keepAlive beanName="situacaoAvaliacaoBean" />
<h:form prependId="false">

	<h2><ufrn:subSistema /> &gt; Situação Avaliação</h2>
	<center>
		<h:messages />
		<div class="infoAltRem">
				<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" />
					<h:commandLink value="Cadastrar Situação" action="#{ situacaoAvaliacaoBean.preCadastrar }" id="linkCadastrar"/>
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Tipo de Situação
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Tipo de Situação				
		</div>
	</center>
	<c:if test="${ not empty situacaoAvaliacaoBean.tiposSituacao }">
		<table class="listagem">
			<caption>Lista de Tipos de Avaliações</caption>
			<thead>
				<tr>
					<td>Descrição</td>
					<td></td>
					<td></td>
				</tr>
			</thead>
				<c:forEach items="#{ situacaoAvaliacaoBean.tiposSituacao }" var="item" varStatus="status">
					<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td>${ item.descricao }</td>
						<td width=20>
							<h:commandLink title="Alterar Tipo de Situação" action="#{ situacaoAvaliacaoBean.atualizar }" id="linkAlterar">
								<f:param name="id" value="#{ item.id }" />
								<h:graphicImage url="/img/alterar.gif" />
							</h:commandLink>
						</td>
						<td width=20>
							<h:commandLink title="Remover Tipo de Situação" action="#{ situacaoAvaliacaoBean.inativar }" id="linkRemover" onclick="return confirm('Deseja realmente remover este tipo de situação?');">
								<f:param name="id" value="#{ item.id }"/>
								<h:graphicImage url="/img/delete.gif" />
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
		</table>
	</c:if>
	<c:if test="${ empty situacaoAvaliacaoBean.tiposSituacao }">
		<center>Nenhum tipo de avaliação encontrado.</center>
	</c:if>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>
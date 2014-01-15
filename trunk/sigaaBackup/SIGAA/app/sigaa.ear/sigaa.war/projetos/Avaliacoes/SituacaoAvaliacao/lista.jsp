<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<f:view>
<a4j:keepAlive beanName="situacaoAvaliacaoBean" />
<h:form prependId="false">

	<h2><ufrn:subSistema /> &gt; Situa��o Avalia��o</h2>
	<center>
		<h:messages />
		<div class="infoAltRem">
				<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" />
					<h:commandLink value="Cadastrar Situa��o" action="#{ situacaoAvaliacaoBean.preCadastrar }" id="linkCadastrar"/>
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Tipo de Situa��o
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Tipo de Situa��o				
		</div>
	</center>
	<c:if test="${ not empty situacaoAvaliacaoBean.tiposSituacao }">
		<table class="listagem">
			<caption>Lista de Tipos de Avalia��es</caption>
			<thead>
				<tr>
					<td>Descri��o</td>
					<td></td>
					<td></td>
				</tr>
			</thead>
				<c:forEach items="#{ situacaoAvaliacaoBean.tiposSituacao }" var="item" varStatus="status">
					<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td>${ item.descricao }</td>
						<td width=20>
							<h:commandLink title="Alterar Tipo de Situa��o" action="#{ situacaoAvaliacaoBean.atualizar }" id="linkAlterar">
								<f:param name="id" value="#{ item.id }" />
								<h:graphicImage url="/img/alterar.gif" />
							</h:commandLink>
						</td>
						<td width=20>
							<h:commandLink title="Remover Tipo de Situa��o" action="#{ situacaoAvaliacaoBean.inativar }" id="linkRemover" onclick="return confirm('Deseja realmente remover este tipo de situa��o?');">
								<f:param name="id" value="#{ item.id }"/>
								<h:graphicImage url="/img/delete.gif" />
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
		</table>
	</c:if>
	<c:if test="${ empty situacaoAvaliacaoBean.tiposSituacao }">
		<center>Nenhum tipo de avalia��o encontrado.</center>
	</c:if>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>
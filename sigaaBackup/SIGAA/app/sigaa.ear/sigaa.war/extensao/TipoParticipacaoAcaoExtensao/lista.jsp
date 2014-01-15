<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="tipoParticipacaoAcaoExtensao"></a4j:keepAlive>
<h:form>
	<h2><ufrn:subSistema /> > Tipos de Participa��o A��o de Extens�o</h2>
	<div class="infoAltRem" style="width: 100%">
		<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" />
		<h:commandLink action="#{tipoParticipacaoAcaoExtensao.preCadastrar}" value="Cadastrar" />
		<img src="/shared/img/alterar.gif" style="overflow: visible;" />: Alterar 
		<img src="/shared/img/delete.gif" style="overflow: visible;" />: Remover
	</div>
	
		<c:if test="${ not empty tipoParticipacaoAcaoExtensao.all }">
			<table class="listagem">
				<caption class="listagem">Tipos de Participa��o A��o e Extens�o</caption>
				<thead>
					<tr>
						<th>Descri��o</th>
						<th>Tipo de A��o Extens�o</th>
						<th colspan="2"></th>
					</tr>
				</thead>
				<c:forEach items="#{tipoParticipacaoAcaoExtensao.allObj}" var="item"		varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${item.descricao}</td>
						<td>${item.tipoAcaoExtensao}</td>
						<td width="4%" colspan="2" nowrap="nowrap">
						<h:commandLink
							action="#{ tipoParticipacaoAcaoExtensao.atualizar }" rendered="#{! item.tipoParticipacaoAcaoExtensaoFixaNoSistema}">
							<f:verbatim>
								<img src="/shared/img/alterar.gif" alt="Alterar" title="Alterar" />
							</f:verbatim>
							<f:param name="id" value="#{ item.id }" />
						</h:commandLink>
						<h:commandLink
							action="#{ tipoParticipacaoAcaoExtensao.inativar }" rendered="#{! item.tipoParticipacaoAcaoExtensaoFixaNoSistema}"
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

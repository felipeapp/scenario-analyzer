<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="analiseManifestacaoDiscente" />

<f:view>
	<h:form id="form">
	<h2>
		<ufrn:subSistema /> &gt; Detalhes da Manifestação
	</h2>
	
	<table class="formulario" width="100%">
	<caption>Detalhes da Manifestação</caption>
	<tbody>
	<tr><td>
		<c:set var="manifestacao" value="#{analiseManifestacaoDiscente.obj }" scope="page" />
		<c:set var="historicos" value="#{analiseManifestacaoDiscente.historicos }" scope="page" />
		<c:set var="copias" value="#{analiseManifestacaoDiscente.copias }" scope="page" />
		<c:set var="delegacoes" value="#{analiseManifestacaoDiscente.delegacoes }" scope="page" />
		<%@ include file="/ouvidoria/Manifestacao/include/dados_detalhes_manifestacao.jsp" %>
		
		</td></tr>
		</tbody>
		<c:if test="${analiseManifestacaoDiscente.operacaoVisualizarBusca }">
			<tfoot>
				<tr>
					<td>
						<h:commandButton value="<< Voltar" action="#{analiseManifestacaoDiscente.listarManifestacoes }" />
					</td>
				</tr>
			</tfoot>
		</c:if>
	</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
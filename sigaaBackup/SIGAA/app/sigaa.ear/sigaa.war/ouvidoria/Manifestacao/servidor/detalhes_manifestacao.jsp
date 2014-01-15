<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="analiseManifestacaoServidor" />

<f:view>
	<c:set var="hideSubsistema" value="true" />

	<h:form id="form">
	<h2>
		Detalhes da Manifesta��o
	</h2>
	
	<table class="formulario" width="100%">
	<caption>Detalhes da Manifesta��o</caption>
	<tbody>
	<tr><td>

		<c:set var="manifestacao" value="#{analiseManifestacaoServidor.obj }" scope="page" />
		<c:set var="historicos" value="#{analiseManifestacaoServidor.historicos }" scope="page" />
		<c:set var="copias" value="#{analiseManifestacaoServidor.copias }" scope="page" />
		<c:set var="delegacoes" value="#{analiseManifestacaoServidor.delegacoes }" scope="page" />
		<%@ include file="/ouvidoria/Manifestacao/include/dados_detalhes_manifestacao.jsp" %>

		</td></tr>
		</tbody>
		<c:if test="${analiseManifestacaoServidor.operacaoVisualizarBusca }">
			<tfoot>
				<tr>
					<td>
						<h:commandButton value="<< Voltar" action="#{analiseManifestacaoServidor.listarManifestacoes }" />
					</td>
				</tr>
			</tfoot>
		</c:if>
	</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
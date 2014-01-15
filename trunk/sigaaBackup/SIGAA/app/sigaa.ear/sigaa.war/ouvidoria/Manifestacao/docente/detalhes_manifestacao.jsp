<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="analiseManifestacaoDocente" />

<f:view>
	<h:form id="form">
	<h2>
		<ufrn:subSistema /> &gt; Detalhes da Manifestação
	</h2>
	
	<table class="formulario" width="100%">
	<caption>Detalhes da Manifestação</caption>
	<tbody>
	<tr><td>
		<c:set var="manifestacao" value="#{analiseManifestacaoDocente.obj }" scope="page" />
		<c:set var="historicos" value="#{analiseManifestacaoDocente.historicos }" scope="page" />
		<c:set var="copias" value="#{analiseManifestacaoDocente.copias }" scope="page" />
		<c:set var="delegacoes" value="#{analiseManifestacaoDocente.delegacoes }" scope="page" />
		<%@ include file="/ouvidoria/Manifestacao/include/dados_detalhes_manifestacao.jsp" %>
		
		</td></tr>
		</tbody>
		<c:if test="${analiseManifestacaoDocente.operacaoVisualizarBusca }">
			<tfoot>
				<tr>
					<td>
						<h:commandButton value="<< Voltar" action="#{analiseManifestacaoDocente.listarManifestacoes }" />
					</td>
				</tr>
			</tfoot>
		</c:if>
	</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
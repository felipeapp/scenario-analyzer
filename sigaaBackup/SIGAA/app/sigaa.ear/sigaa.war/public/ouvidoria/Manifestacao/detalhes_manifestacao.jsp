<%@ include file="../../include/cabecalho.jsp" %>
<a4j:keepAlive beanName="esclarecimentoOuvidoria" />

<script type="text/javascript" src="/shared/javascript/consulta_cep.js"></script>

<f:view>
	<h:form id="form">
	<h2>
		Ouvidoria > Detalhes da Manifestação
	</h2>
	
	<table class="formulario" width="100%">
	<caption>Detalhes da Manifestação</caption>
	<tbody>
	<tr><td>
		<c:set var="manifestacao" value="#{esclarecimentoOuvidoria.obj }" scope="page" />
		<c:set var="historicos" value="#{esclarecimentoOuvidoria.historicos }" scope="page" />
		<c:set var="copias" value="#{esclarecimentoOuvidoria.copias }" scope="page" />
		<c:set var="delegacoes" value="#{esclarecimentoOuvidoria.delegacoes }" scope="page" />
		<%@ include file="/ouvidoria/Manifestacao/include/dados_detalhes_manifestacao.jsp" %>
		
		</td></tr>
		</tbody>
	</table>
	</h:form>
	<%@include file="/public/include/voltar.jsp"%>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
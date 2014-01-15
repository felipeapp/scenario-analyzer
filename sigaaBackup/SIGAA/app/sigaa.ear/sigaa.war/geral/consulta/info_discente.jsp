<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2 class="title">Consulta de Discente</h2>
<h:form id="infoDiscente">
<c:set value="#{consultaDiscente.obj}" var="discente" />
<%@ include file="/graduacao/info_discente.jsp"%>
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
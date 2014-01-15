<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@taglib uri="/tags/primefaces-p" prefix="p"%>
<jwr:style src="/css/portal_familiar.css" media="all" />
<style>
#cboxLoadedContent iframe {
	background-color:#FFFFFF;
}
</style>
<c:set var="hideSubsistema" value="true" />

<f:view>

<p:resources />

<link rel="stylesheet" type="text/css" href="/sigaa/primefaces_resource/1.1/skins/sam/skin.css" /><link rel="stylesheet" type="text/css" href="/sigaa/primefaces_resource/1.1/jquery/plugins/ui/jquery.ui.dialog.css" />
<script type="text/javascript" src="/sigaa/primefaces_resource/1.1/primefaces/core/core.js"></script>
<script type="text/javascript" src="/sigaa/primefaces_resource/1.1/primefaces/dialog/dialog.js"></script>
<script type="text/javascript" src="/sigaa/primefaces_resource/1.1/primefaces/button/button.js"></script>
<link rel="stylesheet" type="text/css" href="/sigaa/ava/primefaces/redmond/skin.css" />

<h:form id="form">
	<h2><ufrn:subSistema /></h2>
		
	<c:if test="${ acesso.portalFamiliar }">
	
		<div id="conteudo">
		
			<%@include file="include/menu_familiar.jsp" %>
			
		</div>
		
	</c:if>
</h:form>
</f:view>

<div class="linkRodape">
    <html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
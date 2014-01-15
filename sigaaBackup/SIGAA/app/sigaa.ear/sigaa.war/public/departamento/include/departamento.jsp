<div id="colDirTop">
	<h1>${portalPublicoDepartamento.unidade.sigla}</h1>
	<span class="flags" >
		<h:form id="formIdioma">
	<%--
		<c:if test="${portalPublicoDepartamento.lc != 'pt_BR' && portalPublicoDepartamento.lc != 'pt'}">
			<a href="?lc=pt_BR&id=${portalPublicoDepartamento.unidade.id}"><h:graphicImage url="../images/pt-BR.png" title="Versão Português" /></a>
		</c:if>
		<c:if test="${portalPublicoDepartamento.lc != 'en_US' && portalPublicoDepartamento.lc != 'en'}">
			<a href="?lc=en_US&id=${portalPublicoDepartamento.unidade.id}"><h:graphicImage url="../images/en-US.png" title="Versão Português" /></a>
		</c:if>
		<c:if test="${portalPublicoDepartamento.lc != 'fr_FR' && portalPublicoDepartamento.lc != 'fr'}">
			<a href="?lc=fr_FR&id=${portalPublicoDepartamento.unidade.id}"><h:graphicImage url="../images/fr-FR.png" title="Versão Português" /></a>
		</c:if>
		<c:if test="${portalPublicoDepartamento.lc!='es_ES' && portalPublicoDepartamento.lc != 'es'}">
			<a href="?lc=es_ES&id=${portalPublicoDepartamento.unidade.id}"><h:graphicImage url="../images/es-ES.png" title="Versão Português" /></a>
		</c:if>
	--%>	
		</h:form>
 	
	</span>
	<h2>${portalPublicoDepartamento.unidade.nome}</h2>
	<a href="/sigaa/public/centro/portal.jsf?lc=pt_BR&id=${portalPublicoDepartamento.unidade.gestora.id}"
	title="Clique para acessar o portal do centro.">
	${portalPublicoDepartamento.unidade.gestora.nome}
	</a>
	
</div>
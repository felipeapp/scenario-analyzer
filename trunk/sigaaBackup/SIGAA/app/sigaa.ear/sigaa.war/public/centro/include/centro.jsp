<div id="colDirTop">
	<h1>${portalPublicoCentro.unidade.sigla} (${portalPublicoCentro.unidade.municipio.nome}) </h1>
	<span class="flags" >
	<%--
	<h:form id="formIdioma">
		<c:if test="${portalPublicoCentro.lc != 'pt_BR' && portalPublicoCentro.lc != 'pt'}">
			<a href="?lc=pt_BR&id=${portalPublicoCentro.unidade.id}"><h:graphicImage url="../images/pt-BR.png" title="Vers�o Portugu�s" /></a>
		</c:if>
		<c:if test="${portalPublicoCentro.lc != 'en_US' && portalPublicoCentro.lc != 'en'}">
			<a href="?lc=en_US&id=${portalPublicoCentro.unidade.id}"><h:graphicImage url="../images/en-US.png" title="Vers�o Ingl�s" /></a>
		</c:if>
		<c:if test="${portalPublicoCentro.lc != 'fr_FR' && portalPublicoCentro.lc != 'fr'}">
			<a href="?lc=fr_FR&id=${portalPublicoCentro.unidade.id}"><h:graphicImage url="../images/fr-FR.png" title="Vers�o Franc�s" /></a>
		</c:if>
		<c:if test="${portalPublicoCentro.lc!='es_ES' && portalPublicoCentro.lc != 'es'}">
			<a href="?lc=es_ES&id=${portalPublicoCentro.unidade.id}"><h:graphicImage url="../images/es-ES.png" title="Vers�o Espanhol" /></a>
		</c:if>
	</h:form>
	 --%>
	</span>
	<h2>${portalPublicoCentro.unidade.nome}</h2>
</div>
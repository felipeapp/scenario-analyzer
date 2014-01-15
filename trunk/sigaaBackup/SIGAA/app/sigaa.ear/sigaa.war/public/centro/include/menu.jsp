<div id="logo">
		<p>
			<span>
			<c:if test="${not empty portalPublicoCentro.detalhesSite.idLogo}">
			<a href="portal.jsf?lc=${portalPublicoCentro.lc}&id=${portalPublicoCentro.unidade.id}">
			<img src="${ctx}/verFoto?idFoto=${portalPublicoCentro.detalhesSite.idLogo}&key=${ sf:generateArquivoKey(portalPublicoCentro.detalhesSite.idLogo) }">
			</a>
			</c:if>
			<c:if test="${empty portalPublicoCentro.detalhesSite.idLogo}">
			<img src="../images/departamento/brasao_ufrn.png">
			</c:if>
			</span>
		</p>
	</div>
	<br clear="all">
	
<h:form id="formMenuCentro">
	<h:messages showDetail="true"/>
	<div id="menu-pri">
	<ul>
		<li><a href="./portal.jsf?lc=${portalPublicoCentro.lc}&id=${portalPublicoCentro.unidade.id}" class="apresentacao"><h:outputText value="#{idioma.apresentacao}"/> </a></li>
		<li><a href="./lista_departamentos.jsf?lc=${portalPublicoCentro.lc}&id=${portalPublicoCentro.unidade.id}" class="departamento"><h:outputText value="#{idioma.departamentos}"/></a></li>
		<li><a href="./lista_cursos.jsf?lc=${portalPublicoCentro.lc}&id=${portalPublicoCentro.unidade.id}" class="graduacao"><h:outputText value="#{idioma.graduacao}"/></a></li>
		<li><a href="./lista_programas.jsf?lc=${portalPublicoCentro.lc}&id=${portalPublicoCentro.unidade.id}" class="programa"><h:outputText value="#{idioma.posGraduacao}"/></a></li>
		<li><a href="./bases_pesquisa.jsf?lc=${portalPublicoCentro.lc}&id=${portalPublicoCentro.unidade.id}" class="basesPesquisa"><h:outputText value="#{idioma.basesPesquisa}"/></a></li>
		<li><a href="./documentos.jsf?lc=${portalPublicoCentro.lc}&id=${portalPublicoCentro.unidade.id}" class="doc"><h:outputText value="#{idioma.documentos}"/></a></li>
		<c:if test="${not empty programa.siteExtra}">
		<li><a href="${programa.siteExtra}" class="oficial"><h:outputText value="#{idioma.siteAlternativo}"/></a></li>
		</c:if>
	</ul>		
	</div>
	
	<c:set var="secao" value="#{portalPublicoCentro.secaoExtra}"/>

		<div id="menu-opcoes">
		
		<ul>
			<li class="titulo"><h:outputText	value="#{idioma.outrasOpcoes}" /></li>
				<li><a href="/sigaa/" class="especial"><h:outputText value="#{idioma.linkSigaa}"/> </a></li>
				<c:if test="${not empty secao}">
					<c:forEach var="secaoExtra"
						items="#{secao}" varStatus="loop">
						<li>
						<c:if test="${not empty secaoExtra.linkExterno}">
							<a href="http://${secaoExtra.linkExterno}" target="_blank">${secaoExtra.titulo}</a>
						</c:if>
						 <c:if test="${empty secaoExtra.linkExterno}">
							<a href="secao_extra.jsf?lc=${portalPublicoCentro.lc}&id=${portalPublicoCentro.unidade.id}&extra=${secaoExtra.id}">
							${secaoExtra.titulo}
							</a>
						</c:if>
					</li>
					</c:forEach>
				</c:if>
			<li class="pe"><a href=""></a></li>
		</ul>
		</div>

	<c:set var="noticiaSiteDest" value="#{portalPublicoCentro.noticiaSiteDestaques}"/>
	<c:if test="${not empty noticiaSiteDest}">
		<div id="menu-noticias">
			<ul>
				<li class="titulo"><h:outputText value="#{idioma.noticias}"/></li>
					<h:form id="formNoticia">
					<c:forEach var="noticiaDest" items="#{noticiaSiteDest}" varStatus="loop">
						<li>
						<a href="noticias_desc.jsf?lc=${portalPublicoCentro.lc}&id=${portalPublicoCentro.unidade.id}&noticia=${noticiaDest.id}">
						${noticiaDest.tituloResumido}
						</a>
					</li>
					</c:forEach>
					</h:form>
					<c:if test="${not empty noticiaSiteDest}">
					<li class="aba"><a style="overflow: hidden;" href="noticias.jsf?lc=${portalPublicoCentro.lc}&id=${portalPublicoCentro.unidade.id}"></a></li>
					</c:if>
			</ul>	
		</div>
	</c:if>

	<div id="home-link">
		<h:commandLink title="Ir para o Portal Público do SIGAA"
			action="#{portalPublicoCentro.cancelar}">
			<h:outputText	value="#{idioma.irMenuPrincipal}" />
		</h:commandLink>
	</div>
</h:form>
<br clear="all"/>
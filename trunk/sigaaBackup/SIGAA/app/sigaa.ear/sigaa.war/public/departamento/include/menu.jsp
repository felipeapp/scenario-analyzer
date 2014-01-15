	<div id="logo">
				<p>
					<span>
						<a href="portal.jsf?id=${portalPublicoDepartamento.unidade.id}">
						<c:if test="${not empty portalPublicoDepartamento.detalhesSite.idLogo}">
						<img src="${ctx}/verFoto?idFoto=${portalPublicoDepartamento.detalhesSite.idLogo}&key=${ sf:generateArquivoKey(portalPublicoDepartamento.detalhesSite.idLogo) }" border="0">
						</c:if>
						<c:if test="${empty portalPublicoDepartamento.detalhesSite.idLogo}">
						<img src="../images/departamento/brasao_ufrn.png" border="0">
						</c:if>
						</a>
					</span>
				</p>
	</div>
	<br clear="all"/>
	<h:form id="formMenuDepartamento">
<div id="menu-pri">
	<ul>
		<c:if test="${not empty portalPublicoDepartamento.detalhesSite.url}">
			<li><a href="${portalPublicoDepartamento.detalhesSite.url}" class="oficial" target="_blank">Página Oficial</a></li>
		</c:if>
		<li><a href="${ctx}/public/departamento/portal.jsf?id=${portalPublicoDepartamento.unidade.id}" class="apresentacao">Apresentação</a></li>
		<li><a href="${ctx}/public/departamento/administrativo.jsf?id=${portalPublicoDepartamento.unidade.id}" class="adm">Corpo Administrativo</a></li>
		<li><a href="${ctx}/public/departamento/professores.jsf?id=${portalPublicoDepartamento.unidade.id}" class="prof">Corpo Docente</a></li>
		<li><a href="${ctx}/public/departamento/componentes.jsf?id=${portalPublicoDepartamento.unidade.id}" class="curriculo">Componentes Curriculares</a></li>
		<li><a href="${ctx}/public/departamento/extensao.jsf?id=${portalPublicoDepartamento.unidade.id}" class="extensao">Ações de Extensão</a></li>
		<li><a href="${ctx}/public/departamento/pesquisa.jsf?id=${portalPublicoDepartamento.unidade.id}" class="pesq">Projetos de Pesquisa</a></li>
		<li><a href="${ctx}/public/departamento/monitoria.jsf?id=${portalPublicoDepartamento.unidade.id}" class="monitoria">Projetos de Monitoria</a></li>
		<li><a href="${ctx}/public/departamento/documentos.jsf?lc=${portalPublicoCurso.lc}&id=${portalPublicoDepartamento.unidade.id}" class="doc"><h:outputText value="#{idioma.documentos}"/></a></li>
		<c:if test="${not empty portalPublicoDepartamento.detalhesSite.siteExtra && portalPublicoDepartamento.detalhesSite.siteExtra!='http://'}">
		<li><a href="${portalPublicoDepartamento.detalhesSite.siteExtra}" class="oficial"><h:outputText value="#{idioma.siteAlternativo}"/></a></li>
		</c:if>
	</ul>		
	</div>
	<c:set var="secao" value="#{portalPublicoDepartamento.secaoExtra}"/>

		<div id="menu-opcoes">
		<ul>
			<li class="titulo"><h:outputText value="#{idioma.outrasOpcoes}" /></li>
			<li><a href="/sigaa/" class="especial"><h:outputText value="#{idioma.linkSigaa}"/></a></li>
				<c:if test="${not empty secao}">
					<c:forEach var="secaoExtra"
						items="#{secao}" varStatus="loop">
						<li>
						<c:if test="${not empty secaoExtra.linkExterno}">
							<a href="http://${secaoExtra.linkExterno}" target="_blank">${secaoExtra.titulo}</a>
						</c:if>
						 <c:if test="${empty secaoExtra.linkExterno}">
							<a href="${ctx}/public/departamento/secao_extra.jsf?lc=${portalPublicoDepartamento.lc}&id=${portalPublicoDepartamento.unidade.id}&extra=${secaoExtra.id}">
							${secaoExtra.titulo}
							</a>
						</c:if>
					</li>
					</c:forEach>
				</c:if>
			<li class="pe"></li>
		</ul>
		</div>

	<c:set var="noticiaSite" value="#{portalPublicoDepartamento.noticiaSiteDestaques}"/>
	<c:if test="${not empty noticiaSite}">
		<div id="menu-noticias">
		<h:form id="formNoticia">
			<ul>
					<li class="titulo"><h:outputText value="#{idioma.noticias}"/></li>
					<c:forEach var="noticia" items="#{noticiaSite}" varStatus="loop">
					<li>
						<a href="${ctx}/public/departamento/noticias_desc.jsf?lc=${portalPublicoDepartamento.lc}&id=${portalPublicoDepartamento.unidade.id}&noticia=${noticia.id}">
						${noticia.tituloResumido}
						</a>
					</li>
					</c:forEach>
					<c:if test="${not empty noticiaSite}">
					<li class="aba"><a href="${ctx}/public/departamento/noticias.jsf?lc=${portalPublicoDepartamento.lc}&id=${portalPublicoDepartamento.unidade.id}"></a></li>
					</c:if>
			</ul>	
		</h:form>
		</div>
	</c:if>

	<div id="home-link">
		<h:commandLink title="Ir para o Portal Público do SIGAA"
			action="#{portalPublicoDepartamento.cancelar}">
			<h:outputText	value="#{idioma.irMenuPrincipal}" />
		</h:commandLink>
	</div>
	
</h:form>
<br clear="all"/>
	
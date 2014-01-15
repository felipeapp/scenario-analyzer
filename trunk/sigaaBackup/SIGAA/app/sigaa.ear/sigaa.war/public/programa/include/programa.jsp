<%-- topo ufrn --%>
<div id="topo">
	<div id="sigaa">
    	<a href="${ configSistema['linkSigaa']}" target="_blank" title="SIGAA">SIGAA - Sistema Integrado de Gestão de Atividades Acadêmicas</a>
    </div>
	<div id="instituicao">
    	<a href="${portalPublicoPrograma.urlInstituicao}" target="_blank" 
    		title="<h:outputText value="#{idioma.acessarPagina}"/> ${ configSistema['siglaInstituicao'] }">
    		<img src="${portalPublicoPrograma.logoInstituicaoPrograma}" />
    	</a>
    </div>
	<div id="idiomas">
		<c:if test="${portalPublicoPrograma.lc != 'pt_BR' && portalPublicoPrograma.lc != 'pt'}">
			<a href="?lc=pt_BR&id=${portalPublicoPrograma.unidade.id}"><img src="${ctx}/public/programa/img/idioma_portugues.png" title="<h:outputText value="#{idioma.versaoPortugues}"/>" /></a>
		</c:if>
		<c:if test="${portalPublicoPrograma.lc != 'en_US' && portalPublicoPrograma.lc != 'en'}">
			<a href="?lc=en_US&id=${portalPublicoPrograma.unidade.id}"><img src="${ctx}/public/programa/img/idioma_ingles.png" title="<h:outputText value="#{idioma.versaoIngles}"/>" /></a>
		</c:if>
    	<%--<a href="" title="Español"><img src="img/idioma_espanhol.png" /></a> --%>
	</div>
</div>
				
<%-- inicio site --%>
<div id="site">
	<div id="margem">
		<div id="corpo">
			<div id="topo_site">
				<div id="logo">
		    		<c:if test="${not empty portalPublicoPrograma.detalhesSite.idLogo}">
						<a href="portal.jsf?${portalPublicoPrograma.parametroURL}">
							<img src="${ctx}/verFoto?idFoto=${portalPublicoPrograma.detalhesSite.idLogo}&key=${ sf:generateArquivoKey(portalPublicoPrograma.detalhesSite.idLogo) }">
						</a>
					</c:if>
					<c:if test="${empty portalPublicoPrograma.detalhesSite.idLogo}">
						<a href="portal.jsf?${portalPublicoPrograma.parametroURL}">
							<img src="${ configSistema['brasaoInstituicao'] }">
						</a>
					</c:if>
			    </div>
			  	<div id="nomes">
			         
			          <span class="sigla">
			          	 ${portalPublicoPrograma.unidade.sigla}
			          </span>
			         
			          <span class="nome_programa">
						<a href="portal.jsf?${portalPublicoPrograma.parametroURL}">
							<h:outputText value="#{idioma.programa}"/> ${portalPublicoPrograma.unidade.nome}
						</a>
			          </span>
			          
			          <span class="nome_centro">
			          		<a href="/sigaa/public/centro/portal.jsf?lc=pt_BR&id=${portalPublicoPrograma.unidade.gestora.id}"
							title="${idioma.acessarPagina}" class="nomeCentro">
								${portalPublicoPrograma.unidade.gestora.nome}
							</a>
			          </span>
					  
					  <span class="telefone_programa">
						 	<h:outputText styleClass="telefoneRamal" value="#{idioma.telefoneRamal}: " /> 
							<c:if test="${not empty portalPublicoPrograma.coordenador.telefoneContato1}">
								${portalPublicoPrograma.coordenador.telefoneContato1}<c:if test="${not empty portalPublicoPrograma.coordenador.ramalTelefone1}">/${portalPublicoPrograma.coordenador.ramalTelefone1}</c:if>
							</c:if>
							<c:if test="${empty portalPublicoPrograma.coordenador.telefoneContato1 && empty portalPublicoPrograma.coordenador.telefoneContato2}">
								<h:outputText value="#{idioma.naoInformado}"/>
							</c:if>
					  </span>
						
			          <c:if test="${not empty portalPublicoPrograma.urlOficial}" >
							<span class="url_programa">
								<a href="">${portalPublicoPrograma.urlOficial}</a>
							</span>
					  </c:if>
					  
			      </div>
			</div>
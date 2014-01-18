<%-- topo ufrn --%>
<div id="topo">
	<div id="sigaa">
    	<a href="${ configSistema['linkSigaa']}" target="_blank" title="SIGAA">SIGAA - Sistema Integrado de Gestão de Atividades Acadêmicas</a>
    </div>
	<div id="instituicao">
    	<a href="${portalPublicoCurso.urlInstituicao}" target="_blank" 
    		title="<h:outputText value="#{idioma.acessarPagina}"/> ${ configSistema['siglaInstituicao'] }">
    		<img src="${portalPublicoCurso.logoInstituicaoCurso}" />
    	</a>
    </div>
	<div id="idiomas">
		<c:if test="${portalPublicoCurso.lc != 'pt_BR' && portalPublicoCurso.lc != 'pt'}">
			<a href="?lc=pt_BR&id=${portalPublicoCurso.curso.id}"><img src="${ctx}/public/curso/img/idioma_portugues.png" title="<h:outputText value="#{idioma.versaoPortugues}"/>" /></a>
		</c:if>
		<c:if test="${portalPublicoCurso.lc != 'en_US' && portalPublicoCurso.lc != 'en'}">
			<a href="?lc=en_US&id=${portalPublicoCurso.curso.id}"><img src="${ctx}/public/curso/img/idioma_ingles.png" title="<h:outputText value="#{idioma.versaoIngles}"/>" /></a>
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
		    		<c:if test="${not empty portalPublicoCurso.detalhesSite.idLogo}">
						<a href="${ctx}/public/curso/portal.jsf?${portalPublicoCurso.parametroURL}">
							<img src="${ctx}/verFoto?idFoto=${portalPublicoCurso.detalhesSite.idLogo}&key=${ sf:generateArquivoKey(portalPublicoCurso.detalhesSite.idLogo) }">
						</a>
					</c:if>
					<c:if test="${empty portalPublicoCurso.detalhesSite.idLogo}">
						<a href="${ctx}/public/curso/portal.jsf?${portalPublicoCurso.parametroURL}">
							<img src="${ configSistema['brasaoInstituicao'] }">
						</a>
					</c:if>
			    </div>
			  	<div id="nomes">
			         
			          <span class="nome_curso">
			          	<c:choose>
							<c:when test="${portalPublicoCurso.curso.tecnico || portalPublicoCurso.curso.lato}">
								<a href="#">
								${portalPublicoCurso.nomeCurso} / ${portalPublicoCurso.curso.unidade.sigla}
								</a>
							</c:when>
							<c:otherwise>
								<a href="/sigaa/public/centro/portal.jsf?lc=pt_BR
								&id=${portalPublicoCurso.curso.unidade.gestora.id}"
								title="Clique para acessar o portal do centro.">
								${portalPublicoCurso.nomeCurso} / ${portalPublicoCurso.curso.unidade.sigla}
								</a>
							</c:otherwise>	
						</c:choose>	
			          </span>
						          
			          <span class="nome_centro">
			          	<c:choose>
			          		<c:when test="${portalPublicoCurso.curso.tecnico || portalPublicoCurso.curso.lato}">
								<a href="#">
								${portalPublicoCurso.curso.unidade.nome} - ${portalPublicoCurso.curso.unidade.sigla}
								</a>
							</c:when>
							<c:otherwise>
								<a href="/sigaa/public/centro/portal.jsf?lc=pt_BR
								&id=${portalPublicoCurso.curso.unidade.gestora.id}"
								title="Clique para acessar o portal do centro.">
								${portalPublicoCurso.curso.unidade.nome} - ${portalPublicoCurso.curso.unidade.sigla}
								</a>
							</c:otherwise>
						</c:choose>	
			          </span>
					  
					  <span class="telefone">
						 	<h:outputText styleClass="telefoneRamal" value="#{idioma.telefoneRamal}: " /> 
							<label>
							<c:if test="${not empty portalPublicoCurso.coordenador.telefoneContato1}">
								${portalPublicoCurso.coordenador.telefoneContato1}<c:if test="${not empty portalPublicoCurso.coordenador.ramalTelefone1}">/${portalPublicoCurso.coordenador.ramalTelefone1}</c:if>
							</c:if>
							<c:if test="${not empty portalPublicoCurso.coordenador.telefoneContato1 && not empty portalPublicoCurso.coordenador.telefoneContato2 }">
								 | 
							</c:if>
							<c:if test="${not empty portalPublicoCurso.coordenador.telefoneContato2}">
								${portalPublicoCurso.coordenador.telefoneContato2}<c:if test="${not empty portalPublicoCurso.coordenador.ramalTelefone2}">/${portalPublicoCurso.coordenador.ramalTelefone2}</c:if>
							</c:if>
							<c:if test="${empty portalPublicoCurso.coordenador.telefoneContato1 && empty portalPublicoCurso.coordenador.telefoneContato2}">
								<h:outputText value="#{idioma.naoInformado}"/>
							</c:if>
							</label>
					  </span>
						
			          <c:if test="${not empty portalPublicoCurso.urlOficial}" >
							<span class="url_programa">
								<a href="">${portalPublicoCurso.urlOficial}</a>
							</span>
					  </c:if>
					  
			      </div>
			</div>












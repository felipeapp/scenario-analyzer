<%-- Cabeçalho --%>
<%@ include file="include/cabecalho.jsp" %>

<f:view locale="#{portalPublicoPrograma.lc}">
<a4j:keepAlive beanName="portalPublicoPrograma"/>
<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>

<%-- topo --%>
<%@ include file="include/programa.jsp" %>
<%-- menu flutuante --%>
<%@ include file="include/menu.jsp" %>
							
<%-- conteudo --%>
<div id="conteudo">
	<div class="titulo"><h:outputText value="#{idioma.apresentacao}"/></div>
  	<div class="texto">
        <c:if test="${not empty portalPublicoPrograma.detalhesSite.idFoto}">
			<img class="foto" src="${ctx}/verFoto?idFoto=${portalPublicoPrograma.detalhesSite.idFoto}&key=${ sf:generateArquivoKey(portalPublicoPrograma.detalhesSite.idFoto) }">
		</c:if>
      	<h:outputText value="#{portalPublicoPrograma.introducaoLocale}" escape="false" rendered="#{portalPublicoPrograma.introducaoLocale != null}" />
      	<h:outputText value="#{idioma.vazio}" rendered="#{portalPublicoPrograma.introducaoLocale == null}" />  
    </div>
    
    <c:if test="${not empty portalPublicoPrograma.detalhesSite.siteExtra}">
		<br clear="all"/>
		<div class="titulo"><h:outputText value="#{idioma.siteAlternativo}"/></div>
		<br clear="all"/>
 	  	<div class="texto_afastado">
	 	 	<ul id="listagem">
				<li class="primeiro">		 	
				- <a target="_blank" href="${portalPublicoPrograma.detalhesSite.siteExtra}">
					<h:outputText value="#{portalPublicoPrograma.detalhesSite.siteExtra}"/>
				</a>
				</li>
			</ul>
		</div>
		<br clear="all"/>
	</c:if>	

 	<div class="titulo"><h:outputText value="#{idioma.coordenacaoPrograma}"/></div>
 	<br clear="all"/>
    <div class="texto_afastado">
       	<ul id="listagem">
          	<c:if test="${not empty portalPublicoPrograma.coordenadorPrograma}">
       		   	<c:forEach var="coord"  items="#{portalPublicoPrograma.coordenadorPrograma}" varStatus="i"  >
		           	<li class="primeiro">
		               	- <a title="Clique aqui para acessar a página pública do Coordenador"
							href="${ctx}/public/docente/portal.jsf?siape=${coord.servidor.siape}">
							<h:outputText value="#{coord.servidor.pessoa.nome}"/>
						  </a>
		                <span class="dados">
		               		<p>
		               			<h:outputText value="#{idioma.telefone}" />:
		               			<span class="cor">
			               			<h:outputText value="#{coord.telefoneContato1}" rendered="#{not empty coord.telefoneContato1}" /><h:outputText value="/#{coord.ramalTelefone1}" rendered="#{not empty coord.telefoneContato1 && not empty coord.ramalTelefone1}" />
			               			<h:outputText value="#{idioma.vazio}" rendered="#{empty coord.telefoneContato1}" />
		               			</span>
		               		</p>
		               		<p>	
		               			<h:outputText value="#{idioma.telefone2}" />:
		               			<span class="cor">
			               			<h:outputText value="#{coord.telefoneContato2}" rendered="#{not empty coord.telefoneContato2}" /><h:outputText value="/#{coord.ramalTelefone2}" rendered="#{not empty coord.telefoneContato2 && not empty coord.ramalTelefone2}" />
			               			<h:outputText value="#{idioma.vazio}" rendered="#{empty coord.telefoneContato2}" />
		               			</span>
		               		</p>
		               		<p>
		               			 <h:outputText value="#{idioma.email}" />:
		               			 <span class="cor">
		               			 	<h:outputText value="#{coord.emailContato}"/>
									<h:outputText value="#{idioma.vazio}" rendered="#{empty coord.emailContato}" />
		               			 </span>
		               		</p>
		                </span>
					</li>
			        <li></li>
				</c:forEach>
			</c:if>
		</ul>
	</div>		
</div>
</f:view>
<%@ include file="./include/rodape.jsp" %>
<%-- Cabe�alho --%>
<%@ include file="include/cabecalho.jsp" %>

<f:view locale="#{portalPublicoPrograma.lc}">
<a4j:keepAlive beanName="portalPublicoCurso"/>
<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>

<%-- topo --%>
<%@ include file="include/curso.jsp" %>
<%-- menu flutuante --%>
<%@ include file="include/menu.jsp" %>
	 
<%-- conteudo --%>
<div id="conteudo">
	
	<div class="titulo"><h:outputText value="#{idioma.projetoPoliticoPedagogico}"/></div>
	
	<br clear="all"/>
	<div class="texto">
		<c:if test="${!portalPublicoCurso.curso.tecnico}">
			<!-- Inicio Perfil Profissional -->
		
				 <span class="topico2">
				 		<label><h:outputText value="#{idioma.perfilProfissional}"/>:</label>
						${portalPublicoCurso.curso.perfilProfissional}
						<h:outputText value="#{idioma.vazio}"
						 rendered="#{empty portalPublicoCurso.curso.perfilProfissional}" />
				 </span>
			<!-- Fim Perfil Profissional -->

		</c:if>
		
		<!-- Inicio �rea de Atua��o -->
		<c:if test="${!portalPublicoCurso.curso.tecnico}">
				 <span class="topico2">
				 	<label>
						<h:outputText value="#{idioma.areaAtuacao}"/>:
					</label>
						${portalPublicoCurso.curso.campoAtuacao}
						<h:outputText value="#{idioma.vazio}"
						 rendered="#{empty portalPublicoCurso.curso.campoAtuacao}" />
				</span>
		</c:if>		
		<!-- Fim �rea de Atua��o -->
		
		<!-- Inicio Compet�ncias e Habilidades do Profissional: -->
		<c:if test="${!portalPublicoCurso.curso.tecnico}">
				<span class="topico2">
				 	<label><h:outputText value="#{idioma.competenciaHabilidadeProfissional}"/>:</label>
						${portalPublicoCurso.curso.competenciasHabilidades}
						<h:outputText value="#{idioma.vazio}"
						 rendered="#{empty portalPublicoCurso.curso.competenciasHabilidades}" />
				</span>	
		</c:if>		
		<!-- Fim Compet�ncias e Habilidades do Profissional: -->
		
		<!-- Inicio Metodologia: -->
		<c:if test="${!portalPublicoCurso.curso.tecnico}">
				<span class="topico2">
				 	<label><h:outputText value="#{idioma.metodologia}"/>:</label>
						${portalPublicoCurso.curso.metodologia}
						<h:outputText value="#{idioma.vazio}"
						 rendered="#{empty portalPublicoCurso.curso.metodologia}" />
				</span>	
		</c:if>		
		<!-- Fim Metodologia: -->
		
		<!-- Inicio Sistema de Gest�o do Curso -->
		<c:if test="${!portalPublicoCurso.curso.tecnico}">
				<span class="topico2">
				 	<label><h:outputText value="#{idioma.sistemasGestaoCurso}"/>:</label>
						${portalPublicoCurso.curso.gestaoCurso}
						<h:outputText value="#{idioma.vazio}"
						 rendered="#{empty portalPublicoCurso.curso.gestaoCurso}" />
				</span>	
		</c:if>		
		<!-- Fim Sistema de Gest�o do Curso -->
		
		<!-- Inicio Avalia��o do Curso -->
		<c:if test="${!portalPublicoCurso.curso.tecnico}">
				<span class="topico2">
				 	<label><h:outputText value="#{idioma.avaliacaoCurso}"/>:</label>
						${portalPublicoCurso.curso.avaliacaoCurso}
						<h:outputText value="#{idioma.vazio}"
						 rendered="#{empty portalPublicoCurso.curso.avaliacaoCurso}" />
				</span>	
		</c:if>		
		<!-- Fim Avalia��o do Curso -->
		
		<!-- Inicio Arquivo Projeto Pol�tico Pedag�gico  -->
		<c:if test="${!portalPublicoCurso.curso.tecnico}">
			<span class="topico2">
				 	<label><h:outputText value="#{idioma.projetoPoliticoPedagogico}"/>:</label>
				
					<c:choose>
						<c:when test="${portalPublicoCurso.idArquivo > 0}">
							<a class="download"
								href="${ctx}/verProducao?idProducao=${portalPublicoCurso.idArquivo}&&key=${ sf:generateArquivoKey(portalPublicoCurso.idArquivo) }"
								target="_blank"> <h:outputText value="#{idioma.downloadArquivo}"/> </a>
						</c:when>
						
						<c:otherwise>
							<i> <h:outputText value="#{idioma.vazio}"/> </i>
						</c:otherwise>
					</c:choose>
				
			</span>
		</c:if>		
		<!-- Fim Arquivo Projeto Pol�tico Pedag�gico -->

	<!--  FIM CONTE�DO  -->	
	</div>
</div>
	</f:view>
	<%@ include file="./include/rodape.jsp" %>
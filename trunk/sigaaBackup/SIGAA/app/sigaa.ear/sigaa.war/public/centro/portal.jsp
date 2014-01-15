	<%@ include file="./include/cabecalho.jsp" %>
	<f:view locale="#{portalPublicoCentro.lc}">
	<a4j:keepAlive beanName="portalPublicoCentro"/>
	<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>	

	<div id="colEsq">
		<%@ include file="./include/menu.jsp" %>
	</div>

	<div id="colDir">
		<%@ include file="./include/centro.jsp" %>
		<div id="colDirCorpo">
		<!-- INÍCIO CONTEÚDO -->

		<!-- Início dos Destaques -->
				<%@ include file="./include/noticias_capa.jsp" %>	
		<!-- Fim dos Destaques -->	


		<!-- Início Apresentação -->	
			<br clear="all">
			
			<div id="titulo">
				<h1>
					<h:outputText value="#{idioma.apresentacao}" /> do Centro
				</h1>
			</div>
			
			<c:choose>
				<c:when test="${not empty portalPublicoCentro.introducaoLocale}">
					<dl class="apresentacao">	
						<dd>${portalPublicoCentro.introducaoLocale}</dd>
					</dl>
					<br clear="all"/>	
				</c:when>
				<c:otherwise>
					<p class="vazio">
						<h:outputText value="#{idioma.vazio}" />
					</p>
				</c:otherwise>
			</c:choose>
			
			<dl>

				<dt> Diretoria do Centro:</dt>
				<c:choose>
					<c:when test="${not empty portalPublicoCentro.diretoresCentro}">
						<c:forEach var="diretor"  items="#{portalPublicoCentro.diretoresCentro}" varStatus="i"  >
							<dd>
								<a title="Clique aqui para acessar a página pública dos
								Diretores do Centro" href="${ctx}/public/docente/portal.jsf?siape=
								${diretor.siape}">
								<h:outputText  value="#{diretor.pessoa.nome}"/>		
								</a>
							</dd>	
						</c:forEach>
					</c:when>
					<c:otherwise>
						<dd>Não informado</dd>
					</c:otherwise>
				</c:choose>
				
				<dt> Telefone(s):</dt>
				<dd>
				 <h:outputText value="#{portalPublicoCentro.unidade.telefone}"/>
				 <c:if test="${empty portalPublicoCentro.unidade.telefone}">
					 Não informado
				 </c:if>
				<!--
				 &nbsp; 
				 <a href="${configSistema['linkSipac']}/public/lista_linhas_telefonicas.jsf">
				 (Outros telefones clique para visualizar) 
				 </a>
				 -->
				</dd> 	
				
				<dt> Endereço Alternativo:</dt>
				<dd>
				<c:choose>
					<c:when test="${not empty portalPublicoCentro.detalhesSite.siteExtra
					&& portalPublicoCentro.detalhesSite.siteExtra != 'http://'}">
						<a title="Clique aqui para acessar o enderço alternativo" target="_blank" href="${portalPublicoCentro.detalhesSite.siteExtra}">
						<h:outputText value="#{portalPublicoCentro.detalhesSite.siteExtra}"/>
						</a>
					</c:when>
					<c:otherwise>
						Não informado
					</c:otherwise>
				</c:choose>
				</dd>
				 	
			</dl>
			

		<!-- Fim Apresentação -->	
	
	
		<!--  FIM CONTEÚDO -->	
		</div>
	</div>
	</br cler="all">
	</f:view>
	<%@ include file="../include/rodape.jsp" %>
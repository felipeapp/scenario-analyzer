	<%@ include file="./include/cabecalho.jsp" %>
	<f:view  locale="#{portalPublicoDepartamento.lc}">
	<a4j:keepAlive beanName="portalPublicoDepartamento"/>
	<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
	<div id="colEsq">
		<%@ include file="./include/menu.jsp" %>
	</div>
	<div id="colDir">
		<%@ include file="./include/departamento.jsp" %>
		<div id="colDirCorpo">
		<!-- INÍCIO CONTEÚDO -->
			<c:if test="${portalPublicoDepartamento.detalhesSite.idFoto != null}">
				<img src="${ctx}/verFoto?idFoto=${portalPublicoDepartamento.detalhesSite.idFoto}&key=${ sf:generateArquivoKey(portalPublicoDepartamento.detalhesSite.idFoto) }"/>
				<br clear="all"/>
				<br/>
			</c:if>
			
			<!-- Início dos Destaques -->		
				<h2 class="iconNoticiaDestaque">
					&nbsp;&nbsp;&nbsp;&nbsp;<h:outputText value="#{idioma.ultimasNoticias}"/>
				</h2>
				<table id="noticiasDestaque">
					
					<h:form id="formNoticia">
					<c:set var="noticias" value="#{portalPublicoDepartamento.noticiaSiteDestaques}"/>
					
					<c:if test="${not empty noticias}">
						<c:forEach var="noticia" items="#{noticias}" varStatus="loop">
							<tr>
								<td class="dataNoticia">
								<ufrn:format type="data" valor="${noticia.dataCadastro}"></ufrn:format>
								</td>
								<td width="87%" align="left">
								<a href="noticias_desc.jsf?lc=${portalPublicoDepartamento.lc}
								&id=${portalPublicoDepartamento.unidade.id}&noticia=${noticia.id}">
								<h:outputText value="#{noticia.tituloResumido}" />
								</a>
								</td>
							</tr>
						</c:forEach>
					</c:if>
					
					<c:if test="${empty noticias}">
						<tr>
							<td class="vazio" colspan="2">
							<h:outputText value="#{idioma.vazio}"/>
							</td>
						</tr>
					</c:if>
					
					<c:if test="${not empty noticias}">
					<tr>
						<td class="mais" colspan="2">
							<a href="noticias.jsf?lc=${portalPublicoDepartamento.lc}
							&id=${portalPublicoDepartamento.unidade.id}">
							</a>
						</td>
					</tr>
					</c:if>
					</h:form>
					
				</table>
				<br clear="all"/>
		<!-- Fim dos Destaques -->	



		<!-- Início Apresentação -->	
			
			<div id="titulo">
				<h1><h:outputText value="#{idioma.apresentacao}" /> do Departamento</h1>
			</div>
			
			<c:if test="${not empty portalPublicoDepartamento.introducaoLocale}">
				<dl class="apresentacao">	
					<dd>${portalPublicoDepartamento.introducaoLocale}</dd>
				</dl>	
			</c:if>
			<c:if test="${empty portalPublicoDepartamento.introducaoLocale}">
				<p class="vazio">
					<h:outputText value="#{idioma.vazio}" />
				</p>
			</c:if>
			
			<dl>
					
					<dt> Chefia do Departamento:</dt>
					<c:set value="#{portalPublicoDepartamento.chefesDepto}" var="chefes" />
					
					<c:choose>
						<c:when test="${not empty chefes}">
							<c:set var="chefeAnt" value=""/>
							<c:forEach var="chefe"  items="#{chefes}" varStatus="i"  >
								<c:if test="${chefeAnt!=chefe.siape}">
								<dd>
									<a title="Clique aqui para acessar a página pública dos Chefes
									 do Centro" href="${ctx}/public/docente/portal.jsf?siape=
									${chefe.siape}">
									<h:outputText value="#{chefe.pessoa.nome}"/>		
									</a>
								</dd>	
								</c:if>
								<c:set var="chefeAnt" value="${chefe.siape}"/>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<dd>Não informado</dd>
						</c:otherwise>
					</c:choose>
					
					<dt> Telefone(s):</dt>
					<dd>
						 <h:outputText value="#{portalPublicoDepartamento.unidade.telefone}"/>
						 <c:if test="${empty portalPublicoDepartamento.unidade.telefone}">
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
							<c:when test="${not empty portalPublicoDepartamento.detalhesSite.siteExtra
							&& portalPublicoDepartamento.detalhesSite.siteExtra != 'http://'}">
								<a title="Clique aqui para acessar o enderço alternativo" 
								target="_blank" href="${portalPublicoDepartamento.detalhesSite.siteExtra}">
								<h:outputText value="#{portalPublicoDepartamento.detalhesSite.siteExtra}"/>
								</a>
							</c:when>
							<c:otherwise>
								Não informado
							</c:otherwise>
						</c:choose>
					</dd> 	
			</dl>
			<br clear="all"/>
		<!-- Fim Apresentação -->	
			
		<!--  FIM CONTEÚDO -->	
			<center>
				<a href="javascript:void(0);" onClick="history.go(-1);"> <<	voltar </a>
			</center>	
		</div>
	</div>
	</f:view>
	<%@ include file="../include/rodape.jsp" %>
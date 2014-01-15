	<%@ include file="./include/cabecalho.jsp" %>
	<f:view locale="#{portalPublicoPrograma.lc}">
	<a4j:keepAlive beanName="portalPublicoPrograma"/>
	<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>	

	<div id="colEsq">
		<%@ include file="./include/menu.jsp" %>
	</div>

	<div id="colDir">
		<%@ include file="./include/programa.jsp" %>
		<div id="colDirCorpo">
		<%-- INÍCIO CONTEÚDO --%>
				<c:if test="${not empty portalPublicoPrograma.detalhesSite.idFoto}">
				<img src="${ctx}/verFoto?idFoto=${portalPublicoPrograma.detalhesSite.idFoto}&key=${ sf:generateArquivoKey(portalPublicoPrograma.detalhesSite.idFoto) }">
				<br clear="all"/>
				<br/>
				</c:if>
		
				<%-- Início dos Destaques --%>		
				<h1 class="iconNoticiaDestaque">
					<h:outputText value="#{idioma.ultimasNoticias}"/>
				</h1>
				
				<table id="noticiasDestaque">
					<h:form id="formNoticia">
					<c:set var="noticias" value="#{portalPublicoPrograma.noticiaSiteDestaques}"/>
					<c:if test="${not empty noticias}">
						<c:forEach var="noticia" items="#{noticias}" varStatus="loop">
							<tr>
								<td class="dataNoticia" align="left" title="Cadastrada em <ufrn:format type="data" valor="${noticia.dataCadastro}"/>">
								&nbsp;<a href="noticias_desc.jsf?lc=${portalPublicoPrograma.lc}&id=${portalPublicoPrograma.unidade.id}&noticia=${noticia.id}">
								<h:outputText value="#{noticia.tituloResumido}" />
								</a>
								</td>
							</tr>
						</c:forEach>
					</c:if>
					<c:if test="${empty noticias}">
						<tr>
							<td class="vazio">
								<h:outputText value="#{idioma.vazio}"/>
							</td>
						</tr>
					</c:if>
					<c:if test="${not empty noticias}">
					<tr>
						<td class="mais ${portalPublicoPrograma.lc}">
							<a href="noticias.jsf?lc=${portalPublicoPrograma.lc}&id=${portalPublicoPrograma.unidade.id}"></a></td>
						</tr>
					</c:if>
					</h:form>
				</table>
				<br clear="all"/>
				<%-- Fim dos Destaques --%>	

			<%-- Início Apresentação --%>	
				<br clear="all">
				<h1 class="apresentacao"><h:outputText value="#{idioma.apresentacao}" /></h1>
				
				<c:if test="${not empty portalPublicoPrograma.introducaoLocale}">
					<dl class="apresentacao">	
						<dd>
							${portalPublicoPrograma.introducaoLocale}
						</dd>
					</dl>	
				</c:if>
				<c:if test="${empty portalPublicoPrograma.introducaoLocale}">
					<p class="vazio">
						<h:outputText value="#{idioma.vazio}" />
					</p>
				</c:if>
			<%-- Fim Apresentação --%>	
	
		<%-- Início Coordenador e Vice --%>	
			<br clear="all"/>
		
			<c:set value="#{portalPublicoPrograma.coordenadorPrograma}" var="coords" />
			<c:choose>
				<c:when test="${not empty coords}">
				
					<c:forEach var="coord"  items="#{coords}" varStatus="i"  >
					
						<c:if test="${coord.ativo}">
						<dl>	
							<dt>
							<h:outputText value="#{idioma.coordenacaoPrograma}" rendered="#{coord.coordenador}"  />
							<h:outputText value="#{idioma.viceCoordenacaoPrograma}" rendered="#{!coord.coordenador}"  />:
							</dt>
							<dd>
								<a title="Clique aqui para acessar a página pública do Coordenador"
								 href="${ctx}/public/docente/portal.jsf?siape=
								${coord.servidor.siape}">
								<h:outputText value="#{coord.servidor.pessoa.nome}"/>
								</a>
								<br/><b><h:outputText value="#{idioma.telefone}" />:</b>
								<h:outputText value="#{coord.telefoneContato1}"/>
								<h:outputText value=" e #{coord.telefoneContato2}" rendered="#{not empty coord.telefoneContato2}" />
								<h:outputText value="#{idioma.vazio}" rendered="#{empty coord.telefoneContato1 && empty coord.telefoneContato2}" />
								<br/><b><h:outputText value="#{idioma.email}" />:</b> 
								<h:outputText value="#{coord.emailContato}"/>
								<h:outputText value="#{idioma.vazio}" rendered="#{empty coord.emailContato}" />
							</dd>
						</dl>		
						</c:if>
					
					</c:forEach>
					
				</c:when>
				<c:otherwise>
					<dl>	
						<dd><h:outputText value="#{idioma.vazio}"/></dd>
					</dl>	
				</c:otherwise>
			</c:choose>
			<br clear="all"/>
			
		<%-- Fim Coordenador e Vice --%>	

		<%-- Início Processos Seletivos --%>
			<h:outputText value="#{processoSeletivo.create}"/>					
				<dl id="processosSeletivos" >	
					<dt> <h:outputText value="#{idioma.processoSeletivo}" />:</dt>
					<dd>
				<c:if test="${not empty portalPublicoPrograma.processosSeletivos}">
					<h:form>
							<table class="listagem">
								<thead>
									<tr>	
										<th><h:outputText value="#{idioma.nivel}" /></th>
										<th class="periodo"><h:outputText value="#{idioma.periodo}" /></th>
										<th></th>
									</tr>										
								</thead>	
							<c:forEach items="#{portalPublicoPrograma.processosSeletivos}" var="prsl" varStatus="status">
								<tr>
									<td>
										${prsl.curso.tipoCursoStricto.descricao}
									</td>
									<td class="periodo">
										<ufrn:format type="data" valor="${prsl.editalProcessoSeletivo.inicioInscricoes}"></ufrn:format>
									 	- <ufrn:format type="data" valor="${prsl.editalProcessoSeletivo.fimInscricoes}"></ufrn:format>
									</td>
									<td align="right">
									<h:commandLink title="#{idioma.visualizarProcessoSeletivo}" 
										action="#{processoSeletivo.viewPublico}">
										<h:graphicImage url="/img/seta.gif" />
										<f:param name="id" value="#{prsl.id}" />
									</h:commandLink>
									</td>
								</tr>
							</c:forEach>
								<tfoot>
									<tr>
									<td colspan="3" align="center" class="infoAltRem" >
										<h:graphicImage url="/img/seta.gif" width="13px" height="13px" />
										:	<h:outputText value="#{idioma.visualizarProcessoSeletivo}" /> 
									</td>
									</tr>
								</tfoot>
						</table>
					</h:form>
				</c:if>
				<c:if test="${empty portalPublicoPrograma.processosSeletivos}">
						<p class="vazio"><h:outputText value="#{idioma.vazio}"/></p>
				</c:if>
				<dd>
				</dl>			

		<%-- Fim Processos Seletivos --%>
	
		<%--  FIM CONTEÚDO --%>
			<center>
				<a href="javascript:void(0);" onClick="history.go(-1);"> <<	<h:outputText value="#{idioma.voltar}" /> </a>
			</center>	
		</div>
	</div>
	</br cler="all">
	</f:view>
	<%@ include file="../include/rodape.jsp" %>
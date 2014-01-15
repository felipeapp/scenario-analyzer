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
	<div class="titulo"><h:outputText value="#{idioma.documentos}"/></div>
	
			<c:if test="${not empty portalPublicoPrograma.documentos}">
					
					<c:set var="tipoDocSite" value=""/>

						<c:forEach items="#{portalPublicoPrograma.documentos}" var="doc" varStatus="status">
							
							<c:if test="${tipoDocSite!=doc.tipoDocumentoSite.nome}">
								<c:if test="${!status.first}">
											</tbody>
											</table>
											
										</div>
									</c:if>
		
								<div id="listagem_tabela">
									<div id="group_lt">
										${portalPublicoPrograma.pt?(doc.tipoDocumentoSite.nome):(doc.tipoDocumentoSite.nomeEn)}
									</div>
									<table id="table_lt">
									<tbody>	
										<tr class="campos">
											<td><h:outputText value="#{idioma.nome}"/></td>
										</tr>
		
							</c:if>
									<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
										<td>
											<a class="cor" href="/sigaa/verProducao?idProducao=${doc.idArquivo}
											&key=${ sf:generateArquivoKey(doc.idArquivo) }" target="_blank">
											${doc.nome}
											</a>
										</td>
									</tr>	
								
								<c:set var="tipoDocSite" value="${doc.tipoDocumentoSite.nome}"/>
									
							</c:forEach>
						</tbody>
					</table>
					</div>
				</c:if>
				
				<c:if test="${empty portalPublicoPrograma.documentos}">
					<p class="vazio"><h:outputText value="#{idioma.vazio}" /></p><br clear="all"/>
				</c:if>
				
			<%--  FIM CONTEÚDO  --%>	
		</div>
</f:view>
<%-- Rodapé --%>
<%@ include file="./include/rodape.jsp" %>
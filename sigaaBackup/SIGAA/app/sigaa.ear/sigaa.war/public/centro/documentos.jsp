	<%@ include file="./include/cabecalho.jsp" %>
	<style>
	ul li{
		background: #C8D5EC;
		font-weight: bold;
	}
	</style>
	<f:view locale="#{portalPublicoCentro.lc}">
		<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
	<div id="colEsq">
		<%@ include file="./include/menu.jsp" %>
	</div>

	<div id="colDir">
		<%@ include file="./include/centro.jsp" %>
		<div id="colDirCorpo">
		<!--  INÍCIO CONTEÚDO -->
						
		<h1 class="documento"><h:outputText value="#{idioma.documentos}" /> </h1>
			<div id="documentos">
				<c:if test="${not empty portalPublicoCentro.documentos}">
					<div class="legenda">
							 <h:graphicImage value="../../../shared/img/icones/download.png" style="overflow: visible;" />
						: Baixar Arquivo
					</div>
					<br clear="all"/>
					
					<c:set var="tipoDocSite" value=""/>
					<table class="listagem" style="width: 98%;">
						
						<thead>
							<tr>
								<th>Nome do Arquivo</th>
								<th width="20px"></th>
							</tr>
						</thead>
						
						<tbody>
							<c:forEach items="#{portalPublicoCentro.documentos}" var="doc" 
							varStatus="status">
								<c:if test="${tipoDocSite!=doc.tipoDocumentoSite.nome}">
									<tr>
										<td colspan="2" class="subListagem">
											${doc.tipoDocumentoSite.nome}
										</td>
									</tr>
								</c:if>
								<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
									<td >
										${doc.nome}
									</td>
									<td width="20px" align="center">
										<a href="/sigaa/verProducao?idProducao=${doc.idArquivo}
										&key=${ sf:generateArquivoKey(doc.idArquivo) }" target="_blank">
											 <h:graphicImage value="../../../shared/img/icones/download.png" style="overflow: visible;" />
										</a>
									</td>
								</tr>	
								<c:set var="tipoDocSite" value="${doc.tipoDocumentoSite.nome}"/>
							</c:forEach>
						</tbody>
						
						<tfoot>
							<tr>
								<td colspan="2" align="center"><b>${fn:length(portalPublicoCentro.documentos)}</b> 
								Documento(s) encontrado(s)</td>
							</tr>
						</tfoot>
					</table>
				</c:if>
				
				<c:if test="${empty portalPublicoCentro.documentos}">
					<p class="vazio">	<h:outputText value="#{idioma.vazio}" /></p><br clear="all"/>
				</c:if>
				
			</div>
			<!--  FIM CONTEÚDO  -->	
		</div>
	</div>
	</f:view>
	<%@ include file="../include/rodape.jsp" %>
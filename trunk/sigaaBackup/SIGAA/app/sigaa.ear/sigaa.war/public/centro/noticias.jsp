	<%@ include file="./include/cabecalho.jsp" %>
	<f:view locale="#{portalPublicoPrograma.lc}">
		<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
	<div id="colEsq">
		<%@ include file="./include/menu.jsp" %>
	</div>
	<div id="colDir">
		<%@ include file="./include/centro.jsp" %>
		<div id="colDirCorpo">
		<!--  INÍCIO CONTEÚDO -->
			<h1><h:outputText value="#{idioma.noticias}"/></h1>
			
			<table class="listagem">
				<thead>
					<tr>
						<th>Data</th>
						<th>Título</th>
					</tr>
				</thead>
				
				<tbody>
				<c:set var="_noticias" value="${portalPublicoCentro.allNoticiaSite}"/>
				<c:forEach items="${_noticias}" var="noticia" varStatus="status">
					<tr  class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
						<td>
						<ufrn:format type="dataHora" valor="${noticia.dataCadastro}"></ufrn:format>
						</td>
						<td>
						<a href="noticias_desc.jsf?noticia=${noticia.id}">${noticia.titulo}</a>
						</td>
					</tr>
				</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="2" align="center">
						<b>${fn:length(_noticias)}</b> 
								Notícia(s) encontrada(s)
						</td>
					</tr>
				</tfoot>
			</table>
		<!--  FIM CONTEÚDO  -->	
		</div>
	</div>
	</f:view>
	<%@ include file="../include/rodape.jsp" %>
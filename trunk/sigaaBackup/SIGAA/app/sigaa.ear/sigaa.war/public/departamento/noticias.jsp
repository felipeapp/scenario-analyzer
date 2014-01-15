	<%@ include file="./include/cabecalho.jsp" %>
	<f:view  locale="#{portalPublicoDepartamento.lc}">
	<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
	<div id="colEsq">
		<%@ include file="./include/menu.jsp" %>
	</div>
	<div id="colDir">
		<%@ include file="./include/departamento.jsp" %>
		<div id="colDirCorpo">
		<!--  INÍCIO CONTEÚDO -->
			<h1><h:outputText value="#{idioma.noticias}"/></h1>
			<ul class="list-noticia">
			<c:forEach items="${portalPublicoDepartamento.allNoticiaSite}" var="noticia" varStatus="status">
			<li><a href="noticias_desc.jsf?noticia=${noticia.id}"><strong>
			<ufrn:format type="dataHora" valor="${noticia.dataCadastro}"></ufrn:format>
			</strong>&nbsp;::&nbsp;${noticia.titulo}</a></li>
			</c:forEach>
			</ul>
		<!--  FIM CONTEÚDO  -->	
		</table>
		</div>
	</div>
	</f:view>
	<%@ include file="../include/rodape.jsp" %>
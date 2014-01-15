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
	<div class="titulo">
		<h:outputText value="#{idioma.noticias}"/>
	</div>

	<br>

	<div class="texto_afastado">
   		<ul id="listagem">
			<c:forEach items="${portalPublicoPrograma.allNoticiaSite}" var="noticia" varStatus="status">
			<li class="primeiro">
				- <span class="data" title="${idioma.cadastraEm}">(<ufrn:format type="dataHora" valor="${noticia.dataCadastro}"/>)</span> <a href="noticias_desc.jsf?${portalPublicoPrograma.parametroURL}&noticia=${noticia.id}" class="cor">${noticia.titulo}</a>
			</li>
			</c:forEach>
		</ul>
	</div>
	
<%--  FIM CONTEÚDO  --%>	
</div>
</f:view>
<%@ include file="./include/rodape.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

	<!-- INÍCIO DAS NOTÍCIAS -->
	<c:set var="noticias" value="#{portalPublicoCentro.noticiaSiteDestaques}"/>
	<c:if test="${not empty noticias}">
		<h1>
			<h:outputText value="#{idioma.ultimasNoticias}"/>
		</h1>
		
		<ul id="noticiasPortal">
			<c:forEach  var="_noticia" items="#{noticias}" varStatus="i" end="2">
				<li class="${ i.first ? 'primeira' : ''} ${ fn:length(noticias)==2 ? 'segunda' : ''}" >
					<c:if test="${_noticia.idFoto > 0}">
						<img src="${ctx}/verFoto?idFoto=${_noticia.idFoto}&key=${ sf:generateArquivoKey(_noticia.idFoto) }" 
						align="left" class="pequena" />
					</c:if>
					<a href="noticias_desc.jsf?noticia=${_noticia.id}&id=${portalPublicoCentro.unidade.id}">
						<h:outputText  value="#{_noticia.titulo}" />
					</a>
					<p>
						<c:choose>
							<c:when test="${i.first}">
								<ufrn:format type="texto" valor="${_noticia.descricaoGrande}"/>
							</c:when>
							<c:otherwise>
								<h:outputText value="#{_noticia.descricaoMedia}" escape="false" />
							</c:otherwise>
						</c:choose>		
					</p>
					<br clear="all"/>
				</li>
			</c:forEach>
		</ul>	

     </c:if>
	<!-- FIM DAS NOTÍCIAS -->

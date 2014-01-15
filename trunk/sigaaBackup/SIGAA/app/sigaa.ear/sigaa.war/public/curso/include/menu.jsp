<div id="menu" class="menu">
  	<ul>
          <li class="li-menu primeiro">
           	<a class="item-menu"  title="${idioma.apresentacao}"  
           		href="${ctx}/public/curso/portal.jsf?${portalPublicoCurso.parametroURL}">
           			<h:outputText value="#{idioma.apresentacao}"/>
           	</a>
          </li>
          <li class="li-menu">
              <span class="item-menu" title="Ensino"><h:outputText value="#{idioma.ensino}"/></span>
              <div class="sub-menu">
                  <ul>
					<li class="primeiro"><a title="${idioma.alunosAtivos}" href="alunos.jsf?${portalPublicoCurso.parametroURL}">&middot; <h:outputText value="#{idioma.alunosAtivos}"/></a></li>
					<c:if test="${!portalPublicoCurso.curso.lato}" >
						<li><a href="curriculo.jsf?lc=${portalPublicoCurso.lc}&id=${portalPublicoCurso.curso.id}">&middot; <h:outputText value="#{idioma.curriculos}"/></a></li>
					</c:if>
					<li><a href="monografias.jsf?lc=${portalPublicoCurso.lc}&id=${portalPublicoCurso.curso.id}"">&middot; <h:outputText value="#{idioma.buscarMonografias}"/></a></li>
					<li><a title="${idioma.turmas}" href="turmas.jsf?${portalPublicoCurso.parametroURL}">&middot; <h:outputText value="#{idioma.turmas}"/></a></li>
                   </ul>
              </div>
          </li>
        <c:if test="${!portalPublicoCurso.curso.lato}">
		<li class="li-menu"><a class="item-menu"  title="${idioma.calendario}" href="calendario.jsf?${portalPublicoCurso.parametroURL}"><h:outputText value="#{idioma.calendario}"/></a></li>
		</c:if>
		<c:if test="${!portalPublicoCurso.curso.tecnico && !portalPublicoCurso.curso.lato}">
		<li class="li-menu"><a  class="item-menu"  href="ppp.jsf?lc=${portalPublicoCurso.lc}&id=${portalPublicoCurso.curso.id}" class="ppp"><h:outputText value="#{idioma.projetoPoliticoPedagogico}"/></a></li>
		</c:if>
        <li class="li-menu"><a class="item-menu"  title="${idioma.noticias}"  href="noticias.jsf?${portalPublicoCurso.parametroURL}"><h:outputText value="#{idioma.noticias}"/></a></li>
		       
        <c:set var="tiposDoc" value="#{portalPublicoCurso.tiposDocumentos}"/>
       	<c:if test="${not empty tiposDoc}">
        <li class="li-menu">
	         <span class="item-menu" title="${idioma.documentos}"><h:outputText value="#{idioma.documentos}" /></span>
              <div class="sub-menu">
	                  <ul>
	                      <c:forEach var="_tpDoc" items="#{tiposDoc}" varStatus="loop1">
							<li  class="${loop1.first?'primeiro':''}">
								<a href="documentos.jsf?${portalPublicoCurso.parametroURL}&idTipo=${_tpDoc.id}">&middot; ${portalPublicoCurso.pt?(_tpDoc.nome):(_tpDoc.nomeEn)}</a>
							</li>
							</c:forEach>
	                  </ul>
	         </div>
	    </li>
	    </c:if>  

		<c:if test="${ not empty portalPublicoCurso.secaoExtra }">
	       	<c:set var="secao" value="#{portalPublicoCurso.secaoExtra}"/>
	        <li class="li-menu">
		         <span class="item-menu" title="${idioma.outrasOpcoes}"><h:outputText value="#{idioma.outrasOpcoes}" /></span>
	              <div class="sub-menu">
	                  <ul>
	                      <c:forEach var="secaoExtra" items="#{secao}" varStatus="loop">
							<li  class="${loop.first?'primeiro':''}">
								<c:if test="${not empty secaoExtra.linkExterno}">
									<a href="${secaoExtra.linkExterno}" target="_blank">&middot; ${secaoExtra.titulo}</a>
								</c:if>
								 <c:if test="${empty secaoExtra.linkExterno}">
									<a href="secao_extra.jsf?${portalPublicoCurso.parametroURL}&extra=${secaoExtra.id}">
									&middot; ${secaoExtra.titulo}
									</a>
								</c:if>
							</li>
							</c:forEach>
	                  </ul>
		         </div>
		       </li>
	       </c:if>
      </ul>
</div>
<%@include file="/WEB-INF/jsp/include/erros.jsp"%>
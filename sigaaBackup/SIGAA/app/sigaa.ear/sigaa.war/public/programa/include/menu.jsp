<div id="menu" class="menu">
  	<ul>
          <li class="li-menu primeiro">
              <span class="item-menu" title="Programa"><h:outputText value="#{idioma.programa}"/></span>
              <div class="sub-menu">
                  <ul>
                      <li class="primeiro">
	                      <a title="${idioma.apresentacao}" href="apresentacao.jsf?${portalPublicoPrograma.parametroURL}">
	                      	&middot; <h:outputText value="#{idioma.apresentacao}"/>
	                      </a> 
					  </li>
					  <li>
					  	  <a title="${idioma.areaConcentracao}" href="areas.jsf?${portalPublicoPrograma.parametroURL}">
					  		&middot; <h:outputText value="#{idioma.areaConcentracao}"/>
					  	  </a>
					  </li>
					  <li><a title="${idioma.cursos}" href="cursos.jsf?${portalPublicoPrograma.parametroURL}">&middot; <h:outputText value="#{idioma.cursos}"/></a></li>
					  <li>
						  <a title="${idioma.gradeCurricular}" href="curriculo.jsf?${portalPublicoPrograma.parametroURL}">
						  	&middot; <h:outputText value="#{idioma.gradeCurricular}"/>
						  </a>
					  </li>
                  </ul>
              </div>
          </li>
          <li class="li-menu">
              <span class="item-menu" title="Ensino"><h:outputText value="#{idioma.ensino}"/></span>
              <div class="sub-menu">
                  <ul>
					<li class="primeiro"><a title="${idioma.alunosAtivos}" href="alunos.jsf?${portalPublicoPrograma.parametroURL}">&middot; <h:outputText value="#{idioma.alunosAtivos}"/></a></li>
					<li><a title="${idioma.corpoDocente}" href="equipe.jsf?${portalPublicoPrograma.parametroURL}">&middot; <h:outputText value="#{idioma.corpoDocente}"/></a></li>
					<li><a title="${idioma.cursos}" href="cursos.jsf?${portalPublicoPrograma.parametroURL}">&middot; <h:outputText value="#{idioma.cursos}"/></a></li>
					<li><a title="${idioma.dissertacoesTeses}" href="defesas.jsf?${portalPublicoPrograma.parametroURL}">&middot; <h:outputText value="#{idioma.dissertacoesTeses}"/></a></li>
					<li><a title="${idioma.turmas}" href="turma.jsf?${portalPublicoPrograma.parametroURL}">&middot; <h:outputText value="#{idioma.turmas}"/></a></li>
                   </ul>
              </div>
          </li>
		<li class="li-menu"><a class="item-menu"  title="${idioma.projetoPesquisa}" href="pesquisa.jsf?${portalPublicoPrograma.parametroURL}"><h:outputText value="#{idioma.projetoPesquisa}"/></a></li>
		<li class="li-menu"><a class="item-menu"  title="${idioma.calendario}" href="calendario.jsf?${portalPublicoPrograma.parametroURL}"><h:outputText value="#{idioma.calendario}"/></a></li>
		<li class="li-menu"><a class="item-menu"  title="${idioma.processoSeletivo}" href="processo_seletivo.jsf?${portalPublicoPrograma.parametroURL}"><h:outputText value="#{idioma.processoSeletivo}"/> </a></li>
        <li class="li-menu"><a class="item-menu"  title="${idioma.noticias}"  href="noticias.jsf?${portalPublicoPrograma.parametroURL}"><h:outputText value="#{idioma.noticias}"/></a></li>
        
        <c:set var="tiposDoc" value="#{portalPublicoPrograma.tiposDocumentos}"/>
       	<c:if test="${not empty tiposDoc}">
        <li class="li-menu">
	         <span class="item-menu" title="${idioma.documentos}"><h:outputText value="#{idioma.documentos}" /></span>
              <div class="sub-menu">
	                  <ul>
	                      <c:forEach var="_tpDoc" items="#{tiposDoc}" varStatus="loop1">
							<li  class="${loop1.first?'primeiro':''}">
								<a href="documentos.jsf?${portalPublicoPrograma.parametroURL}&idTipo=${_tpDoc.id}">&middot; ${portalPublicoPrograma.pt?(_tpDoc.nome):(_tpDoc.nomeEn)}</a>
							</li>
							</c:forEach>
	                  </ul>
	         </div>
	    </li>
	    </c:if>  

       	<c:set var="secao" value="#{portalPublicoPrograma.secaoExtra}"/>
       	<c:if test="${!portalPublicoPrograma.principal && not empty secao}">
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
									<a href="secao_extra.jsf?${portalPublicoPrograma.parametroURL}&extra=${secaoExtra.id}">
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

  
 
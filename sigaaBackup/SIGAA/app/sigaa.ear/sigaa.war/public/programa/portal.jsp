<%-- Cabeçalho --%>
<%@ include file="include/cabecalho.jsp" %>


<f:view locale="#{portalPublicoPrograma.lc}">
<a4j:keepAlive beanName="portalPublicoPrograma"/>
<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>

<%-- topo --%>	
<%@ include file="include/programa.jsp" %>
<%-- menu flutuante --%>
<%@ include file="include/menu.jsp" %>

<c:set var="_notDestaques" value="#{portalPublicoPrograma.noticiaSiteDestaques}"/>
<c:set var="_destaque" value="#{portalPublicoPrograma.noticiaSiteDetalhes}"/>
	 
<%-- conteudo --%>
<div id="conteudo">

	<%-- menu lateral --%>
	 <c:if test="${not empty secao}">
		<div id="menu_lateral">
	    	<div id="titulo"><h:outputText value="#{idioma.outrasOpcoes}"/></div>
	        <div id="links">
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
	    </div>
	 </c:if>   

	<%-- conteudo da esquerda --%>
	<div id="esquerda" class="${not empty secao?'com_menu_lateral':''}"> 
	
		<%-- noticia em destaque --%>
	    <div class="titulo"><h:outputText value="#{idioma.ultimasNoticias}"/></div>
	    <div id="destaque">
	    	
	    	<span class="d_titulo"><h:outputText value="#{_destaque.titulo}"/></span>
	        <span class="d_texto">
	        
	        	<h:outputText escape="false" value="<img src='/sigaa/verFoto?idFoto=#{_destaque.idFoto}&key=#{ sf:generateArquivoKey(_destaque.idFoto) }'
	        		 width='209px' class='foto' align='left'/>" rendered="#{not empty _destaque.idFoto}" />
              	<h:outputText escape="false" value="#{_destaque.descricaoMedia}" rendered="#{not empty _destaque.idFoto}" />
              	<h:outputText escape="false" value="#{_destaque.descricaoGrande}" rendered="#{empty _destaque.idFoto}" />
              	<br clear="all"/>
	            <span class="data">
	            	<h:outputText value="#{idioma.cadastraEm}: "/> <h:outputText value="#{_destaque.dataCadastro}"/>
	            </span>
	            
			</span>
			<div class="leia_mais">
				<a href="noticias_desc.jsf?${portalPublicoPrograma.parametroURL}&noticia=<h:outputText value="#{_destaque.id}"/>"><h:outputText value="+ #{idioma.leiaMais}"/></a>
			</div>
		</div>
	
		<%-- separador das noticias --%>
	    <div id="linha"></div>
	
		<%-- ultimas noticias --%>
	    <div class="titulo"><h:outputText value="#{idioma.maisNoticias}"/></div>
	    <div id="listagem">
	    	<ul>
	        	<c:if test="${fn:length(_notDestaques)>0}">
	        	<c:forEach var="noticia" items="#{_notDestaques}" varStatus="loop">
		        	<li>
		            	&raquo; 
		                <a href="noticias_desc.jsf?lc=${portalPublicoPrograma.parametroURL}&noticia=${noticia.id}">
		                	<span class="data"><ufrn:format type="data" valor="${noticia.dataCadastro}"/></span>
		 					- <h:outputText value="#{noticia.titulo}" />
						</a>
					</li>
				</c:forEach>
				</c:if>
			</ul>
	      	<div class="leia_mais">
	      		<a href="noticias.jsf?${portalPublicoPrograma.parametroURL}"> 
	      			<h:outputText value="+ #{idioma.leiaMais}"/>
	      		</a>
	      	</div>
		</div>
		
	</div>
	
	<%-- conteudo da direita --%>
	<div id="direita">
	
		<%-- chamada do processo seletivo --%>
		<div id="chamada">
			<div id="icone"><img src="${ctx}/public/programa/img/icone_ps.png" /></div>
			<div id="caixa">
				<span id="titulo" class="titulo_menor"><h:outputText value="#{idioma.processoSeletivo}" /></span>
				<span class="introducao">
					<h:outputText value="#{idioma.introducaoProcessoSeletivo}"/>
				</span>
				<br/>
				<div id="listagem2">
					<h:form id="formProcessos">
					<ul>
						<c:if test="${fn:length(portalPublicoPrograma.processosSeletivos)>0}">
						<c:forEach items="#{portalPublicoPrograma.processosSeletivos}" end="4" var="prsl" varStatus="status">
						<li>
						    &raquo; 
						    <h:commandLink target="_blank" id="visualizarProcessoSeletivo" title="#{idioma.visualizarProcessoSeletivo}" 
						    	action="#{processoSeletivo.viewPublico}">
						    	<h:outputText value="#{prsl.curso.nivelDescricao}" styleClass="#{prsl.inscricoesAbertas?'destaqueProcessoeletivo':''}" />
						        <f:param name="id" value="#{prsl.id}" />
						        <span class="data ${prsl.inscricoesAbertas?'destaqueProcessoeletivo':''}">
						       	 (<ufrn:format type="data" valor="${prsl.editalProcessoSeletivo.inicioInscricoes}"/>
								 : <ufrn:format type="data" valor="${prsl.editalProcessoSeletivo.fimInscricoes}"/>)
								</span>
						    </h:commandLink>
						</li>
						</c:forEach>
						</c:if>
	                </ul>
	                </h:form>
				</div>
			</div>
		</div>
	
	    <%-- espaco entre as chamadas --%>
	    <div id="espaco"></div>
	
	    <%-- chamada do calendario --%>
	    <div id="chamada">
	        <div id="icone"><img src="${ctx}/public/programa/img/icone_cal.png" /></div>
	        <div id="caixa">
	        	<span id="titulo" class="titulo_menor"><h:outputText value="#{idioma.calendario}"/></span>
	          	<span class="introducao">
		              <h:outputText value="#{idioma.fiquePorDentroEvento}"/>
				</span>
				<div id="listagem3">
	                <ul>
							<c:set var="calendarioVigente" value="#{portalPublicoPrograma.calendarioVigente}" />
							<c:set var="proximoPeriodo" value="#{portalPublicoPrograma.calendarioProximoPeriodo}" />
							<c:if test="${not empty calendarioVigente}">
						<li>	
								<c:if test="${not empty calendarioVigente.inicioMatriculaOnline}">
									<ufrn:format type="data" valor="${calendarioVigente.inicioMatriculaOnline}"/>
									- <ufrn:format type="data" valor="${calendarioVigente.fimMatriculaOnline}"/>
								    <span class="evento">
								    	&middot; <h:outputText value="#{idioma.matricular}"/> 
												${calendarioVigente.ano}.${calendarioVigente.periodo}.
									</span>
								</c:if>
						</li>		
	
						<li>		
								<c:if test="${not empty calendarioVigente.inicioPeriodoLetivo}">
										<ufrn:format type="data" valor="${calendarioVigente.inicioPeriodoLetivo}"/>
										<span class="evento">
								    	&middot; <h:outputText value="#{idioma.inicioPeriodoLetivo}"/> ${calendarioVigente.ano}.${calendarioVigente.periodo}.
								    	</span>
								</c:if>
						</li>		
							</c:if>	
							<c:if test="${not empty proximoPeriodo}">
						<li>		
								<c:if test="${not empty proximoPeriodo.inicioMatriculaOnline}">
									<ufrn:format type="data" valor="${proximoPeriodo.inicioMatriculaOnline}"/>
									- <ufrn:format type="data" valor="${proximoPeriodo.fimMatriculaOnline}"/>
								    <span class="evento">
								    	&middot; <h:outputText value="#{idioma.matricular}"/> 
												${proximoPeriodo.ano}.${proximoPeriodo.periodo}.
									</span>
								</c:if>
						</li>		
								
							</c:if>

	                </ul>
	              	<div class="saiba_mais"><a href="calendario.jsf?${portalPublicoPrograma.parametroURL}"><h:outputText value="#{idioma.cliqueSaibaMais}"/></a></div>
				</div>
			</div>
		</div>
		
	</div>
	
</div>

</f:view>
<%-- Rodapé --%>
<%@ include file="./include/rodape.jsp" %>
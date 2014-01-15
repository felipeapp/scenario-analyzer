<%-- Cabeçalho --%>
<%@ include file="include/cabecalho.jsp" %>


<f:view locale="#{portalPublicoCurso.lc}">
<a4j:keepAlive beanName="portalPublicoCurso"/>
<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>

<%-- topo --%>	
<%@ include file="include/curso.jsp" %>
<%-- menu flutuante --%>
<%@ include file="include/menu.jsp" %>

 
<%-- conteudo --%>
<div id="conteudo">
	
	<!-- conteudo da esquerda -->
  	<div id="esquerda">

      	<div class="titulo"><h:outputText value="#{idioma.apresentacao}"/></div>
	<!-- apresentacao -->
      	<div class="texto">
            <c:if test="${portalPublicoCurso.detalhesSite.idFoto != null}">
              <img class="foto" src="${ctx}/verFoto?idFoto=${portalPublicoCurso.detalhesSite.idFoto}&key=${ sf:generateArquivoKey(portalPublicoCurso.detalhesSite.idFoto) }" />
			</c:if>
            <h:outputText value="#{portalPublicoCurso.introducaoLocale}" escape="false" rendered="#{portalPublicoCurso.introducaoLocale != null}" />
      		<h:outputText value="#{idioma.vazio}" rendered="#{portalPublicoCurso.introducaoLocale == null}" />  
        </div>

          <span class="topico">
          	<h:outputText value="#{idioma.coordenacaoPrograma}"/>: 
          	<strong>
          		<h:outputText value="#{portalPublicoCurso.coordenador.servidor.pessoa.nome}" rendered="#{not empty portalPublicoCurso.coordenador.servidor}"/>
          		<h:outputText value="#{idioma.vazio}"
						 rendered="#{empty portalPublicoCurso.coordenador.servidor}" />
          	</strong>
          </span>
          <div class="informacoes">
              <p>Telefone: 
              	<span class="cor">
              	<h:outputText value="#{portalPublicoCurso.coordenador.telefoneContato1}" rendered="#{not empty portalPublicoCurso.coordenador.telefoneContato1}" />
              	<h:outputText value="/#{portalPublicoCurso.coordenador.ramalTelefone1}" 
              			rendered="#{not empty portalPublicoCurso.coordenador.telefoneContato1 && not empty portalPublicoCurso.coordenador.ramalTelefone1}" />
			    <h:outputText value="#{idioma.vazio}" rendered="#{empty portalPublicoCurso.coordenador.telefoneContato1}" />
              	</span>
              </p>
              <p>E-mail: 
              <span class="cor">
              	<h:outputText value="#{portalPublicoCurso.coordenador.emailContato}"/>
				<h:outputText value="#{idioma.vazio}" rendered="#{empty portalPublicoCurso.coordenador.emailContato}" />
              </span>
              </p>
          </div>
		<c:if test="${!portalPublicoCurso.curso.lato}">
          <span class="topico">
          	<h:outputText value="#{idioma.tituloProfissional}" />: 
          	<strong>
          		<h:outputText value="#{portalPublicoCurso.curso.titulacaoMasculino}"/>
          		<h:outputText value="#{idioma.vazio}"
				 rendered="#{empty portalPublicoCurso.curso.titulacaoMasculino}" />
          	</strong>
          </span>


          <span class="topico">
          	<h:outputText value="#{idioma.areaConhecimentoCNPQ}"/>: 
          	<strong>
          		<h:outputText value="#{portalPublicoCurso.curso.areaCurso.nome}"/>
				<h:outputText value="#{idioma.vazio}"
				 rendered="#{empty portalPublicoCurso.curso.areaCurso.nome}" />
			</strong>
		  </span>
		</c:if>
		<c:if test="${portalPublicoCurso.curso.lato}">
		  <span class="topico">
          	<h:outputText value="#{idioma.periodoCurso}"/>:
          	<strong>
          		<ufrn:format type="data" valor="${portalPublicoCurso.curso.dataInicio}"/> à <ufrn:format type="data" valor="${portalPublicoCurso.curso.dataFim}"/>
			</strong>
		  </span>
		</c:if>
          <c:if test="${portalPublicoCurso.curso.tecnico}">
			<!-- Inicio Modalidade Ensino -->
				<span class="topico"><h:outputText value="#{idioma.modalidadeEnsino}"/>:
					<strong>
						<h:outputText value="#{portalPublicoCurso.curso.modalidadeCursoTecnico.descricao}"/>
						<h:outputText value="#{idioma.vazio}"
						 rendered="#{empty portalPublicoCurso.curso.modalidadeCursoTecnico.descricao}" />
					</strong>
				</span>	
			<!-- Fim Modalidade Ensino -->

			<!-- Início Qualificações Técnicas -->
				<span class="topico">
					<h:outputText value="#{idioma.qualificacoesTecnicas}"/>:
					<c:forEach items="#{portalPublicoCurso.curso.qualificacoes}" 
					var="itemLoop" varStatus="loop">
						<strong>
							${itemLoop.descricao}	${itemLoop.id}
						</strong>
					</c:forEach>
					<h:outputText escape="false" value="<strong>#{idioma.vazio}</strong>"
						 rendered="#{empty portalPublicoCurso.curso.qualificacoes}" />
				</span>
			<!-- Fim Qualificações Técnicas -->	
					
		</c:if>

	
		<!-- Inicio Convenio -->
		<c:if test="${!portalPublicoCurso.curso.tecnico && not empty portalPublicoCurso.curso.convenio}">
				<span class="topico"><h:outputText value="#{idioma.convenioAcademico}"/>:
					<strong>
						<h:outputText value="#{portalPublicoCurso.curso.convenio.descricao}"/>
						<h:outputText value="#{idioma.vazio}"
						 rendered="#{empty portalPublicoCurso.curso.convenio.descricao}" />
					</strong>
				</span>		
		</c:if>		
		<!-- Fim Convenio -->

		<c:if test="${!portalPublicoCurso.curso.tecnico}">
	
			<!-- Inicio Modalidade de Curso -->
				<span class="topico">
					<h:outputText value="#{idioma.modalidadeCurso}"/>:
					<strong>
						<h:outputText value="#{portalPublicoCurso.curso.modalidadeEducacao.descricao}"/>
						<h:outputText value="#{idioma.vazio}"
						 rendered="#{empty portalPublicoCurso.curso.modalidadeEducacao.descricao}" />
					</strong>
				</span>	
			<!-- Fim Modalidade de Curso -->

		</c:if>		
          
          
          
          
      </div>

	<%-- conteudo da direita --%>
	<div id="direita">
	
	
	    <%-- chamada do calendario --%>
	    
	    <c:if test="${ not portalPublicoCurso.curso.lato }">
		    <div id="chamada">
		        <div id="icone"><img src="${ctx}/public/curso/img/icone_cal.png" /></div>
		        <div id="caixa">
		        	<span id="titulo" class="titulo_menor"><h:outputText value="#{idioma.calendario}"/></span>
		          	<span class="introducao">
			              <h:outputText value="#{idioma.fiquePorDentroEvento}"/> 
					</span>
					<div id="listagem3">
		                <ul>
								<c:set var="calendarioVigente" value="#{portalPublicoCurso.calendarioVigente}" />
								<c:set var="proximoPeriodo" value="#{portalPublicoCurso.calendarioSegundoPeriodo}" />
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
		              	<div class="saiba_mais"><a href="calendario.jsf?${portalPublicoCurso.parametroURL}"><h:outputText value="#{idioma.cliqueSaibaMais}"/></a></div>
					</div>
				</div>
		</div>
		</c:if>
		
	    <%-- espaco entre as chamadas --%>
	    <div id="espaco"></div>
	
		<%-- chamada das notícias --%>
		<div id="chamada">
			<div id="icone"><img src="${ctx}/public/curso/img/icone_news.png" /></div>
			<div id="caixa">
				<span id="titulo" class="titulo_menor"><h:outputText value="#{idioma.noticias}" /></span>
				<span class="introducao">
					<h:outputText value="#{idioma.introducaoNoticia}"/>
				</span>
			
				<div id="listagem2">
					<h:form id="formNoticias">
					<ul>
						<c:if test="${fn:length(portalPublicoCurso.noticiaSiteDestaques)>0}">
						<c:forEach items="${portalPublicoCurso.noticiaSiteDestaques}" end="2" var="_not" varStatus="status">
						<li>
						    &raquo; 
						    <a href="noticias_desc.jsf?lc=${portalPublicoCurso.parametroURL}&noticia=${_not.id}"
						    	 id="visualizarNoticia" title="${idioma.visualizarNoticia}">
						    	<ufrn:format type="texto" valor="${_not.titulo}" length="95"></ufrn:format> 
							</a>	
						</li>
						</c:forEach>
						</c:if>
	                </ul>
	                </h:form>
				</div>
			</div>
		</div>
	
		
	</div>
	
</div>

</f:view>
<%-- Rodapé --%>
<%@ include file="./include/rodape.jsp" %>
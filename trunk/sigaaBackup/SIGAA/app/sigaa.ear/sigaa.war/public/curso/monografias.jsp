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
		<div class="titulo">
			<h:outputText value="#{idioma.buscarMonografias}"/>
		</div>	
   	    <div class="texto">	
			<p>Nesta página você poderá buscar todas as Monografias publicadas e que possuem seus trabalhos anexados.</p>
			<p>	
			Para efetuar uma busca digite um dos critérios de busca que faça referência 
			à monografia que deseja encontrar.
			</p> 
		</div>				
		<h:form id="form">				
			<div id="caixa_formulario">
			  <div id="icon_cf"><img src="${ctx}/public/curso/img/icone_bus.png" /></div>
                  <div id="formulario">
                      <div id="head_f">
                          Informe os critérios da Busca
                      </div>
                      <div id="body_f">

                           	<span class="campo_maior">
                               	<label><h:selectBooleanCheckbox value="#{portalPublicoCurso.filtroNome}" id="checkNome" />${idioma.aluno}: </label>
                                  <h:inputText id="nome" value="#{portalPublicoCurso.nome}"  size="50"  maxlength="80"	 
                                  onkeydown="marcar('form:checkNome')" onchange="marcar('form:checkNome');" />
                            </span>
                           	<span class="campo_maior">
                               	<label><h:selectBooleanCheckbox value="#{portalPublicoCurso.filtroTitulo}" id="checkTitulo" />${idioma.titulo}: </label>
                                 <h:inputText id="titulo" value="#{portalPublicoCurso.titulo}" size="50" maxlength="80" 
                                onkeydown="marcar('form:checkTitulo')"				
								onchange="marcar('form:checkTitulo');" />
                            </span>
                           	<span class="campo_maior">
                               	<label><h:selectBooleanCheckbox value="#{portalPublicoCurso.filtroAno}" id="checkAno" />${idioma.ano}: </label>
                                 <h:inputText id="ano" onkeyup="return formatarInteiro(this);" value="#{portalPublicoCurso.ano}" 
                                 size="4" maxlength="4" onkeydown="marcar('form:checkAno')"				
								onchange="marcar('form:checkAno');" />
                            </span>
     					  	<span class="botoes">
                           	<h:inputHidden id="lc" value="#{portalPublicoCurso.lc}" />
							<h:inputHidden id="idCurso" value="#{portalPublicoCurso.curso.id}" />					
							<h:commandButton  styleClass="bt_buscar" id="buscar" action="#{portalPublicoCurso.buscarMonografias}" value="Buscar"/>&nbsp;
							<h:commandButton styleClass="bt_cancelar" action="#{portalPublicoCurso.cancelar}" onclick="#{confirm}" value="Cancelar"/>
							</span>
                       </div>
                  	</div>
                </div>
             </div> 
             
             
			 <!-- Lista da busca -->
             
                      
				
			
			
					
			<c:set var="lista" value="#{portalPublicoCurso.listagemMonografias}" />
			<c:if test="${not empty lista}">	
				<div class="legenda">
					 <h:graphicImage value="../../../shared/img/icones/download.png" style="overflow: visible;" />
					: <h:outputText value="#{idioma.downloadArquivo}"/>
				</div>
				<div id="listagem_tabela">
	             	<div id="group_lt">
	                	Lista das Monografias
					</div>           							
						<table id="table_lt" width="100%">
							<tr class="campos">
								<td style="text-align: center;">${idioma.ano}</td>
								<td style="text-align: center;">${idioma.data}</td>
								<td>${idioma.aluno}</td>
								<td>${idioma.orientador}</td>
								<td>${idioma.curso}</td>
								<td></td>
							</tr>
							<c:forEach items="#{lista}" var="item"  varStatus="status">
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td style="text-align: center;">${item.ano}</td>
									<td style="text-align: center;">
										<fmt:formatDate value="${item.dataDefesa}" pattern="dd/MM/yyyy"/>
									</td>
									<c:if test="${ not empty item.orientando.pessoa.nome}">
										<td>${item.orientando.pessoa.nome}</td>
									</c:if>
									<c:if test="${empty  item.orientando.pessoa.nome}">
										<td>${item.orientandoString}</td>
									</c:if>
									<td>${item.servidor.pessoa.nome}</td>							
									<td>${item.orientando.curso.descricao}</td>							
									<td>
									   <c:if test="${not empty item.idArquivo}">			
											<a href="/sigaa/verProducao?idProducao=${item.idArquivo}
											&key=${ sf:generateArquivoKey(item.idArquivo) }" target="_blank">
												 <h:graphicImage value="../../../shared/img/icones/download.png" title="#{idioma.downloadArquivo}" style="overflow: visible;" />
											</a>													
									   </c:if>
									</td>
								</tr>
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td style="font-weight:bold;">${idioma.titulo}:</td>
									<td colspan="5">${item.titulo}</td>
								</tr>
							</c:forEach>
						</table>	
				</div>	
			</c:if>		

		</h:form>			
		<!--  FIM CONTEÚDO  -->	

</f:view>
<%@ include file="./include/rodape.jsp" %>
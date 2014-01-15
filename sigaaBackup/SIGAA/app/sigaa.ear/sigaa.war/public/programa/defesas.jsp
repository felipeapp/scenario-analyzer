<%-- Cabeçalho --%>
<%@ include file="include/cabecalho.jsp" %>

<f:view locale="#{portalPublicoPrograma.lc}">
<a4j:keepAlive beanName="portalPublicoPrograma"/>
<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>

<%-- topo --%>
<%@ include file="include/programa.jsp" %>
<%-- menu flutuante --%>
<%@ include file="include/menu.jsp" %>

<style>
	.ano {vertical-align:top;width:45px;border:0px; }
	.descricao {width:100%;padding-left:0px;line-height:1.25em;}
	a.linkArquivo {display: block;width: auto;margin:5px 0px 0px;padding-left:20px;line-height: 1.9em;background: url(/sigaa/img/pdf.png) no-repeat;font-size: 1.0em;font-weight: normal;float:left;}
	a.verResumo {display: block;width: auto;margin:5px 0px 0px;padding-left:20px;line-height: 1.9em;background: url(../../img/biblioteca/baixo.gif) no-repeat;font-size: 1.0em;font-weight: normal;float:left;}
	.txtResumo {padding:8px 10px 8px; width:98%; text-align:justify;}
	.lupa {width:60px;padding-left:0px;}
	.ano {font-size:14px;}
</style>
<script>
		/**
		* Param el(Elemento recebe ação onlcik do mouse.
		* 		view(Elmento contendo o conteúdo a ser escondido)		
		*/
		
		function toggleResumo(el,view){
	
			var imgCima = "url(../../img/biblioteca/cima.gif)";
			var imgBaixo = "url(../../img/biblioteca/baixo.gif)";
	
			//Esconde todos elementos exceto ele mesmo
			jQuery(view + ':not(this)').slideUp();	
			
			if(el!= ""){
				//Altera a imagem background de todas os links
				jQuery("." + el.className).css("background-image",imgBaixo);
				jQuery("." + el.className).html('${idioma.mostrarResumo}');
				if(jQuery(el).parent().next(view).css("display") == "none"){			
					jQuery(el).parent().next(view).slideDown("fast");
					jQuery(el).css("background-image",imgCima);
					jQuery(el).html('${idioma.esconderResumo}');
				}
			}	
		
		}
</script>
	 
<%-- conteudo --%>
<div id="conteudo">
	<div class="titulo"><h:outputText value="#{idioma.dissertacoesTeses}"/></div>
	
		<h:form id="formListaDefesasPosGraduacao">


				<br clear="clear"/>
				<p>
					Clique 	
					<h:commandLink target="_blank" title="#{idioma.visualizarDetalhesPesquisa}"	action="#{portalPublicoPrograma.redirectBDTD}"> <b>aqui</b> </h:commandLink> 
					para acessar os arquivos diretamente da Biblioteca Digital de Teses e Dissertações da ${ configSistema['siglaInstituicao'] } 
				</p>
		
				<c:if test="${not empty portalPublicoPrograma.defesas}">
					<c:set var="anoAnterior" value="null"/>

					<c:forEach items="${portalPublicoPrograma.defesas}" var="dissertacaoTese" varStatus="status">
						
						
						<c:if test="${not empty dissertacaoTese.dadosDefesa.discente.discente.nome}">
							
							<c:if test="${anoAnterior != dissertacaoTese.ano}">
								
								<c:if test="${!status.first}">
											</body>
										</table>
									</div>	
								</c:if>
								
								<div id="listagem_tabela">
							
									<div id="group_lt">
										 <c:choose>
										 	<c:when test="${not empty dissertacaoTese.ano}">
												${dissertacaoTese.ano}
										 	</c:when>
										 	<c:otherwise>
												 <h:outputText value="#{idioma.vazio}"/>
										 	</c:otherwise>
										 </c:choose>
									</div>	 
									<table id="table_lt">
										<tbody>
										<tr class="campos">
											<th width="2px"></th>
											<td><h:outputText value="#{idioma.descricao}" /></td>
											<th width="2px"></th>
										</tr>
							</c:if>
						
							<tr class="${status.index % 2 == 0 ? '' : 'linha_impar'}">
								
								<td width="2px"></td>
								 
								<td> 
								 <ul>
									
								 	<li>	
								 		<b>${dissertacaoTese.dadosDefesa.discente.discente.nome}</b>
									</li>
									
									<li>									  	
								  		${dissertacaoTese.dadosDefesa.titulo}
								  	</li>
								  	
								  	<c:if test="${not empty dissertacaoTese.dadosDefesa.discente.orientacao.servidor.nome}">
									  	<li>
									  		<b><h:outputText value="#{idioma.orientador}"/> : </b>${dissertacaoTese.dadosDefesa.discente.orientacao.servidor.nome}
									    </li>
								   	</c:if>

								 	<li>	
								 		<b>Data: </b><fmt:formatDate value="${dissertacaoTese.data}"/>
									</li>
								   	
								   	<li>
									  	<c:if test="${portalPublicoPrograma.parametrosPrograma.visualizarDefesa && not empty dissertacaoTese.dadosDefesa.linkArquivo}">
											<a class="linkArquivo" title="${idioma.visualizarDissertacaoTese}"  href="${dissertacaoTese.dadosDefesa.linkArquivo}" target="_blank">
												<h:outputText value="#{idioma.visualizarDissertacaoTese}" /> &nbsp;
											</a>
								 	    </c:if>
										 	    
								 	 	<c:if test="${not empty dissertacaoTese.dadosDefesa.resumo}">
									 	 	<a href="javascript:void(0);" onClick="toggleResumo(this,'.txtResumo')" class="verResumo" >
												<h:outputText value="#{idioma.mostrarResumo}"/>
											</a>
										</c:if>
										<br clear="all"/>
									</li> 
									
									<c:if test="${not empty dissertacaoTese.dadosDefesa.resumo}">
										<li class="txtResumo" >
			
											${dissertacaoTese.dadosDefesa.resumo}
										</li>
									</c:if>
								</ul>
								</td>
								<td width="2px"></td>
								
							</tr>
							
							<c:set var="anoAnterior" value="${dissertacaoTese.ano}"/>
						</c:if>
					</c:forEach>

					</body>
				</table>

				</c:if>
				
				<c:if test="${empty portalPublicoPrograma.defesas}">
					<p class="vazio">
						<h:outputText value="#{idioma.vazio}" />
					</p>
				</c:if>
	</table>
			</h:form>
			</div>	
			
		<%--  FIM CONTEÚDO  --%>	
	<rich:jQuery selector=".txtResumo" query="hide()" timing="onload" />
	</div>
</f:view>
<%-- Rodapé --%>
<%@ include file="./include/rodape.jsp" %>
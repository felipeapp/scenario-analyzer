<%@include file="../include/cabecalho.jsp"%>

<meta name = "format-detection" content = "telephone=no">

<f:view>
	<div data-role="page" id="page-topico-aula-docente" data-theme="b" data-add-back-btn="false" data-nobackbtn="true">
   		<h:form id="form-topico-aula-docente" styleClass="form-topico-aula-docente">
   		
   			<style>
				.ui-header div.ui-btn{
					display:none;
				}
				
				.ui-footer div.ui-btn{
					display:none;
				}
				
				.ui-icon-minhas-turmas {
					background: url("/sigaa/ava/img/group.png") no-repeat;
					background-size: 16px 16px;
				}
				
				.ui-icon-noticias {
					background: url("/sigaa/mobile/touch/img/08-chat.png") no-repeat;
					background-size: 18px 16px;
				}
				
				.ui-icon-frequencia {
					background: url("/sigaa/mobile/touch/img/83-calendar.png") no-repeat;
					background-size: 15px 15px;
				}
				
   			</style>
			
				<div data-role="header" data-position="inline" data-theme="b">
					<script>
						function verTopicoAnterior() {
							$('#form-topico-aula-docente\\:linkEsquerda').click();
							//$('#form-topico-aula-docente\\:linkEsquerdaFalso').click();
// 							$('#form-topico-aula-docente\\:linkEsquerda').bind('click', function () {
// 								return false;
// 							});
// 							$('#form-topico-aula-docente\\:linkDireita').bind('click', function () {
// 								return false;
// 							});
						}
				
						function verProximoTopico() {
							$('#form-topico-aula-docente\\:linkDireita').click();
							//$('#form-topico-aula-docente\\:linkDireitaFalso').click();
// 							$('#form-topico-aula-docente\\:linkDireita').bind('click', function () {
// 								return false;
// 							})
// 							$('#form-topico-aula-docente\\:linkEsquerda').bind('click', function () {
// 								return false;
// 							});
						}
						
						$(document).ready(function() {
							$('.ui-content').live('swipeleft swiperight',function(event){
								if (event.type == "swipeleft") {
									verProximoTopico();
								} else if (event.type == "swiperight") {
									verTopicoAnterior();
								}
								event.preventDefault();
							});
						});
						
						$('#form-topico-aula-docente').attr('data-ajax','false');
						//$('#form-topico-aula-docente').attr('data-transition','fade');
					</script>

					<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
					
					<h:commandButton id="linkEsquerda" rendered="#{ portalDocenteTouch.idTopicoAnterior > 0 }"
						action="#{ portalDocenteTouch.exibirTopico }">
						<f:setPropertyActionListener value="#{ portalDocenteTouch.idTopicoAnterior }" target="#{ portalDocenteTouch.topicoSelecionado.id }"/>
					</h:commandButton>
					
					<h:commandButton id="linkDireita" rendered="#{ portalDocenteTouch.idProximoTopico > 0 }"
						action="#{ portalDocenteTouch.exibirTopico }">
						<f:setPropertyActionListener value="#{ portalDocenteTouch.idProximoTopico }" target="#{ portalDocenteTouch.topicoSelecionado.id }"/>
					</h:commandButton>
				</div>
				
				<div data-role="navbar" data-theme="b" data-iconpos="left">
					<ul>
						<li><h:commandLink value="Voltar" id="lnkVoltar" action="#{ portalDocenteTouch.listarMinhasTurmas }" /></li>
						<li><h:commandLink value="Início" action="#{ portalDocenteTouch.acessarPortal }" id="lnkInicio"/></li>
						<li><h:commandLink value="Sair" action="#{ loginMobileTouch.logoff }" id="lnkSair" onclick="#{logout }" /></li>
					</ul>
				</div>
		
				<div data-role="content">
					<div style="text-align: center;">
						<small>
							<strong>
								<h:outputText value="#{ portalDocenteTouch.turma.disciplina.codigoNome} (#{portalDocenteTouch.turma.anoPeriodo})" escape="false" />
							</strong>
						</small>
					</div>
					
					<%@include file="/mobile/touch/include/mensagens.jsp"%>
					
					<a4j:outputPanel layout="block" rendered="#{ not empty noticiaTurmaTouch.ultimaNoticia }">
						<div data-role="collapsible-set" data-content-theme="b">
							<div data-role="collapsible" data-theme="b" data-content-theme="b" data-collapsed="true">
					    		<h3><h:outputText style="white-space: normal;" value="#{ noticiaTurmaTouch.ultimaNoticia.descricao }"  escape="false"/></h3>
					    		<p>
									<strong><h:outputText value="#{ noticiaTurmaTouch.ultimaNoticia.data }"/></strong>
									<h:outputText value="<p>#{ noticiaTurmaTouch.ultimaNoticia.noticia }</p>"  escape="false" />
								</p>
						    </div>
					    </div>
					    <br/>
					</a4j:outputPanel>
					
					<table align="center">
						<tr>
							<th>
								<c:if test="${portalDocenteTouch.idTopicoAnterior > 0}">
									<a href="#" onclick="verTopicoAnterior();" data-role="button" data-iconpos="notext" data-icon="arrow-l"></a>
								</c:if>
							</th>
							<th>
								<strong>
	    							<h:outputText value="#{ portalDocenteTouch.topicoSelecionado.descricao } <br/>
	    				 			<small>(#{ portalDocenteTouch.topicoSelecionado.dataFormatada } - #{ portalDocenteTouch.topicoSelecionado.dataFimFormatada })</small>" 
	    							rendered="#{not empty portalDocenteTouch.topicoSelecionado.descricao && !portalDocenteTouch.topicoSelecionado.aulaCancelada}" escape="false"/>  
	    						</strong>
	    					</th>
	    					<th>
								<c:if test="${ portalDocenteTouch.idProximoTopico > 0 }">
									<a href="#" onclick="verProximoTopico();" data-role="button" data-iconpos="notext" data-icon="arrow-r"></a>
								</c:if>
							</th>
						</tr>
					</table>
	    			
	    			<c:if test="${not empty portalDocenteTouch.topicoSelecionado.conteudo }">
	    				<p><h:outputText value="#{ portalDocenteTouch.topicoSelecionado.conteudo }" escape="false" /></p>
	    			</c:if>
	    			
				    <ul data-role="listview" data-inset="true">
				    	<c:forEach items="#{portalDocenteTouch.topicoSelecionado.materiais }" var="material">
				    		<li>
			    				<c:if test="${ !material.tipoRotulo || (material.tipoQuestionario && material.finalizado) || (!material.tipoRotulo && (turmaVirtual.docente || permissao.docente))}">
			    					<h:graphicImage value="#{ material.icone }" styleClass="ui-li-icon"/>
			    				</c:if>
			    				
				    			<c:if test="${!material.tipoArquivo && !material.tipoForum && (!material.tipoIndicacao || !material.site) && !material.tipoVideo}">
				    				<a4j:outputPanel layout="block">
										<strong><u><h:outputText value="#{ material.nome }" escape="false" /></u></strong>
				    				</a4j:outputPanel>
				    			</c:if>
				    			
				    			<%-- Se o material é um Arquivo --%>
				    			<c:if test="${ material.tipoArquivo }">
				    				<a4j:outputPanel layout="block">							
										<%--<h:commandLink id="idMostrarArquivo" action="#{ downloadMobile.baixarArquivo }" rendered="#{not empty material.arquivo }" target="_blank">
											<h:outputText value="#{ material.nome }" />
											<f:param name="id" value="#{ material.arquivo.idArquivo }"/>
											<f:param name="tid" value="#{ portalDocenteTouch.turma.id }"/>
										</h:commandLink> --%>
										<c:if test="${not empty material.arquivo && not empty material.arquivo.idArquivo}">
											<c:set var="idArquivo" value="${ material.arquivo.idArquivo }"/>
											<a href="/sigaa/mobile/touch/download?id=<h:outputText value="#{ material.arquivo.idArquivo }"/>&tid=${portalDocenteTouch.turma.id}&key=${sf:generateArquivoKey(idArquivo)}" target="_blank" rel="external">
												<h:outputText value="#{ material.nome }" />
											</a>
										</c:if>
									</a4j:outputPanel>
								</c:if>
								
								<%-- Se o material é uma Indicação de Referência --%>
								<c:if test="${ material.tipoIndicacao }">
									<a4j:outputPanel>
										<h:outputText value="<a href='#{material.url}' target='_blank' rel='external'> " escape="false" rendered="#{material.site}"/>
											<h:outputText value=" #{ material.nome }" escape="false" />
										<h:outputText value="</a>" escape="false"/>
									</a4j:outputPanel>
								</c:if>
					
								<%-- Se o material é um Fórum --%>
								<c:if test="${ material.tipoForum }">
									<a4j:outputPanel layout="block">										
										<h:commandLink id="idMostrarForum" action="#{ forumBean.view }">
											<h:outputText value=" #{ material.nome }"/>
											<f:setPropertyActionListener value="#{ material.forum.id }" target="#{ forumBean.obj.id }"/>
											<f:param name="tid" value="#{ portalDocenteTouch.turma.id }"/>
										</h:commandLink>
									</a4j:outputPanel>
								</c:if>
								
								<%-- Se o material é um Vídeo --%>
								<c:if test="${ material.tipoVideo}">
									<a4j:outputPanel>
										<a4j:outputPanel rendered="#{ material.link == '' || material.link == null || material.linkVideo == null }">
											<strong><u><h:outputText value="#{ material.titulo }" /></u></strong>
										</a4j:outputPanel>
									
										<%-- Se o docente enviou um vídeo com link não reconhecido, exibe o link para que o discente clique e assista ao vídeo na sua própria página. --%>
										<a4j:outputPanel rendered="#{ material.linkVideo != null }" styleClass="inline">
											<a href="<h:outputText value="#{ material.linkVideo }" />" target="_blank">
												<h:outputText value="#{ material.titulo }" />
											</a>
										</a4j:outputPanel>
									</a4j:outputPanel>
								</c:if>
								
								<p>
									<h:outputText style="white-space: normal;" escape="false">
										<p style="white-space: normal;">${ material.descricaoGeral}</p>
									</h:outputText>
								</p>			    			
				    		</li>
				    	</c:forEach>
				    </ul>
				    
				    <h:panelGroup rendered="#{ empty portalDocenteTouch.topicosAulas }">
				    	<p align="center">
				    		<h:outputText value="Não há tópicos de aula cadastrados para esta turma."/>
				   		</p>
				    </h:panelGroup>
				    
				    <br/>
				    
				    <div data-role="navbar" data-iconpos="left" style="padding-bottom: 5px;">
						<ul>
							<li><a href="/sigaa/<h:outputText value="#{ portalDocenteTouch.paginaListarAulas  }"/>" data-role="button" data-icon="search" data-iconpos="left" data-rel="dialog" data-transition="slidedown">Escolher Aula</a></li>
							<li><h:commandLink value="Minhas Turmas" id="lnkMinhasTurmas" action="#{ portalDocenteTouch.listarMinhasTurmas }" /></li>
						</ul>
					</div>
					
					<div data-role="navbar" data-iconpos="left">
						<ul>
							<%--<c:if test="${empty portalDocenteTouch.turma.subturmas}"> --%>
							<c:if test="${not portalDocenteTouch.turma.agrupadora}">
								<li><h:commandLink value="Frequências" id="lnkFrequencia" action="#{ portalDocenteTouch.exibirCalendarios }" /></li>
							</c:if>
							<%--<c:if test="${not empty portalDocenteTouch.turma.subturmas}"> --%>
							<c:if test="${portalDocenteTouch.turma.agrupadora}">
								<li><h:commandLink value="Frequências" id="lnkFrequencia" action="#{portalDocenteTouch.acessarLancarFrequenciaST}" /></li>
							</c:if>
							<li><h:commandLink value="Notícias" id="lnkVerNoticias" action="#{ noticiaTurmaTouch.listarNoticiasDocente }" /></li>
						</ul>
					</div>
					
					<%--<fieldset class="ui-grid-a">
						<div class="ui-block-a">
							<a data-mini="true" href="/sigaa/<h:outputText value="#{ portalDocenteTouch.paginaListarAulas  }"/>" data-role="button" data-icon="search" data-iconpos="left" data-rel="dialog" data-transition="slidedown">Escolher Aula</a>
						</div>
						<div class="ui-block-b">
							<h:commandButton onclick="configurarAjax();" value="Minhas Turmas" id="lnkMinhasTurmas" action="#{ portalDocenteTouch.listarMinhasTurmas }" />
						</div>
					</fieldset>
					
					<fieldset class="ui-grid-a">
						<div class="ui-block-a">
							<a4j:commandLink value="Frequências" id="lnkFrequencia" action="#{ portalDocenteTouch.exibirCalendarios }" />
						</div>
						<div class="ui-block-b">
							<h:commandButton value="Notícias" id="lnkVerNoticias" action="#{ noticiaTurmaTouch.listarNoticiasDocente }" />
						</div>
					</fieldset> --%>
					
					<h:panelGroup rendered="#{portalDocenteTouch.mensagemDicaSwipe}">
						<br/>
						<small>
							<center>
								<strong>Dica:</strong> saiba
								<a href="/sigaa/mobile/touch/ava/ajuda_swipe.jsf" data-rel="dialog" data-transition="pop">aqui</a>
								como mudar os tópicos de aula rapidamente.
							</center>
						</small>
					</h:panelGroup>
				    
				    <script>
			   			$("#form-topico-aula-docente\\:lnkMinhasTurmas").attr("data-icon", "minhas-turmas");
			   			//$("#form-topico-aula-docente\\:lnkMinhasTurmas").attr("data-mini", "true");
						$("#form-topico-aula-docente\\:lnkVerNoticias").attr("data-icon", "noticias");
						//$("#form-topico-aula-docente\\:lnkVerNoticias").attr("data-mini", "true");
						//$("#form-topico-aula-docente\\:lnkFrequencia").attr("data-role", "button");
						$("#form-topico-aula-docente\\:lnkFrequencia").attr("data-icon", "frequencia");
						//$("#form-topico-aula-docente\\:lnkFrequencia").attr("data-mini", "true");
						
						$("#form-topico-aula-docente\\:lnkVoltar").attr("data-icon", "back");
						$("#form-topico-aula-docente\\:lnkInicio").attr("data-icon", "home");
						$("#form-topico-aula-docente\\:lnkSair").attr("data-icon", "sair");
				    </script>				    
				    
				</div>
			
				<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
		   </h:form>
		   <%@include file="../include/modo_classico.jsp"%>
		</div>
</f:view>

<%@include file="../include/rodape.jsp"%>
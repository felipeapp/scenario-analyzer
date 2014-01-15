<%@include file="../include/cabecalho.jsp"%>

<meta name = "format-detection" content = "telephone=no">

<f:view>
	<div data-role="page" id="page-topico-aula-discente" data-theme="b" data-add-back-btn="false" data-nobackbtn="true">
		<h:form id="form-topico-aula-discente" styleClass="form-topico-aula-discente" rendered="#{not empty acesso.usuario.discente}">
		
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
   			</style>
			
				<div data-role="header" data-position="inline" data-theme="b">
				
					<script>
						function verTopicoAnterior() {
							$('#form-topico-aula-discente\\:linkEsquerda').click();
// 							$('#form-topico-aula-discente\\:linkEsquerdaFalso').click();
// 							$('#form-topico-aula-discente\\:linkEsquerda').bind('click', function () {
// 								return false;
// 							});
// 							$('#form-topico-aula-discente\\:linkDireita').bind('click', function () {
// 								return false;
// 							});
						}
					
						function verProximoTopico() {
							$('#form-topico-aula-discente\\:linkDireita').click();
// 							$('#form-topico-aula-discente\\:linkDireitaFalso').click();
// 							$('#form-topico-aula-discente\\:linkDireita').bind('click', function () {
// 								return false;
// 							})
// 							$('#form-topico-aula-discente\\:linkEsquerda').bind('click', function () {
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
						
						$('#form-topico-aula-discente').attr('data-ajax','false');
						//$('#form-topico-aula-discente').attr('data-transition','fade');
					</script>
					
					<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
					
					<h:commandButton id="linkEsquerda" rendered="#{portalDiscenteTouch.idTopicoAnterior > 0}" 
						action="#{ portalDiscenteTouch.exibirTopico }">
						<f:setPropertyActionListener value="#{ portalDiscenteTouch.idTopicoAnterior }" target="#{ portalDiscenteTouch.topicoSelecionado.id }"/>
					</h:commandButton>
								
					<h:commandButton id="linkDireita" rendered="#{ portalDiscenteTouch.idProximoTopico > 0 }"
						action="#{ portalDiscenteTouch.exibirTopico }">
						<f:setPropertyActionListener value="#{ portalDiscenteTouch.idProximoTopico   }" target="#{ portalDiscenteTouch.topicoSelecionado.id }"/>
					</h:commandButton>
				</div>
				
				<div data-role="navbar" data-theme="b" data-iconpos="left">
					<ul>
						<li><h:commandLink value="Voltar" id="lnkVoltar" action="#{ portalDiscenteTouch.listarMinhasTurmas }" /></li>
						<li><h:commandLink value="Início" action="#{ portalDiscenteTouch.acessarPortal }" id="lnkInicio"/></li>
						<li><h:commandLink value="Sair" action="#{ loginMobileTouch.logoff }" id="lnkSair" onclick="#{logout }" /></li>
					</ul>
				</div>
		
				<div data-role="content">
					<div style="text-align: center;">
						<small>
							<strong>
								<h:outputText value="#{ portalDiscenteTouch.turma.disciplina.codigoNome} (#{portalDiscenteTouch.turma.anoPeriodo})" escape="false" />
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
						
					<%-- Esses dois próximos commandLinks são necessários para driblar um bug do jQuery Mobile --%>
					<%--<h:commandLink style="display: none;" onclick="reativaBotoes();" id="linkEsquerdaFalso" rendered="#{! (portalDiscenteTouch.idTopicoAnterior > 0)}"/>
					<h:commandLink style="display: none;" onclick="reativaBotoes();" id="linkDireitaFalso" rendered="#{! (portalDiscenteTouch.idProximoTopico > 0 )}"/> --%>
					
					<table align="center">
						<tr>
							<th>
								<c:if test="${portalDiscenteTouch.idTopicoAnterior > 0}">
									<a href="#" onclick="verTopicoAnterior();" data-role="button" data-iconpos="notext" data-icon="arrow-l"></a>
								</c:if>
							</th>
							<th>
								<strong>
									<h:outputText value="#{ portalDiscenteTouch.topicoSelecionado.descricao } 
		    			 				(#{ portalDiscenteTouch.topicoSelecionado.dataFormatada } - #{ portalDiscenteTouch.topicoSelecionado.dataFimFormatada })" 
		    							rendered="#{not empty portalDiscenteTouch.topicoSelecionado.descricao && !portalDiscenteTouch.topicoSelecionado.aulaCancelada}"/>
		    					</strong>
							</th>
							<th>
								<c:if test="${ portalDiscenteTouch.idProximoTopico > 0 }">
									<a href="#" onclick="verProximoTopico();" data-role="button" data-iconpos="notext" data-icon="arrow-r"></a>
								</c:if>
							</th>
						</tr>
					</table>
			
		    		<c:if test="${not empty portalDiscenteTouch.topicoSelecionado.conteudo }">
		    			<p><h:outputText value="#{ portalDiscenteTouch.topicoSelecionado.conteudo }" escape="false" /></p>
		    		</c:if>
		    		
		    		<ul data-role="listview" data-inset="true">
		    			<c:forEach var="material" items="#{ portalDiscenteTouch.topicoSelecionado.materiais }">
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
								<c:if test="${ material.tipoArquivo}">
									<a4j:outputPanel layout="block">						
										<%--<h:commandLink id="idMostrarArquivo" action="#{ downloadMobile.baixarArquivo }" rendered="#{not empty material.arquivo }" target="_blank">
											<h:outputText value="#{ material.nome }" />
											<f:param name="id" value="#{ material.arquivo.idArquivo }"/>
											<f:param name="tid" value="#{ portalDiscenteTouch.turma.id }"/>
										</h:commandLink> --%>
									
										<c:if test="${not empty material.arquivo && not empty material.arquivo.idArquivo}">
											<c:set var="idArquivo" value="${ material.arquivo.idArquivo }"/>
											
											<a href="/sigaa/mobile/touch/download?id=<h:outputText value="#{ material.arquivo.idArquivo }"/>&tid=${portalDiscenteTouch.turma.id}&key=${sf:generateArquivoKey(idArquivo)}" target="_blank" rel="external">
												<h:outputText value="#{ material.nome }" />
											</a> 
										</c:if>
									</a4j:outputPanel>
								</c:if>
								
								<%-- Se o material é uma Indicação de Referência --%>
								<c:if test="${ material.tipoIndicacao }">
									<a4j:outputPanel>
										<h:outputText value="<a href='#{material.url}' target='_blank'> " escape="false" rendered="#{material.site}"/>
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
											<f:param name="tid" value="#{ portalDiscenteTouch.turma.id }"/>
										</h:commandLink>
									</a4j:outputPanel>
								</c:if>
									
								<%-- Se o material é um Vídeo --%>
								<c:if test="${ material.tipoVideo }">
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
					    
					<h:panelGroup rendered="#{empty portalDiscenteTouch.topicosAulas}">
					<p align="center">
						<h:outputText value="Não há tópicos de aula cadastrados para esta turma."/>
					</p>
					</h:panelGroup>
					
					<br/>
					
					<div data-role="navbar" data-iconpos="left" style="padding-bottom: 5px;">
						<ul>
							<li><a href="/sigaa/<h:outputText value="#{ portalDiscenteTouch.paginaListarAulas  }"/>" data-role="button" data-icon="search" data-iconpos="left" data-rel="dialog" data-transition="slidedown">Escolher Aula</a></li>
						</ul>
					</div>
					
					<div data-role="navbar" data-iconpos="left">
						<ul>
							<li><h:commandLink value="Minhas Turmas" id="lnkMinhasTurmas" action="#{ portalDiscenteTouch.listarMinhasTurmas }" /></li>					    	
							<li><h:commandLink value="Notícias" id="lnkVerNoticias" action="#{ noticiaTurmaTouch.listarNoticiasDiscente }" /></li>
						</ul>
					</div>
					
					<h:panelGroup rendered="#{portalDiscenteTouch.mensagemDicaSwipe}">
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
						$("#form-topico-aula-discente\\:lnkMinhasTurmas").attr("data-icon", "minhas-turmas");
						$("#form-topico-aula-discente\\:lnkVerNoticias").attr("data-icon", "noticias");
						
						$("#form-topico-aula-discente\\:lnkSair").attr("data-icon", "sair");
						$("#form-topico-aula-discente\\:lnkInicio").attr("data-icon", "home");
						$("#form-topico-aula-discente\\:lnkVoltar").attr("data-icon", "back");
					</script>
				</div>
							
				<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
			</h:form>
				
			<%@include file="../include/modo_classico.jsp"%>
		</div>
			
			
</f:view>
<%@include file="../include/rodape.jsp"%>


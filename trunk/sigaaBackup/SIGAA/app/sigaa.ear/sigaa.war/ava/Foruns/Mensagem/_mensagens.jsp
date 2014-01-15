
<%@include file="/ava/Foruns/Mensagem/_view_panel.jsp"%>

<c:if test="${ not empty forumMensagemBean.mensagens }">
	<c:forEach items="#{ forumMensagemBean.mensagens }" var="_mensagem" varStatus="loop">
		<div style="position: relative; margin-left: ${ forumMensagemBean.ordem.respostasAninhadas ? (_mensagem.nivel * 20) : 10 }px;">
			<ul class="show">
				<h:panelGroup rendered="#{ forumMensagemBean.ordem.respostasComConteudo }" id="comConteudo">				
					<table style="border: 1px solid #C4D2EB; width: 98%;">													
						<%-- Foto e mensagem --%>
						<tr>
							<td align="center" style="background-color: #EFF3FA;padding: 2px;" valign="top" rowspan="2" width="10%">				 
								<c:if test="${ not empty _mensagem.usuario.idFoto }">
									<img src="${ ctx }/verFoto?idFoto=${ _mensagem.usuario.idFoto }&key=${ sf:generateArquivoKey(_mensagem.usuario.idFoto) }" width="60" height="80"/>
								</c:if>
								<c:if test="${ empty _mensagem.usuario.idFoto }">
									<img src="${ ctx }/img/no_picture.png" width="60" height="80"/>
								</c:if>
							</td>
						
							<%-- Cabeçalho --%>
							<td style="padding: 3px;">
								<b>${ _mensagem.titulo }</b><br/>
								<i> por </i> ${ _mensagem.usuario.pessoa.nome } 
							</td>

							<td align="right" style="border-right: 1px solid #C4D2EB; padding: 3px;">
								 <i><fmt:formatDate value="${ _mensagem.data }" pattern="dd/MM/yyyy HH:mm:ss"/></i>
							</td>
						</tr>
							
						<%-- Conteúdo --%>
						<tr>	  
							<td valign="top" class="conteudoMensagemForum" style="background-color: #FCFCFC;padding: 3px;" colspan="3">
								<br />								
								<p style="text-align: justify;"><h:outputText value="#{ _mensagem.conteudo }" escape="false"/></p>								
								<br />
								<h:commandLink action="#{ forumMensagemBean.viewArquivo }" title="Visualizar Arquivo"	
									id="verArquivoResposta" target="blank" rendered="#{ _mensagem.possuiArquivoAnexo }">
		                      		<f:param name="idArquivo" value="#{ _mensagem.idArquivo }" />
		                      		<h:graphicImage url="/img/view.gif" />Ver Arquivo Anexo
                        		</h:commandLink>
							</td>
						</tr>
						
						<%@include file="/ava/Foruns/Mensagem/_menu.jsp"%>
														
					</table>
				</h:panelGroup>
				
				<br/>
				
				<h:panelGroup rendered="#{ !forumMensagemBean.ordem.respostasComConteudo }" id="semConteudo">				
					<table style="width:90%">		
						<tr>
							<td>
								<a4j:commandLink action="#{ forumMensagemBean.mensagemShowModal }" id="btnView"
				               		oncomplete="Richfaces.showModalPanel('showModal')" immediate="true" reRender="showModal">
									<h:outputText value="#{ _mensagem.titulo }" />
									<f:param name="id" value="#{ _mensagem.id }" />
								</a4j:commandLink> 
								
								<b> por </b> 
									${ _mensagem.usuario.pessoa.nome } 
								
								<b> em </b> 
									<fmt:formatDate value="${ _mensagem.data }" pattern="dd/MM/yyyy HH:mm:ss"/>
							</td>
						</tr>
					</table>
				</h:panelGroup>
				
			</ul>
		</div>
	</c:forEach>
	
	<br/>
	
	<h:form id="paginacaoForm">
		<div style="text-align: center;">
			
				<h:graphicImage value="/img/primeira_des.gif" alt="Primeira" rendered="#{ paginacao.paginaAtual == 0 }"/>
				<h:commandButton image="/img/primeira.gif" title="Primeira" actionListener="#{ forumMensagemBean.primeiraPagina }" rendered="#{ paginacao.paginaAtual != 0 }" />
			                 
				<h:graphicImage value="/img/voltar_des.gif" alt="Voltar" rendered="#{ paginacao.paginaAtual == 0 }"/>
			    <h:commandButton image="/img/voltar.gif" title="Voltar" actionListener="#{ forumMensagemBean.previousPage }" rendered="#{ paginacao.paginaAtual > 0 }"/>
			 
				    	<h:selectOneMenu value="#{ paginacao.paginaAtual }" valueChangeListener="#{ forumMensagemBean.changePage }" onchange="submit()" immediate="true">
							<f:selectItems id="paramPagina" value="#{ paginacao.listaPaginas}"/>
				    	</h:selectOneMenu>
			 
			    <h:commandButton image="/img/avancar.gif" title="Avançar" actionListener="#{ forumMensagemBean.nextPage }"  rendered="#{ paginacao.paginaAtual < (paginacao.totalPaginas - 1) }"/>
			    <h:graphicImage value="/img/avancar_des.gif" alt="Avançar" rendered="#{ paginacao.paginaAtual == (paginacao.totalPaginas - 1) }"/>
			    
	    		<h:commandButton image="/img/ultima.gif" title="Última" actionListener="#{ forumMensagemBean.ultimaPagina }" rendered="#{ paginacao.paginaAtual < (paginacao.totalPaginas - 1) }" />
				<h:graphicImage value="/img/ultima_des.gif" alt="Última" rendered="#{ paginacao.paginaAtual == (paginacao.totalPaginas - 1) }"/>
	    	
	    	<br/><br/>
	 
	    	<em><h:outputText value="#{ paginacao.totalRegistros }"/> Registro(s) Encontrado(s)</em>
		</div>
	</h:form>
	
</c:if>


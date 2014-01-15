<%@include file="/ava/cabecalho.jsp"%>

<a4j:keepAlive beanName="chatTurmaBean" />
<f:view>
	
	<%@include file="/ava/menu.jsp"%>
	
	<h:form id="form">
		<c:set var="chats" value="#{ chatTurmaBean.listagem }" />
	
		<fieldset>		
			<legend>Chats Agendados</legend>
	
				<h:panelGroup layout="block" styleClass="menu-botoes" style="text-align:center;width:210px;margin: 0 auto;" rendered="#{ chatTurmaBean.permiteCadastrarChat }">
					<ul class="menu-interno">
							<li class="botao-medio novoChat;">
								<h:commandLink action="#{ chatTurmaBean.agendarNovoChatTurma }">
									<h:outputText value="<p style='margin-left:20px;font-variant:small-caps;font-size:1.3em;font-weight:bold;'>Agendar Novo Chat</p>" escape="false" /> 
								</h:commandLink>
							</li>
					</ul>	
				</h:panelGroup>
			<div style="clear:both;"></div>	
			 
			<c:if test="${ empty chats }">
				<p class="empty-listing">Nenhum item foi encontrado.</p>
			</c:if>
			 
			<c:if test="${ not empty chats }">
				<div class="infoAltRem">
					<img src="${ctx}/ava/img/zoom.png"/>: Visualizar
					<c:if test="${ chatTurmaBean.permiteCadastrarChat}">
						<img src="${ctx}/ava/img/page_edit.png"/>: Alterar
						<img src="${ctx}/ava/img/bin.png"/>: Remover
					</c:if>
				</div>
			</c:if>
			 
			<h:panelGroup id="listaChats">
				<c:if test="${ not empty chats }">
					<table class="listing">
						<thead>
							<tr>
								<th>Título</th>
								<th>Início</th>
								<th>Fim</th>
								<th>Conteúdo Publicado</th>
								<th></th>
								<c:if test="${ chatTurmaBean.permiteCadastrarChat }">
									<th></th>
									<th></th>
								</c:if>
							</tr>
						</thead>
						<tbody>							
							<a4j:repeat var="material" value="#{ chats }" rowKeyVar="idx">
								<tr class="${ idx % 2 == 0 ? 'even' : 'odd' }">
								
									<td width="40%" style="border-left: thin;">
										<a4j:commandLink rendered="#{ !material.videoChat }" action="#{ chatTurmaBean.createChatParam }" 
											oncomplete="window.open('/shared/EntrarChat?idchat=#{ material.id }&idusuario=#{ chatTurmaBean.usuarioLogado.id }&passkey=#{ chatTurmaBean.chatPassKey }&chatName=#{ material.titulo }&origem=turmaVirtual', 'chat_#{ material.id }', 'height=485,width=685,location=0,resizable=0'); return false;"
											reRender="listaChats">
											<h:outputText value="#{ material.titulo }"/>
											<f:param name="id" value="#{ material.id }"/> 
										</a4j:commandLink>
										
										<a4j:commandLink rendered="#{ material.videoChat }" action="#{ chatTurmaBean.createChatParam }" oncomplete='exibirJanelaVideoChat("#{ material.id }", #{chatTurmaBean.usuarioLogado.id}, "#{chatTurmaBean.chatPassKey}", "#{ material.titulo }", "#{ turmaVirtual.usuarioLogado.pessoa.nome }", #{ (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) ? "true" : "false" }, "#{ turmaVirtual.enderecoServidorVideo }");'>
											<h:outputText value="#{ material.titulo }"/> 
											<f:param name="id" value="#{ material.id }"/>
										</a4j:commandLink>
										<br/>
										<h:outputText value="<b>Aula: </b><i>" escape="false" rendered="#{not empty material.aula.descricao}" />
										<h:outputText value="#{ material.aula.descricao }" rendered="#{not empty material.aula.descricao}" />
										<h:outputText value="</i> <br/>" escape="false" rendered="#{not empty material.aula.descricao}" />										
										<h:outputText value="#{ material.descricao }" escape="false"/>
									</td>
									
									<td width="20%" align="center"  style="border-left: thin;">
										<h:outputText value="#{ material.dataInicio }"/>&nbsp;
										<h:outputText value="#{ material.horaInicio }">
											<f:convertDateTime pattern="HH:mm"/>
										</h:outputText>										
									</td>
									
									<td width="20%" align="center">
										<h:outputText value="#{ material.dataFim }"/>&nbsp;
										<h:outputText value="#{ material.horaFim }">
											<f:convertDateTime pattern="HH:mm"/>
										</h:outputText>
									</td>
	
									<td width="10%" align="center">
										<h:outputText value="#{ material.publicarConteudo ? 'Sim' : 'Não' }" />
									</td>
		
									<%-- Ações para os chats --%>								
									<td class="icon">
										<h:commandLink action="#{ chatTurmaBean.mostrar }">
											<f:param name="id" value="#{ material.id }"/><h:graphicImage value="/ava/img/zoom.png" title="Visualizar"/>
										</h:commandLink>
									</td>
									
									<c:if test="${ chatTurmaBean.permiteCadastrarChat  }">
										<td class="icon">
											<h:commandLink id="lnkEditar" action="#{ chatTurmaBean.editar }" title="Alterar" styleClass="naoImprimir"
												rendered="#{ turmaVirtual.docente }">
													<h:graphicImage value="/ava/img/page_edit.png"/>
													<f:param name="id" value="#{ material.id }"/>
											</h:commandLink>
										</td>	
										
										<td class="icon">
											<h:commandLink id="lnkRemover" action="#{ chatTurmaBean.inativar }" title="Remover" 
												onclick="return(confirm('Deseja realmente excluir este chat?'));" styleClass="naoImprimir"
												rendered="#{ turmaVirtual.docente }">
												<h:graphicImage value="/ava/img/bin.png"/>
												<f:param name="id" value="#{ material.id }"/>
											</h:commandLink>
										</td>
									</c:if>
									
								</tr>
							</a4j:repeat>
						</tbody>
					</table>
				  </c:if>
				</h:panelGroup>			
		</fieldset>
	</h:form>
</f:view>
<%@include file="/ava/rodape.jsp"%>

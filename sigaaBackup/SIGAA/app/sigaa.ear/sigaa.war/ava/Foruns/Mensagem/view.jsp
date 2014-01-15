<%@include file="/ava/cabecalho.jsp" %>

<f:view>
	<%@include file="/ava/menu.jsp" %>

<style>
<!--
.formAva th { 
	font-weight: bold!important;
	text-align: left!important;
	width: 5%!important;
	}
-->
</style>

	<h:form id="form">

		<br/>
		<div align="right">
				<t:selectOneMenu id="ordem" value="#{ forumMensagemBean.ordem.id }" title="Ordem" 
					valueChangeListener="#{ forumMensagemBean.changeOrdenarMensagens }" onchange="submit();">
					<f:selectItems value="#{ ordemMensagensForumBean.allCombo }" />
				</t:selectOneMenu>
				<span class="descricao-campo"></span>
		</div>


		<fieldset>		
			
			<table class="formAva" style="margin-left:0">
				<tr>
					<th>Assunto:</th>
					<td>
						<h:outputText value="#{ forumMensagemBean.obj.titulo }" />
					</td>
				</tr>
			
				<tr>
					<th>Mensagem: </th>
					<td class="conteudoMensagemForum">
						<h:outputText value="#{ forumMensagemBean.obj.conteudo }"
							escape="false" />
					</td>
				</trtr>
				
				<tr>		
					<th>Autor(a): </th>
					<td><h:outputText value="#{ forumMensagemBean.obj.usuario.pessoa.nome }" escape="false"/></td>
				</li>
				
						
				<tr>		
					<th>Arquivo: </th>
					<td>
						<h:commandLink action="#{ forumMensagemBean.viewArquivo }" title="Visualizar Arquivo"	id="verArquivo" target="blank" 
							rendered="#{ not empty forumMensagemBean.obj.idArquivo }">
		                      <f:param name="idArquivo" value="#{ forumMensagemBean.obj.idArquivo }" />
                              <h:graphicImage url="/img/view.gif" />
                        </h:commandLink>																		
					</td>
				</tr>
			
			
				<tr>
					<th>Criado em: </th>
					<td>
						<h:outputText value="#{ forumMensagemBean.obj.data }">
							<f:convertDateTime pattern="dd/MM/yyyy HH:mm:ss" />
						</h:outputText>
					</td>
				</tr>
			</table>
		
			<div class="botoes">
			
				<div class="form-actions">
					<h:commandButton action="#{ forumMensagemBean.responder }"
						value="Responder #{ forumMensagemBean.obj.forum.tipo.labelMensagem }" id="cmdResponderNoTopico"
						rendered="#{ forumMensagemBean.obj.forum.tipo.permiteResponderTopico }">
						<f:setPropertyActionListener value="#{ forumMensagemBean.obj.id }" target="#{ forumMensagemBean.mensagemRespondida.id }" />
						<f:param name="responder" value="true"></f:param>
					</h:commandButton>
				</div>
				
				<div class="other-actions">
					<h:commandButton action="#{ forumMensagemBean.atualizar }"
						value="Editar " id="cmdEditar" immediate="true"
						rendered="#{forumMensagemBean.usuarioLogado.id == forumMensagemBean.obj.usuario.id}">
					</h:commandButton>
					
					<h:commandButton action="#{ forumBean.view }" value="<< Voltar"	id="cmdView" immediate="true">
						<f:setPropertyActionListener value="#{ forumBean.obj.forum }" target="#{ forumBean.obj }" />
					</h:commandButton>					
				</div>
				
			</div>
			
		</fieldset>
		
		<c:if test="${ forumMensagemBean.permiteVerDiscussaoTopico }">
			<%-- Lista de respostas para o tópico --%>
			<%@include file="/ava/Foruns/Mensagem/_mensagens.jsp"%>
		</c:if>
		
		<c:if test="${ !forumMensagemBean.permiteVerDiscussaoTopico }">
			<div class="descricaoOperacao">
				Para visualizar a discussão deste tópico você deve primeiro postar uma mensagem. Isto permite que a primeira mensagem de cada participante do tópico seja original e independente.
			</div>
		</c:if>
		
	</h:form>
	
</f:view>
<%@include file="/ava/rodape.jsp" %>

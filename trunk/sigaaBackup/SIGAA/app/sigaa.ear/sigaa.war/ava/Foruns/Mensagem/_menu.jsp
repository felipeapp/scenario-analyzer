<%-- Menu para cada mensagem --%>
<tr>
	<td colspan="3"></td>
</tr>
<tr style="background-color: #C4D2EB;">
	<td colspan="3" align="right" style="padding: 2px; font-size: x-small;">
		<h:commandLink action="#{ forumMensagemBean.atualizar }" value=" Editar " rendered="#{ forumMensagemBean.usuarioLogado.id == _mensagem.usuario.id }">
			<f:setPropertyActionListener value="#{ _mensagem }" target="#{ forumMensagemBean.obj }" />
		</h:commandLink>
		
		<h:outputText value=" " /> 		
		<h:commandLink action="#{ forumMensagemBean.preInterromper }" value=" Interromper " 
			rendered="#{ forumMensagemBean.usuarioLogado.id == _mensagem.usuario.id && _mensagem.forum.tipo.permiteInterromperTopico }">
			<f:setPropertyActionListener value="#{ _mensagem }" target="#{ forumMensagemBean.obj }" />
		</h:commandLink>
		
		<h:outputText value=" " />
		<h:commandLink action="#{ forumMensagemBean.preRemover }" value=" Apagar " rendered="#{ forumMensagemBean.usuarioLogado.id == _mensagem.usuario.id }">
			<f:setPropertyActionListener value="#{ _mensagem }" target="#{ forumMensagemBean.obj }" />
		</h:commandLink>
		
		<h:outputText value=" " />		 	
		<h:commandLink action="#{ forumMensagemBean.responder }" value=" Responder " rendered="#{ _mensagem.forum.tipo.permiteComentarResposta }">
			<f:setPropertyActionListener value="#{ _mensagem }" target="#{ forumMensagemBean.mensagemRespondida }"/> 
		</h:commandLink>
	</td>
</tr>

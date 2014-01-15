<%@include file="/ava/cabecalho.jsp" %>

<f:view>
	<%@include file="/ava/menu.jsp" %>

	<h:form id="form">
		<fieldset>
			<legend>Interromper Mensagem</legend>
			
			<ul class="show">				
				<li>		
					<div class="descricaoOperacao">
						<b>Atenção:</b><br/>
						Ao interromper esta mensagem ela será convertida em um novo tópico do fórum e todas as respostas associadas a ela serão movidas para este novo tópico também.						
					</div>
				</li>						
			</ul>
			
			<div class="botoes">
				<div class="form-actions">
					<h:commandButton action="#{ forumMensagemBean.interromper }" value="Confirmar Interrupção" id="cmdInterromperMensagem" 
						rendered="#{forumMensagemBean.usuarioLogado.id == forumMensagemBean.obj.usuario.id}">
						<f:setPropertyActionListener value="#{ forumMensagemBean.obj.id }" target="#{ forumMensagemBean.obj.id }"/> 
					</h:commandButton>		
				</div>
				<div class="other-actions">	
					<h:commandButton action="#{ forumMensagemBean.cancelar }" value="Cancelar" id="cmdCancelar" />
				</div>
			</div>	
		</fieldset>
		
		<%@include file="/ava/Foruns/Mensagem/_mensagens.jsp"%>
		
	</h:form>
	
</f:view>
<%@include file="/ava/rodape.jsp" %>

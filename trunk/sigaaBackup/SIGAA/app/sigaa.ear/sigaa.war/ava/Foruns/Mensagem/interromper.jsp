<%@include file="/ava/cabecalho.jsp" %>

<f:view>
	<%@include file="/ava/menu.jsp" %>

	<h:form id="form">
		<fieldset>
			<legend>Interromper Mensagem</legend>
			
			<ul class="show">				
				<li>		
					<div class="descricaoOperacao">
						<b>Aten��o:</b><br/>
						Ao interromper esta mensagem ela ser� convertida em um novo t�pico do f�rum e todas as respostas associadas a ela ser�o movidas para este novo t�pico tamb�m.						
					</div>
				</li>						
			</ul>
			
			<div class="botoes">
				<div class="form-actions">
					<h:commandButton action="#{ forumMensagemBean.interromper }" value="Confirmar Interrup��o" id="cmdInterromperMensagem" 
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

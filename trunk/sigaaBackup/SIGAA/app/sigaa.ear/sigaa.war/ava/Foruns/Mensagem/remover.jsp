<%@include file="/ava/cabecalho.jsp" %>

<f:view>
	<%@include file="/ava/menu.jsp" %>

	<h:form id="form">
		<fieldset>
			<legend>Remover Mensagem</legend>
			
			<ul class="show">
				<li>		
					<div class="descricaoOperacao">
						<b>Aten��o:</b><br/>
						Ao remover esta mensagem todas as respostas associadas a ela tamb�m ser�o removidas.						
					</div>
				</li>						
			</ul>
			
			<div class="botoes">
				<div class="form-actions">
					<h:commandButton action="#{ forumMensagemBean.remover }" value="Confirmar Remo��o" id="cmdRemoverMensagem">
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

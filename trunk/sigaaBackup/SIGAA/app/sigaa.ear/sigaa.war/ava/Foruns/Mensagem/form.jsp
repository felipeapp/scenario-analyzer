<%@include file="/ava/cabecalho.jsp" %>

<f:view>
	<%@include file="/ava/menu.jsp" %>

	<h:form id="form" enctype="multipart/form-data">
		<fieldset>
			<legend><h:outputText value="#{ forumMensagemBean.confirmButton }" /></legend>
			
			<%@include file="/ava/Foruns/Mensagem/_form.jsp" %>
			
			<div class="botoes">
				<div class="form-actions">
					<h:commandButton action="#{ forumMensagemBean.cadastrar }" value="#{ forumMensagemBean.confirmButton }" id="btnSalvar"/>
				</div>
				<div class="other-actions">				
					<c:if test="${forumMensagemBean.obj.topico.id == 0 }">
						<h:commandButton action="#{ forumBean.view }" value="<< Voltar" id="btnVoltar" immediate="true" rendered="#{ forumMensagemBean.obj.forum.id > 0 }">
							<f:setPropertyActionListener value="#{ forumMensagemBean.obj.forum }" target="#{ forumBean.obj }"/>
						</h:commandButton>
					</c:if>
					 
					<h:commandButton action="#{ forumMensagemBean.view }" value="<< Voltar" id="btnView" immediate="true" rendered="#{ forumMensagemBean.obj.topico.id > 0 }">
						<f:setPropertyActionListener value="#{ forumMensagemBean.obj.topico }" target="#{ forumMensagemBean.obj }"/>
					</h:commandButton>
					<h:commandButton action="#{ forumMensagemBean.cancelar }" value="Cancelar" id="btnCancelar" immediate="true" onclick="#{confirm}"/>
					
				</div>
				<div class="required-items">
					<span class="required"></span>Campos de Preenchimento Obrigatório
				</div>
			</div>
		
		</fieldset>
	
	</h:form>
</f:view>
<%@include file="/ava/rodape.jsp"%>

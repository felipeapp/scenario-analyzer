<%@include file="/ava/cabecalho.jsp" %>

<f:view>
	<%@include file="/ava/menu.jsp" %>

	<h:form id="form" enctype="multipart/form-data">
		<fieldset>
			<legend><h:outputText value="#{ forumBean.confirmButton }" /> Fórum</legend>
			
			<ul class="form">
				<%@include file="/ava/Foruns/_form.jsp" %>
			</ul>
			
			<div class="botoes">
				<div class="form-actions">
					<h:commandButton action="#{ forumBean.cadastrar }" value="#{ forumBean.confirmButton }" id="btnSalvar"/>
				</div>
				<div class="other-actions">
					<h:commandButton action="#{ forumBean.cancelar }" value="Cancelar" immediate="true" id="btnCancelar" onclick="#{confirm}"/> 
					<%-- h:commandButton action="#{ forumBean.listar }" value="<< Voltar" immediate="true" id="btnListar" / --%> 
				</div>
				<div class="required-items">
					<span class="required"></span>Itens de Preenchimento Obrigatório
				</div>
			</div>
		
		</fieldset>
	
	</h:form>
</f:view>
<%@include file="/ava/rodape.jsp" %>

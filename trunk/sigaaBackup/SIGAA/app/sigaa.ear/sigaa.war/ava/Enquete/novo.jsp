<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form id="formAva">


<fieldset>
	<legend>Nova Enquete</legend>
	
	<%@include file="/ava/Enquete/_form.jsp" %>
	
	<div class="botoes">
		<div class="form-actions">
			<h:commandButton action="#{enquete.cadastrar}" value="Cadastrar" /> 
		</div>
		<div class="other-actions">
			<h:commandButton action="#{ enquete.listar }" value="<< Voltar"/> 
			<h:commandButton action="#{ enquete.cancelar }" onclick="#{confirm}"  value="Cancelar"/> 	
		</div>
		<div class="required-items">
			<span class="required">&nbsp;</span>
			Campos de Preenchimento Obrigatório.
		</div>
	</div>

</fieldset>

</h:form>
</f:view>
<%@include file="/ava/rodape.jsp" %>

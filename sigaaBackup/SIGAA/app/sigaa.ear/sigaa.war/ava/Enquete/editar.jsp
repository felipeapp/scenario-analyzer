<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form id="formAva">

<fieldset>
	<legend>Editar Enquete</legend>
	
	<%@include file="/ava/Enquete/_form.jsp" %>
	
	<input type="hidden" name="id" value="${ enquete.object.id }"/>
	
	<div class="botoes">
		<div class="form-actions">
			<h:commandButton action="#{enquete.atualizar}" value="Atualizar Dados" /> 
		</div>
		<div class="right-buttons">
			<h:commandButton action="#{ enquete.mostrar }" value="Mostrar"></h:commandButton>
			<h:commandButton action="#{ enquete.listar }" value="<< Voltar"/> 
			<h:commandButton action="#{ enquete.cancelar }" onclick="#{confirm}"  value="Cancelar"/> 
		</div>
		<div class="required-items">
			<span class="required">&nbsp;</span>
			Campos de Preenchimento Obrigatório. Devem existir ao menos duas respostas.
		</div>
	</div>

</fieldset>

</h:form>
</f:view>
<%@include file="/ava/rodape.jsp" %>


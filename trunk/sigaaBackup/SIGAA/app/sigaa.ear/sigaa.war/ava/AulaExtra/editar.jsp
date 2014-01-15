<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form id="formAva">


<h:messages showDetail="true" />

<fieldset>
	<legend>Editar Aula Extra</legend>
	
	<%@include file="/ava/AulaExtra/_form.jsp" %>
	
	<h:inputHidden value="#{ aulaExtra.object.id }"/>
	
	<div class="botoes">
		<div class="form-actions">
			<h:commandButton action="#{aulaExtra.atualizar}" value="Atualizar Dados" /> 
		</div>
		<div class="other-actions">
			<h:commandButton action="#{ aulaExtra.listar }" value="<< Voltar"/> 
			<h:commandButton action="#{ aulaExtra.cancelar }" onclick="#{confirm}" value="Cancelar"/> 
		</div>
		<div class="required-items">
			<span class="required">&nbsp;</span>
			Itens de Preenchimento Obrigatório
		</div>
	</div>

</fieldset>

</h:form>


</f:view>
<%@include file="/ava/rodape.jsp" %>

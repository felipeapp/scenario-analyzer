<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form id="formAva">


<h:messages showDetail="true" />

<fieldset>
	<legend>Editar Aula de Ensino Individual</legend>
	
	<%@include file="/ava/AulaEnsinoIndividual/_form.jsp" %>
	
	<h:inputHidden value="#{ aulaEnsinoIndividual.object.id }"/>
	
	<div class="botoes">
		<div class="form-actions">
			<h:commandButton action="#{aulaEnsinoIndividual.atualizar}" value="Atualizar Dados" /> 
		</div>
		<div class="other-actions">
			<h:commandButton action="#{ aulaEnsinoIndividual.listar }" value="<< Voltar"/> 
			<h:commandButton action="#{ aulaEnsinoIndividual.cancelar }" onclick="#{confirm}" value="Cancelar"/> 
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

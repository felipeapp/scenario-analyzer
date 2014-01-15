<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<a4j:keepAlive beanName="dataAvaliacao"/>
<h:form id="formAva">

<fieldset>
	<legend>Editar Data de Avaliação</legend>
	
	<%@include file="/ava/DataAvaliacao/_form.jsp" %>
	
	<h:inputHidden value="#{ dataAvaliacao.object.id }"/>
	
	<div class="botoes">
		<div class="form-actions">
			<h:commandButton action="#{dataAvaliacao.atualizar}" value="Atualizar Dados" /> 
		</div>
		<div class="right-buttons">
			<h:inputHidden value="#{ dataAvaliacao.object.id }" id="id"/>
			<h:commandButton action="#{ dataAvaliacao.mostrar }" value="Mostrar"/>|
			<h:commandButton action="#{ dataAvaliacao.listar }" value="<< Voltar"/> 
			<h:commandButton action="#{ dataAvaliacao.cancelar }" onclick="#{confirm}"  value="Cancelar"/> 	
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

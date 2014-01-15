
<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<a4j:keepAlive beanName="dataAvaliacao"/>
<h:form id="formAva" prependId="false">

<fieldset>
	<legend>Nova Data de Avaliação</legend>
	
	<%@include file="/ava/DataAvaliacao/_form.jsp" %>
	
	<div class="botoes">
		<div class="form-actions">
			<h:commandButton action="#{dataAvaliacao.cadastrar}" value="Cadastrar" /> 
		</div>
		<div class="other-actions">
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
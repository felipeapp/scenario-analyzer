<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form id="formAva">


<h:messages showDetail="true" />

<fieldset>
	<legend>Editar Permissão</legend>

	<%@include file="/ava/PermissaoAva/_form.jsp" %>
	
	<h:inputHidden value="#{ permissaoAva.object.id }"/>
	
	<div class="botoes">
		<div class="form-actions">
			<h:commandButton action="#{permissaoAva.atualizar}" value="Atualizar Dados" /> 
		</div>
		<div class="other-actions">
			<h:commandButton action="#{ permissaoAva.listar }" value="<< Voltar"/> 
		</div>
	</div>
</fieldset>


</h:form>
</f:view>
<%@include file="/ava/rodape.jsp" %>

<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form>


<h:messages showDetail="true" />

<fieldset>
	<legend>Editar Fórum</legend>
	
	<%@include file="/ava/Forum/_form.jsp" %>
	
	<h:inputHidden value="#{ forum.object.id }"/>
	
	<div class="botoes">
		<div class="form-actions">
			<h:commandButton action="#{forum.atualizar}" value="Atualizar Dados" /> 
		</div>
		<div class="other-actions">
			<h:commandLink action="#{ forum.mostrar }" value="Mostrar">
			<f:param name="id" value="#{ forum.object.id }"/>
			</h:commandLink> | <h:commandLink action="#{ forum.listar }" value="Voltar"/> 
		</div>
		<div class="required-items">
			<span class="required"/>
			Itens de Preenchimento Obrigatório
		</div>
	</div>

</fieldset>

</h:form>
</f:view>
<%@include file="/ava/rodape.jsp" %>

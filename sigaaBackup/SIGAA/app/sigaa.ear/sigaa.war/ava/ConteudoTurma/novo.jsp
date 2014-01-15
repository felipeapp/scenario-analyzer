<%@include file="/ava/cabecalho.jsp" %>

<f:view>

<a4j:keepAlive beanName="conteudoTurma" />

<%@include file="/ava/menu.jsp" %>
<h:form>


<h:messages showDetail="true" />

<fieldset>
	<legend>Novo Conteúdo</legend>
	
	<%@include file="/ava/ConteudoTurma/_form.jsp" %>
	
	<div class="botoes">
		<div class="form-actions">
			<h:commandButton action="#{conteudoTurma.cadastrar}" value="Cadastrar" /> 
		</div>
		<div class="other-actions">
			<h:commandButton action="#{ conteudoTurma.listar }" value="<< Voltar"/> 
			<h:commandButton action="#{ conteudoTurma.cancelar }" onclick="#{confirm}" value="Cancelar"/> 
		</div>
		<div class="required-items">
			<span class="required">&nbsp;</span>
			Campos de Preenchimento Obrigatório
		</div>
	</div>

</fieldset>

</h:form>

</f:view>
<%@include file="/ava/rodape.jsp" %>
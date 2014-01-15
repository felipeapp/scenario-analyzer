<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<a4j:keepAlive beanName="forumMensagem" />
<%@include file="/ava/menu.jsp" %>
<h:form>


<h:messages showDetail="true" />

<fieldset>	
	<legend>Dados da Mensagem</legend>

	<%@include file="/ava/ForumMensagem/_form.jsp" %>
	
	<div class="botoes">
		<div class="form-actions">
			<h:commandButton action="#{forumMensagem.cadastrarTopico}" value="Cadastrar" /> 
		</div>
		<div class="other-actions">
			<h:commandButton action="#{ forumMensagem.listar }" value="<< Voltar"/> 
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
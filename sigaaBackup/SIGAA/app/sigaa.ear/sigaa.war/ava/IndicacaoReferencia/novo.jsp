<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form id="formAva">


<h:messages showDetail="true" />

<fieldset>
	<legend>Nova Referência</legend>
	
	<%@include file="/ava/IndicacaoReferencia/_form.jsp" %>
	
	<div class="botoes">
		<div class="form-actions">
			<h:commandButton action="#{indicacaoReferencia.cadastrar}" value="Cadastrar" /> 
		</div>
		<div class="other-actions">
			<h:commandButton action="#{ indicacaoReferencia.listar }" value="<< Voltar"/> 
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
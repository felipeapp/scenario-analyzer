<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form id="formAva">
	
<h:messages showDetail="true" />

<fieldset>
	<legend>Editar Referência</legend>
	
	<%@include file="/ava/IndicacaoReferencia/_form.jsp" %>
	
	<input type="hidden" name="id" value="${ indicacaoReferencia.object.id }"/>
	
	<div class="botoes">
		<div class="form-actions">
			<h:commandButton action="#{indicacaoReferencia.atualizar}" value="Atualizar Dados" /> 
		</div>
		
		<div class="right-buttons">
			<h:commandButton action="#{ indicacaoReferencia.voltarAnterior }" value="<< Voltar"/> 
			| <h:commandButton action="#{ indicacaoReferencia.mostrar }" value="Mostrar" />
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
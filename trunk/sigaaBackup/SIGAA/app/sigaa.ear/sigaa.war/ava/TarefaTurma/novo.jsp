<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form enctype="multipart/form-data" id="formAva">

<fieldset>
	<legend>Nova Tarefa</legend>
	
	<c:set var="exibirTurmas" value="true"/>
	<%@include file="/ava/TarefaTurma/_form.jsp" %>
	
	<div class="botoes">
		<div class="form-actions">	
			<h:commandButton value="Cadastrar" action="#{tarefaTurma.cadastrar}" onclick="return(mensagemNotas());"/>		
		</div>
		<div class="other-actions">
			<h:commandButton action="#{ tarefaTurma.listar }" value="<< Voltar"/> 
			<h:commandButton action="#{ tarefaTurma.cancelar }" onclick="#{confirm}"  value="Cancelar"/> 
		</div>
		<div class="required-items">
			<span class="required">&nbsp;</span>
			Campos de Preenchimento Obrigatório
		</div>
	</div>
<br/><br/><br/><br/><br/>

</fieldset>

</h:form>
</f:view>

<%@include file="/ava/rodape.jsp" %>

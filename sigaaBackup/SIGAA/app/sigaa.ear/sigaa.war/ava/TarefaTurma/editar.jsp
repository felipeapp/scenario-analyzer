<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form id="formAva" enctype="multipart/form-data">

<h:messages showDetail="true" />

<fieldset>
	<legend>Atualizar</legend>
	
	<c:set var="exibirTurmas" value="false"/>
	<%@include file="/ava/TarefaTurma/_form.jsp" %>
	<br/><br/>
	<div class="botoes">
		<div class="form-actions">
			<h:commandButton action="#{tarefaTurma.atualizar}" onclick="return(mensagemAtualizar());" value="Atualizar" /> 
		</div>
		<div class="other-actions">
			<h:commandButton action="#{ tarefaTurma.listar }" value="<< Voltar"/>
			<h:commandButton action="#{ tarefaTurma.cancelar }" onclick="#{confirm}"  value="Cancelar"/> 
		</div>
		<div class="required-items">
			<span class="required"></span>
			Campos de Preenchimento Obrigatório
		</div>
	</div>

</fieldset>
<br/><br/><br/><br/><br/>
</h:form>
</f:view>
<%@include file="/ava/rodape.jsp" %>

<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form enctype="multipart/form-data" id="formAva">

<fieldset>
	<legend>Alterar Video</legend>
	
	<c:set var="exibirTurmas" value="false"/>
	<%@include file="/ava/VideoTurma/form.jsp" %>

		<div class="botoes" style="width:97%!important;margin-left:auto;margin-right:auto;">
			<div class="form-actions" style="width:250px;text-align:left;">
				<h:commandButton action="#{ videoTurma.salvarVideo }" value="Alterar" />
			</div>
			<div class="other-actions" style="width:auto!important;">
				<h:commandButton action="#{ videoTurma.listarVideos }" value="<< Voltar" /> 
				<h:commandButton action="#{ videoTurma.cancelar }" onclick="#{confirm}"  value="Cancelar"/> 
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

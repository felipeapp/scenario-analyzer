<%@include file="/ava/cabecalho.jsp" %>

<f:view>

<a4j:keepAlive beanName="conteudoTurma" />

<%@include file="/ava/menu.jsp" %>
<h:form>

<h:messages showDetail="true" />

<fieldset>
	<legend>Editar Conteúdo</legend>
	
	<%@include file="/ava/ConteudoTurma/_form.jsp" %>
	
	<h:inputHidden value="#{ conteudoTurma.object.id }"/>
	
	<div class="botoes">
	
		<input type="hidden" name="id" value="${ conteudoTurma.object.id }"/>
	
		<div class="form-actions">
			<h:commandButton action="#{conteudoTurma.atualizar}" value="Atualizar Dados" /> 
		</div>
		<div class="right-buttons">
			<h:commandButton action="#{ conteudoTurma.mostrar }" value="Mostrar">
			</h:commandButton> 
			<h:commandButton action="#{ conteudoTurma.listar }" value="<< Voltar"/> 
			<h:commandButton action="#{ conteudoTurma.cancelar }" onclick="#{confirm}" value="Cancelar"/> 
		</div>
		<div class="required-items">
			<span class="required"></span>
			Campos de Preenchimento Obrigatório
		</div>
	</div>

</fieldset>

</h:form>

</f:view>
<%@include file="/ava/rodape.jsp" %>


<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
tinyMCE.init({
	mode : "textareas", theme : "advanced", width : "420", height : "250", language : "pt",
	theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
	theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
	theme_advanced_buttons3 : "",
	plugins : "searchreplace,contextmenu,advimage",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left"
});
</script>	
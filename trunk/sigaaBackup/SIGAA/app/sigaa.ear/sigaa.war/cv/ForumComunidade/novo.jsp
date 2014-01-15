<%@include file="/cv/cabecalho.jsp" %>

<f:view>
<h:form>

<%@include file="/cv/menu.jsp" %>

<h:messages showDetail="true" />

<fieldset>
	<legend>Novo Fórum</legend>
	
	<%@include file="/cv/ForumComunidade/_form.jsp" %>
	
	<div class="botoes">
		<div class="form-actions">
			<h:commandButton action="#{forum.cadastrar}" value="Cadastrar" /> 
		</div>
		<div class="other-actions">
			<h:commandLink action="#{ forum.listar }" value="Voltar"/> 
		</div>
		<div class="required-items">
			<span class="required"/>
			Itens de Preenchimento Obrigatório
		</div>
	</div>

</fieldset>

</h:form>
<%@include file="/cv/rodape.jsp" %>
</f:view>

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
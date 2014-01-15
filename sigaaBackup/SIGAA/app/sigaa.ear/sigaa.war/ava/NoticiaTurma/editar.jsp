<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form id="formAva">


<h:messages showDetail="true" />

<fieldset>
	<legend>Editar Notícia</legend>
	
	<%@include file="/ava/NoticiaTurma/_form.jsp" %>
	
	<input type="hidden" name="id" value="${ noticiaTurma.object.id }"/>
	<h:inputHidden value="#{noticiaTurma.object.id}"/>
	
	<div class="botoes">
		<div class="form-actions">
			<h:commandButton action="#{noticiaTurma.atualizar}" value="Atualizar Dados" /> 
		</div>
		<div class="right-buttons">
			<h:commandButton action="#{ noticiaTurma.mostrar }" value="Mostrar">
			</h:commandButton> | <h:commandButton action="#{ noticiaTurma.listar }" value="<< Voltar"/> 
		</div>
		<div class="required-items">
			<span class="required">&nbsp;</span>
			Itens de Preenchimento Obrigatório.
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

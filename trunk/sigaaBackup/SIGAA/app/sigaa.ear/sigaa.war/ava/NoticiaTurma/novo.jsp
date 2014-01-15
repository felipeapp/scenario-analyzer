<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form id="formAva">


<h:messages showDetail="true" />

<fieldset>
	<legend>Nova Notícia</legend>
	
	<%@include file="/ava/NoticiaTurma/_form.jsp" %>
	
	<div class="botoes">
		<div class="form-actions">
			<h:commandButton action="#{noticiaTurma.cadastrar}" value="Cadastrar" /> 
		</div>
		<div class="other-actions">
			<h:commandButton action="#{noticiaTurma.listar}" value="<< Voltar"> </h:commandButton> 
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
<c:if test="${turmaVirtual.acessoMobile == false}">
	tinyMCE.init({
		mode : "textareas", theme : "advanced", width : "420", height : "250", language : "pt",
		theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
		theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
		theme_advanced_buttons3 : "",
		plugins : "searchreplace,contextmenu,advimage",
		theme_advanced_toolbar_location : "top",
		theme_advanced_toolbar_align : "left"
	});
</c:if>	

</script>

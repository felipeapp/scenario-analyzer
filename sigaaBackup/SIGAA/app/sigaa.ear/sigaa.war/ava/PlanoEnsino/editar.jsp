<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form>


<h:messages showDetail="true" />

<fieldset>
	<legend>Editar Plano de Ensino</legend>
	
	<%@include file="/ava/PlanoEnsino/_form.jsp" %>
	
	<h:inputHidden value="#{ planoEnsino.object.id }"/>
	
	<div class="botoes">
		<div class="form-actions">
			<h:commandButton action="#{planoEnsino.salvar}" value="Salvar" /> 
		</div>
		<div class="other-actions">
			<h:commandLink action="#{ planoEnsino.mostrar }" value="Mostrar">
			<f:param name="id" value="#{ planoEnsino.object.id }"/>
			</h:commandLink> 
		</div>
		<div class="required-items">
			<span class="required"/>
			Itens de Preenchimento Obrigatório
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
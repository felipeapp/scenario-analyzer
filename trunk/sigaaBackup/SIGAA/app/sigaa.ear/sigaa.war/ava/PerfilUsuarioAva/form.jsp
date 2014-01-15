<%@include file="/ava/cabecalho.jsp" %>

<f:view>
	<%@include file="/ava/menu.jsp" %>
	
	<h:form>
		<h:inputHidden value="#{perfilUsuarioAva.object.id}" />
		<h:inputHidden value="#{perfilUsuarioAva.object.turma.id}" />
		<h:inputHidden value="#{perfilUsuarioAva.object.pessoa.id}" />
	
		<h:messages showDetail="true" />
		
		<fieldset>
			<legend>Gerenciar seu perfil para esta turma</legend>
			
			<ul class="form">
				<li>
					<label>Perfil: <span class="obrigatorio">&nbsp;</span></label>
					<h:inputTextarea id="perfil" value="#{ perfilUsuarioAva.object.perfil }" />
				</li>
			</ul>
		
			<div class="botoes">
				<div class="form-actions">
					<h:commandButton action="#{ perfilUsuarioAva.salvar }" value="Cadastrar" /> 
				</div>
				<div class="other-actions">
					<h:commandButton action="#{ perfilUsuarioAva.cancelar }" value="Cancelar" immediate="true" onclick="#{confirm}" /> 
				</div>
				<div class="required-items">
					<span class="required"></span>
					Itens de Preenchimento Obrigatório
				</div>
			</div>
	
		</fieldset>
	
	</h:form>
	
	<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
	<script type="text/javascript">
	tinyMCE.init({
		mode : "textareas", theme : "advanced", width : "460", height : "250", language : "pt",
		theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist",
		theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
		theme_advanced_buttons3 : "",
		plugins : "searchreplace,contextmenu,advimage",
		theme_advanced_toolbar_location : "top",
		theme_advanced_toolbar_align : "left"
	});
	</script>
	
</f:view>

<%@include file="/ava/rodape.jsp" %>
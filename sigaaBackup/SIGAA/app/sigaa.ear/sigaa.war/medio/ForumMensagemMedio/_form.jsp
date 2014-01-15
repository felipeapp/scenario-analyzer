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

	<h2>Dados da Mensagem</h2>

<ul>
	<li>
		<input type=hidden value="${forumMensagemMedio.object.forum.id}" />
		<label>Título: </label>
		<h:inputText id="titulo" size="40" value="#{forumMensagemMedio.object.titulo}"/><br><br>
	</li>
	
	<li>
		<label>Conteúdo: </label>
		<t:inputTextarea id="descricao" rows="2" value="#{forumMensagemMedio.object.conteudo}"/>
	</li>


</ul>

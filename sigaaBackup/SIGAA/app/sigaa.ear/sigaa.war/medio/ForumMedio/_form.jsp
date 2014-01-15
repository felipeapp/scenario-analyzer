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

<ul>
	<li>
		<label>T�tulo</label>
		<h:inputText id="titulo" size="59" value="#{forumMedio.object.titulo}"/>
	</li>
	<li>
		<label>Descri��o</label>
		<t:inputTextarea id="descricao" rows="2" value="#{forumMedio.object.descricao}"/>
	</li>
	<li>
		<br>
		<label>Tipo: </label>
		<t:selectOneMenu id="tipo" value="#{forumMedio.object.topicos}" styleClass="noborder">
			<f:selectItem itemLabel="F�rum de Mensagens" itemValue="false"/>
			<f:selectItem itemLabel="F�rum de T�picos" itemValue="true"/>
		</t:selectOneMenu>
		<br><span>(F�rum de T�picos � um tipo de f�rum em que as mensagens podem ser agrupadas por assunto, atrav�s da cria��o de t�picos de discuss�o.)</span>
		<br><span>(F�rum de Mensagens � um tipo de f�rum em que todas as mensagens s�o vistas em uma mesma p�gina.)</span>
	</li>
	<li>
		<label>Turma, mural ou pesquisa? </label>
		<t:selectOneMenu id="modeloforum" value="#{forumMedio.object.tipo}" styleClass="noborder">
			<f:selectItem itemLabel="Turma" itemValue="1" />
			<f:selectItem itemLabel="Mural" itemValue="2" />
			<f:selectItem itemLabel="Pesquisa" itemValue="3" />
		</t:selectOneMenu>
	</li>
	</li>
		<label>Notificar por e-mail?</label>
		<t:selectOneMenu id="email" value="#{forumMedio.notificar}" styleClass="noborder">
			<f:selectItem itemLabel="N�o" itemValue="false"/>
			<f:selectItem itemLabel="Sim" itemValue="true"/>
		</t:selectOneMenu>
		<span><br>(Selecione sim se desejar enviar um e-mail para todos os participantes da turma avisando sobre a cria��o do F�rum.)</span>
	</li>
	<li>
		<label>F�rum Ativo?</label>
		<t:selectOneMenu id="ativo" value="#{forumMedio.object.ativo}" styleClass="noborder">
			<f:selectItem itemLabel="Sim" itemValue="true"/>
			<f:selectItem itemLabel="N�o" itemValue="false"/>
		</t:selectOneMenu>
		<br><span>(Se o F�rum estiver marcado como inativo, n�o ser� permitido postar mensagens.)</span>
	</li>
</ul>

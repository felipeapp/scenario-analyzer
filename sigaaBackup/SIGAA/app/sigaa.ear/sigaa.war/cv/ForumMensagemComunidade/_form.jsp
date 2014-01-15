<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

	<input type=hidden value="${forumMensagemComunidadeMBean.object.forum.id}" />
	<tr>
		<th class="required"><h:outputLabel for="descricao">Título:</h:outputLabel></th>
		<td>
			<h:inputText id="titulo" size="89" maxlength="69" value="#{forumMensagemComunidadeMBean.object.titulo}"/>
		</td>
	</tr>
	
	<tr>
		<th class="required"><h:outputLabel for="descricao">Conteúdo:</h:outputLabel></th>
		<td>
			<t:inputTextarea value="#{forumMensagemComunidadeMBean.object.conteudo}" rows="15" cols="100" />
		</td>
	</tr>
	
	<tr>
		<td style="text-align: right;"> <h:selectBooleanCheckbox id="notificacao" value="#{ forumMensagemComunidadeMBean.notificar }" styleClass="noborder" /> </td>
		<th style="text-align: left;">Notificar por e-mail?</th>
	</tr>
		
<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
	tinyMCE.init({
		mode : "textareas", theme : "advanced", width: "90%", height: "250", language : "pt",
		theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
		theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
		theme_advanced_buttons3 : "",
		plugins : "searchreplace,contextmenu,advimage",
		theme_advanced_toolbar_location : "top",
		theme_advanced_toolbar_align : "left"
	});
</script>

<%--
<ul class="form">
	<li>
		<input type=hidden value="${forumMensagemComunidadeMBean.object.forum.id}" />
		
		<label>Título: <span class="required">&nbsp;</span></label>
		<h:inputText id="titulo" size="89" maxlength="69" value="#{forumMensagemComunidadeMBean.object.titulo}"/>
	</li>
	
	<li>
		<label class="required" for="inicio">Conteúdo: <span class="required">&nbsp;</span></label>
	</li>

	<li>
		<t:inputTextarea value="#{forumMensagemComunidadeMBean.object.conteudo}" rows="15" cols="100" />
	</li>
</ul>
--%>
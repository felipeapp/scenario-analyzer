<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

	<tr>
		<th class="required"> <h:outputLabel for="descricao">Descrição:</h:outputLabel> </th>
		<td>
			<h:inputText value="#{noticiaComunidadeMBean.object.descricao}" maxlength="50" styleClass="texto" id="titulo"/>
		</td>
	</tr>
	
	<tr>
		<th class="required"><h:outputLabel for="noticia">Notícia</h:outputLabel></th>
		<td>
			<h:inputTextarea value="#{noticiaComunidadeMBean.object.noticia}" styleClass="texto" id="conteudo"/>
		</td>
	</tr>

	<tr>
		<th><h:outputLabel for="notificar">Enviar e-mail?:</h:outputLabel></th>
		<td>
			<h:selectBooleanCheckbox value="#{noticiaComunidadeMBean.notificar}" styleClass="noborder" id="notificar"/>
		</td>
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
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

	<tr>
		<th class="required"><h:outputLabel for="topico">Tópico:</h:outputLabel></th>
		<td>
			<h:selectOneMenu value="#{ conteudoComunidadeMBean.object.topico.id }" styleClass="texto" id="topico">
				<f:selectItem itemValue="0" itemLabel="- SELECIONE UM TÓPICO -"/>
				<f:selectItems value="#{ topicoComunidadeMBean.comboIdentado }"/>
			</h:selectOneMenu>
		</td>
	</tr>

	<tr>
		<th class="required"><h:outputLabel for="titulo">Título:</h:outputLabel></th>
		<td><h:inputText value="#{ conteudoComunidadeMBean.object.titulo }" maxlength="50" styleClass="texto" id="titulo"/></td>
	</tr>

	<tr>
		<th class="required"><h:outputLabel for="titulo">Conteúdo:</h:outputLabel></th>
		<td><h:inputTextarea value="#{ conteudoComunidadeMBean.object.conteudo }" styleClass="texto" id="conteudo"/></td>
	</tr>

	<tr>
		<th><h:selectBooleanCheckbox value="#{ conteudoComunidadeMBean.object.permanente }" styleClass="texto" id="permanente"/></th>
		<td><h:outputLabel for="titulo">Permanente:</h:outputLabel></td>
	</tr>
	
	<tr>
		<th style="text-align: right;"> <h:selectBooleanCheckbox id="notificacao" value="#{ conteudoComunidadeMBean.object.notificarMembros }" styleClass="noborder" /> </th>
		<td style="text-align: left;">Notificar por e-mail?</td>
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
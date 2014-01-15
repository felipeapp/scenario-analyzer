<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

	<tr>
		<td></td>
		<th class="required"> <h:outputLabel for="descricao">Tópico:</h:outputLabel> </th>
		<td>
			<h:selectOneMenu value="#{ indicacaoReferenciaComunidadeMBean.object.topico.id }" styleClass="texto" id="topico">
				<f:selectItem itemValue="0" itemLabel="- SELECIONE UM TÓPICO -"/>
				<f:selectItems value="#{ topicoComunidadeMBean.comboIdentado }"/>
			</h:selectOneMenu>
		</td>
	</tr>

	<tr>
		<td></td>
		<th class="required"> <h:outputLabel for="descricao">Título:</h:outputLabel> </th>
		<td><h:inputText value="#{ indicacaoReferenciaComunidadeMBean.object.titulo }" maxlength="50" styleClass="texto" id="titulo"/></td>
	</tr>
	<tr>
		<td></td>
		<th class="required"> <h:outputLabel for="descricao">Tipo:</h:outputLabel> </th>
		<td>
			<h:selectOneMenu value="#{indicacaoReferenciaComunidadeMBean.object.tipo}" style="width: 60%" id="tipoReferencia">
				<f:selectItem itemLabel="- SELECIONE UM TIPO DE REFERÊNCIA -" itemValue="-"/>
				<f:selectItem itemLabel="ARTIGO" itemValue="A"/>
				<f:selectItem itemLabel="LIVRO" itemValue="L"/>
				<f:selectItem itemLabel="REVISTA" itemValue="R"/>
				<f:selectItem itemLabel="SITE" itemValue="S"/>
				<f:selectItem itemLabel="OUTROS" itemValue="O"/>
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<td></td>
		<th> <h:outputLabel for="descricao">Endereço (URL):</h:outputLabel> </th>
		<td><h:inputText value="#{ indicacaoReferenciaComunidadeMBean.object.url }" maxlength="255" styleClass="texto" id="endereco"/></td>
	</tr>
	<tr>
		<td></td>
		<th> <h:outputLabel for="descricao">Descrição:</h:outputLabel> </th>
		<td><h:inputTextarea rows="5" value="#{indicacaoReferenciaComunidadeMBean.object.descricao}" styleClass="texto" id="detalhesReferencia"/></td>
	</tr>
	<tr>
		<td></td>
		<td style="text-align: right;"> <h:selectBooleanCheckbox id="notificacao" value="#{ indicacaoReferenciaComunidadeMBean.object.notificarMembros }" styleClass="noborder" /> </td>
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
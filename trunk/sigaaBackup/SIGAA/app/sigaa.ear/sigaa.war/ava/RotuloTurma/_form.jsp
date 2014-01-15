<table class="formAva">
	<tr>
		<th class="required">Descrição:</th> 
		<td><h:inputTextarea value="#{ rotuloTurmaBean.object.descricao }" cols="20" rows="20" id="descricao" title="Descrição"/></td>
	</tr>
	
	<a4j:outputPanel rendered="#{ not empty rotuloTurmaBean.object.aula }">
		<tr id="topicoAula">
			<th class="required">Tópico de Aula:</th>
			<td>
			<h:outputText value="Rótulo de Turma" rendered="#{ empty rotuloTurmaBean.object.aula }" />
			<h:selectOneMenu id="aula" value="#{ rotuloTurmaBean.object.aula.id }">
				<f:selectItem itemValue="0" itemLabel=" -- Nenhum tópico de aula selecionado -- "/>
				<f:selectItems value="#{ topicoAula.comboIdentado }" />
			</h:selectOneMenu>
			<h:selectOneMenu id="sem-aula" value="#{ rotuloTurmaBean.object.aula.id }" styleClass="sem-topicos-aula" rendered="#{ (empty topicoAula.comboIdentado) }">
				<f:selectItem itemLabel="Nenhum Tópico de Aula foi cadastrado" itemValue="0"/>
			</h:selectOneMenu>
			<ufrn:help>Selecione um tópico de aula para exibir este rótulo na página inicial da turma virtual.</ufrn:help>
			</td>
		</tr>
	</a4j:outputPanel>
	
	<tr>
		<th>Visível:</th>
		<td>
			<h:selectOneRadio id="visivel" value="#{ rotuloTurmaBean.object.visivel }" title="Visível">
				<f:selectItems value="#{ rotuloTurmaBean.simNao }" />
			</h:selectOneRadio>
			<span class="descricao-campo" style="padding-left: 0;">Habilita a exibição do rótulo na lista de tópicos de aula</span>
		</td>
	</tr>
</table>
<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
tinyMCE.init({
	mode : "textareas", theme : "advanced", width : "700", height : "250", language : "pt",
	theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
	theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
	theme_advanced_buttons3 : "",
	plugins : "searchreplace,contextmenu,advimage",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left"
});
</script>
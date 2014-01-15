<p:resources/>
<link rel="stylesheet" type="text/css" href="/sigaa/ava/primefaces/redmond/skin.css" />

<c:set var="topicosParaCadastro" value="#{ topicoComunidadeMBean.comboIdentado }" /> 
<c:set var="topicosParaEdicao" value="#{ topicoComunidadeMBean.comboIdentadoEdicaoSemRepetirEleMesmo }" />

	<c:if test="${ not empty topicosParaCadastro or not empty topicosParaEdicao }">
		<tr>
			<th> <h:outputLabel for="topicoPai">Tópico Pai:</h:outputLabel> </th>
			<td> 
				<c:if test="${ not empty topicosParaCadastro && topicoComunidadeMBean.object.modoCadastro == true }">
					<h:selectOneMenu value="#{topicoComunidadeMBean.object.topicoPai.id}" styleClass="texto">
						<f:selectItem itemValue="0" itemLabel=" NENHUM TÓPICO PAI " />
						<f:selectItems value="#{topicoComunidadeMBean.comboIdentado}" />
					</h:selectOneMenu>
				</c:if>
				
				<c:if test="${ not empty topicosParaEdicao && topicoComunidadeMBean.object.modoCadastro == false }">
					<h:selectOneMenu value="#{topicoComunidadeMBean.object.topicoPai.id}" styleClass="texto" style="padding-left: 10px;">
						<f:selectItem itemValue="0" itemLabel=" NENHUM TÓPICO PAI " />
						<f:selectItems value="#{topicoComunidadeMBean.comboIdentadoEdicaoSemRepetirEleMesmo}" />
					</h:selectOneMenu>
				</c:if>
			</td>
		</tr>
	</c:if>	
	
	<tr>
		<th><h:outputLabel for="descricao">Cor de fundo:</h:outputLabel></th>
		<td> 
			<span style="display:inline-block;"><p:colorPicker header="Selecione uma cor" value="#{topicoComunidadeMBean.object.cor }" widgetVar="colorPicker">  
				<f:converter converterId="colorPickerConverter" />  
			</p:colorPicker></span>
			
			<a href="#" onclick="zerarCor(); return false;">Remover Cor</a>
			<ufrn:help>Evite escolher cores muito escuras para não dificultar a leitura do contéudo. Mas caso uma cor escura seja escolhida, altere a cor do texto para uma cor clara</ufrn:help>
		</td>
	</tr>
	
	<tr>
		<th class="required"> <h:outputLabel for="descricao">Descrição:</h:outputLabel> </th>
		<td> 
			<h:inputText value="#{topicoComunidadeMBean.object.descricao}" id="descricao" maxlength="100" styleClass="texto"/> 
		</td>
	</tr>
	<tr>
		<th> <h:outputLabel for="conteudo">Conteúdo:</h:outputLabel> </th>
		<td> 
			<h:inputTextarea value="#{ topicoComunidadeMBean.object.conteudo }" rows="10" id="conteudo" styleClass="texto"/>
		</td>
	</tr>
	<tr>
		<td style="text-align: right;"> <h:selectBooleanCheckbox id="notificacao" value="#{ topicoComunidadeMBean.object.notificarMembros }" styleClass="noborder" /> </td>
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
	
	function zerarCor () {
		//colorPicker.selectColor(null);
		document.getElementById(colorPicker.id + "_input").value = null;
	    jQuery(colorPicker.jqId + "_livePreview").css("backgroundColor", "");
	}
</script>
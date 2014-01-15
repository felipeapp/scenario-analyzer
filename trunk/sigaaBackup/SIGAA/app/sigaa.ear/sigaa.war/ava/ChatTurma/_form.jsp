<table class="formAva" style="width:90%;">
	<tr>
		<th id="turma">Turma: </th> 
		<td>
			<b><h:outputText value="#{ chatTurmaBean.object.turma.nome }" id="turma"/></b>
		</td>
	</tr>

	<tr>
		<th class="required" id="titulo">Título:</th> 
		<td>
			<h:inputText value="#{ chatTurmaBean.object.titulo }" size="80" id="titulo"/>
		</td>
	</tr>

	<tr>
		<th class="required" id="descricao">Descrição: </th> 
		<td>
			<h:inputTextarea value="#{ chatTurmaBean.object.descricao }" cols="20" rows="20" id="descricao" title="Descrição"/>
		</td>
	</tr>

	<tr>
		<th class="required" id="dataInicioTh">Início:</th>
		<td>	
			<t:inputCalendar id="dataInicio" value="#{ chatTurmaBean.object.dataInicio }" readonly="false" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" title="Data de início">
				<f:convertDateTime pattern="dd/MM/yyyy"/>
			</t:inputCalendar>&nbsp;
			
			<t:inputText id="horaInicio" value="#{ chatTurmaBean.object.horaInicio }" maxlength="5" title="Hora de início" size="4" onkeypress="return(formatarMascara(this,event,'##:##'))">
				<f:convertDateTime pattern="HH:mm"/>
			</t:inputText> HH:mm
		</td>
	</tr>
	
	<tr>
		<th class="required" id="dataFimTh">Fim:</th>
		<td>		
			<t:inputCalendar id="dataFim" value="#{ chatTurmaBean.object.dataFim }" readonly="false" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" title="Data de fim">
				<f:convertDateTime pattern="dd/MM/yyyy"/>
			</t:inputCalendar>&nbsp;
			
			<t:inputText id="horaFim" value="#{ chatTurmaBean.object.horaFim }" maxlength="5" title="Hora de fim" size="4" onkeypress="return(formatarMascara(this,event,'##:##'))">
				<f:convertDateTime pattern="HH:mm"/>
			</t:inputText> HH:mm
		</td>
	</tr>

	<tr>
		<a4j:outputPanel rendered="#{ not empty chatTurmaBean.object.aula }">
			<th>Tópico de Aula:</th>
			<td>	
				<h:outputText value="Chat de Turma" rendered="#{ empty chatTurmaBean.object.aula }" />
				<h:selectOneMenu id="aula" value="#{ chatTurmaBean.object.aula.id }">
					<f:selectItem itemValue="0" itemLabel=" -- Nenhum tópico de aula selecionado -- "/>
					<f:selectItems value="#{ topicoAula.comboIdentado }" />
				</h:selectOneMenu>
				<h:selectOneMenu id="sem-aula" value="#{ chatTurmaBean.object.aula.id }" styleClass="sem-topicos-aula" rendered="#{ (empty topicoAula.comboIdentado)}">
					<f:selectItem itemLabel="Nenhum Tópico de Aula foi cadastrado" itemValue="0"/>
				</h:selectOneMenu>
				<ufrn:help>Selecione um tópico de aula para exibir este chat na página inicial da turma virtual.</ufrn:help>
			</td>	
		</a4j:outputPanel>
	</tr>


	<tr>
		<th id="monitorarLeituraTh">Publicar conteúdo:</th>
		<td>
			<h:selectOneRadio id="monitorarLeitura" value="#{ chatTurmaBean.object.publicarConteudo }" title="Publicar Conteúdo">
				<f:selectItems value="#{ chatTurmaBean.simNao }" />
			</h:selectOneRadio>
		</td>	
	</tr>
	
	<tr>
		<th>Vídeo chat:</th>
		<td>
			<h:selectOneRadio id="videoChat" value="#{ chatTurmaBean.object.videoChat }" title="Publicar Conteúdo">
				<f:selectItems value="#{ chatTurmaBean.simNao }" />
			</h:selectOneRadio>
		</td>
	</tr>
	
	<tr>
		<th id="formAva:descricao">Notificação: </th>
		<td>
			<h:selectBooleanCheckbox id="notificacao" value="#{ chatTurmaBean.object.notificarAlunos }" />
			<span class="texto-ajuda">(Notificar os alunos por e-mail)</span> 
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
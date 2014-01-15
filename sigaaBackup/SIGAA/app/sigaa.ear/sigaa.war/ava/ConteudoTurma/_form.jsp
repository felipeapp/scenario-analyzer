
<table class="formAva" style="width:100%;">

	<tr>
		<th class="required">Título:</th>
		<td>
			<h:inputText value="#{ conteudoTurma.object.titulo }" maxlength="255" size="59" id="titulo"/>
		</td>
	</tr>
	
	<tr>
		<th class="required">Tópico de Aula:</th>
		<td>
			<h:selectOneMenu value="#{ conteudoTurma.object.aula.id }" rendered="#{ not empty topicoAula.comboIdentado }">
				<f:selectItem itemValue="0" itemLabel=""/>
				<f:selectItems value="#{ topicoAula.comboIdentado }"/>
			</h:selectOneMenu>
			<h:selectOneMenu value="#{ conteudoTurma.object.aula.id }" styleClass="sem-topicos-aula" rendered="#{ empty topicoAula.comboIdentado }">
				<f:selectItem itemLabel="Nenhum Tópico de Aula foi cadastrado" itemValue="0"/>
			</h:selectOneMenu>
		</td>
	</tr>

	<tr>
		<th class="required">Conteúdo:</th>
		<td>
			<h:inputTextarea value="#{ conteudoTurma.object.conteudo }" id="conteudo" cols="10" rows="10" style="width: 80%;"/>
		</td>
	</tr>
	
	<c:if test="${ not empty turmaVirtual.turmasSemelhantes && conteudoTurma.object.id == 0 }">	
	<tr>
		<th class="required">Criar Em:</th>
		<td>
			<t:selectManyCheckbox value="#{ conteudoTurma.cadastrarEm }" layout="pageDirection">
				<t:selectItems var="ts" itemLabel="#{ ts.descricaoSemDocente }" itemValue="#{ ts.id }" value="#{ turmaVirtual.turmasSemestrePermissaoDocente }"/>
			</t:selectManyCheckbox>
		</td>
	</tr>
	</c:if>
	
	<tr>
		<th style="vertical-align:top;">Notificação:</th>
		<td>
			<h:selectBooleanCheckbox id="notificacao" value="#{ conteudoTurma.object.notificarAlunos }" />
			<span class="descricaoCampo">(Notificar os alunos por e-mail)</span> 
		</td>
	</tr>
	
</table>

<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
<c:if test="${turmaVirtual.acessoMobile == false}">
	tinyMCE.init({
		mode : "textareas", theme : "advanced", width : "420", height : "250", language : "pt",
		theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
		theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
		theme_advanced_buttons3 : "",
		plugins : "searchreplace,contextmenu,advimage",
		theme_advanced_toolbar_location : "top",
		theme_advanced_toolbar_align : "left"
	});
</c:if>	
</script>	
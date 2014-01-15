<%--Se a tarefa possui avaliacoes --%>
<a4j:region>
	<table class="formAva" width="100%" cellspacing="10" style="border-collapse: separate;">
		<tr>
			<th class="required">Título:</th>
			<td><h:inputText id="titulo" size="59" maxlength="200" value="#{ tarefaTurma.object.titulo }"/></td>
		</tr>
		<tr>
			<th class="required">Texto:</th>
			<td><h:inputTextarea id="texto" style="width:90%" value="#{ tarefaTurma.object.conteudo }"/></td>
		</tr>
		<tr>
			<th>Arquivo:</th>
			<td>
				<t:inputFileUpload id="arquivo" value="#{ tarefaTurma.arquivo }"/>
				<span class="descricao-campo" style="padding-left: 0;">(Opcional, apenas se desejar enviar um arquivo junto à tarefa. Tamanho Máximo: ${tarefaTurma.tamanhoMaximoArquivo} MB.)</span>
			</td>
		</tr>
		<c:if test="${ empty tarefaTurma.object.respostas }">
			<tr>
				<th class="required">Em grupo?</th>
				<td>
					<h:selectOneMenu id="grupo" value="#{tarefaTurma.object.emGrupo}" style="width: 50px">
						<f:selectItems value="#{tarefaTurma.simNao}" />
					</h:selectOneMenu>
				</td>
			</tr>
		</c:if>
		<tr>
			<th class="required">Tipo de tarefa:</th>
			<td>
				<h:selectOneMenu id="tipo" value="#{ tarefaTurma.tipoTarefa }">
					<f:selectItem itemLabel="Envio de Arquivo" itemValue="1" />
					<f:selectItem itemLabel="Texto Online" itemValue="2" />
					<f:selectItem itemLabel="Trabalho Offline" itemValue="3" />
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th class="required">Tópico de Aula:</th>
			<td>
				<h:selectOneMenu id="aula" value="#{ tarefaTurma.object.aula.id }" rendered="#{ not empty topicoAula.comboIdentado }">
					<f:selectItem itemValue="0" itemLabel="Selecione um tópico de aula"/>
					<f:selectItems value="#{topicoAula.comboIdentado}" />
				</h:selectOneMenu>
				<h:selectOneMenu id="semAula" value="#{ tarefaTurma.object.aula.id }" styleClass="sem-topicos-aula" rendered="#{ empty topicoAula.comboIdentado }">
					<f:selectItem itemLabel="Nenhum Tópico de Aula foi cadastrado" itemValue="0"/>
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th>Permitir novo envio:</th>
			<td>
				<h:selectOneMenu id="novo" value="#{ tarefaTurma.object.permiteNovoEnvio }" style="width: 50px">
					<f:selectItem itemLabel="Sim" itemValue="true"/>
					<f:selectItem itemLabel="Não" itemValue="false"/>
				</h:selectOneMenu>
			</td>
		</tr>

		<tr>
			<th class="required">Data de abertura:</th>
			<td>
				<span  style="position:absolute;margin-top:-5px;">
					<t:inputCalendar id="dataInicio" value="#{tarefaTurma.object.dataInicio}" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" title="Data Inicial">
						<f:convertDateTime pattern="dd/MM/yyyy"/>
					</t:inputCalendar>
				</span>
			</td>
		</tr>
		<tr>
			<th>Hora de abertura:</th>
			<td>
				<h:selectOneMenu id="horaInicio" value="#{ tarefaTurma.object.horaInicio }">
					<f:selectItems value="#{ tarefaTurma.horas }" />
				</h:selectOneMenu>
				 : 
				<h:selectOneMenu id="minutoInicio" value="#{ tarefaTurma.object.minutoInicio }">
					<f:selectItems value="#{ tarefaTurma.minutos }" />
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th class="required">Data de fechamento:</th>
			<td>
				<span  style="position:absolute;margin-top:-5px;">
					<t:inputCalendar value="#{tarefaTurma.object.dataEntrega}" readonly="false" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" title="Data Final">
						<f:convertDateTime pattern="dd/MM/yyyy"/>
					</t:inputCalendar>
				</span>	
			</td>
		</tr>
		<tr>
			<th>Hora de fechamento:</th>
			<td>
				<h:selectOneMenu id="horaFim" value="#{ tarefaTurma.object.horaEntrega }">
					<f:selectItems value="#{ tarefaTurma.horas }" />
				</h:selectOneMenu>
				 : 
				<h:selectOneMenu id="minutoFim" value="#{ tarefaTurma.object.minutoEntrega }">
					<f:selectItems value="#{ tarefaTurma.minutos }" />
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th>Notificação:</th>
			<td>
				<h:selectBooleanCheckbox id="notificacao" value="#{ tarefaTurma.notificarAlunos }"/>
				<ufrn:help>Notificar os alunos por e-mail</ufrn:help>
			</td>
		</tr>
		<c:if test="${exibirTurmas}">
			<tr>
				<th class="required">Criar em:</th>
				<td>
					<t:selectManyCheckbox id="criarEm" value="#{ tarefaTurma.cadastrarEm }" layout="pageDirection">
						<t:selectItems var="ts" itemLabel="#{ ts.descricaoSemDocente }" itemValue="#{ ts.id }" value="#{ turmaVirtual.turmasSemestrePermissaoDocente }"/>
					</t:selectManyCheckbox>
				</td>
			</tr>
		</c:if>
	
		<tr>
			<td colspan="2">
				<h2>Avaliação</h2>
				<div class="descricaoOperacao"><p>Defina abaixo as configurações da avaliação desta tarefa, informando os dados que constarão na planilha de notas da turma.</p></div>
			</td>
		</tr>

		<tr>
			<td colspan="2"><span style="display:inline-block;width:163px;text-align:right;">Possui Nota:<span class="required"></span></span>
				<h:selectOneMenu id="possuiNota" onchange="exibeCamposNotas(this);" value="#{ tarefaTurma.object.possuiNota }">
					<f:selectItems value="#{ tarefaTurma.simNao }"/>
				</h:selectOneMenu>
			</td>
		</tr>

		<tr><td colspan="2">
			<table id="tableAvaliacao" class="form" width="100%" cellspacing="10" style="border-collapse: separate;">
			
			<tr id="avaliacaoAbreviacao">
				<th style="width:137px;" class="required">Abreviação:</th>	
				<td>
					<h:inputText id="abreviacao" value="#{ tarefaTurma.object.abreviacao }"  maxlength="4" size="5"/>
				</td>
			</tr>
			<tr>
				<th id="avaliacaoUnidade" class="required">Unidade:</th>
				<td>
					<h:selectOneMenu id="unidade" value="#{ tarefaTurma.object.unidade }" onchange="exibePeso(this),exibeNotaMaxima(this);">
						<f:selectItems value="#{ tarefaTurma.unidades }"/>
						<a4j:support event="onclick" onsubmit="true" reRender="panelNotas"/>	
					</h:selectOneMenu>
				</td>
			</tr>
			<tr id="notaMaxima">
				<th class="required"><span id="labelNotaMaximaUnidade">Nota Máxima:</th>
				<td>
					<span id="valorNotaMaximaUnidade">
						<h:selectOneMenu id="notaMaxima" value="#{ tarefaTurma.notaMaxima }">
							<f:selectItems value="#{ tarefaTurma.notas }"/>
						</h:selectOneMenu>
						<ufrn:help>A nota máxima informada aqui será utilizada na avaliação referente a esta tarefa. Caso o valor selecionado seja maior que dez, o valor máximo da avaliação será um décimo deste.</ufrn:help>
					</span>
				</td>
			</tr>
			<tr>
				<th  class="required"><span id="labelPesoUnidade">Peso: </span></th>
				<td>
					<span id="valorPesoUnidade">
						<h:inputText id="peso" value="#{ tarefaTurma.object.peso }" onkeyup="return formatarInteiro(this);" maxlength="2" size="3" converter="#{intConverter}"/>
						<ufrn:help>O peso só deve ser informado se a unidade tiver sua nota calculada através de média ponderada.</ufrn:help>
					</span>
				</td>
			</tr>
		</table>
		</td></tr>
	</table>
</a4j:region>
<br/><br/>

<input type="hidden" id="possuiAvaliacoes" value="${ tarefaTurma.possuiAvaliacoes }"/>

<a4j:outputPanel id="panelNotas" style="display:none">
	<h:outputText id="unidadePossuiNotas" value="#{ tarefaTurma.unidadePossuiNota }" />
</a4j:outputPanel>

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
<script type="text/javascript">

//P - Com Pesos; A - Média Aritmética; S - Soma; 0 - Não configurado
	var calculoPrimeiraUnidade = '<h:outputText value="#{ questionarioTurma.config != null ? questionarioTurma.config.tipoMediaAvaliacoes1 : 0 }" />';
	var calculoSegundaUnidade = '<h:outputText value="#{ questionarioTurma.config != null ? questionarioTurma.config.tipoMediaAvaliacoes2 : 0 }" />';
	var calculoTerceiraUnidade = '<h:outputText value="#{ questionarioTurma.config != null ? questionarioTurma.config.tipoMediaAvaliacoes3 : 0 }" />';
	
	function exibePeso (unidade) {
		if (unidade.value == 1 && calculoPrimeiraUnidade == 'P' || unidade.value == 2 && calculoSegundaUnidade == 'P' || unidade.value == 3 && calculoTerceiraUnidade == 'P'){
			document.getElementById("labelPesoUnidade").style.display = "";
			document.getElementById("valorPesoUnidade").style.display = "";
		} else {
			document.getElementById("labelPesoUnidade").style.display = "none";
			document.getElementById("valorPesoUnidade").style.display = "none";
		}
	}
	
	function exibeNotaMaxima (unidade) {
		if (unidade.value == 1 && calculoPrimeiraUnidade == 'S' || unidade.value == 2 && calculoSegundaUnidade == 'S' || unidade.value == 3 && calculoTerceiraUnidade == 'S'){
			document.getElementById("notaMaxima").style.display = "";
			document.getElementById("labelNotaMaximaUnidade").style.display = "";
			document.getElementById("valorNotaMaximaUnidade").style.display = "";
		} else {
			document.getElementById("notaMaxima").style.display = "none";
			document.getElementById("labelNotaMaximaUnidade").style.display = "none";
			document.getElementById("valorNotaMaximaUnidade").style.display = "none";
		}
	}
	
	var possuiAvaliacoes = Boolean(document.getElementById('possuiAvaliacoes').value);

	function exibeCamposNotas (select){
		if (select.value == "false"){
			document.getElementById("tableAvaliacao").style.display = "none";
		} else {
			document.getElementById("tableAvaliacao").style.display = "block";
		}
	}
	
	function mensagemAtualizar ()
	{
		var nota = document.getElementById("formAva:possuiNota");
		var possuiNotas = document.getElementById('formAva:unidadePossuiNotas').innerHTML == "true";
		
		if ( possuiAvaliacoes == "true" && nota.value == "-1.0" ) 
			return(confirm("Esta tarefa possui nota, caso seu campo \"Possui Nota\" seja alterado para \"N\u00e3o\" " +
						 		"as notas desta tarefa na planilha de notas ser\u00e3o removidas. " +
						 		"Deseja realmente alterar este item?"));
		else {
			if (possuiNotas )	
				return(confirm("J\u00e1 foram cadastradas notas para esta unidade. Ao alterar o campo \"Possui Nota\" para \"Sim\" a unidade na planilha de notas" +
								" ser\u00e1 desmembrada em avali\u00e7\u00f5es e as notas ser\u00e3o perdidas." +
				 				" Deseja continuar com a opera\u00e7\u00e3o?"));
		}
	}
	function mensagemNotas(){
		var nota = document.getElementById("formAva:possuiNota");
		var possuiNotas = document.getElementById('formAva:unidadePossuiNotas').innerHTML == "true";

		if ( nota.value != "-1.0" ){
			if ( possuiNotas )	
				return(confirm("J\u00e1 foram cadastradas notas para esta unidade. Ao cadastrar uma tarefa que \"Possui Nota\" a unidade na planilha de notas" +
								" ser\u00e1 desmembrada em avali\u00e7\u00f5es e as notas ser\u00e3o perdidas." +
				 				" Deseja continuar com a opera\u00e7\u00e3o?"));
		}
	}
	
	jQuery(document).ready(function () {
		exibeCamposNotas(document.getElementById("formAva:possuiNota"));
		exibePeso (document.getElementById("formAva:unidade"));
		exibeNotaMaxima (document.getElementById("formAva:unidade"));
	});
	
</script>
<link rel="stylesheet" type="text/css" href="/sigaa/primefaces_resource/1.1/yui/colorpicker/assets/skins/sam/colorpicker.css" />		

<script type="text/javascript" src="/sigaa/primefaces_resource/1.1/yui/utilities/utilities.js"></script>
<script type="text/javascript" src="/sigaa/primefaces_resource/1.1/yui/slider/slider-min.js"></script>
<script type="text/javascript" src="/sigaa/primefaces_resource/1.1/yui/colorpicker/colorpicker-min.js"></script>
<script type="text/javascript" src="/sigaa/primefaces_resource/1.1/primefaces/colorpicker/colorpicker.js"></script>
	
<table class="formAva">

	<div class="descricaoOperacao">É possível definir um Tópico de
	Aula como <b>visível</b> ou não. Caso ele seja definido como visível,
	será visto pelos discentes normalmente, porém caso o mesmo seja
	definido como <b>não visível</b> ele <b>não</b> irá aparecer para os
	discentes. Os tópicos não visíveis irão aparecer para você normalmente,
	em uma lista separada, abaixo dos tópicos visíveis. A visibilidade de
	um tópico pode ser modificada sempre que desejar, para isso basta
	clicar em 'Editar Tópico de Aula'.</b></div>

	<c:if test="${((!turmaVirtual.turma.ead && !turmaVirtual.turma.infantil) || topicoAula.object.id != 0) }">
		<tr>
			<th class="required" id="inicioTh">Data	Inicial:</th> 
			<td>
			<h:selectOneMenu value="#{topicoAula.dataInicio}" id="inicio" 
				onchange="atualizaData(true);">
				<c:if test="${topicoAula.object.id > 0}">
					<f:selectItems value="#{topicoAula.dataInicial}" />
				</c:if>
				<f:selectItems value="#{topicoAula.aulasCombo}" />
			</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th class="required" id="fimTh">Data Final:</th> 
			<td>
				<h:selectOneMenu value="#{topicoAula.dataFim}" id="fim"
				onchange="atualizaData(false);">
				<c:if test="${topicoAula.object.id > 0}">
					<f:selectItems value="#{topicoAula.dataFinal}" />
				</c:if>
				<f:selectItems value="#{topicoAula.aulasCombo}" />
				</h:selectOneMenu>
			</td>
		</tr>
	</c:if>

	<c:if test="${(turmaVirtual.turma.infantil || topicoAula.object.id != 0) }">
		<tr>
			<th class="required" id="inicioThInf">Data Início:</th> 
				<td>
					<t:inputCalendar id="dataInicioInf" value="#{topicoAula.object.data}" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" title="Data Inicial">
						<f:convertDateTime pattern="dd/MM/yyyy"/>
					</t:inputCalendar>
				</td>
			</tr>	
			<tr>
			<th class="required" id="fimThInf">Data Final:</th> 
			<td>
				<t:inputCalendar id="dataFimInf" value="#{topicoAula.object.fim}" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" title="Data Inicial">
					<f:convertDateTime pattern="dd/MM/yyyy"/>
				</t:inputCalendar>
			</td>
		</tr>	
	</c:if>

	<c:if test="${(turmaVirtual.turma.ead && topicoAula.object.id == 0) }">
		<tr>
			<th class="required" id="dataInicioTh">Data	Inicial:</th> 
			<td>
				<t:inputCalendar id="dataInicio" value="#{topicoAula.object.data}"
					renderAsPopup="true" renderPopupButtonAsImage="true" size="10"
					onkeypress="return (formataData(this,event));" maxlength="10"
					title="Data Início">
					<f:convertDateTime pattern="dd/MM/yyyy" />
				</t:inputCalendar>
			</td>	
		</tr>
		<tr>
			<th class="required" id="dataFimTh">Data Final:</th> 
			<td>
				<t:inputCalendar id="dataFim" value="#{topicoAula.object.fim}" renderAsPopup="true"
					renderPopupButtonAsImage="true" size="10"
					onkeypress="return (formataData(this,event));" maxlength="10"
					title="Data Fim">
					<f:convertDateTime pattern="dd/MM/yyyy" />
				</t:inputCalendar>
			</td>	
		</tr>
	</c:if>

	<c:if test="${!turmaVirtual.turma.ead || topicoAula.object.id != 0 }">
		<tr>
			<th id="descricaoTh" class="required">Descrição:</th> 
			<td>
				<h:inputText value="#{topicoAula.object.descricao}" id="descricao" size="59" maxlength="200" />
			</td>
		</tr>
		<tr>
			<th>Cor de fundo:</th>
			<td>
				<p:colorPicker header="Selecione uma cor" widgetVar="colorPicker" value="#{topicoAula.object.cor}">  
					<f:converter converterId="colorPickerConverter" />  
				</p:colorPicker>
				<a href="#" onclick="zerarCor(); return false;">Remover Cor</a>
				<ufrn:help><div>É possível selecionar uma cor para o plano de fundo do Tópico de Aula.<br /> Evite escolher cores muito escuras para não dificultar a leitura do contéudo. Mas caso uma cor escura seja escolhida, altere a cor do texto para uma cor clara. </div></ufrn:help>	
			</td>
		</tr>
		<tr>
			<th id="thConteudo">Conteúdo:</th>
			<td>
				<h:inputTextarea id="conteudo" value="#{ topicoAula.object.conteudo }"  cols="10" rows="10" style="width:90%"/>
			</td>
		</tr>
	</c:if>

	<tr>
		<th id="paiTh">Tópico Pai:</th> 
		<td>
			<c:set var="topicosParaCadastro" value="#{ topicoAula.comboIdentado }" /> 
			<c:set var="topicosParaEdicao" value="#{ topicoAula.comboIdentadoEdicaoSemRepetirEleMesmo }" /> 
			<c:if test="${ empty topicosParaCadastro }"></c:if>
			<h:selectOneMenu id="pai" value="#{topicoAula.object.topicoPai.id}"	style="width:350px;">
				<f:selectItem itemValue="0" itemLabel="-- NENHUM --" />
				<c:if
					test="${ not empty topicosParaCadastro && topicoAula.object.modoCadastro == true }">
					<f:selectItems value="#{topicoAula.comboIdentado}" />
				</c:if>
				<c:if
					test="${ not empty topicosParaEdicao && topicoAula.object.modoCadastro == false }">
					<f:selectItems
						value="#{topicoAula.comboIdentadoEdicaoSemRepetirEleMesmo}" />
				</c:if>
			</h:selectOneMenu>
		</td>	
	</tr>


	<c:if test="${turmaVirtual.turma.ead && topicoAula.object.id == 0 }">
		<tr>
			<td colspan="2">

				<table class="listing">
					<thead>
						<tr>
							<th></th>
							<th>Aula</th>
							<th>Descrição</th>
						</tr>
					</thead>
		
					<tbody>
						<h:inputHidden id="descricao"
							value="#{ topicoAula.object.descricao }" />
						<h:inputHidden id="conteudo" value="#{ topicoAula.object.conteudo }" />
		
						<c:forEach var="t" items="#{ topicoAula.listagemItensProgramaPorDisciplina }" varStatus="loop">
							<tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }">
								<td class="first"><input type="radio" name="id" id="id" value="${t.id}"
									class="noborder" onclick="atualizaConteudo(this);" /></td>
								<td>${ t.aula }</td>
								<td class="conteudo">${ t.conteudo }</td>
							</tr>
						</c:forEach>
		
						<script>
							function atualizaConteudo(input){
								var descricao = input.parentNode.nextSibling;
								var conteudo = descricao.nextSibling;
		
								document.getElementById("formAva:descricao").value = "Aula " + descricao.innerHTML;
								document.getElementById("formAva:conteudo").value = conteudo.innerHTML;
							}
						</script>
					</tbody>
				</table>
			</td>
		</tr>
	</c:if>

	<c:if test="${ not empty turmaVirtual.turmasSemestrePermissaoDocente && topicoAula.object.id == 0 }">
		<tr>
			<th class="required">Criar em:</th>
			<td>
				<t:selectManyCheckbox id="criarEm" value="#{ topicoAula.cadastrarEm }" layout="pageDirection">
					<t:selectItems var="ts" itemLabel="#{ ts.descricaoSemDocente }" itemValue="#{ ts.id }" value="#{ turmaVirtual.turmasSemestrePermissaoDocente }" />
				</t:selectManyCheckbox>
			</td>
		</tr>
	</c:if>

	<tr>
		 <th id="perguntaTh" class="required">Visível:</th>
		 <td>
			<h:selectOneMenu id="pergunta" value="#{ topicoAula.object.visivel }">
				<f:selectItems value="#{ topicoAula.simNao }" />
			</h:selectOneMenu>
		</td>
	</tr>

	<tr>
		<th id="cancelarAulaTh">Cancelar Aula:</th>
		<td>
			<h:selectBooleanCheckbox id="cancelarAula" onclick="atualizaAulaCancelada(this)" value="#{ topicoAula.object.aulaCancelada }" />
			<ufrn:help>É possível cadastrar um tópico para indicar que não haverá aula. Neste caso não será nescessário o lançamento da frequência
	   		 e os discentes não poderão notificar falta do docente.</ufrn:help>
		</td>
	</tr>

	<tr>
		<th id="docenteTh" class="required">Docente(s):</th>
		<td>
			<h:outputText value="#{topicoAula.nomeDocente }" rendered="#{ !topicoAula.turmaComVariosDocentes }" /> 
			<h:selectManyListbox
				id="docente" value="#{ topicoAula.idsDocentes }"
				rendered="#{ topicoAula.turmaComVariosDocentes }">
				<f:selectItems value="#{ topicoAula.docentes }" />
			</h:selectManyListbox>
		</td>	
	</tr>
	<c:if test="${ topicoAula.object.id != 0 }">
		<tr>
			<td colspan="2">
				<br />
				<table class="listing" style="width: 530px; margin-left: 10px;">
					<caption style="font-weight: bold; text-align: center; background: #CCCCCC; padding: 2px;">Conteúdo deste Tópico de Aula</caption>
					<thead>
						<tr>
							<th style="text-align: center; width: 80px;">Data</th>
							<th style="text-align: left;">Nome</th>
							<th style="text-align: left; width: 80px;">Tipo</th>
						</tr>
					</thead>
					<c:forEach var="m_" items="#{topicoAula.object.materiais}" varStatus="loop">
						<tr tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }">
							<td class="first" style="text-align: center;">
								<ufrn:format type="data" valor="${m_.dataCadastro}" />
							</td>
							<td style="text-align: left;">${m_.nome}</td>
							<td style="text-align: left;">
								<c:if test="${m_.tipoArquivo}">Arquivo</c:if>
								<c:if test="${m_.tipoIndicacao }">Indicação</c:if>
								<c:if test="${m_.tipoTarefa}">Tarefa</c:if>
								<c:if test="${m_.tipoConteudo }">Conteúdo</c:if>
								<c:if test="${m_.tipoVideo }">Vídeo</c:if>
								<c:if test="${m_.tipoQuestionario }">Questionário</c:if>
								<c:if test="${m_.tipoForum }">Fórum</c:if>
								<c:if test="${m_.tipoEnquete }">Enquete</c:if>
								<c:if test="${m_.tipoChat }">Chat</c:if>
							</td>
		
						</tr>
					</c:forEach>
				</table>
			</td>
		</tr>
	</c:if>
</table>

<script language="javascript">
	function atualizaData(inicial){
		var inicio = document.getElementById("formAva:inicio").value;
		var fim = document.getElementById("formAva:fim").value;

		if (inicial) {
			if (inicio > fim)
				document.getElementById("formAva:fim").value = inicio;
		} else 
			if (fim < inicio)
				document.getElementById("formAva:inicio").value = fim;			
	}

	function atualizaAulaCancelada(elem){

		var descricao = jQuery("#formAva\\:descricao");
		var conteudo = jQuery("#thConteudo");
		var inicio = jQuery("#formAva\\:inicio");
		var fim = jQuery("#formAva\\:fim");
		
		if ( elem == null )
			elem = jQuery("#formAva\\:cancelarAula")[0];
		else 
			clicked = true;
		
		if ( elem.checked ){
			descricaoVal = descricao.val()
			descricao.val("N\u00e3o Haver\u00e1 Aula");
			conteudo.html('Motivo:');
			fim.val(inicio.val());
		}
		else if ( !elem.checked && clicked ) {
			descricao.val(descricaoVal);
			conteudo.html('Conteúdo:');
		}

	}


	var clicked = false;
	var descricaoVal = "";
	atualizaAulaCancelada(null);
	
	function zerarCor () {
		document.getElementById(colorPicker.id + "_input").value = null;
	    jQuery(colorPicker.jqId + "_livePreview").css("backgroundColor", "");
	}
</script>

<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
<c:if test="${turmaVirtual.acessoMobile == false}">
	tinyMCE.init({
		mode : "textareas", theme : "advanced", width : "460", height : "250", language : "pt",
		theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
		theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
		theme_advanced_buttons3 : "",
		plugins : "searchreplace,contextmenu,advimage",
		theme_advanced_toolbar_location : "top",
		theme_advanced_toolbar_align : "left"
	});
</c:if>	
</script>
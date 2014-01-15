<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style type="text/css">
	table tbody tr td input[type=radio] { margin: 0 19px; }
</style>

<f:view>
<h2> <ufrn:subSistema /> &gt; Registro da Evolução da Criança </h2>

<c:set var="discente" value="#{registroEvolucaoCriancaMBean.obj.discente}"/>
<c:set var="turma" value="#{registroEvolucaoCriancaMBean.obj.turma}"/>

	<div id="ajuda" class="descricaoOperacao" style="text-align: justify;">
		Essa tela tem por finalidade realizar o preenchimento do formulário de evolução dos discentes do nível Infantil e Fundamental. 
		<br />
		<ul>
			<li> <b> Avaliar: </b> Informa a forma de avaliação da turma, podendo ser definidos da seguinte forma: </li>
			<rich:dataTable value="#{ formularioEvolucaoCriancaMBean.formasAvaliacaoValidas }" var="formasAva" width="100%" style="border:0; background: #FFFFE4;">
				<rich:column style="border:0; background: #FFFFE4;" width="40%;">
					<li>
						<b> <h:outputText id="formaAvaDescricaoaa" value="#{ formasAva.legenda }" /> </b> -
						<h:outputText id="formaAvaDescricao" value="#{ formasAva.opcoes }" />
					</li>
				</rich:column>
			</rich:dataTable>
		</ul>
	</div>

	<table class="visualizacao" >
		<tr>
			<th width="20%"> Turma: </th>
			<td colspan="3"> ${turma.descricaoTurmaInfantil } </td>
		</tr>
		<tr>
			<th width="20%"> Local: </th>
			<td colspan="3"> ${turma.local} </td>
		</tr>
		<tr>
			<th width="20%"> Matrícula: </th>
			<td colspan="3"> ${discente.matricula } </td>
		</tr>
		<tr>
			<th> Discente: </th>
			<td colspan="3"> ${discente.pessoa.nome } </td>
		</tr>
		<tr>
			<th> Status: </th>
			<td> ${discente.statusString } </td>
		</tr>
	</table>
	<br />

<h:form id="form">
	
	<table class="formulario" width="30%">
		<caption>Critérios para a avaliação</caption>
		<tr>
			<th> Bimestre: </th>
			<td>
				<h:selectOneMenu id="selectOneArea" value="#{ registroEvolucaoCriancaMBean.formulario.periodo }" onchange="submit();" 
					valueChangeListener="#{ registroEvolucaoCriancaMBean.alterarCriterios }">
					<f:selectItems id="itemsSelectBimestre" value="#{ registroEvolucaoCriancaMBean.bimestres }" />
				</h:selectOneMenu>
			</td>
		</tr>
	</table>
	
	<br />

	<table class="formulario" width="100%">
		<caption><h:outputText value="#{ registroEvolucaoCriancaMBean.formulario.periodo }° Bimestre" /></caption>
			
			<a4j:repeat value="#{registroEvolucaoCriancaMBean.itens}" var="itemForm" rowKeyVar="row">
		
					<a4j:region rendered="#{ itemForm.bloco }">
						<tr>
							<td>
								<h:outputText id="labelItemDescricaoBloco" value="#{ itemForm.item.descricao }" style="font-size: 18px; font-weight: bold;" />
							</td>
						</tr>
					</a4j:region>

					<a4j:region rendered="#{ itemForm.area }">
						<tr>
							<td>
								<h:outputText id="labelItemDescricaoArea" value="#{ itemForm.item.descricao }" style="font-size: 16px; font-weight: bold;" />
							</td>
						</tr>
					</a4j:region>
				
 					<a4j:region rendered="#{ itemForm.conteudo }"> 
						<tr>
							<td class="subFormulario">Conteúdo/Objetivos</td>
							<a4j:region rendered="#{ itemForm.item.formaAvaliacao.indefinida }">
								<td  style="text-align: center;" class="subFormulario"></td>
							</a4j:region>
							
							<a4j:region rendered="#{ not itemForm.item.formaAvaliacao.indefinida }">
								<td  style="text-align: center;" class="subFormulario">Avaliar</td>
							</a4j:region>
							
						</tr>
						<tr>
							<td class="subFormulario">
								<h:outputText id="labelItemDescricaoConteudo" value="#{ itemForm.item.descricao }" style="font-size: 14px; padding-left: 10px;"/>
							</td>
							
							<td class="subFormulario" style="text-align: center;">
								<a4j:repeat value="#{itemForm.item.formaAvaliacao.headOpcoes}" var="formaAva">
									<h:outputText value="#{ formaAva }" rendered="#{ itemForm.conteudo && not itemForm.item.formaAvaliacao.indefinida }" style="padding: 20px;"/> 
								</a4j:repeat>
							</td>
						</tr>
					</a4j:region>
					
					<a4j:region rendered="#{ itemForm.subCont }">
							<tr>
								<td class="subFormulario" colspan="2">
									<h:outputText id="labelItemDescricaoSubConteudo" value="#{ itemForm.item.descricao }" style="font-size: 14px; padding-left: 10px;"/>
								</td>
							</tr>
					</a4j:region>
					
					<a4j:region rendered="#{ itemForm.objetivo }">
						<tr>
							<td>
								<h:outputText id="labelItemDescricaoObj" value="#{ itemForm.item.descricao }" style="padding-left: 15px; font-size: 12px;"/>
							</td>
							<td>
								<center>
									<a4j:repeat value="#{itemForm.item.formaAvaliacao.headOpcoes}" var="formaAva">
										<h:outputText value="#{ formaAva }" rendered="#{ itemForm.conteudo && not itemForm.item.formaAvaliacao.indefinida }" style="padding: 20px;"/> 
									</a4j:repeat>
									
									<h:selectOneRadio id="item2" layout="lineDirection" value="#{ itemForm.itemPeriodo.resultado }" disabled="#{ registroEvolucaoCriancaMBean.verRegistros }"
										rendered="#{ itemForm.objetivo && not itemForm.item.formaAvaliacao.indefinida && not empty itemForm.item.formaAvaliacao.allFormasAva }">
										<f:selectItems value="#{ itemForm.item.formaAvaliacao.allFormasAva }" />
									</h:selectOneRadio>
								</center>
							</td>		
						</tr>
					</a4j:region>								
					
					<a4j:region rendered="#{ itemForm.exibirTextArea }">
						<tr>
							<td class="subFormulario" colspan="2"> Observações </td>
						</tr>
						<tr>
							<td colspan="2">
								<h:inputTextarea value="#{ itemForm.itemPeriodo.observacoes }" disabled="#{ registroEvolucaoCriancaMBean.verRegistros }"/>
							</td>
						</tr>
					</a4j:region>
		
			</a4j:repeat>
	
	    <tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton id="btnConcluirPreenchimento" value="Gravar e Encerrar" action="#{registroEvolucaoCriancaMBean.gravarFinalizar}" rendered="#{ not registroEvolucaoCriancaMBean.verRegistros }"/>
					<h:commandButton id="btnGravar" value="Gravar" action="#{registroEvolucaoCriancaMBean.gravar}" rendered="#{ not registroEvolucaoCriancaMBean.verRegistros }"/>
					<h:commandButton id="btnImprimir" value="Imprimir" action="#{registroEvolucaoCriancaMBean.imprimir}" />
					<h:commandButton id="btnVoltar" value="<< Voltar" action="#{registroEvolucaoCriancaMBean.formPreenchidos}" rendered="#{ registroEvolucaoCriancaMBean.verRegistros }"/>
					<h:commandButton id="btnCancelar2" value="Cancelar" action="#{registroEvolucaoCriancaMBean.cancelar}" immediate="true" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
		
	</table>
	
</h:form>

</f:view>
<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
tinyMCE.init({
	mode : "textareas", theme : "advanced", width : "100%", height : "150", language : "pt",
	theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
	theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
	theme_advanced_buttons3 : "",
	plugins : "searchreplace,contextmenu,advimage",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left"
});
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
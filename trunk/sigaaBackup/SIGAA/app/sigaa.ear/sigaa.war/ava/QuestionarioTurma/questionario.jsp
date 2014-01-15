<%@include file="/ava/cabecalho.jsp" %>
<f:view>
	<a4j:keepAlive beanName="questionarioTurma" />

	<%@include file="/ava/menu.jsp" %>
	<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
	<link href="/sigaa/css/ensino/questionarios.css" rel="stylesheet" type="text/css" />

	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	<h2 class="title">  <ufrn:subSistema/> &gt; Visualização do Questionário</h2>
	<style>
		.inline {
			display: inline;
		}
	</style>

	<h:form id="formQuestionario">
	<div class="infoAltRem">
		<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/> <h:commandLink value="Nova Pergunta" action="#{questionarioTurma.iniciarAdicionarPergunta}" />
		<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>
		<h:commandLink value="Adicionar perguntas do banco" action="#{categoriaPerguntaQuestionarioTurma.listar}">
			<f:param name="voltar_ao_questionario" value="true" />
		</h:commandLink>
		<h:graphicImage value="/img/prodocente/cima.gif"style="overflow: visible;"/>
        / <h:graphicImage value="/img/prodocente/baixo.gif" style="overflow: visible; margin-left: 0.3em;"/>
		: Mover pergunta para cima ou para baixo
		<br />
        <h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar pergunta
        <h:graphicImage value="/img/garbage.png" style="overflow: visible;"/>: Remover pergunta
	</div>

	<table class="formulario" width="100%">
		<caption>Resumo do Questionário</caption>
		<tr>
			<td colspan="2"><span style="display:inline-block;width:146px;text-align:right;">Título:</span>
				<b>${questionarioTurma.questionario.titulo}</b>
			</td>
		</tr>
		<tr>
			<td colspan="2"><span style="display:inline-block;width:146px;text-align:right;">Disponível de:</span>
				<b><ufrn:format type="data" valor="${questionarioTurma.questionario.inicio}" /> 
				a <ufrn:format type="data" valor="${questionarioTurma.questionario.fim}" /></b>
			</td>
		</tr>

		<tr><td colspan="2">
			<a4j:outputPanel id="perguntas">
			<h:outputText value="<div style='width:100%;margin:20px;font-weight:bold;text-align:center;color:#CC0000;'>Selecione uma das opções acima para adicionar perguntas a este questinoário.</div>" escape="false" rendered="#{questionarioTurma.modelPerguntas.rowCount == 0 }" />
			<h:outputText value="#{questionarioTurma.mensagemErroPergunta}" style='color:#AA0000;margin:10px;text-align:center;display:block;font-weight:bold;' escape="false"/>
			<rich:dataTable var="pergunta" value="#{questionarioTurma.modelPerguntas}" width="100%" styleClass="listagem" id="dataTableQuestionario" rowKeyVar="row" rendered="#{questionarioTurma.modelPerguntas.rowCount > 0}">
			
				 <f:facet name="header">
	                 <rich:columnGroup>
	                     <rich:column colspan="2">
	                         <h:outputText value="Questionário" />
	                     </rich:column>
	                 </rich:columnGroup>
                 </f:facet>
			
				<rich:column rendered="#{pergunta.ativo}">
				
					<h:outputText value="#{row + 1}. #{pergunta.nome}" styleClass="pergunta" />
				
					<h:outputText escape="false" value="#{pergunta.pergunta}" styleClass="descricaoPergunta" /> 
					
					<h:panelGroup rendered="#{pergunta.vf}">
						<rich:panel>
							<h:outputText value="#{pergunta.gabaritoVf == null ? 'Verdadeiro / Falso' : (pergunta.gabaritoVf ? 'Verdadeiro' : 'Falso')}"/>
						</rich:panel>
					</h:panelGroup>
					
					<h:panelGroup rendered="#{pergunta.dissertativa}">
						<rich:panel>
							<h:outputText value="#{empty pergunta.gabaritoDissertativa ? 'Resposta Dissertativa' : pergunta.gabaritoDissertativa}"/>
						</rich:panel>
					</h:panelGroup>
					
					<h:panelGroup rendered="#{pergunta.numerica}">
						<rich:panel>
							<h:outputText value="#{empty pergunta.gabaritoNumerica ? 'Resposta Numérica' : pergunta.gabaritoNumericaString}"/>
						</rich:panel>
					</h:panelGroup>
				
				</rich:column>
				
				<rich:column width="65px" style="vertical-align: top; padding-top: 12px;" rendered="#{pergunta.ativo}">
					<a4j:commandButton id="cima" image="/img/prodocente/cima.gif" title="Mover para cima" 
						actionListener="#{questionarioTurma.movePerguntaCima}" 
						reRender="dataTableQuestionario" styleClass="noborder"/>
						
					<a4j:commandButton id="baixo" image="/img/prodocente/baixo.gif" title="Mover para baixo" 
						actionListener="#{questionarioTurma.movePerguntaBaixo}" 
						reRender="dataTableQuestionario"  styleClass="noborder"/>
						
					<h:commandButton id="alterarItem" image="/img/alterar.gif" title="Alterar Pergunta" 
						action="#{questionarioTurma.alterarPergunta}" styleClass="noborder"/>
						
					<a4j:commandButton id="removerItem" image="/img/garbage.png" title="Remover Pergunta" 
						actionListener="#{questionarioTurma.removerPergunta}" onclick="if (!confirm('Tem certeza que deseja remover esta pergunta?')) return false;"
						reRender="perguntas,perguntasPanel" styleClass="noborder"/>
				</rich:column>
						
				
				<rich:subTable var="alternativa" value="#{pergunta.alternativas}" rendered="#{pergunta.ativo && pergunta.unicaEscolha}" rowClasses="alternativa" >
					<rich:column styleClass="alternativa" rendered="#{alternativa.ativo}">
						<span class="radio <h:outputText value="#{alternativa.gabarito ? 'marcado' : ''}"/>">
						<h:outputText value="#{alternativa.letraAlternativa} #{alternativa}"/>
						</span>
					</rich:column>
                </rich:subTable>
			
			
				<rich:subTable var="alternativa" value="#{pergunta.alternativas}" rendered="#{pergunta.ativo && pergunta.multiplaEscolha}"  rowClasses="alternativa">
					<rich:column styleClass="alternativa" rendered="#{alternativa.ativo}">
						<span class="checkbox <h:outputText value="#{alternativa.gabarito ? 'marcado' : ''}"/>">
							<h:outputText value="#{alternativa.letraAlternativa} #{alternativa}"/>
						</span>
					</rich:column>
                </rich:subTable>
			
			</rich:dataTable>
			</a4j:outputPanel>
			<hr style="color:#CCC;"/><br/>
		</td></tr>
		<tr><td colspan="2">
			<a4j:outputPanel rendered="#{questionarioTurma.modelPerguntas.rowCount > 0}">
			<table>
				<tr>
					<th>Exibir um sub-conjunto aleatório de perguntas?</th>
					<td>
						<h:selectOneRadio id="exibirSubConjunto" value="#{ questionarioTurma.questionario.exibirSubConjunto }" onclick="exibeTamanhoSubConjunto()">
							<f:selectItems value="#{ questionarioTurma.simNao }" />
						</h:selectOneRadio>
					</td>
				</tr>	
				<tr id="subConjunto" >
					<th><span style="margin-left:20px;">Questões por tentativa:<span class="required">&nbsp;</span></span> </th>
					<td>			
						<h:inputText id="questoPorTentativas" value="#{ questionarioTurma.questionario.tamanhoSubConjunto }" style="width:25px;" maxlength="3" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);" />
					</td>
				</tr>
				<tr id="depende">
					<th><span style="margin-left:20px;">Depende da última tentativa:<span class="required">&nbsp;</span></span></th>
					<td>		
						<h:selectOneRadio id="dependeUltima" value="#{ questionarioTurma.questionario.dependeUltima }" styleClass="inline">
							<f:selectItems value="#{ questionarioTurma.simNao }" />
						</h:selectOneRadio>
						<ufrn:help>
							Caso o questionário aceite mais de uma tentativa, será gerado um subconjunto aleatório de perguntas para primeira tentativa do aluno, no entanto para as demais tentativas sempre será exibido o mesmo subconjunto. 
						</ufrn:help>
					</td>
				</tr>
				
				<tr>
					<td colspan="2">
						<h2>Avaliação</h2>
						<div class="descricaoOperacao"><p>Defina abaixo as configurações da avaliação deste questionário, informando os dados que constarão na planilha de notas da turma.</p></div>
					</td>
				</tr>
				
				<tr>
					<td colspan="2"><span style="display:inline-block;width:146px;text-align:right;">Possui Nota:</span>
						<h:selectOneMenu id="possuiNota" onchange="exibeCamposNotas(this);" value="#{ questionarioTurma.questionario.possuiNota }">
							<f:selectItems value="#{ questionarioTurma.simNao }"/>
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr><td colspan="2">
					<table id="tableAvaliacao">
						<tr id="avaliacaoAbreviacao">
							<th style="width:137px;" class="required">Abreviação:</th>	
							<td>
								<h:inputText id="abreviacao" value="#{ questionarioTurma.questionario.abreviacao }"  maxlength="4" size="5"/>
							</td>
						</tr>
						
						<tr id="avaliacaoUnidade">
							<th class="required">Unidade:</th>
							<td>
								<h:selectOneMenu id="unidade" value="#{ questionarioTurma.questionario.unidade }" onchange="exibePeso(this),exibeNotaMaxima(this);">
									<f:selectItems value="#{ tarefaTurma.unidades }"/>
									<a4j:support event="onclick" onsubmit="true" reRender="panelNotas"/>	
								</h:selectOneMenu>
							</td>
						</tr>
						
						<tr>
							<th><span id="labelNotaMaximaUnidade">Nota Máxima:<span class="required" style="margin-left:1px;padding-left:8px"> </span></span></th>
							<td>	
								<span id="valorNotaMaximaUnidade">
									<h:selectOneMenu id="nota" value="#{ questionarioTurma.questionario.notaMaxima }">
										<f:selectItems value="#{ tarefaTurma.notas }"/>
									</h:selectOneMenu>
									<ufrn:help>A nota máxima informada aqui será utilizada na avaliação referente a esta tarefa. Caso o valor selecionado seja maior que dez, o valor máximo da avaliação será um décimo deste.</ufrn:help>
								</span>
							</td>
						</tr>
				
						<tr id="avaliacaoPeso">
							<th><span id="labelPesoUnidade">Peso:<span class="required" style="margin-left:1px;padding-left:8px"> </span></span></th>	
							<td>
								<span id="valorPesoUnidade">
									<h:inputText id="peso" value="#{ questionarioTurma.questionario.peso }" onkeyup="return formatarInteiro(this);" maxlength="2" size="3" converter="#{intConverter}"/>
									<ufrn:help>O peso só deve ser informado se a unidade tiver sua nota calculada através de média ponderada.</ufrn:help>
								</span>
							</td>
						</tr>
					</table>
				</td></tr>
				
				<tr>
					<th>Notificação:</th>
					<td>
						<h:selectBooleanCheckbox id="notificacao" value="#{ questionarioTurma.notificarAlunos }" /> Notificar os alunos por e-mail após a finalização deste questionário
					</td>
				</tr>
			</table>		
			</a4j:outputPanel>
		</td></tr>
		<tfoot>
			<tr><td colspan="2">
				<h:commandButton value="Salvar sem publicar" action="#{ questionarioTurma.salvarQuestionarioSemFinalizar }" id="btnCadastrar" onclick="return(mensagemNotas());" rendered="#{!questionarioTurma.questionario.finalizado}"/>
				<h:commandButton value="Salvar e despublicar" action="#{ questionarioTurma.salvarQuestionarioSemFinalizar }" id="btnDespublicar" onclick="return(mensagemNotas());" rendered="#{questionarioTurma.questionario.finalizado}"/>
				<h:commandButton value="Publicar Questionário" action="#{questionarioTurma.finalizarQuestionario}" id="btnFinalizar" onclick="return(mensagemNotas());"/>
				<h:commandButton value="<< Editar Dados Gerais" action="#{questionarioTurma.alterarDadosDoQuestionario}" id="btnDadosGerais"/>
				<h:commandButton value="<< Voltar aos Questionários" action="#{questionarioTurma.listarQuestionariosDocente}" id="btnCancelar"/>
			</td></tr>
		</tfoot>
	
	</table>
	<input type="hidden" id="possuiAvaliacoes" value="${ questionarioTurma.possuiAvaliacoes }"/>
	<a4j:outputPanel id="panelNotas" style="display:none;">
		<h:outputText id="unidadePossuiNotas" value="#{questionarioTurma.unidadePossuiNota}" />
	</a4j:outputPanel>

	</h:form>
	
	<script>

		// P - Com Pesos; A - Média Aritmética; S - Soma; 0 - Não configurado
		var calculoPrimeiraUnidade = '<h:outputText value="#{ questionarioTurma.config != null ? questionarioTurma.config.tipoMediaAvaliacoes1 : 0 }" />';
		var calculoSegundaUnidade = '<h:outputText value="#{ questionarioTurma.config != null ? questionarioTurma.config.tipoMediaAvaliacoes2 : 0 }" />';
		var calculoTerceiraUnidade = '<h:outputText value="#{ questionarioTurma.config != null ? questionarioTurma.config.tipoMediaAvaliacoes3 : 0 }" />';
		
		function exibePeso (unidade) {
			if (unidade.value == 1 && calculoPrimeiraUnidade == 'P' || unidade.value == 2 && calculoSegundaUnidade == 'P' || unidade.value == 3 && calculoTerceiraUnidade == 'P'){
				document.getElementById("labelPesoUnidade").style.display = "inline";
				document.getElementById("valorPesoUnidade").style.display = "inline";
			} else {
				document.getElementById("labelPesoUnidade").style.display = "none";
				document.getElementById("valorPesoUnidade").style.display = "none";
			}
		}

		function exibeNotaMaxima (unidade) {
			if (unidade.value == 1 && calculoPrimeiraUnidade == 'S' || unidade.value == 2 && calculoSegundaUnidade == 'S' || unidade.value == 3 && calculoTerceiraUnidade == 'S'){
				document.getElementById("labelNotaMaximaUnidade").style.display = "inline";
				document.getElementById("valorNotaMaximaUnidade").style.display = "inline";
			} else {
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
	
		function mensagemAtualizar () {
			var nota = document.getElementById("formQuestionario:nota");
			
			if ( possuiAvaliacoes && nota.value == "false" ) 
				return(confirm("Este questionário possui nota, caso seu campo \"Possui Nota\" seja alterado para  \"N\u00e3o\" " +
							 		"as notas deste questionário na planilha de notas ser\u00e3o removidas. Deseja realmente alterar este item?"));
			else return true;
		}
	
		function exibeTamanhoSubConjunto () {
			var radio = document.getElementById("formQuestionario:exibirSubConjunto:0");
			var subConjunto = document.getElementById("subConjunto");
			var dependeUltima = document.getElementById("depende");
			
			subConjunto.style.display = radio.checked ? "" : "none";
			dependeUltima.style.display = radio.checked ? "" : "none";
		}
	
		function corrigeNumero (campo) {
			campo.value = campo.value.replace(/[^0-9\.\-]/, '');
	
			var apagar = false;
	
			if (campo.value.length == 0)
				return;
	
			if (campo.value[0] == ".")
				apagar = true;
			
			var pontos = 0;
			for (i = 0; i < campo.value.length; i++){
				if (campo.value[i] == ".") pontos ++;
				if (pontos > 1){
					apagar = true;
					break;
				}
	
				if (campo.value[i] == "-" && i > 0){
					apagar = true;
					break;
				}
			}
	
			if (apagar){
				campo.value = "";
				return false;
			}
		}
		
		function mensagemNotas(){
			var nota = document.getElementById("formQuestionario:possuiNota");
			var possuiNotas = document.getElementById('formQuestionario:unidadePossuiNotas').innerHTML == "true";

				if ( nota.value == "true" ){
					if ( possuiNotas )
						return(confirm("J\u00e1 foram cadastradas notas para esta unidade. Ao cadastrar um question\u00e1rio que \"Possui Nota\" a unidade na planilha de notas" +
								" ser\u00e1 desmembrada em avali\u00e7\u00f5es e as notas ser\u00e3o perdidas. " +
				 				" Deseja continuar com a opera\u00e7\u00e3o?"));
			}
		}
		
		jQuery(document).ready(function () {
			exibeTamanhoSubConjunto ();
			exibeCamposNotas(document.getElementById("formQuestionario:possuiNota"));
			exibePeso (document.getElementById("formQuestionario:unidade"));
			exibeNotaMaxima (document.getElementById("formQuestionario:unidade"));
		});
	</script>
	
</f:view>
<%@include file="/ava/rodape.jsp" %>
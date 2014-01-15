<style>
.botoes {
	align: right;
	width: 10%;
}
.descricaoAlternativas {
    text-align: left;
	align: left;
}
.selectAlternativas{
	align: left;
	width: 3%;
}


</style>

<c:set var="confirmResumoQuestionario" scope="application" 
	value="if (!confirm('Todos os dados preenchidos da pergunta serão perdidos! Tem certeza que deseja realizar esta operação?')) return false;" />

<h:messages showDetail="true"></h:messages>

<%@page import="br.ufrn.sigaa.questionario.dominio.PerguntaQuestionario" %>

<h:form id="adicionarPergunta">
			
<table class="formulario" style="border-collapse: separate;border-spacing: 10px;">
<c:if test="${!alterarPergunta}">
	<caption class="formulario">Adicionar Pergunta</caption>
</c:if>
<c:if test="${alterarPergunta}">
	<caption class="formulario">Alterar Pergunta</caption>
</c:if>
<a4j:region>

	<tr>
		<h:outputText escape="false" value="<th width='15%'>Adicionar esta pergunta em uma Categoria:</th>" rendered="#{ !questionarioTurma.adicionarEmCategoria }" />
		<h:outputText escape="false" value="<th class='required' width='15%'>Categoria:</th>" rendered="#{ questionarioTurma.adicionarEmCategoria }" />
		<td>
			<h:selectOneMenu id="categoria" value="#{questionarioTurma.pergunta.categoria.id}">
				<f:selectItem itemValue="0" itemLabel="-- SELECIONE UMA CATEGORIA --"/>
				<f:selectItems value="#{ categoriaPerguntaQuestionarioTurma.allCombo}"/>
			</h:selectOneMenu>
			&nbsp;
			<h:commandLink action="#{ categoriaPerguntaQuestionarioTurma.preCadastrar }">
				<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" />
				<h:outputText value="Criar categoria" />
				<f:param name="voltar_cadastro_pergunta" value="true" />
			</h:commandLink>
		</td>
	</tr>
	
	<tr>
		<th class="required">Tipo de pergunta:</th>
		<td>			
			<h:selectOneMenu id="tipoPergunta" value="#{questionarioTurma.pergunta.tipo}" onchange="alterarTipo(this); submit();">
				<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
				<f:selectItems value="#{ questionarioTurma.tiposPergunta}"/>
			</h:selectOneMenu>
		</td>
	</tr>

	<tr>
		<th class="required">Pergunta:</th>
		<td>
			<h:inputText styleClass="mceEditor" value="#{questionarioTurma.pergunta.nome}" style="width:400px;" maxlength="400" />
		</td>
	</tr>
	<tr>
		<th style="vertical-align:middle;"></th>
		<td>
			<div class="descricaoOperacao" style="text-align:justify;width:70%;line-height:1.9em;margin-left:0px">
				É possível adicionar fórmulas matemáticas utilizando Latex na descrição da pergunta. Para isto, basta adicionar qualquer fórmula
				entre os delimitadores <b>"\("</b> e <b>"\)"</b> ou <b>"\["</b> e "<b>\]"</b>, e o sistema irá gerar automaticamente uma imagem contendo a fórmula.
				Por exemplo, a fórmula \(lim_{x\rightarrow\infty} \frac{x^{3}}{e^{x}}\) gerará a seguinte imagem: 
				<img style="vertical-align:middle;" title="fórmula" src="/sigaa/ava/img/questionarios/formula.png">
			</div>
		</td>
		
	</tr>
	<tr>
		<th style="vertical-align:middle;">Descrição da Pergunta:</th>
		<td>
			<c:if test="${empty questionarioTurma.pergunta.perguntaFormula}">
				<h:inputTextarea styleClass="mceEditor" value="#{questionarioTurma.pergunta.pergunta}" rows="4" cols="80" id="txtPergunta" />
			</c:if>
			<c:if test="${not empty questionarioTurma.pergunta.perguntaFormula}">
				<h:inputTextarea styleClass="mceEditor" value="#{questionarioTurma.pergunta.perguntaFormula}" rows="4" cols="80" id="txtPergunta" />
			</c:if>
		</td>
		
	</tr>
	
	<tr id="trResposta" style="display: none">
		<td colspan="2" width="100%">
			<table width="100%" style="border-collapse: separate;border-spacing: 10px;">
				<caption style="text-align: center">Gabarito</caption>
				<tr><td colspan="2"><div class="descricaoOperacao">Para perguntas dissertativas, o gabarito não é obrigatório. Deixando-o em branco, será necessário avaliar as respostas manualmente. Lembre-se que o aluno deverá responder exatamente o mesmo texto digitado abaixo para sua resposta ser considerada válida.</div></td></tr>
				<tr align="center">
				
				<c:if test="${questionarioTurma.pergunta.dissertativa}">
					<tr align="right">
									
								<th width="15%" align="right">Limitar resposta em:</th>
								<td align="left">
									<h:selectOneMenu id="tipoLimitacao" value="#{questionarioTurma.pergunta.tipoLimitadorDissertativa}" onchange="submit();">
										<f:selectItem itemValue="1" itemLabel="NÃO LIMITAR"/>
										<f:selectItem itemValue="2" itemLabel="CARACTERES"/>
										<f:selectItem itemValue="3" itemLabel="PALAVRAS"/>
									</h:selectOneMenu>
								</td>
						
					</tr>
					
					<a4j:outputPanel id="dadosLimitacaoTamanhoRespostaDissertativa" rendered="#{not questionarioTurma.pergunta.naoLimitar}">
						<h:outputText id="auxDadosLimitacao1" value="<tr align='left' id='campoLimite'><th align='right' class='required'>Número máximo de #{questionarioTurma.pergunta.limitarPalavras ? 'palavras:' : 'caracteres:'}</th><td align='left' style='text-align:left!importatn;'>" escape="false" />
						<h:inputText id="valorLimitadorDissertativa" value="#{questionarioTurma.pergunta.valorLimitadorDissertativa}" size="4" maxlength="4" onkeyup="return formatarInteiro(this);" onblur="submit();"/>
						<h:outputText id="auxDadosLimitacao2" value="</td></tr>" escape="false" />
					</a4j:outputPanel>
					  
					
					
				</c:if>
				
				<c:if test="${questionarioTurma.pergunta.dissertativa}">
					<th width="15%" align="right" style="vertical-align:middle;">Resposta:</th>
				</c:if>
				
				<c:if test="${not questionarioTurma.pergunta.dissertativa}">
					<th width="15%" align="right" class="required">Resposta:</th>
				</c:if>
						
				<td align="left">
					<h:inputText id="respostaNumerica" value="#{questionarioTurma.pergunta.gabaritoNumericaString}" rendered="#{questionarioTurma.pergunta.numerica}" size="5" onkeyup="return corrigeNumero(this);" onblur="return corrigeNumero(this);" />
					
					<c:if test="${questionarioTurma.pergunta.dissertativa}">		
						<h:inputTextarea id="gabaritoDissertativa" value="#{questionarioTurma.pergunta.gabaritoDissertativa}" cols="80" rows="4" rendered="#{questionarioTurma.pergunta.dissertativa}" onkeydown="return contar(#{questionarioTurma.pergunta.id}, this, #{questionarioTurma.pergunta.tipoLimitadorDissertativa}, #{questionarioTurma.pergunta.valorLimitadorDissertativa},event)" onkeypress="return contar(#{questionarioTurma.pergunta.id}, this, #{questionarioTurma.pergunta.tipoLimitadorDissertativa}, #{questionarioTurma.pergunta.valorLimitadorDissertativa},event)" onkeyup="return contar(#{questionarioTurma.pergunta.id}, this, #{questionarioTurma.pergunta.tipoLimitadorDissertativa}, #{questionarioTurma.pergunta.valorLimitadorDissertativa},event)"/>
						<a4j:outputPanel rendered="#{not questionarioTurma.pergunta.naoLimitar}">
							<p>Número máximo de <h:outputText value='#{questionarioTurma.pergunta.limitarPalavras ? "palavras " : "caracteres "}' /> da resposta: <h:outputText id="limiteCampoCaracteres" value="#{questionarioTurma.pergunta.valorLimitadorDissertativa}"/>
								<c:if test="${empty questionarioTurma.pergunta.valorLimitadorDissertativa}">
									0
								</c:if>
							</p>
							<p>Você ainda pode digitar <h:outputText styleClass="limitador_#{questionarioTurma.pergunta.id}" id="commentCountCaracteres" value="#{questionarioTurma.pergunta.valorLimitadorDissertativa}"/> 
								<c:if test="${empty questionarioTurma.pergunta.valorLimitadorDissertativa}">
									0
								</c:if>
							<h:outputText value='#{questionarioTurma.pergunta.limitarPalavras ? " palavras." : " caracteres."}' /></p>
						</a4j:outputPanel>
					</c:if>
									
					<h:selectOneRadio rendered="#{questionarioTurma.pergunta.vf}" value="#{questionarioTurma.pergunta.gabaritoVf}" id="perguntaVF">
						<f:selectItem itemValue="true" itemLabel="Verdadeiro"/>
						<f:selectItem itemValue="false" itemLabel="Falso"/>
					</h:selectOneRadio>
				</td>

				
				
				
			</table>
		</td>
	</tr>
	
	<tr id="trAdicionarAlternativas" style="display: none">
		<td colspan="2">
		
			<table class="subformulario" style="border-collapse: separate;border-spacing: 10px;" width="100%">
				<caption style="text-align: center">Adicionar alternativas</caption>

				<tr>
					<td colspan="5">
						<div class="infoAltRem" style="width: 100%">
							<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>: Adicionar alternativa
							<h:graphicImage value="/img/prodocente/cima.gif"style="overflow: visible;"/> 
								/ <h:graphicImage value="/img/prodocente/baixo.gif" style="overflow: visible; margin-left: 0.3em"/>: 
								Mover alternativa para cima ou para baixo
					        <h:graphicImage value="/img/garbage.png" style="overflow: visible;"/>: Remover alternativa <br/>
						</div>
					</td>
				</tr>

				<tr>
					<td colspan="5"> 
						<h:panelGroup id="errosQuestionario">
							<h:outputText rendered="#{not empty errosQuestionario}" value="<div id='painel-erros' style='position: relative; padding-bottom: 10px;'><ul class='erros'><li>#{errosQuestionario}</li></ul></div>" escape="false"/>
						</h:panelGroup>
					</td>
				</tr>
				
				<tr>
					<td colspan="5">
						<div class="descricaoOperacao">
							<p>Esta pergunta deve possuir alternativas.</p>
							<p>Para adicionar uma alternativa, preencha os campos "Texto" e, em seguida, selecione a opção <img src="${ctx}/img/adicionar.gif" />. Após isso, informe o feedback que o aluno receberá ao selecionar a alternativa. O Feedback é uma mensagem a ser exibida quando o discente selecionar a alternativa, de acordo com a configuração do questionário.</p>
						</div>
					</td>
				</tr>

				<tr>
					<th>&nbsp;</th><th style="text-align:left;">Texto</th><th>&nbsp;</th><th>&nbsp;</th>
				</tr>

				<tr>
					<th width="15%" class="required">Alternativa: </th>
					<td> <h:inputTextarea value="#{questionarioTurma.alternativa.alternativa}" rows="1" id="txtAlternativa" style="width: 98%"/></td>

					<td width="5%"> 
						<a4j:commandButton image="/img/adicionar.gif" id="adicionarAlternativa"
							title="Adicionar Resposta" actionListener="#{questionarioTurma.adicionarAlternativa}" 
							reRender="dataTableAlternativasPergunta, txtAlternativa, errosQuestionario" styleClass="noborder"/>
					</td>
					<td width="5%">
			            <a4j:status>
			                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
			            </a4j:status>
					</td>
				</tr>
			</table>
		
		</td>
	</tr>
	
	<tr id="trAlternativas" style="display: none">		
		<td colspan="2" align="center">
		
			<h:dataTable var="alternativa" value="#{questionarioTurma.modelAlternativas}" id="dataTableAlternativasPergunta" 
				width="95%" columnClasses="selectAlternativas, descricaoAlternativas, descricaoAlternativas, botoes" rowClasses="linhaPar, linhaImpar">
				
				
				<h:column>
				
					<h:selectBooleanCheckbox value="#{alternativa.gabarito}" id="checkMultiplaEscolha" 
						rendered="#{questionarioTurma.pergunta.multiplaEscolha}"/>
				
					<t:selectOneRadio id="radioUnicaEscolha" forceId="true" forceIdIndex="false" 
						value="#{questionarioTurma.gabaritoUnicaEscolha}" 
						rendered="#{questionarioTurma.pergunta.unicaEscolha}">
						<f:selectItem itemValue="#{alternativa.ordem}" itemLabel=""/>
					</t:selectOneRadio>
					
				</h:column>
				
				<h:column>
					<f:facet name="header">
						<h:outputText value="ALTERNATIVAS"/>
					</f:facet>
					<h:outputText value="#{alternativa}"/>
				</h:column>
				
				<h:column>
					<f:facet name="header">
						<h:outputText value="FEEDBACK" />
					</f:facet>
					<h:inputText value="#{alternativa.feedback}" style="width:90%;" />
				</h:column>
				
				<h:column>
					<a4j:commandButton id="cima" image="/img/prodocente/cima.gif"
						title="Mover alternativa para cima" actionListener="#{questionarioTurma.moveAlternativaCima}" 
						reRender="dataTableAlternativasPergunta" styleClass="noborder" />
						
					<a4j:commandButton id="baixo" image="/img/prodocente/baixo.gif" title="Mover alternativa para baixo" 
						actionListener="#{questionarioTurma.moveAlternativaBaixo}" 
						reRender="dataTableAlternativasPergunta"  styleClass="noborder" />
						
					<a4j:commandButton id="removerItem" image="/img/garbage.png" title="Remover alternativa" 
						actionListener="#{questionarioTurma.removerAlternativa}" 
						reRender="dataTableAlternativasPergunta" styleClass="noborder" />
				</h:column>
			</h:dataTable>
		</td>
	</tr>
	
	<c:if test="${questionarioTurma.pergunta.tipo > 0}">
		<tr><td colspan="2"><h2>Feedbacks</h2></td></tr>
		<tr>
			<td colspan="2" style="padding-left:110px;">
				Feedback que será exibido se o aluno acertar a questão:<br>
				<h:inputTextarea value="#{questionarioTurma.pergunta.feedbackAcerto}" rows="4" cols="80" id="feedbackAcerto"/>
			</td>
		</tr>
		
		<tr>
			<td colspan="2" style="padding-left:110px;">
				Feedback que será exibido se o aluno errar a questão:<br>
				<h:inputTextarea value="#{questionarioTurma.pergunta.feedbackErro}" rows="4" cols="80" id="feedbackErro"/>
			</td>
		</tr>
	</c:if>
</a4j:region>	
	<tfoot>
		<tr>
		<td colspan="2">
			<h:commandButton value="Salvar pergunta" action="#{questionarioTurma.adicionarPergunta}" id="btnAdd"/>
			<input type="hidden" name="naoSalvar" value="true"/>
			<h:commandButton value="<< Voltar" action="#{questionarioTurma.gerenciarPerguntasDoQuestionario}" id="voltarParaQuestionario" rendered="#{questionarioTurma.voltarAoQuestionario}" />	
			<h:commandButton value="<< Voltar" action="#{categoriaPerguntaQuestionarioTurma.listar}" id="voltarParaLista" rendered="#{!questionarioTurma.voltarAoQuestionario}" />
		</td>
		</tr>
	</tfoot>
	
</table>

</h:form>


<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>

<script type="text/javascript" charset="UTF-8">
	$('adicionarPergunta:txtPergunta').focus();
	
	function alterarTipo(sel){
		var val = null; 
		if( sel != null) 
			val = sel.options[sel.selectedIndex].value;
			
		if ( val == <%=PerguntaQuestionario.MULTIPLA_ESCOLHA%> || val == <%=PerguntaQuestionario.UNICA_ESCOLHA%>) {
			$('trAdicionarAlternativas').show();
			$('trAlternativas').show();
			$('trResposta').hide();
		} else if ( (val == <%=PerguntaQuestionario.DISSERTATIVA%> 
			|| val == <%=PerguntaQuestionario.NUMERICA%> 
			|| val == <%=PerguntaQuestionario.VF%>) ){
				$('trAdicionarAlternativas').hide();
				$('trAlternativas').hide();
				$('trResposta').show();
		} else {
			$('trAdicionarAlternativas').hide();
			$('trAlternativas').hide();
			$('trResposta').hide();
		}
		
	}
	
	alterarTipo($('adicionarPergunta:tipoPergunta'));

	<c:if test="${turmaVirtual.acessoMobile == false}">
		tinyMCE.init({
			mode : "textareas", editor_selector : "mceEditor", theme : "advanced", width : "460", height : "150", language : "pt",
			theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
			theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
			theme_advanced_buttons3 : "", plugins : "searchreplace,contextmenu,advimage", theme_advanced_toolbar_location : "top", theme_advanced_toolbar_align : "left"
		});
	</c:if>
	
	function corrigeNumero (campo) {
		campo.value = campo.value.replace(/[^0-9\.\,\-]/g, '');

		var apagar = false;

		if (campo.value.length == 0)
			return;

		if (campo.value[0] == "." || campo.value[0] == ",")
			apagar = true;
		
		var pontos = 0;
		for (i = 0; i < campo.value.length; i++){
			if (campo.value[i] == "." || campo.value[i] == ",") pontos ++;
			if (pontos > 1){
				apagar = true;
				break;
			}

			if (campo.value[i] == "-" && i > 0){
				apagar = true;
				break;
			}
		}

		if (apagar)
			campo.value = "";
	}


	function isSeparador(caracter) {

		if (caracter == ' ' || caracter == '\n' || caracter == '.' || caracter == ',' || caracter == ';' || caracter == ':' || caracter == '/')  
			return true;
		else
			return false;
	}
	
	function contar(id, campo, tipoLimitador, total,event) {  

		if (tipoLimitador == 1 ) {
			if (campo.value.length > 200000)
				campo.value = campo.value.substring(0,200000);
			
			return true;
		}
		var max = J(".limitador_"+id);
		var quantidade = 0;

		
		//Conta o numero de palavras do texto 
		if (tipoLimitador == 3){
			for (i = 0; i < campo.value.length; i++){
				if (isSeparador(campo.value[i]) && i-1 > 0 && !isSeparador(campo.value[i-1]) ){
					quantidade++;
				}
			}
		} else//conta o numero de caracteres
			quantidade = campo.value.length;


		// Limpa a area de texto caso o usuario consiga digitar mais que o limite de palavras.
		if (quantidade > total && tipoLimitador == 2) {
			campo.value = campo.value.substring(0,total);
		}

		// Limpa a area de texto caso o usuario consiga digitar mais que o limite de palavras. 
		if (quantidade > total && tipoLimitador == 3) {
			var i,p = 0;
			//Conta quantos caracteres existem 
			for (i = 0; i < campo.value.length && p < total; i++){
				if (isSeparador(campo.value[i]) && i-1 > 0 && !isSeparador(campo.value[i-1]) ){
					p++;
				}
			}
			campo.value = campo.value.substring(0,i);
			quantidade = p;
		}

		//Caso o campo estaja com o velor maximo, nao permitir que o usuario insira novos caracteres
		if (quantidade >= total && event.keyCode != 8 && event.keyCode != 46 && !(event.keyCode >= 33 && event.keyCode <= 40)) {
			max.html(total - quantidade);
			return false;
		}

		//Atualiza a contagem
		max.html(total - quantidade);
		if (campo.value.length == 0)
			max.html(total);
	}

</script>
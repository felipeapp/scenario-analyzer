<%@include file="/ava/cabecalho.jsp" %>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<a4j:keepAlive beanName="questionarioTurma" />
	<a4j:keepAlive beanName="categoriaPerguntaQuestionarioTurma" />

	<style media="all" type="text/css">
		td label {
			display:inline-block;
			padding-right:10px;
		}
		
		#relogio {
			text-align:center;
			border:1px solid #CCC;
			margin:10px auto 10px auto;
			padding:10px;
			width:250px;
		}
		
		#relogio span.numerosRelogio {
			display:inline-block;
			width: 25px;
			font-weight:bold;
			text-align:right;
			font-size:12pt;
		}
		
		#relogio span.nomesRelogio {
			display:inline-block;
		}
		
		.dr-table {
			border: 0;
		}
	
		.dr-pnl {
			margin: 3px 10px;
			border: 0;
			background: transparent;
		}
	
		.dr-pnl-b {
			padding: 3px 10px;
		}
		
		.dr-table-cell {
			border: 0;
		}
		
		.pergunta {
			font-weight: bold;
		}
		
		.tabelaQuestionario label{
			margin-left:5px;
		}
	</style>

	<%@include file="/ava/menu.jsp" %>
	
	<div style="text-align:center;font-weight:bold;font-size:12pt;margin-bottom:10px;">${ questionarioTurma.questionario.titulo }</div>
	<h:outputText escape="false" value="<div style='margin:0px 30px 30px 30px;border:1px solid #CCC;padding:10px;'>#{ questionarioTurma.questionario.descricao }</div>" rendered="#{not empty questionarioTurma.questionario.descricao}" />

	<div id="relogio">
		<p><strong style="font-weight:bold;">Tempo restante:</strong></p><br/>
		<span id="relogioh" class="numerosRelogio"></span> <span class="nomesRelogio">horas</span>
		<span id="relogiom" class="numerosRelogio"></span> <span class="nomesRelogio">minutos</span>
		<span id="relogios" class="numerosRelogio"></span> <span class="nomesRelogio">segundos</span>
	</div>

	<a4j:region>
		<h:form id="form">
			<a4j:poll interval="#{5*60*1000}" action="#{ questionarioTurma.submeterRespostasPoll }" onsubmit="desativarBotao()" oncomplete="ativarBotao()"/>
			
			<rich:dataTable var="resposta" value="#{questionarioTurma.respostasModel}" width="100%" id="dataTableQuestionario" styleClass="tabelaQuestionario" rowKeyVar="row" rowClasses="linhaPar, linhaImpar" >
			
				<rich:column rendered="#{resposta.pergunta.ativo}">
					<h:outputText value="#{row + 1}. #{resposta.pergunta.nome}" styleClass="pergunta" />
					<h:outputText value="#{resposta.pergunta.pergunta}" styleClass="descricaoPergunta" escape="false" />
					 		
					<h:panelGroup>
						<rich:panel>
							<h:selectOneRadio value="#{resposta.respostaVF}" 
								rendered="#{resposta.pergunta.vf}" id="respostaVf">
								<f:selectItem itemValue="true" itemLabel="Verdadeiro"/>
								<f:selectItem itemValue="false" itemLabel="Falso"/>
							</h:selectOneRadio>
							
									
									 	<a4j:outputPanel rendered="#{resposta.pergunta.dissertativa}">
											<h:inputTextarea value="#{resposta.respostaDissertativa}" rows="4" style="width: 98%;" id="respostaDissertativaLimiteCaracteres"  onkeydown="return contar(#{resposta.pergunta.id}, this, #{resposta.pergunta.tipoLimitadorDissertativa}, #{resposta.pergunta.valorLimitadorDissertativa},event)" onkeypress="return contar(#{resposta.pergunta.id}, this, #{resposta.pergunta.tipoLimitadorDissertativa}, #{resposta.pergunta.valorLimitadorDissertativa},event)" onkeyup="return contar(#{resposta.pergunta.id}, this, #{resposta.pergunta.tipoLimitadorDissertativa}, #{resposta.pergunta.valorLimitadorDissertativa},event)"/>
											<a4j:outputPanel rendered="#{not resposta.pergunta.naoLimitar}">
												<p>Número máximo de <h:outputText value='#{resposta.pergunta.limitarPalavras ? "palavras " : "caracteres "}' /> da resposta: <h:outputText id="limiteCampoCaracteres" value="#{resposta.pergunta.valorLimitadorDissertativa}"/></p>
												</p>Você ainda pode digitar <h:outputText styleClass="limitador_#{resposta.pergunta.id}" id="commentCountCaracteres" value="#{resposta.pergunta.valorLimitadorDissertativa}"/> <h:outputText value='#{resposta.pergunta.limitarPalavras ? " palavras." : " caracteres."}' /></p>
											</a4j:outputPanel>
										</a4j:outputPanel>
								
								
							<h:inputText value="#{resposta.respostaNumericaString}" size="5" onkeyup="return corrigeNumero(this);" onblur="return corrigeNumero(this);" rendered="#{resposta.pergunta.numerica}" id="respostaNumerica"/>
							
							<h:selectOneRadio value="#{resposta.alternativaEscolhida}" layout="pageDirection" converter="convertAlternativaPerguntaQuestionarioTurma" rendered="#{resposta.pergunta.unicaEscolha}" id="unicaEscolha"> 
								<t:selectItems value="#{resposta.pergunta.alternativasDesordenadas}" var="a" itemLabel="#{a.alternativa}" itemValue="#{a}"/>
							</h:selectOneRadio>
							
							<h:selectManyCheckbox value="#{resposta.alternativasEscolhidas}" layout="pageDirection" converter="convertAlternativaPerguntaQuestionarioTurma" rendered="#{resposta.pergunta.multiplaEscolha}" id="multiplaEscolha">
								<t:selectItems value="#{resposta.pergunta.alternativasDesordenadas}" var="a" itemLabel="#{a.alternativa}" itemValue="#{a}"/>
							</h:selectManyCheckbox>
						</rich:panel>
					</h:panelGroup>
			
				</rich:column>
			
			</rich:dataTable>
				
			<table class="formulario" width="100%">
				<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton value="Enviar" id="enviarRespostas" action="#{ questionarioTurma.submeterRespostas }" />
							<h:commandButton value="<< Voltar aos Questionários" id="botaoListarQuestionarios" action="#{ questionarioTurma.listarQuestionariosDiscente }" /> 
						</td>
					</tr>
				</tfoot>
			</table>
		</h:form>
	</a4j:region>
	
	<script type="text/javascript"> 
 
	var tempoRestante = ${ questionarioTurma.resposta.tempoRestante };
 
	var h = document.getElementById("relogioh");
	var m = document.getElementById("relogiom");
	var s = document.getElementById("relogios");
 
	var relogio = setInterval(cronometro, 1000);
 
	function desativarBotao () {
		var btn = document.getElementById("form:enviarRespostas");
		btn.disabled = true;
	}
	
	function ativarBotao () {
		var btn = document.getElementById("form:enviarRespostas");
		btn.disabled = false;
	}
	
	function cronometro () {
		var segundos = tempoRestante --;
		if (segundos < 0){
			segundos = 0;
			clearInterval(relogio);
		}
 
		var horas = Math.floor(segundos / 3600);
		segundos -= horas * 3600;
		var minutos = Math.floor(segundos / 60);
		segundos -= minutos * 60;
		h.innerHTML = horas >= 0 ? horas : 0;
		m.innerHTML = minutos >= 0 ? minutos : 0;
		s.innerHTML = segundos >= 0 ? segundos : 0;

		if (h.innerHTML < 10) h.innerHTML = "0" + h.innerHTML;
		if (m.innerHTML < 10) m.innerHTML = "0" + m.innerHTML;
		if (s.innerHTML < 10) s.innerHTML = "0" + s.innerHTML;
	}

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
	
</f:view>

<%@include file="/ava/rodape.jsp"%>
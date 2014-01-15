<%@include file="/ava/cabecalho.jsp" %>

<f:view>

	<a4j:keepAlive beanName="respostaTarefaTurma" />

	<%@include file="/ava/menu.jsp" %>
	<h:form id="form" prependId="false" enctype="multipart/form-data">
	
		
		<fieldset>
			<legend>Corrigir Tarefa</legend>
			
			<div class="infoAltRem">
				<img src="/sigaa/ava/img/selecionado.gif">: Tarefa Corrigida
				<c:if test="${respostaTarefaTurma.obj.idArquivoCorrecao > 0 }">	
					<h:graphicImage value="/ava/img/page_white_put.png" />: Baixar Arquivo
					<h:graphicImage value="/ava/img/page_white_get.png" />: Remover Arquivo			
				</c:if>
			</div>
			
			<ul class="form">
				<li>
					<label style="font-weight: bold;">Tarefa:</label>
					<span style="margin-left:3px">${respostaTarefaTurma.obj.tarefa.titulo}</span>
					<c:if test="${ respostaTarefaTurma.obj.lida }">
						<img src="/sigaa/ava/img/selecionado.gif">
					</c:if>
				</li>
				<li>
					<c:if test="${ not respostaTarefaTurma.obj.tarefa.emGrupo}">	
						<label style="font-weight: bold;">Aluno:</label>
						<span style="margin-left:3px">${respostaTarefaTurma.obj.usuarioEnvio.pessoa.nome}</span>
					</c:if>
					<c:if test="${respostaTarefaTurma.obj.tarefa.emGrupo}">	
						<c:if test="${!respostaTarefaTurma.obj.existeGrupo}">	
							<label>Grupo:</label>
								<h:outputText value="#{ respostaTarefaTurma.obj.usuarioEnvio.pessoa.nome }"/>
								<ufrn:help>Esta tarefa foi criada antes do SIGAA possuir a opção de Gerenciar Grupos de Alunos.</ufrn:help>
							</c:if>
						<c:if test="${respostaTarefaTurma.obj.existeGrupo}">
							<label>Grupo:</label>
								<span style="margin-left:3px">${respostaTarefaTurma.obj.grupoDiscentes.nome}</span>
								<c:forEach items="#{ respostaTarefaTurma.obj.grupoDiscentes.discentes }" var="d" varStatus="loop">
									<li style="margin-left:135px">
										<c:if test="${!d.removidoGrupo}">
											<h:outputText value="#{ d.pessoa.nome }"/>
										</c:if>
										<c:if test="${d.removidoGrupo}">
											<span style="text-decoration: line-through;"><h:outputText value="#{ d.pessoa.nome }" title="Aluno não se encontra mas neste grupo."/></span>
										</c:if>						
									</li>
								</c:forEach>
						</c:if>		
					</c:if>
				</li>
				
				
				<c:if test="${not respostaTarefaTurma.obj.tarefa.envioArquivo}">
					<li>
						<c:if test="${respostaTarefaTurma.obj.tarefa.respostaOnline}">
							<label style="font-weight:bold;">Resposta do(s) Aluno(s):</label><br />
							${respostaTarefaTurma.obj.textoResposta}
						</c:if>
						
					</li>
					<li>	
						<label style="font-weight:bold;">Comentários do(s) Aluno(s):</label><br />
						${respostaTarefaTurma.obj.comentarios}
					</li>
				</c:if>
				
				<c:if test="${ respostaTarefaTurma.obj.tarefa.possuiNota}">
					<li>
						<label style="font-weight:normal;">Nota:<span class="required">&nbsp;</span></label>
						<span><h:inputText id="Nota" value="#{respostaTarefaTurma.obj.nota}" onclick="clicked=true;" onkeydown="return formatarValorNota(this, event, 1)" size="3" maxlength="4">
						</h:inputText>
						
						</span>
					</li>
				</c:if>
				<li>
					<label style="font-weight:normal;">Comentários:<span class="required">&nbsp;</span></label>
					<span><h:inputTextarea value="#{respostaTarefaTurma.obj.textoCorrecao}" onclick="clicked=true;" cols="60" rows="5"/></span>
				</li>
				<li>
					<label style="font-weight:normal;">Arquivo:</label>
					<span id="input">
						<t:inputFileUpload value="#{respostaTarefaTurma.arquivo }" size="50"/>
						<h:graphicImage value="/ava/img/page_white_get.png" onclick="removerArquivo('input')" title="Remover Arquivo" alt="Remover Arquivo"/>
					</span>
				</li>
				
				<c:if test="${respostaTarefaTurma.obj.tarefa.envioArquivo}">		
				<li>
					<label style="font-weight:normal;">Baixar Arquivo do Discente:</label>
					<span>
						<h:commandLink id="idVisualizarArquivo" action="#{respostaTarefaTurma.visualizarArquivo}" title="Arquivo">
							<f:param name="idArquivo" value="#{ respostaTarefaTurma.obj.idArquivo }"/>
							<h:graphicImage value="/ava/img/page_white_put.png" alt="Baixar Arquivo Enviado pelo Aluno" title="Baixar Arquivo Enviado pelo Aluno" />
						</h:commandLink>
						${respostaTarefaTurma.obj.nomeArquivo}
					</span>	
				</li>
				</c:if>
				
				<c:if test="${respostaTarefaTurma.obj.idArquivoCorrecao > 0 }">
					<li>
						<label style="font-weight:normal;">Arquivo enviado na Última Correção:</label>
						<span>
							<a href="/sigaa/verProducao?idProducao=${respostaTarefaTurma.obj.idArquivoCorrecao}&key=${sf:generateArquivoKey(respostaTarefaTurma.obj.idArquivoCorrecao)}" title="Baixar Arquivo Enviado na Última Correção">
								<h:graphicImage value="/ava/img/page_white_put.png" alt="Baixar Arquivo Enviado na última Correção" />
								${respostaTarefaTurma.obj.nomeArquivoCorrecao}
							</a>
						</span>
					</li>
					<li>
						<label style="font-weight:normal;">Remover:</label>
						<span>
							<h:selectBooleanCheckbox value="#{respostaTarefaTurma.removerArquivo}"/>
							<ufrn:help>Deixe selecionado e clique em "Corrigir" para que a correção desta tarefa fique sem um arquivo associado.</ufrn:help>
						</span>
					</li>
				</c:if>

					<li>
						<label style="font-weight:normal;">Notificar:</label>
						<span>
							<h:selectBooleanCheckbox value="#{respostaTarefaTurma.notificar}"/>
						</span>
					</li>

			</ul>

			<div class="botoes">
				<div style="float: right;margin-top:4px;margin-">
					<h:commandButton value="Corrigir" action="#{respostaTarefaTurma.corrigir}" />
					<h:commandButton value="Corrigir/Próximo >>" action="#{respostaTarefaTurma.corrigirProximo}"/>
					<h:commandButton value="Próximo >>" action="#{respostaTarefaTurma.proximo}"onclick="return(mensagemSalvar());"/>	
				</div>
				<div style="float: left;margin-top:4px;">
					<h:commandButton value="<< Anterior"action="#{respostaTarefaTurma.anterior}" onclick="return(mensagemSalvar());" />
					<h:commandButton value="<< Corrigir/Anterior" action="#{respostaTarefaTurma.corrigirAnterior}" />
					<h:commandButton value="Cancelar" action="#{respostaTarefaTurma.avaliarTarefas}" onclick="return(confirm('Deseja realmente cancelar esta operação?'));"/>
				</div>
				<div class="required-items" style="width:50%;">
					<span class="required">&nbsp;</span>
					Itens de Preenchimento Obrigatório
				</div>
			</div>

		</fieldset>
	
	</h:form>
	
</f:view>

<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
var clicked = false;

function mensagemSalvar () {
	if ( clicked ){
		return(confirm("Tem certeza que deseja ir para pr\u00f3xima tarefa sem salvar? Todos os dados digitados ser\u00e3o perdidos."));
	}
}

function formatarValorNota(campo, event, casas) {

	var point = '.';
	var comma = ',';
	var sep = 0;
	var key = '';
	var i = j = 0;
	var len = len2 = 0;
	var strCheck = '0123456789';
	var aux = aux2 = '';
	var rcode = event.which ? event.which : event.keyCode;
	casas = parseInt(casas);

	var e = YAHOO.ext.EventObject;
	e.setEvent(event);
	
	var ctrlCmd = null; 
	if ( event.metaKey != undefined )
		ctrlCmd = (e.ctrlKey || event.metaKey)
	else	
	 	ctrlCmd = e.ctrlKey

	 if (campo.value == '0.00'){
		campo.value = "";
	 }
	 
    if (teclasEspeciais.indexOf(rcode) != -1) {
    	event.returnValue = true;
         return true; // Teclas especiais
    }

    if ($(campo).readAttribute('maxlength') && campo.value.length >= $(campo).readAttribute('maxlength')) {
    	event.returnValue = false;
        return;
    }

	if ( event.metaKey != undefined )
		ctrlCmd = (e.ctrlKey || event.metaKey)
	else	
	 	ctrlCmd = e.ctrlKey
	if ((ctrlCmd && rcode == 67) || (ctrlCmd && rcode == 86) || rcode == 13) {
		event.returnValue = true;
		return true;
	}


	 if (rcode >= 96 && rcode <= 105)
		rcode -= 48; // Teclado num?rico, c?digo diferente

    key = String.fromCharCode(rcode); // Pega o valor da tecla pelo c?digo

    if (strCheck.indexOf(key) == -1 && ((ctrlCmd != 118) && (ctrlCmd  != 99))){
         event.returnValue = false;
         return false; // Filtra teclas inv?lidas
    }


    len = campo.value.length;
    for(; i < len; i++){
         if (strCheck.indexOf(campo.value.charAt(i))!=-1){
              aux += campo.value.charAt(i);
         }
    }

    aux += key;
    len = aux.length;

	if (len == 0)     { campo.value = ''; }
    if (len <= casas) { campo.value = aux; }
    if (len > casas) {
         aux2 = '';
         for (j = 0, i = len - (casas + 1); i >= 0; i--) {
              if (j == casas + 1) {
                   aux2 += point;
                   j = 0;
              }
              aux2 += aux.charAt(i);
              j++;
         }
         campo.value = '';
         aux3 = '';
         len2 = aux2.length;
         for (i = len2 - 1; i >= 0; i--){
              aux3 += aux2.charAt(i);
         }

         if( aux != 100 )
         	campo.value += aux3.charAt(0) + point + aux.charAt(1);
         else if (aux == 100)
        	 campo.value += "10.0";
    }
    event.returnValue = false;
    return false;
}

function removerArquivo(id) {
	var input = document.getElementById(id);
	input.innerHTML = html;
}

var html = document.getElementById("input").innerHTML;

//Arredondando o valor da nota
var nota = document.getElementById("Nota");
if ( nota != null )
	nota.value = Math.round(document.getElementById("Nota").value*Math.pow(10,1))/Math.pow(10,1);

tinyMCE.init({
	mode : "textareas", theme : "advanced", width : "460", height : "250", language : "pt",
	theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
	theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
	theme_advanced_buttons3 : "",
	plugins : "searchreplace,contextmenu,advimage",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left"
});
</script>
<%@include file="/ava/rodape.jsp" %>
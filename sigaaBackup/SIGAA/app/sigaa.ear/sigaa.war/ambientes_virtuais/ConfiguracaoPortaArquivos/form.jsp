<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.ensino.jsf.DocenteTurmaMBean"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<script type="text/javascript">
	JAWR.loader.script('/javascript/prototype-1.6.0.3.js');
</script>
<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.GESTOR_AMBIENTES_VIRTUAIS } %>">

<a4j:keepAlive beanName="configuracaoPortaArquivos" />

<f:view>
	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>

	<h2> <ufrn:subSistema/> &gt; Configurar Porta Arquivos:</h2>

	<h:form id="formDocentes">
		<table class="formulario" width="90%">
			<caption>Configurações do Porta Arquivos </caption>
			<tr>
				<th class="required">Docente:</th>
					<td style="text-align: left;">
						<h:inputHidden value="#{docenteTurmaBean.tipoBuscaDocente}" id="idTipoBusca"/>
						<div id="abas-docentesTurma">
							<div id="docentesTurma"  class="aba">
								<table width="100%">
									<tr>
										<th class="required">Docente:</th>
										<td style="text-align: left;">
											<a4j:region id="docenteTurma">
												<h:inputText value="#{configuracaoPortaArquivos.docente.pessoa.nome}" id="nomeDocente" style="width: 400px;"/>
												<rich:suggestionbox width="400" height="100" for="nomeDocente" id="sbDocente"
													minChars="3" nothingLabel="#{servidor.textoSuggestionBox}"
													suggestionAction="#{servidor.autocompleteDocente}" var="_servidor" fetchValue="#{_servidor.nome}"
													onsubmit="$('formDocentes:imgStDocente').style.display='inline';" 
												    oncomplete="$('formDocentes:imgStDocente').style.display='none';">
													<h:column>
														<h:outputText value="#{_servidor.descricaoCompleta}"/>
													</h:column>
												   <a4j:support event="onselect" action="#{configuracaoPortaArquivos.carregaConfiguracaoDocente}" reRender="panelCapacidade">
														<f:setPropertyActionListener value="#{_servidor.id}" target="#{configuracaoPortaArquivos.docente.id}" />
												  </a4j:support>	
												</rich:suggestionbox>
												<h:graphicImage id="imgStDocente" style="display:none; overflow: visible;" value="/img/indicator.gif"/>	
											</a4j:region>	
										</td>
									</tr>
								</table>
							</div>
						</div>
						<div id="docentesTurmaExternos" class="aba">
								<table width="100%">
									<tr>
										<th class="required">Docente Externo:</th>
										<td colspan="2" style="text-align: left;">
											<a4j:region id="docenteTurmaExterno">
												<h:inputText value="#{configuracaoPortaArquivos.docenteExterno.servidor.pessoa.nome}" id="nomeDocenteExterno" style="width: 400px;"/>
												<rich:suggestionbox width="400" height="100" for="nomeDocenteExterno" id="sbDocenteExterno"
													minChars="3" 
													suggestionAction="#{docenteExterno.autoCompleteNomeDocenteExterno}" var="_docenteExterno" fetchValue="#{_docenteExterno.nome}"
													onsubmit="$('formDocentes:imgStDocenteExterno').style.display='inline';" 
												    oncomplete="$('formDocentes:imgStDocenteExterno').style.display='none';">
													<h:column>
														<h:outputText value="#{_docenteExterno.nome}"/>
													</h:column>
													<h:column>
														<h:outputText value="#{_docenteExterno.instituicao.nome}"/>
													</h:column>
												   <a4j:support event="onselect" action="#{configuracaoPortaArquivos.carregaConfiguracaoDocenteExterno}" reRender="panelCapacidade">
														<f:setPropertyActionListener value="#{_docenteExterno.id}" target="#{configuracaoPortaArquivos.docenteExterno.id}" />
												  </a4j:support>								
												</rich:suggestionbox>
												<h:graphicImage id="imgStDocenteExterno" style="display:none; overflow: visible;" value="/img/indicator.gif"/>	
											</a4j:region>
										</td>	
									</tr>
								</table>
							</div>
					</td>
			</tr>
			<tr>
				<th></th>
				<td>
					<div class="descricaoOperacao">
		
						Defina a capacidade máxima do porta-arquivos do professor.
						Lembre-se que a capacidade é dada em <b>Gigabytes</b>.
						
					</div>
				</td>	
			</tr>
			<tr>
					<th class="required">
						<h:outputText value="Capacidade do Porta Arquivos:"/>
					</th>
					<td>
						<a4j:outputPanel id="panelCapacidade">
							<h:inputText id="tamanhoPortaArquivos" style="text-align:right;" value="#{configuracaoPortaArquivos.capacidade}" size="4" maxlength="4" onfocus="javascript:select()" onkeypress="return(mascararGigas(this,event))">
							</h:inputText>

						</a4j:outputPanel>	
						<h:outputText style="font-weight:bold;" value="GB"/>
					</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Salvar"	action="#{configuracaoPortaArquivos.salvar}" onclick="return(verificaCapacidade())" id="salvar"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{configuracaoPortaArquivos.cancelar}" id="cancelar" />
					</td>
				</tr>
			</tfoot>
			
		</table>
	</h:form>
</f:view>
<br/>
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
<script type="text/javascript">
		var Abas = function() {
		
			var setTipoBusca = function(e, aba) {
				var idAba = aba.id;
				var categoria = getEl('formDocentes:idTipoBusca');
				switch(idAba) {
					case 'membrosPrograma': categoria.dom.value = <%= DocenteTurmaMBean.DOCENTE_PROGRAMA %>; break;
					case 'externoPrograma': categoria.dom.value = <%= DocenteTurmaMBean.DOCENTE_EXTERNO_PROGRAMA %>; break;
					case 'docentesExternos':
					case 'docentesTurmaExternos': categoria.dom.value = <%= DocenteTurmaMBean.DOCENTES_EXTERNOS %>; break;
					case 'docentesTurma': categoria.dom.value = <%= DocenteTurmaMBean.DOCENTES_TURMA %>; break;
				}
			};
			return {
			    init : function(){
				        var abas = new YAHOO.ext.TabPanel('abas-docentesTurma');
						abas.on('tabchange', setTipoBusca);
						abas.addTab('docentesTurma', "Docentes");
						abas.addTab('docentesTurmaExternos', "Docentes Externos");
						switch( getEl('formDocentes:idTipoBusca').dom.value ) {
							case ''+<%=1%>:  abas.activate('docentesTurma'); break;
							case ''+<%=2%>:  abas.activate('docentesTurmaExternos'); break;
							default: abas.activate('docentesTurma'); break;
						}

			    }
		    }
		}();
		YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);

function verificaCapacidade() {
	var tamanhoInput = document.getElementById("formDocentes:tamanhoPortaArquivos");
	var tamanho = parseFloat(tamanhoInput.value.replace(',','.'));
	if ( tamanho > 20) 
		return(confirm("A capacidade do porta arquivos est\u00E1\ sendo configurada para mais que 20 Gigabytes. " +
					 		"Deseja realmente salvar?"));
	else return true;
}
function mascararGigas(campo, event) {
	if ($(campo).readAttribute('maxlength') && campo.value.length >= $(campo).readAttribute('maxlength')) return;

	var point = '.';
	var comma = ',';
	var sep = 0;
	var key = '';
	var i = j = 0;
	var len = len2 = 0;
	var strCheck = '0123456789';
	var aux = aux2 = '';
	var rcode = event.which ? event.which : event.keyCode;
	var casas = 1;

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
        
        if( aux < 100 )
           	campo.value += aux3.charAt(0) + point + aux.charAt(1);
        else 
      	 campo.value += aux3.charAt(0) + aux2.charAt(0) + point + aux.charAt(2);
    }
    event.returnValue = false;
    return false;
}

</script>

</ufrn:checkRole>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>


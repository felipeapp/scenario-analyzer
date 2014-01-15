<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
	tinyMCE.init({
		editor_deselector : "instrucoesEspecificas"
    	mode : "textareas",
        theme : "advanced",
        width : "100%",
        height : "320",
        language : "pt",
        plugins : "preview, emotions, iespell, print, fullscreen, advhr, directionality, searchreplace, insertdatetime, paste",
        plugin_preview_width : "500",
        plugin_preview_height : "600",
        extended_valid_elements : "hr[class|width|size|noshade]",
        plugin_insertdate_dateFormat : "%Y-%m-%d",
        plugin_insertdate_timeFormat : "%H:%M:%S"
	});
</script>

<style>
#abas-descricao .aba {
	padding: 10px;
}

p.ajuda {
	text-align: center;
	margin: 5px 90px;
	color: #083772;
	font-style: italic;
}
div#msgAddProcessos{
	background:#FFF none repeat scroll 0 0;
	border:1px solid #FFDFDF;
	color:#FF1111;
	line-height:1.2em;
	padding:10px;
	width:95%;
}
</style>

<f:view>
	<%--  <%@include file="/stricto/menu_coordenador.jsp"%> --%>
	<h2><ufrn:subSistema /> > Cadastro de Processo Seletivo</h2>

	<center>
		<h:form>
		<div class="infoAltRem" style="text-align: center; width: 100%">
			<h:graphicImage value="/img/listar.gif" style="overflow: visible;"/>
  			<h:commandLink value="Listar processos seletivos cadastrados" action="#{processoSeletivo.listar}"/>
		</div>
		</h:form>
	</center>
	
	<c:if test="${(processoSeletivo.obj.solicitadoAlteracao) && (!empty processoSeletivo.obj.motivoAlteracao)}">
		<table class="visualizacao" width="100%"> 
			<caption>Solicitação de Alteração</caption>
			<tr>
				<td style="color: red;"><b>${processoSeletivo.obj.motivoAlteracao}</b></td>
			</tr>
		</table>
		<br/>
	</c:if>

	<h:form id="formProcessoSeletivo" enctype="multipart/form-data">
	<table class="formulario" style="width: 100%;">
			<caption class="listagem">Dados do Processo Seletivo</caption>
			
			<tr>
				<th class="required">Título do Edital:</th>
				<td>
					<h:inputText id="tituloEdital" size="80" maxlength="80" value="#{processoSeletivo.obj.editalProcessoSeletivo.nome}" />
				</td>
			</tr>
			<tr>
				<th>Início das Inscrições:</th>
				<td>
					<t:inputCalendar value="#{processoSeletivo.obj.editalProcessoSeletivo.inicioInscricoes}" 
						size="10" readonly="#{processoSeletivo.readOnly}"
						id="dataInicio" maxlength="10" onkeypress="return formataData(this,event);" 
						renderAsPopup="true" renderPopupButtonAsImage="true"  
						popupDateFormat="dd/MM/yyyy"> 
						 <f:converter converterId="convertData"/>
					</t:inputCalendar>			
					às 
					<h:inputText id="horaInicio" value="#{processoSeletivo.obj.editalProcessoSeletivo.horaInicioInscricoes}" 
						maxlength="5" size="5" onkeypress="return(formataHora(this, event))" title="Hora do Início das Inscrições">
						<f:converter converterId="convertHora"/>									
					</h:inputText>
					<ufrn:help>Preencher no formato hh:mm.</ufrn:help>	
				</td>
			</tr>
			
			<tr>
				<th>Fim das Inscrições:</th>
				<td>
					<t:inputCalendar value="#{processoSeletivo.obj.editalProcessoSeletivo.fimInscricoes}" 
						size="10" readonly="#{processoSeletivo.readOnly}"
						id="dataFim" maxlength="10" onkeypress="return formataData(this,event);" 
						renderAsPopup="true" renderPopupButtonAsImage="true"  
						popupDateFormat="dd/MM/yyyy"> 
						 <f:converter converterId="convertData"/>
					</t:inputCalendar>
					às 
					<h:inputText id="horaFim" value="#{processoSeletivo.obj.editalProcessoSeletivo.horaFimInscricoes}" 
					maxlength="5" size="5" onkeypress="return(formataHora(this, event))" title="Hora do Final das Inscrições">
					<f:converter converterId="convertHora"/>									
				</h:inputText>
				<ufrn:help>Preencher no formato hh:mm.</ufrn:help>				
				</td>
			</tr>
			
			<c:if test="${not empty processoSeletivo.comboRestricaoInscricao}">
			<tr>
				<th>Restrição na Inscrição</th>
				<td>
					<h:selectOneMenu value="#{processoSeletivo.obj.editalProcessoSeletivo.restricaoInscrito.id}"
						  id="idRestricaoInscricao" >
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
						<f:selectItems value="#{processoSeletivo.comboRestricaoInscricao}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			</c:if>
			
			<tr>
				<th>Edital:</th>
				<td> <t:inputFileUpload value="#{processoSeletivo.edital}" id="edital" style="width:95%;"/> </td>
			</tr>
			
			<tr>
				<th>Manual do Candidato:</th>
				<td> <t:inputFileUpload value="#{processoSeletivo.manualCandidato}" id="manualCandidato" style="width:95%;"/> </td>
			</tr>

			<c:if test="${processoSeletivo.acessoProcessoSeletivoGraduacao}">
			<tr>
				<th>Possui Agendamento:</th>
				<td>
					<h:selectOneRadio value="#{processoSeletivo.obj.editalProcessoSeletivo.possuiAgendamento}" rendered="#{processoSeletivo.obj.id==0}" id="possuiAgendamento">
						<f:selectItems value="#{processoSeletivo.simNao}"/>
						<a4j:support event="onclick" reRender="panelProcessoSeletivo"/>
					</h:selectOneRadio>
					<h:outputText value="Sim" rendered="#{processoSeletivo.obj.editalProcessoSeletivo.possuiAgendamento && processoSeletivo.obj.id>0}" />
					<h:outputText value="Não" rendered="#{!processoSeletivo.obj.editalProcessoSeletivo.possuiAgendamento && processoSeletivo.obj.id>0}" />			
				</td>
			</tr>
			</c:if>
			
			<c:if test="${processoSeletivo.portalCoordenadorStricto && processoSeletivo.parametrosProgramaPos.solicitarOrientadorProcessoSeletivo}">
				<tr>
					<th class="required">Avisar ao orientador quando um aluno o escolher:</th>
					<td>
						<h:selectOneRadio value="#{processoSeletivo.obj.editalProcessoSeletivo.notificarOrientador}" id="notificarOrientador">
							<f:selectItems value="#{processoSeletivo.simNao}"/>
						</h:selectOneRadio>
					</td>
				</tr>
			</c:if>
			
			<tr>
				<th class="required">Verificar disponibilidade de vagas:</th>
				<td>
					<h:selectOneRadio value="#{processoSeletivo.obj.editalProcessoSeletivo.verificaExisteVaga}" 
						id="verificarDisponibilidadeVaga" style="float:left;margin-right:5px;">
						<f:selectItems value="#{processoSeletivo.simNao}"/>
					</h:selectOneRadio>
					<ufrn:help>Se selecionar "sim", as inscrição deve se limitar ao número de vagas definido no próximo passo.</ufrn:help>
				</td>
			</tr>
			<tr>
				<th>Possui Taxa de Inscrição:</th>
				<td>
					<h:selectOneRadio  id="possuiTaxaInscriaco" value="#{ processoSeletivo.possuiTaxaInscricao }"
						valueChangeListener="#{ processoSeletivo.atualizaConfiguracaoGRU }"
						onclick="submit()" onchange="submit()" style="float:left;margin-right:5px;">
						<f:selectItems value="#{processoSeletivo.simNao}"/>
					</h:selectOneRadio>
					<ufrn:help>Caso o Processo Seletivo tenha taxa de inscrição, será necessário informar o valor e uma data de vencimento para o pagamento da GRU (boleto).</ufrn:help>
				</td>
			</tr>
			<c:if test="${processoSeletivo.possuiTaxaInscricao}" >
				<tr>
					<td colspan="2" class="subFormulario">Configuração da GRU</td>
				</tr>
				<tr>
					<th class="rotulo">
						Tipo de Arrecadação:
					</th>
					<td>
						<h:outputText value="#{ processoSeletivo.configuracao.tipoArrecadacao.descricao }" id="tipoArrecadacao"/>
					</td>
				</tr>
				<tr>
					<th class="rotulo">
						Código de Recolhimento:
					</th>
					<td>
						<h:outputText value="#{processoSeletivo.configuracao.tipoArrecadacao.codigoRecolhimento.codigo} - #{processoSeletivo.configuracao.tipoArrecadacao.codigoRecolhimento.descricao}" id="codRecolhimento"/>
					</td>
				</tr>
				<tr>
					<th class="rotulo">
						Tipo de GRU:
					</th>
					<td>
						<h:panelGroup id="tipoGRU">
							<h:outputText value="GRU Simples" rendered="#{not empty processoSeletivo.configuracao and processoSeletivo.configuracao.gruSimples}"/>
							<h:outputText value="GRU Cobrança" rendered="#{not empty processoSeletivo.configuracao and not processoSeletivo.configuracao.gruSimples}"/>
							<h:outputText value="-" rendered="#{empty processoSeletivo.configuracao}"/>
						</h:panelGroup>
					</td>
				</tr>
				<tr>
					<th class="required">Valor da Taxa de Inscrição (R$):</th>
					<td><h:inputText id="valor"
						value="#{processoSeletivo.obj.editalProcessoSeletivo.taxaInscricao}"
						readonly="#{processoSeletivo.readOnly}"
						size="8" maxlength="8"
						style="text-align: right"
						onkeydown="return(formataValor(this, event, 2))"
						onblur="if(this.value==''){ this.value = '0,00';}">
						<f:convertNumber pattern="#,##0.00" />
					</h:inputText>
					</td>
				</tr>
				<tr>
					<th class="required">
						Data de Vencimento da Boleto:
					</th>
					<td>
						<t:inputCalendar id="dataVencimento" renderAsPopup="true"
							renderPopupButtonAsImage="true" size="10" maxlength="10"
							onkeypress="return formataData(this,event)"
							readonly="#{processoSeletivoVestibular.readOnly}"
							disabled="#{readOnly}" popupDateFormat="dd/MM/yyyy"
							value="#{processoSeletivo.obj.editalProcessoSeletivo.dataVencimentoBoleto}"/>
					</td>
				</tr>
				<tr>
					<th valign="top">
						Instruções específicas para a GRU:
					</th>
					<td>
						<h:inputTextarea id="instrucoesEspecificas"
						value="#{processoSeletivo.obj.editalProcessoSeletivo.instrucoesEspecificasGRU}"
						readonly="#{processoSeletivo.readOnly}"
						cols="90" rows="3"
						onkeyup="if (this.value.length > 240) this.value = this.value.substring(0, 240); $('instrucoesEspecificas_count').value = 240 - this.value.length;" 
						onkeydown="if (this.value.length > 240) this.value = this.value.substring(0, 240); $('instrucoesEspecificas_count').value = 240 - this.value.length;"
						/>
						<br/>
						Você pode digitar <input readonly type="text" id="instrucoesEspecificas_count" size="3" value="240"> caracteres.
						<ufrn:help>Digite as instruções que deverão constar na GRU a ser impressa pelo candidato. O texto é limitado a 240 caracteres e no máximo 3 linhas.</ufrn:help>
					</td>
				</tr>
			</c:if>
			<tr>
				<td colspan="2" class="subFormulario">Outras Informações</td>
			</tr>
			<tr>
				<td colspan="6">
				<div id="abas-descricao">
					<div id="descricao-selecao" class="aba">
						<p class="ajuda">
							Utilize o espaço abaixo para definir a descrição do processo seletivo e as orientações de interesse dos candidatos.
						</p>
						<h:inputTextarea value="#{processoSeletivo.obj.editalProcessoSeletivo.descricao}" id="descricao" rows="10" cols="110" readonly="#{processoSeletivo.readOnly}" />
					</div>
					<div id="orientacoes-inscritos" class="aba">
						<p class="ajuda">
							As instruções definidas no espaço abaixo serão apresentadas aos candidatos do processo seletivo, no comprovante
							da inscrição.
						</p>
						<h:inputTextarea value="#{processoSeletivo.obj.editalProcessoSeletivo.orientacoesInscritos}" id="orientacoesInscritos" rows="10" cols="110" readonly="#{processoSeletivo.readOnly}" />
					</div>
				</div>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="4">
						<a4j:outputPanel id="panelProcessoSeletivo">
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{processoSeletivo.cancelar}" id="cancelar"/>
						<c:choose>
							<c:when test="${processoSeletivo.acessoProcessoSeletivoGraduacao && processoSeletivo.obj.editalProcessoSeletivo.possuiAgendamento}">
								<h:commandButton value="Próximo Passo >>" action="#{processoSeletivo.formPeriodoAgenda}" id="periodoAgenda" />
							</c:when>
							<c:otherwise>
								<h:commandButton value="Próximo Passo >>" action="#{processoSeletivo.formCursosProcessoSeletivo}" id="cursosProcessoSeletivo"/>
							</c:otherwise>
						</c:choose>	
					</a4j:outputPanel>
					</td>
				</tr>
			</tfoot>
	</table>
	</h:form>
	<br />
	<center>
	  <h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	 </center>
</f:view>

<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('abas-descricao');
        abas.addTab('descricao-selecao', "Descrição do Processo Seletivo");
        abas.addTab('orientacoes-inscritos', "Orientações aos Inscritos");
        abas.activate('descricao-selecao');
    }
};
YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
$('instrucoesEspecificas_count').value = 240 - $('formProcessoSeletivo:instrucoesEspecificas').value.length;
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	.negrito {font-weight: bold !important;}
</style>

<f:view>

	<h2><ufrn:subSistema /> &gt; Configurar Calend�rio de Monitoria</h2>
	<a4j:keepAlive beanName="calendarioMonitoria" />
	
	<h:form id="frmCadastro">
		<table class="formulario" width="100%">
			<caption>Calend�rio de Monitoria</caption>
				<tr>
					<th width="40%">Calend�rio Selecionado:</th>
					<td>			 
						<h:selectOneMenu value="#{calendarioMonitoria.obj.id}" id="sistema">
							<f:selectItem itemValue="0" itemLabel="--> NOVO <--" />
							<f:selectItems value="#{calendarioMonitoria.calendariosAtivos}" />
							<a4j:support event="onchange" actionListener="#{calendarioMonitoria.carregarCalendario}" reRender="frmCadastro"/>
						</h:selectOneMenu> 
					</td>
				</tr>
				<tr >
					<th><h:outputText value="Ano Refer�ncia:" styleClass="#{calendarioMonitoria.obj.id == 0 ? 'obrigatorio' : 'negrito'}"/></th>
					<td width="50%" id="anoref">
						<c:if test="${calendarioMonitoria.obj.id == 0 }">
							<h:inputText value="#{calendarioMonitoria.obj.anoReferencia}" id="novoAnoReferencia" size="4" readonly="#{calendarioMonitoria.readOnly}" maxlength="4" 
							onkeyup="return formatarInteiro(this)"/>
						</c:if>
						<c:if test="${calendarioMonitoria.obj.id > 0 }">
							<h:outputText value="#{calendarioMonitoria.obj.anoReferencia}" id="antigoAnoReferencia"/>
						</c:if>
					</td>
				</tr>
				<tr>
					<td width="50%">
								
						<table class="formulario" width="100%">
								<tr>
									<td colspan="2" class="subFormulario">Recebimento de Relat�rios Parciais de Projeto</td>
								</tr>
					
								<tr>
									<th width="50%" class="obrigatorio">Projetos submetidos Em:</th>
									<td>
										<h:inputText value="#{calendarioMonitoria.obj.anoProjetoRelatorioParcial}" id="projetosSubmetidosPar" size="4" readonly="#{calendarioMonitoria.readOnly}" maxlength="4" onkeyup="return formatarInteiro(this)"/>
									</td>
								</tr>
					
					
								<tr>
									<th class="obrigatorio">Iniciar Em:</th>
									<td>
										<t:inputCalendar id="IniciarEmPar" value="#{calendarioMonitoria.obj.inicioEnvioRelatorioParcialProjeto}" renderAsPopup="true" renderPopupButtonAsImage="true" 
											popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje �">							 
											<f:converter converterId="convertData" />
										</t:inputCalendar>
									</td>
								</tr>
			
								<tr>
									<th class="obrigatorio">Finalizar Em:</th>
									<td>
										<t:inputCalendar id="FinalizarEmPar" value="#{calendarioMonitoria.obj.fimEnvioRelatorioParcialProjeto}" renderAsPopup="true" renderPopupButtonAsImage="true"
													popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje �">							 
											<f:converter converterId="convertData" />
										</t:inputCalendar> 
									</td>
								</tr>
					
								<tr>
									<td colspan="2" class="subFormulario">
											Recebimento de Relat�rios Finais de Projeto
									</td>
								</tr>
								
								<tr>
									<th class="obrigatorio">Projetos submetidos Em:</th>
									<td>
										<h:inputText value="#{calendarioMonitoria.obj.anoProjetoRelatorioFinal}" id="ProjetosSubmetidosFin" size="4" readonly="#{calendarioMonitoria.readOnly}" maxlength="4" onkeyup="return formatarInteiro(this)"/>
									</td>
								</tr>
			
								<tr>
									<th class="obrigatorio">Iniciar Em:</th>
									<td>
										<t:inputCalendar value="#{calendarioMonitoria.obj.inicioEnvioRelatorioFinalProjeto}" id="InicarEmFin" renderAsPopup="true" renderPopupButtonAsImage="true"
													popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje �">							 
											<f:converter converterId="convertData" />
										</t:inputCalendar> 
									</td>
								</tr>
					
								<tr>
									<th class="obrigatorio">Finalizar Em:</th>
									<td>
										<t:inputCalendar value="#{calendarioMonitoria.obj.fimEnvioRelatorioFinalProjeto}" id="FinalizarEmFin" renderAsPopup="true" renderPopupButtonAsImage="true"
													popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje �">							 
											<f:converter converterId="convertData" />
										</t:inputCalendar>
									</td>
								</tr>
								
								<tr>
									<td colspan="2" class="subFormulario">
											Recebimento de Resumos do Semin�rio de Inicia��o � Doc�ncia
									</td>
								</tr>
								
								<tr>
									<th class="obrigatorio">Projetos submetidos Em:</th>
									<td>
										<h:inputText value="#{calendarioMonitoria.obj.anoProjetoResumoSid}" id="ProjetosResumosIniacao"	size="4" readonly="#{calendarioMonitoria.readOnly}" maxlength="4" onkeyup="return formatarInteiro(this)"/>
									</td>
								</tr>
						
								<tr>
									<th class="obrigatorio">Iniciar Em:</th>
									<td>
										<t:inputCalendar value="#{calendarioMonitoria.obj.inicioEnvioResumoSid}" id="IniciarInciacao" renderAsPopup="true" renderPopupButtonAsImage="true" 
													popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje �">							 
											<f:converter converterId="convertData" />
										</t:inputCalendar>
									</td>
								</tr>
					
								<tr>
									<th class="obrigatorio">Finalizar Em:</th>
									<td>
										<t:inputCalendar value="#{calendarioMonitoria.obj.fimEnvioResumoSid}" id="FinalizarIniacao" renderAsPopup="true" renderPopupButtonAsImage="true" 
													popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje �">							 
											<f:converter converterId="convertData" />
										</t:inputCalendar>
									</td>
								</tr>
						
								<tr>
									<th class="obrigatorio">Permitir Edi��o do Resumo At�:</th>
									<td>
										<t:inputCalendar value="#{calendarioMonitoria.obj.fimEdicaoResumoSid}" id="PemitirEdicaoResumo" renderAsPopup="true" renderPopupButtonAsImage="true" 
													popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje �">							 
											<f:converter converterId="convertData" />
										</t:inputCalendar>
									</td>
								</tr>
						</table>
					
					</td>
			
					<td>
						<table class="formulario" width="100%">
								<tr>
									<td colspan="2" class="subFormulario">
											Recebimento de Relat�rios Parciais de Monitor
									</td>
								</tr>
					
								<tr>
									<th width="50%" class="obrigatorio">Projetos submetidos Em:</th>
									<td>
										<h:inputText value="#{calendarioMonitoria.obj.anoProjetoRelatorioParcialMonitor}" id="ProjetosParMonitor"	size="4" readonly="#{calendarioMonitoria.readOnly}" maxlength="4" onkeyup="return formatarInteiro(this)"/>
									</td>
								</tr>
					
					
								<tr>
									<th class="obrigatorio">Iniciar Em:</th>
									<td>
										<t:inputCalendar value="#{calendarioMonitoria.obj.inicioEnvioRelatorioParcialMonitor}" id="IniciarParMonitor" renderAsPopup="true" renderPopupButtonAsImage="true" 
													popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje �">							 
											<f:converter converterId="convertData" />
										</t:inputCalendar>
									</td>
								</tr>
					
								<tr>
									<th class="obrigatorio">Finalizar Em:</th>
									<td>
										<t:inputCalendar value="#{calendarioMonitoria.obj.fimEnvioRelatorioParcialMonitor}" id="FinalizarParMonitor" renderAsPopup="true" renderPopupButtonAsImage="true" 
													popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje �">							 
											<f:converter converterId="convertData" />
										</t:inputCalendar>
									</td>
								</tr>
					
								<tr>
									<td colspan="2" class="subFormulario">
											Recebimento de Relat�rios Finais de Monitor
									</td>
								</tr>
								
								
								<tr>
									<th class="obrigatorio">Projetos submetidos Em:</th>
									<td>
										<h:inputText value="#{calendarioMonitoria.obj.anoProjetoRelatorioFinalMonitor}" id="projetoFinMonitor"	size="4" readonly="#{calendarioMonitoria.readOnly}" maxlength="4" onkeyup="return formatarInteiro(this)"/>
									</td>
								</tr>
								
					
								<tr>
									<th class="obrigatorio">Iniciar Em:</th>
									<td>
										<t:inputCalendar value="#{calendarioMonitoria.obj.inicioEnvioRelatorioFinalMonitor}" id="iniciarFinMonitor" renderAsPopup="true" renderPopupButtonAsImage="true" 
													popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje �">							 
											<f:converter converterId="convertData" />
										</t:inputCalendar>
									</td>
								</tr>
					
								<tr>
									<th class="obrigatorio">Finalizar Em:</th>
									<td>
										<t:inputCalendar value="#{calendarioMonitoria.obj.fimEnvioRelatorioFinalMonitor}" id="finalizarFinMonitor" renderAsPopup="true" renderPopupButtonAsImage="true" 
													popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje �">							 
											<f:converter converterId="convertData" />
										</t:inputCalendar>
									</td>
								</tr>

								<tr>
									<td colspan="2" class="subFormulario">
										Per�odo de Efetiva��o das bolsas de monitoria
									</td>
								</tr>				
								
								<tr>
									<th><b>Projetos submetidos Em:</b></th>
									<td>Todos os anos</td>
								</tr>
								
								<tr>
									<th class="obrigatorio">Iniciar Em:</th>
									<td>
										<t:inputCalendar value="#{calendarioMonitoria.obj.inicioConfirmacaoMonitoria}" id="periodoEfetivacaoIni" renderAsPopup="true" renderPopupButtonAsImage="true" 
												popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje �">
 											<f:converter converterId="convertData" />
 										</t:inputCalendar>
									</td>
								</tr>
					
								<tr>
									<th class="obrigatorio">Finalizar Em:</th>
									<td>
										<t:inputCalendar value="#{calendarioMonitoria.obj.fimConfirmacaoMonitoria}" id="periodoEfetivacaoFin" renderAsPopup="true" renderPopupButtonAsImage="true" 
 												popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje �">
 											<f:converter converterId="convertData" />
 										</t:inputCalendar>
									</td>
								</tr>

						</table>
					</td>			
				</tr>
					
				<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton value="#{calendarioMonitoria.confirmButton}" action="#{calendarioMonitoria.cadastrar}"	id="cadastrar"/>
							<h:commandButton value="Cancelar" action="#{calendarioMonitoria.cancelar}" id="cancelar" onclick="#{confirm}" immediate="true"/>
						</td>
					</tr>
				</tfoot>

			</table>
	
	</h:form>
		
		<div class="obrigatorio">Campos de preenchimento obrigat�rio.</div>
		
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
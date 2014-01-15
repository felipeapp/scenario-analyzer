<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Configurar Calendário de Monitoria</h2>
	<h:form>

		<h:inputHidden value="#{calendarioMonitoria.confirmButton}" />

		<table class="formulario" width="100%">
		<caption>Calendário de Monitoria</caption>

		<tr>
			<th class="obrigatorio">Ano Referência:</th>
			<td>
				<h:inputText value="#{calendarioMonitoria.obj.anoReferencia}" id="anoReferencia" size="4" readonly="#{calendarioMonitoria.readOnly}" maxlength="4" onkeyup="return formatarInteiro(this)"/>
			</td>
		</tr>

		<tr>
			<td width="50%">
						
				<table class="formulario" width="100%">
							
						<tr>
							<td colspan="2" class="subFormulario">Recebimento de Relatórios Parciais de Projeto</td>
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
									popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">							 
									<f:converter converterId="convertData" />
								</t:inputCalendar>
							</td>
						</tr>
			
						<tr>
							<th class="obrigatorio">Finalizar Em:</th>
							<td>
								<t:inputCalendar id="FinalizarEmPar" value="#{calendarioMonitoria.obj.fimEnvioRelatorioParcialProjeto}" renderAsPopup="true" renderPopupButtonAsImage="true"
											popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">							 
									<f:converter converterId="convertData" />
								</t:inputCalendar> 
							</td>
						</tr>
			
						<tr>
							<td colspan="2" class="subFormulario">
									Recebimento de Relatórios Finais de Projeto
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
											popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">							 
									<f:converter converterId="convertData" />
								</t:inputCalendar> 
							</td>
						</tr>
			
						<tr>
							<th class="obrigatorio">Finalizar Em:</th>
							<td>
								<t:inputCalendar value="#{calendarioMonitoria.obj.fimEnvioRelatorioFinalProjeto}" id="FinalizarEmFin" renderAsPopup="true" renderPopupButtonAsImage="true"
											popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">							 
									<f:converter converterId="convertData" />
								</t:inputCalendar>
							</td>
						</tr>
						
						<tr>
							<td colspan="2" class="subFormulario">
									Recebimento de Resumos do Seminário de Iniciação à Docência
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
											popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">							 
									<f:converter converterId="convertData" />
								</t:inputCalendar>
							</td>
						</tr>
			
						<tr>
							<th class="obrigatorio">Finalizar Em:</th>
							<td>
								<t:inputCalendar value="#{calendarioMonitoria.obj.fimEnvioResumoSid}" id="FinalizarIniacao" renderAsPopup="true" renderPopupButtonAsImage="true" 
											popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">							 
									<f:converter converterId="convertData" />
								</t:inputCalendar>
							</td>
						</tr>
						
						<tr>
							<th class="obrigatorio">Permitir Edição do Resumo Até:</th>
							<td>
								<t:inputCalendar value="#{calendarioMonitoria.obj.fimEdicaoResumoSid}" id="PemitirEdicaoResumo" renderAsPopup="true" renderPopupButtonAsImage="true" 
											popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">							 
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
									Recebimento de Relatórios Parciais de Monitor
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
											popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">							 
									<f:converter converterId="convertData" />
								</t:inputCalendar>
							</td>
						</tr>
			
						<tr>
							<th class="obrigatorio">Finalizar Em:</th>
							<td>
								<t:inputCalendar value="#{calendarioMonitoria.obj.fimEnvioRelatorioParcialMonitor}" id="FinalizarParMonitor" renderAsPopup="true" renderPopupButtonAsImage="true" 
											popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">							 
									<f:converter converterId="convertData" />
								</t:inputCalendar>
							</td>
						</tr>
			
						<tr>
							<td colspan="2" class="subFormulario">
									Recebimento de Relatórios Finais de Monitor
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
											popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">							 
									<f:converter converterId="convertData" />
								</t:inputCalendar>
							</td>
						</tr>
			
						<tr>
							<th class="obrigatorio">Finalizar Em:</th>
							<td>
								<t:inputCalendar value="#{calendarioMonitoria.obj.fimEnvioRelatorioFinalMonitor}" id="finalizarFinMonitor" renderAsPopup="true" renderPopupButtonAsImage="true" 
											popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">							 
									<f:converter converterId="convertData" />
								</t:inputCalendar>
							</td>
						</tr>
						
						
						<tr>
							<td colspan="2" class="subFormulario">
								Período de Efetivação das bolsas de monitoria
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
											popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">							 
									<f:converter converterId="convertData" />
								</t:inputCalendar>
							</td>
						</tr>
			
						<tr>
							<th class="obrigatorio">Finalizar Em:</th>
							<td>
								<t:inputCalendar value="#{calendarioMonitoria.obj.fimConfirmacaoMonitoria}" id="periodoEfetivacaoFin" renderAsPopup="true" renderPopupButtonAsImage="true" 
											popupDateFormat="dd/MM/yyyy" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">							 
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
						<h:commandButton value="#{calendarioMonitoria.confirmButton}" action="#{calendarioMonitoria.cadastrar}"	id="cadastrar" />
						<h:commandButton value="Cancelar" action="#{calendarioMonitoria.cancelar}" id="cancelar" onclick="#{confirm}" immediate="true"/>
					</td>
				</tr>
			</tfoot>

		</table>
		<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
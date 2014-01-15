<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2 class="title"><ufrn:subSistema /> &gt; Calendário de Pesquisa - Calendários Disponíveis</h2>

<br>
	<table width="100%" class="formulario" style="font-size: x-small;">
		<caption>CALENDÁRIOS DE PESQUISA</caption>
			<tr>
				<td valign="top">
						<table class="subFormulario" width="100%" align="center" cellpadding="5">
						<caption class="listagem">
						<h:form>
						Datas do calendário <h:selectOneMenu value="#{calendarioPesquisa.obj.id}"
								id="calendarios" onchange="submit()" valueChangeListener="#{calendarioPesquisa.carregarCalendario}">
								<f:selectItem itemLabel="-- NOVO --" itemValue="0" />
								<f:selectItems value="#{calendarioPesquisa.calendariosCombo}" />
							</h:selectOneMenu>

						</h:form>
						</caption>
						<h:form id="calForm">
							<h:inputHidden value="#{calendarioPesquisa.obj.id}" />
							<tr>
								<th>Ano:</th>
								<td width="50%">
								<c:if test="${calendarioPesquisa.obj.id == 0}">
									<h:inputText value="#{calendarioPesquisa.obj.ano}" id="ano" size="4" maxlength="4" onkeyup="formatarInteiro(this)"/>
								</c:if>
								<c:if test="${calendarioPesquisa.obj.id > 0}">
									<h:outputText value="#{calendarioPesquisa.obj.anoVigente}"></h:outputText>
								</c:if>
								</td>
							</tr>
							<c:if test="${not calendarioPesquisa.obj.vigente && calendarioPesquisa.obj.id > 0}">
							<tr>
								<th>Tornar esse calendário vigente:</th>
								<td><h:selectBooleanCheckbox value="#{calendarioPesquisa.obj.vigente}"
									id="vigencia" /></td>
							</tr>
							</c:if>
							<tr>
								<th>Período de Submissão e renovação de projetos de pesquisa:</th>
								<td > de <t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
									value="#{calendarioPesquisa.obj.inicioEnvioProjetos}" id="inicioEnvioProjetos"  /> até <t:inputCalendar
									renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
									value="#{calendarioPesquisa.obj.fimEnvioProjetos}" id="fimEnvioProjetos" /></td>
							</tr>
								<tr>
									<th>Período de avaliação dos projetos de pesquisa pelos consultores:</th>
									<td> de <t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
										value="#{calendarioPesquisa.obj.inicioAvaliacaoConsultores}" id="inicioAvaliacaoConsultores" /> até <t:inputCalendar
										renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
										value="#{calendarioPesquisa.obj.fimEnvioAvaliacaoConsultores}" id="fimEnvioAvaliacaoConsultores" /></td>
								</tr>
								<tr>
									<th>Período de Submissão de resumos do CIC independentes:</th>
									<td> de <t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
										value="#{calendarioPesquisa.obj.inicioResumoCIC}" id="inicioResumoCIC" /> até <t:inputCalendar
										renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
										value="#{calendarioPesquisa.obj.fimResumoCIC}" id="fimResumoCIC" /></td>
									
								</tr>
								<tr>
									<th>Período de Submissão de relatórios finais de projetos de pesquisa:</th>
									<td> de <t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
										value="#{calendarioPesquisa.obj.inicioRelatorioFinalProjeto}" id="inicioRelatorioFinalProjeto" /> até <t:inputCalendar
										renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
										value="#{calendarioPesquisa.obj.fimRelatorioFinalProjeto}" id="fimRelatorioFinalProjeto" /></td>
								</tr>
								<tr>
									<th>Período de indicação de novos bolsistas para as cotas aprovadas:</th>
									<td> de <t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
										value="#{calendarioPesquisa.obj.inicioIndicacaoBolsista}" id="inicioIndicacaoBolsista" /> até <t:inputCalendar
										renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
										value="#{calendarioPesquisa.obj.fimIndicacaoBolsista}" id="fimIndicacaoBolsista" /></td>
								</tr>
								<tr>
									<th>Segundo período de renovação de projetos:</th>
									<td> de <t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
										value="#{calendarioPesquisa.obj.inicioSegundaRenovacao}" id="inicioSegundaRenovacao" /> até <t:inputCalendar
										renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
										value="#{calendarioPesquisa.obj.fimSegundaRenovacao}" id="fimSegundaRenovacao" /></td>
								</tr>
								<tr>
									<th>Segundo período de envio de relatórios finais de projetos de pesquisa:</th>
									<td> de <t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
										value="#{calendarioPesquisa.obj.inicioRelatorioFinalProjeto2}" id="inicioRelatorioFinalProjeto2" /> até <t:inputCalendar
										renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
										value="#{calendarioPesquisa.obj.fimRelatorioFinalProjeto2}" id="fimRelatorioFinalProjeto2" /></td>
								</tr>
								
						</table>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td>
						<h:commandButton value="#{calendarioPesquisa.confirmButton}" id="confirmar" action="#{calendarioPesquisa.confirmar}" />
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{calendarioPesquisa.cancelar}" id="cancelar" />
					</td>
				</tr>
			</tfoot>
			</h:form>
	</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

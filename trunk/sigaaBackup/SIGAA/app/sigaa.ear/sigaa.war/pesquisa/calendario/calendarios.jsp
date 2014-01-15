<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2 class="title"><ufrn:subSistema /> &gt; Calend�rio de Pesquisa - Calend�rios Dispon�veis</h2>

<br>
	<table width="100%" class="formulario" style="font-size: x-small;">
		<caption>CALEND�RIOS DE PESQUISA</caption>
			<tr>
				<td valign="top">
						<table class="subFormulario" width="100%" align="center" cellpadding="5">
						<caption class="listagem">
						<h:form>
						Datas do calend�rio <h:selectOneMenu value="#{calendarioPesquisa.obj.id}"
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
								<th>Tornar esse calend�rio vigente:</th>
								<td><h:selectBooleanCheckbox value="#{calendarioPesquisa.obj.vigente}"
									id="vigencia" /></td>
							</tr>
							</c:if>
							<tr>
								<th>Per�odo de Submiss�o e renova��o de projetos de pesquisa:</th>
								<td > de <t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
									value="#{calendarioPesquisa.obj.inicioEnvioProjetos}" id="inicioEnvioProjetos"  /> at� <t:inputCalendar
									renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
									value="#{calendarioPesquisa.obj.fimEnvioProjetos}" id="fimEnvioProjetos" /></td>
							</tr>
								<tr>
									<th>Per�odo de avalia��o dos projetos de pesquisa pelos consultores:</th>
									<td> de <t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
										value="#{calendarioPesquisa.obj.inicioAvaliacaoConsultores}" id="inicioAvaliacaoConsultores" /> at� <t:inputCalendar
										renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
										value="#{calendarioPesquisa.obj.fimEnvioAvaliacaoConsultores}" id="fimEnvioAvaliacaoConsultores" /></td>
								</tr>
								<tr>
									<th>Per�odo de Submiss�o de resumos do CIC independentes:</th>
									<td> de <t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
										value="#{calendarioPesquisa.obj.inicioResumoCIC}" id="inicioResumoCIC" /> at� <t:inputCalendar
										renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
										value="#{calendarioPesquisa.obj.fimResumoCIC}" id="fimResumoCIC" /></td>
									
								</tr>
								<tr>
									<th>Per�odo de Submiss�o de relat�rios finais de projetos de pesquisa:</th>
									<td> de <t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
										value="#{calendarioPesquisa.obj.inicioRelatorioFinalProjeto}" id="inicioRelatorioFinalProjeto" /> at� <t:inputCalendar
										renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
										value="#{calendarioPesquisa.obj.fimRelatorioFinalProjeto}" id="fimRelatorioFinalProjeto" /></td>
								</tr>
								<tr>
									<th>Per�odo de indica��o de novos bolsistas para as cotas aprovadas:</th>
									<td> de <t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
										value="#{calendarioPesquisa.obj.inicioIndicacaoBolsista}" id="inicioIndicacaoBolsista" /> at� <t:inputCalendar
										renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
										value="#{calendarioPesquisa.obj.fimIndicacaoBolsista}" id="fimIndicacaoBolsista" /></td>
								</tr>
								<tr>
									<th>Segundo per�odo de renova��o de projetos:</th>
									<td> de <t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
										value="#{calendarioPesquisa.obj.inicioSegundaRenovacao}" id="inicioSegundaRenovacao" /> at� <t:inputCalendar
										renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
										value="#{calendarioPesquisa.obj.fimSegundaRenovacao}" id="fimSegundaRenovacao" /></td>
								</tr>
								<tr>
									<th>Segundo per�odo de envio de relat�rios finais de projetos de pesquisa:</th>
									<td> de <t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
										value="#{calendarioPesquisa.obj.inicioRelatorioFinalProjeto2}" id="inicioRelatorioFinalProjeto2" /> at� <t:inputCalendar
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

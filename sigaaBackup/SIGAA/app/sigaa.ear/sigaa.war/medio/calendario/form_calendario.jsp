<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@ taglib uri="/tags/primefaces-p" prefix="p"%>
<a4j:keepAlive beanName="calendarioMedioMBean"></a4j:keepAlive>

<style>
	.negrito {font-weight: bold !important;}
</style>

<f:view>
	<p:resources />
	<h2 class="title"><ufrn:subSistema /> > Calendário Acadêmico</h2>
		
	<table class="visualizacao">
		<c:if test="${calendarioMedioMBean.obj.unidade.id > 0}">
			<tr>
				<th width="30%">Unidade Responsável:</th>
				<td>${calendarioMedioMBean.obj.unidade.nome }</td>
			</tr>
		</c:if>
		<tr>
			<th>Nível de Ensino:</th>
			<td>${calendarioMedioMBean.obj.nivelDescr}</td>
		</tr>
		<c:if test="${calendarioMedioMBean.obj.curso.id > 0}">
			<tr>
				<th>Curso:</th>
				<td>${calendarioMedioMBean.obj.curso.nome}</td>
			</tr>
		</c:if>
	</table>
	
	<br/>
	
	<table width="100%" class="formulario">
		<caption>Calendário Acadêmico</caption>
		<tr>
			<th width="40%">Calendário Selecionado:</th>
			<td>			 
				<h:form id="formSelecao">
					<a4j:region>
						<h:selectOneMenu
								value="#{calendarioMedioMBean.obj.id}" id="calendarios" 
								valueChangeListener="#{calendarioMedioMBean.carregarCalendario}">
							<f:selectItem itemLabel="-- NOVO --" itemValue="0" />
							<f:selectItems value="#{calendarioMedioMBean.calendariosCombo}" />
							<a4j:support event="onchange"   reRender="form"/>
						</h:selectOneMenu>
					</a4j:region>
				</h:form>
			</td>
		</tr>

		<tr>
			<td colspan="2">
				<h:form id="form">
				<table width="100%" class="formulario">
				<tr>
					<th width="40%"><h:outputText value="Ano:" styleClass="#{calendarioMedioMBean.obj.id == 0 ? 'obrigatorio' : 'negrito'}"/></th>
					<td>
						<c:if test="${calendarioMedioMBean.obj.id == 0}">
							<h:inputText value="#{calendarioMedioMBean.obj.ano}" id="ano" size="4"
								maxlength="4" onkeyup="formatarInteiro(this);" />
						</c:if> 
						<c:if test="${calendarioMedioMBean.obj.id > 0}">
							<h:outputText value="#{calendarioMedioMBean.obj.anoVigente}"></h:outputText>
						</c:if>
					</td>
				</tr>
				<c:if test="${not calendarioMedioMBean.obj.vigente}">
					<tr>
						<th>Tornar esse calendário vigente:</th>
						<td><h:selectBooleanCheckbox
							value="#{calendarioMedioMBean.obj.vigente}" id="vigencia" /></td>
					</tr>
				</c:if>					
				<tr>
					<th class="obrigatorio">Período Letivo:</th>
					<td><t:inputCalendar popupDateFormat="dd/MM/yyyy"
						renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
						maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
						value="#{calendarioMedioMBean.obj.inicioPeriodoLetivo}"
						id="inicioPeriodoLetivo">
						<f:converter converterId="convertData" />
					</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
						renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
						maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
						value="#{calendarioMedioMBean.obj.fimPeriodoLetivo}" id="fimPeriodoLetivo">
						<f:converter converterId="convertData" />
					</t:inputCalendar></td>
				</tr>
				<tr>
					<th>Trancamento de Disciplinas:</th>
					<td><t:inputCalendar popupDateFormat="dd/MM/yyyy"
						renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
						maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
						value="#{calendarioMedioMBean.obj.inicioTrancamentoTurma}"
						id="inicioTrancamentoTurma">
						<f:converter converterId="convertData" />
					</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
						renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
						maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
						value="#{calendarioMedioMBean.obj.fimTrancamentoTurma}"
						id="fimTrancamentoTurma">
						<f:converter converterId="convertData" />
					</t:inputCalendar></td>
				</tr>
				<tr>
					<td class="subFormulario" colspan="2">Calendário das Unidades:</td>
				</tr>	
				<tr>
					<td colspan="2">
						<style>
							.jscalendar-DB-title-style { background-color: #00A !important;}
						</style>
					
						<rich:dataTable styleClass="listagem" style="width:50%;" rowClasses="linhaPar, linhaImpar" 
						value="#{calendarioMedioMBean.calendarioRegra}"	var="item" headerClass="linhaCinza" id="listagemUnidade">
								
							<rich:column>
								<f:facet name="header">
									<h:outputText value="Unidade" />
								</f:facet>
								<h:outputText value="#{item.regra.titulo}" />
							</rich:column>

							<rich:column style="width:120px;">
								<f:facet name="header">
									<h:outputText value="Data Início" />
								</f:facet>
								<t:inputCalendar popupTodayString="Hoje é" popupDateFormat="dd/MM/yyyy" renderAsPopup="true" renderPopupButtonAsImage="true" size="10"
									maxlength="10" onkeypress="return formataData(this,event)" value="#{item.dataInicio}" id="datainicio" />							
							</rich:column>
				
							<rich:column style="width:120px;">
								<f:facet name="header">
									<h:outputText value="Data Fim" />
								</f:facet>
								<t:inputCalendar popupTodayString="Hoje é" popupDateFormat="dd/MM/yyyy" renderAsPopup="true" renderPopupButtonAsImage="true" size="10"
									maxlength="10" onkeypress="return formataData(this,event)" value="#{item.dataFim}" id="dataFim" />
							</rich:column>
					
						</rich:dataTable>					
					</td>
				</tr>			
				<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton value="#{calendarioMedioMBean.confirmButton}" id="confirmar"	action="#{calendarioMedioMBean.confirmar}" /> 
							<h:commandButton action="#{calendarioMedioMBean.iniciar}" id="voltar" value="<< Voltar"/>
							<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{calendarioMedioMBean.cancelar}" id="cancelar" />
						</td>
					</tr>
				</tfoot>				
				</table>
				</h:form>		
			</td>
		</tr>
	</table>
	<center>
		<br/>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena">Campos de preenchimento obrigatório. </span> 
	</center>		
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<link rel="stylesheet" type="text/css" href="/img/prodocente/basic.css" />

<f:view>
<%@include file="/portais/docente/menu_docente.jsp" %>
<h2> Configurações da Agenda </h2>

	<h:form>
		<h:messages tooltip="true" layout="table" globalOnly="false"/>
		<table class="formulario">
			<caption> Informe as configurações desejadas </caption>
			<tr>
				<th> <h:outputLabel for="modoAgenda" value="Modo de Exibição:" /></th>
				<td>
					<h:selectOneRadio id="modoAgenda" value="#{configuracoesAgenda.obj.modoAgenda}">
						<f:selectItem itemValue="0" itemLabel="Dia"/>
						<f:selectItem itemValue="1" itemLabel="Semana de Trabalho"/>
						<f:selectItem itemValue="2" itemLabel="Semana" />
						<f:selectItem itemValue="3" itemLabel="Mês" />
					</h:selectOneRadio>
				</td>
				<td> <h:message for="modoAgenda" /></td>
			</tr>
			<tr>
				<th> <h:outputLabel for="visibleStartHour" value="Começando de:" /> </th>
				<td>
					<h:inputText id="visibleStartHour"
						value="#{configuracoesAgenda.obj.visibleStartHour}" required="true" size="4">
						<f:validateLongRange minimum="0" maximum="11" />
					</h:inputText>
				</td>
				<td> <h:message for="visibleStartHour" /> </td>
			</tr>
			<tr>
				<th> <h:outputLabel for="visibleEndHour" value="Mostrar até:" /></th>
				<td>
					<h:inputText id="visibleEndHour"
						value="#{configuracoesAgenda.obj.visibleEndHour}" required="true" size="4">
						<f:validateLongRange minimum="13" maximum="24" />
					</h:inputText>
				</td>
				<td><h:message for="visibleEndHour" /> </td>
			</tr>
			<tr>
				<th> <h:outputLabel for="workingStartHour" value="Hora de início do dia de trabalho:" /> </th>
				<td> <h:inputText id="workingStartHour"
						value="#{configuracoesAgenda.obj.workingStartHour}" required="true" size="4">
						<f:validateLongRange minimum="7" maximum="11" />
					</h:inputText>
				</td>
				<td><h:message for="workingStartHour" /> </td>
			</tr>
			<tr>
				<th> <h:outputLabel for="workingEndHour" value="Hora de fim do dia de trabalho:" /></th>
				<td>
					<h:inputText id="workingEndHour"
						value="#{configuracoesAgenda.obj.workingEndHour}" required="true" size="4">
						<f:validateLongRange minimum="15" maximum="22" />
					</h:inputText>
				</td>
				<td> <h:message for="workingEndHour" /></td>
			</tr>
			<tr>
				<th><h:outputLabel for="tooltip" value="Mostrar compromisso ao passar o mouse:" /> </th>
				<td>
					<h:selectBooleanCheckbox id="tooltip"
						value="#{configuracoesAgenda.obj.tooltip}" required="true" />
				</td>
				<td> <h:message for="tooltip" /> </td>
			</tr>
			<tr>
				<th> <h:outputLabel for="renderZeroLength" value="Mostrar os dias sem compromissos" /></th>
				<td>
					<h:selectBooleanCheckbox id="renderZeroLength"
						value="#{configuracoesAgenda.obj.renderZeroLength}" required="true" />
		        </td>
				<td> <h:message for="renderZeroLength" /> </td>
			</tr>
			<tr>
				<th> <h:outputLabel for="expandToFitEntries" value="Expand to fit entries" /></th>
				<td>
					<h:selectBooleanCheckbox id="expandToFitEntries"
						value="#{configuracoesAgenda.obj.expandToFitEntries}" required="true" />
				</td>
				<td> <h:message for="expandToFitEntries" />  </td>
			</tr>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton action="#{configuracoesAgenda.gravar}" value="Salvar Configurações" />
					<h:commandButton action="#{configuracoesAgenda.cancelar}" value="Cancelar" immediate="true"/>
				</td>
			</tr>
		</table>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>

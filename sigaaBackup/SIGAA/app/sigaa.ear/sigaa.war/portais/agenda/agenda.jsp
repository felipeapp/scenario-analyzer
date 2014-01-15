<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<link rel="stylesheet" type="text/css" href="/img/prodocente/basic.css" />

<f:view>
<%@include file="/portais/docente/menu_docente.jsp" %>
<h2> Agenda de Compromissos </h2>
<div style="position: relative;">
	<h:messages globalOnly="false" />

	<h:form>
		<t:div style="position: relative; left: 210px; top: 0px; right: 0px; width: 535px">
			<t:schedule value="#{agenda.model}" id="agenda"
				rendered="true"
				visibleStartHour="#{configuracoesAgenda.obj.visibleStartHour}"
				visibleEndHour="#{configuracoesAgenda.obj.visibleEndHour}"
				workingEndHour="#{configuracoesAgenda.obj.workingEndHour}"
				workingStartHour="#{configuracoesAgenda.obj.workingStartHour}"
				readonly="#{configuracoesAgenda.obj.readonly}"
				theme="#{configuracoesAgenda.obj.theme}"
				tooltip="#{configuracoesAgenda.obj.tooltip}"
				headerDateFormat="#{configuracoesAgenda.obj.headerDateFormat}"
				compactWeekRowHeight="#{configuracoesAgenda.obj.compactWeekRowHeight}"
				compactMonthRowHeight="#{configuracoesAgenda.obj.compactMonthRowHeight}"
				detailedRowHeight="#{configuracoesAgenda.obj.detailedRowHeight}"
			/>
		</t:div>

		<t:div style="position: absolute; left: 0px; top:0; width: 200px; overflow: auto; background: #C4D2EB">
			<h:panelGrid columns="1" style="padding: 0 2px 20px 2px;">
				<t:inputCalendar id="scheduleNavigator"
					value="#{agenda.model.selectedDate}" style="background: #EFF3FA;"/>
				<f:verbatim>
					<a href="${pageContext.request.contextPath}/portais/agenda/adiciona_compromisso.jsf"> Cadastrar Compromisso</a> <br/>
					<h:commandLink
						actionListener="#{agenda.removerCompromisso}"
						value="Apagar Compromisso"
						rendered="#{agenda.model.entrySelected}"/>
					<a href="${pageContext.request.contextPath}/portais/agenda/agenda_config.jsf"> Configurações </a>
				</f:verbatim>
			</h:panelGrid>
		</t:div>
	</h:form>

</div>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>
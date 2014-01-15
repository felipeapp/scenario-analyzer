<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
#cepIndicator {
	padding: 0 25px;
	color: #999;
}

span.info {
	font-size: 0.9em;
	color: #888;
}
</style>

<f:view>
	<%@include file="/stricto/menu_coordenador.jsp"%>

	<h2 class="tituloPagina"><ufrn:subSistema /> &gt; Relatório de
	Agendamento para Revalidação de Diplomas</h2>
	<h:outputText value="#{solRevalidacaoDiploma.create}" />

	<h:form id="form">
		<table align="center" class="formulario" width="50%">

			<caption class="listagem">Dados do Relatório</caption>
			<tr>
				<td></td>
				<td align="right">Fila de Espera:</td>
				<td align="left">
				<h:selectOneRadio id="filtroEspera"	value="#{solRevalidacaoDiploma.filtroEspera}">
					<f:selectItem itemLabel="Sim" itemValue="true" />
					<f:selectItem itemLabel="Não" itemValue="false" />
					<a4j:support event="onchange" reRender="data,horario" />
				</h:selectOneRadio>
				</td>
			</tr>

			<a4j:jsFunction reRender="data,horario" name="atualizaFileEspera" />
			<tr>
				<td></td>
				<th align="right" class="required">Edital:</th>
				<td align="left">
					<h:selectOneMenu value="#{solRevalidacaoDiploma.filtroAgenda}" id="edital">
						<f:selectItem itemLabel="-- Selecione --" itemValue="" />
						<f:selectItems value="#{editalRevalidacaoDiploma.allCombo}" />
						<a4j:support event="onchange" reRender="data">
							<f:setPropertyActionListener value="#{solRevalidacaoDiploma.filtroAgenda}"
							 target="#{agendaRevalidacaoDiploma.editalRevalidacaoDiploma.id}"/>
						</a4j:support>
					</h:selectOneMenu>
				</td>
			</tr>

			<tr>
				<td></td>
				<td align="right">Data-Horário:</td>
				<td align="left"><a4j:region>

					<h:selectOneMenu value="#{solRevalidacaoDiploma.filtroData}"
						id="data"
						valueChangeListener="#{agendaRevalidacaoDiploma.carregarHorariosData}"
						disabled="#{solRevalidacaoDiploma.filtroEspera}">
						<f:selectItem itemLabel="Selecione" itemValue="" />
						<f:selectItems value="#{agendaRevalidacaoDiploma.allDatas}" />
						<a4j:support event="onchange" reRender="horario" />
					</h:selectOneMenu>

					<a4j:status>
						<f:facet name="start">
							<h:graphicImage value="/img/indicator.gif" />
						</f:facet>
					</a4j:status>&nbsp;
						
							<h:selectOneMenu
						value="#{solRevalidacaoDiploma.filtroHorario}" id="horario"
						disabled="#{solRevalidacaoDiploma.filtroEspera}">
						<f:selectItem itemLabel="Selecione" itemValue="" />
						<f:selectItems value="#{agendaRevalidacaoDiploma.horariosData}" />
					</h:selectOneMenu>

				</a4j:region></td>
			</tr>
			<tr>
				<td></td>
				<td align="right">Formato:</td>
				<td align="left">
				<h:selectOneRadio id="relatorioFormato"
					value="#{solRevalidacaoDiploma.relatorioformato}">
					<f:selectItem itemValue="xls" itemLabel="Planilha" />
					<f:selectItem itemValue="pdf" itemLabel="PDF" />
				</h:selectOneRadio>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="3" align="center">
					<h:commandButton
						id="gerarRelatorio" value="Gerar Relatório"
						action="#{solRevalidacaoDiploma.gerarRelatorio}" />
					<h:commandButton
						value="Cancelar" action="#{solRevalidacaoDiploma.cancelar}"
						id="cancelar" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<script>atualizaFileEspera();</script>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
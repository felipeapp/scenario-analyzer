<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="cronogramaExecucao" />
	<h:form id="form">
		<h2>
			<ufrn:subSistema />
			>
			<h:commandLink action="#{cronogramaExecucao.voltarFormBusca}"
				value="Cronograma de Execu��o" id="votarFormBusca" />
			> Per�odos
		</h2>


		<table class="formulario" style="width: 100%">
			<caption>Dados Gerais</caption>
			<tbody>
				<!--Lista de M�dulos -->
				<tr>
					<th><strong>M�dulo:</strong></th>
					<td colspan="4">
						${cronogramaExecucao.cronograma.modulo.descricao}</td>
				</tr>

				<!-- Descri��o do Cronograma-->
				<tr>
					<th><strong>Descri��o:</strong></th>
					<td colspan="4">${cronogramaExecucao.cronograma.descricao}</td>
				</tr>

				<!-- Ano Per�odo -->
				<tr>
					<th><strong>Ano - Per�odo</strong></th>
					<td colspan="4">${cronogramaExecucao.cronograma.ano} -
						${cronogramaExecucao.cronograma.periodo}</td>
				</tr>

				<!-- Data inicial e final do per�odo letivo-->
				<tr>
					<th width="20%;"><strong>Per�odo Letivo:</strong></th>
					<td><t:inputCalendar
							value="#{cronogramaExecucao.cronograma.dataInicio}" size="10"
							maxlength="10" disabled="true"
							onkeypress="return formataData(this,event)"
							popupDateFormat="dd/MM/yyyy" id="inicioPeriodo"
							renderAsPopup="true" renderPopupButtonAsImage="true"
							displayValueOnly="true">
						</t:inputCalendar> at� <t:inputCalendar
							value="#{cronogramaExecucao.cronograma.dataFim}" size="10"
							maxlength="10" disabled="true"
							onkeypress="return formataData(this,event)"
							popupDateFormat="dd/MM/yyyy" id="fimPeriodo" renderAsPopup="true"
							renderPopupButtonAsImage="true" displayValueOnly="true">
						</t:inputCalendar></td>
				</tr>

				<!-- Periodicidade do cronograma -->
				<tr>
					<th width="25%"><strong>Periodicidade:</strong></th>
					<td colspan="4">
						${cronogramaExecucao.cronograma.unidadeTempo.descricao}</td>
				</tr>

				<tr>
					<td colspan="2">
						<table class="subFormulario" style="width: 100%">
							<caption>Detalhamento dos per�odos</caption>
							<thead>
								<tr>
									<th style="text-align: left;">N� do Per�odo</th>
									<th style="text-align: left;">Data Inicial</th>
									<th style="text-align: left;">Data Final</th>
									<th style="text-align: left;">C�digo de Integra��o Moodle</th>
								</tr>
							</thead>

							<tbody>
								<c:forEach
									items="${cronogramaExecucao.cronograma.periodosAvaliacao}"
									var="linha" varStatus="countLinha">
									<tr>
										<td style="text-align: left;">${linha.numeroPeriodo}</td>
										<td style="text-align: left;"><fmt:formatDate
												pattern="dd/MM/yyyy" value="${linha.dataInicio}" /></td>
										<td style="text-align: left;"><fmt:formatDate
												pattern="dd/MM/yyyy" value="${linha.datafim}" /></td>
										<td style="text-align: left;">${linha.codigoIntegracao}</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton action="#{cronogramaExecucao.voltarFormBusca}" onclick="#{confirmVoltar}" id="voltar" value="Cancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
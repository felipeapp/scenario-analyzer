<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> <ufrn:subSistema /> > Cronograma de Execu��o </h2>
<a4j:keepAlive beanName="cronogramaExecucao"/>
<h:form id="form">
	<table class="formulario" style="width: 100%">
	  <caption>Dados Gerais</caption>
		<tbody>
			<!--Lista de M�dulos -->
			<tr>
				<th><strong>M�dulo:</strong></th>
				<td>
					${cronogramaExecucao.cronograma.modulo.descricao}
				</td>
			</tr>
			
			<!-- Descri��o do Cronograma-->
			<tr>
				<th><strong>Descri��o:</strong></th>
				<td>
					${cronogramaExecucao.cronograma.descricao}
				</td>
			</tr>
			
			<!-- Ano Per�odo -->
			<tr>
				<th ><strong>Ano - Per�odo:</strong></th>
				<td>
					${cronogramaExecucao.cronograma.ano}
					- ${cronogramaExecucao.cronograma.periodo}
				</td>
			</tr>
			
			<!-- Data inicial e final do per�odo letivo-->	
			<tr>
				<th width="20%;" ><strong>Per�odo Letivo:</strong></th>
				<td><t:inputCalendar value="#{cronogramaExecucao.cronograma.dataInicio}" size="10" maxlength="10" disabled="true"
 						 onkeypress="return formataData(this,event)" popupDateFormat="dd/MM/yyyy" 
 					id="inicioPeriodo" renderAsPopup="true" renderPopupButtonAsImage="true"  displayValueOnly="true"> 
 					</t:inputCalendar > 
 					at�
 					<t:inputCalendar value="#{cronogramaExecucao.cronograma.dataFim}" size="10" maxlength="10" disabled="true" 
 						 onkeypress="return formataData(this,event)" popupDateFormat="dd/MM/yyyy" 
 					id="fimPeriodo" renderAsPopup="true" renderPopupButtonAsImage="true" displayValueOnly="true"> 
 					</t:inputCalendar> 
 				</td>
 			</tr>
 			
 			<!-- Periodicidade do cronograma -->
 			<tr>
				<th width="25%" ><strong>Periodicidade:</strong></th>
				<td>
					${cronogramaExecucao.cronograma.unidadeTempo.descricao}
				</td>
			</tr>

				<tr>
					<td colspan="2">
						<table class="subFormulario" style="width: 100%">
							<caption style="text-align: center">Planilha de Carga Hor�ria</caption>
							<thead>
								<tr>
									<th style="text-align: left;">C�digo</th>
									<th style="text-align: left;">Disciplina</th>
									<c:forEach
										items="${cronogramaExecucao.cronograma.periodosAvaliacao}"
										var="periodo" varStatus="contadorPeriodos">
										<th style="text-align: right;">${periodo.numeroPeriodo}</th>
									</c:forEach>
								</tr>
							</thead>

							<tbody>
								<c:forEach items="${cronogramaExecucao.tabelaCargaHoraria}"
									var="linha" varStatus="countLinha">
									<tr class="${countLinha.index % 2==0 ? 'linhaPar': 'linhaImpar'}">
										<td style="text-align: left;">${cronogramaExecucao.cronograma.modulo.disciplinas[countLinha.index].codigo}</td>
										<td style="text-align: left;">${cronogramaExecucao.cronograma.modulo.disciplinas[countLinha.index].nome}</td>
										<c:forEach items="${linha}" var="coluna">
											<td style="text-align: right;">${coluna.cargaHoraria}</td>
										</c:forEach>
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
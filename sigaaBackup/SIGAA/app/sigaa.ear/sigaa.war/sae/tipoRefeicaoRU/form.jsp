<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
table.subFormulario tbody tr td table tbody tr td{padding:0px !important;float:inherit;} 
</style>

<f:view>
	<h2 class="tituloPagina"> <ufrn:subSistema/> > Horário Refeição R.U.</h2>
	
	<h:form id="form">
		<table class="formulario" width="70%" border="1">
			<caption> Horário Refeição Restaurante Universitário </caption>
				<c:forEach var="linha" items="#{ tipoRefeicaoRUMBean.horarioRefeicaoRU }" varStatus="status">

					<c:choose>

						<c:when test="${ status.index <= 2 }">
						
							<c:if test="${ status.index == 0 }">
								<table class="subFormulario" width="70%">
									<caption> Horário durante a Semana </caption>
									
										<thead>
											<tr>
												<td width="20%">Descrição</td>
												<td colspan="3" width="40%" style="text-align: center;">Horário Inicial</td>
												<td colspan="3" width="40%" style="text-align: center;">Horário Final</td>
												
											</tr>
										</thead>
									
							</c:if>										
							
							<tr>
								<td> ${ linha.descricao } </td>
								<td width="30px" align="right">
									<rich:inputNumberSpinner value="#{ linha.horaInicio }" step="1" maxValue="23" minValue="0" inputSize="1" />
								</td>
								<td style="text-align: center;">:</td>	
								<td  width="30px">
									<rich:inputNumberSpinner value="#{ linha.minutoInicio }" step="5" maxValue="55" minValue="0" inputSize="1"/>
								</td>

								<td width="30px" align="right">
									<rich:inputNumberSpinner value="#{ linha.horaFim }" step="1" maxValue="23" minValue="0" inputSize="1" />
								</td>
								<td style="text-align: center;">:</td>	
								<td  width="30px">
									<rich:inputNumberSpinner value="#{ linha.minutoFim }" step="5" maxValue="55" minValue="0" inputSize="1"/>
								</td>
							</tr>

						</c:when>
						
						<c:otherwise>

								<c:if test="${ status.index == 3 }">
									</table>
									
									<br />
									
									<table class="subFormulario" width="70%">
										<caption> Horário durante o Final de Semana </caption>
										
										<thead>
											<tr>
												<td width="20%">Descrição</td>
												<td colspan="3" width="40%" style="text-align: center;">Horário Inicial</td>
												<td colspan="3" width="40%" style="text-align: center;">Horário Final</td>
												
											</tr>
										</thead>
								</c:if>
							
							<tr>
								<td> ${ linha.descricao } </td>
								<td width="30px" align="right">
									<rich:inputNumberSpinner value="#{ linha.horaInicio }" step="1" maxValue="23" minValue="0" inputSize="1" />
								</td>
								<td style="text-align: center;">:</td>	
								<td  width="30px">
									<rich:inputNumberSpinner value="#{ linha.minutoInicio }" step="5" maxValue="55" minValue="0" inputSize="1"/>
								</td>

								<td width="30px" align="right">
									<rich:inputNumberSpinner value="#{ linha.horaFim }" step="1" maxValue="23" minValue="0" inputSize="1" />
								</td>
								<td style="text-align: center;">:</td>	
								<td  width="30px">
									<rich:inputNumberSpinner value="#{ linha.minutoFim }" step="5" maxValue="55" minValue="0" inputSize="1"/>
								</td>
							</tr>

						</c:otherwise>

				  </c:choose>
				  
				</c:forEach>

			<tfoot>
				<tr>
					<td colspan="8" align="center">
						<h:commandButton value="Alterar" action="#{ tipoRefeicaoRUMBean.cadastrar }" id="submeter" />
						<h:commandButton value="Cancelar" action="#{ residenciaUniversitariaMBean.cancelar }" id="cancelarOperacao" onclick="#{confirm}" /> 
					</td>
				</tr>
			</tfoot>
			
		</table>
	</h:form>
	
	
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>
		<ufrn:subSistema />
		> Cronograma de Execução
	</h2>
	<a4j:keepAlive beanName="cronogramaExecucao" />
<script type="text/javascript">
function mascara(o,f){
    obj = o
    fun = f
    setTimeout("gerarmascara()",1)
}

function gerarmascara(){
    obj.value=fun(obj.value)
}

function masknumeros(texto){
    texto = texto.replace(/\D/g,"")
    return texto
}

</script>
	<h:form id="form">
		<%-- 	<h:inputHidden value="#{cronogramaExecucao.cronograma.curso.id}"/>	 --%>
		<%-- 	<h:inputHidden value="#{cronogramaExecucao.cronograma.modulo.id}"/> --%>
		<table class="formulario" style="width: 100%">
			<caption>Cadastro Cronograma de Execução</caption>
			<tbody>
				<!--Lista de Módulos -->
				<tr>
					<th><strong>Módulo:</strong></th>
					<td>${cronogramaExecucao.cronograma.modulo.descricao}</td>
				</tr>

				<!-- Descrição do Cronograma-->
				<tr>
					<th>Descrição:</th>
					<td><h:inputText
							value="#{cronogramaExecucao.cronograma.descricao}" maxlength="50"
							size="60" /></td>
				</tr>

				<!-- Ano Período -->
				<tr>
					<th class="obrigatorio">Ano - Período</th>
					<td><h:inputText
							value="#{cronogramaExecucao.cronograma.ano}" size="4"
							maxlength="4" id="ano" title="Ano" onkeypress="return mascara(this,masknumeros);" /> - <h:inputText
							value="#{cronogramaExecucao.cronograma.periodo}" size="2"
							maxlength="1" id="periodo" title="Período" onkeypress="return mascara(this,masknumeros);" /></td>
				</tr>

				<!-- Data inicial e final do período letivo-->
				<tr>
					<th width="20%;"><strong>Período Letivo:</strong></th>
					<td><t:inputCalendar
							value="#{cronogramaExecucao.cronograma.dataInicio}" size="10"
							maxlength="10" disabled="true"
							onkeypress="return formataData(this,event)"
							popupDateFormat="dd/MM/yyyy" id="inicioPeriodo"
							renderAsPopup="true" renderPopupButtonAsImage="true"
							displayValueOnly="true">
						</t:inputCalendar> até <t:inputCalendar
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
					<td>${cronogramaExecucao.cronograma.unidadeTempo.descricao}</td>
				</tr>
				<tr>
					<td colspan="2">
							<table class="subFormulario" style="width: 100%">
								<caption style="text-align: center;">Planilha de Carga Horária</caption>
								<tbody>
									<tr>
										<td><rich:dataTable
												value="#{cronogramaExecucao.tabelaCargaHoraria}" var="tab"
												id="tabela2" rowKeyVar="r" styleClass="subFormulario" style="width: 970px; overflow: auto;">
												

												<!--Cabeçalho da tabela -->
												<f:facet name="header">
													<rich:columnGroup styleClass="subFormulario">
														<rich:column rowspan="2" >Código</rich:column>
														<rich:column rowspan="2">Disciplinas</rich:column>
														<rich:column rowspan="1"
															colspan="#{cronogramaExecucao.quantidadePeriodos}">Carga Horária / Encontros</rich:column>
														<rich:column rowspan="2">CH Total</rich:column>
														<rich:columns
															value="#{cronogramaExecucao.cronograma.periodosAvaliacao}"
															var="colunas" index="c" rowspan="1"
															breakBefore="#{c == 0 ? 'true' : 'false'}">
															<f:verbatim>
																<acronym
																	title="PERÍODO ${colunas.numeroPeriodo}: DE <fmt:formatDate value="${colunas.dataInicio}" pattern="dd/MM"/> A <fmt:formatDate value="${colunas.datafim}" pattern="dd/MM"/> (${colunas.chTotalPeriodo}h)"
																	style="text-align: center; cursor: pointer; border: none;">
																	P<fmt:formatNumber value="${colunas.numeroPeriodo}"
																		pattern="00" />
																</acronym>
															</f:verbatim>
														</rich:columns>
													</rich:columnGroup>
												</f:facet>


												<!--Dados da tabela -->
												<rich:column breakBefore="true">
													<h:outputText
														value="#{cronogramaExecucao.cronograma.modulo.disciplinas[r].codigo}" />
												</rich:column>

												<rich:column>
													<h:outputText
														value="#{cronogramaExecucao.cronograma.modulo.disciplinas[r].nome} (#{cronogramaExecucao.cronograma.modulo.disciplinas[r].detalhes.chTotal})" />
												</rich:column>

												<rich:columns
													value="#{cronogramaExecucao.cronograma.periodosAvaliacao}"
													var="colunas" index="c">
													<h:inputText value="#{tab[c].cargaHoraria}" size="1"
														maxlength="2"
														valueChangeListener="#{cronogramaExecucao.calcularCargaHoraria}"
														style="font-align:center;">
														<f:attribute name="linha" value="#{r}" />
														<f:attribute name="coluna" value="#{c}" />
														<a4j:support event="onchange"
															reRender="chSemana, chSemanaj_id_#{c}, chDisciplina, chTotal" />
													</h:inputText>
												</rich:columns>

												<rich:column id="chDisciplina">
													<h:outputText value="#{cronogramaExecucao.chDisciplina[r]}" />
												</rich:column>

												<f:facet name="footer">
													<rich:columnGroup>
														<rich:column colspan="2">
															<h:outputText
																value="Carga Horária do(a) #{cronogramaExecucao.cronograma.unidadeTempo.descricao}" />
														</rich:column>
														<rich:columns
															value="#{cronogramaExecucao.cronograma.periodosAvaliacao}"
															var="colunas" index="c" rowspan="1" id="chSemana"
															styleClass="rich-table-footercell">
															<h:outputText value="#{cronogramaExecucao.chSemana[c]}" />
														</rich:columns>
														<rich:column id="chTotal">
															<h:outputText value="#{cronogramaExecucao.chTotal}" />
														</rich:column>
													</rich:columnGroup>
												</f:facet>
											</rich:dataTable></td>
									</tr>
								</tbody>

							</table>
					</td>
				</tr>
			</tbody>

			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton value="Alterar"
							action="#{cronogramaExecucao.salvar}" /> <h:commandButton
							value="Cancelar" action="#{cronogramaExecucao.cancelarAlteracao}"
							onclick="#{confirm}" id="cancelar" /></td>

				</tr>
			</tfoot>
		</table>


		<br />
		<center>
			<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
			<span class="fontePequena"> Campos de preenchimento
				obrigatório. </span>
		</center>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
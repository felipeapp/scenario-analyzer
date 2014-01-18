<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>
	<h2>
		<ufrn:subSistema />
		> Cronograma de Execução
	</h2>
	<a4j:keepAlive beanName="cronogramaExecucao" />
	<h:form id="form">
		<table class="formulario"
			style="border-right: 1px solid #DEDFE3; width: 100%">
			<tbody>
			<caption>Dados do Cronograma de Execução</caption>
			<tr>
				<th width="25%"><strong>Curso:</strong></th>
				<td>${cronogramaExecucao.cronograma.curso.descricao}</td>
			</tr>

			<!--Lista de Módulos -->
			<tr>
				<th><strong>Módulo:</strong></th>
				<td>${cronogramaExecucao.cronograma.modulo.descricao}</td>
			</tr>

			<!-- Descrição do Cronograma-->
			<tr>
				<th><strong>Descrição:</strong></th>
				<td>${cronogramaExecucao.cronograma.descricao}</td>
			</tr>

			<!-- Ano Período -->
			<tr>
				<th><strong>Ano - Período:</strong></th>
				<td>${cronogramaExecucao.cronograma.ano} -
					${cronogramaExecucao.cronograma.periodo}</td>
			</tr>

			<!-- Data inicial e final do período letivo-->
			<tr>
				<th width="20%;"><strong>Período Letivo:</strong></th>
				<td><fmt:formatDate
						value="${cronogramaExecucao.cronograma.dataInicio}" /> - <fmt:formatDate
						value="${cronogramaExecucao.cronograma.dataFim}" /></td>


				<!-- Periodicidade do cronograma -->
			<tr>
				<th width="25%"><strong>Periodicidade:</strong></th>
				<td >${cronogramaExecucao.cronograma.unidadeTempo.descricao}</td>
			</tr>
			<tr>
				<td colspan="2">
<!-- 					<div id="tabela_carga_horaria" -->
<!-- 						style="width: 975px; overflow: auto;"> -->
						<table class="SubFormulario" style="width: 100%">
							<caption style="text-align: center;">Planilha de Carga Horária</caption>
							<tbody>
								<tr>
									<td><rich:dataTable
											value="#{cronogramaExecucao.tabelaCargaHoraria}" var="tab"
											id="tabela" rowKeyVar="r" styleClass="listagem">

											<!--Cabeçalho da tabela -->
											<f:facet name="header">
												<rich:columnGroup>
													<rich:column rowspan="2">Código</rich:column>
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
																title="PERÍODO ${colunas.numeroPeriodo}: DE <fmt:formatDate value="${colunas.dataInicio}" pattern="dd/MM"/> A <fmt:formatDate value="${colunas.datafim}" pattern="dd/MM"/>"
																style="text-align: center; cursor: pointer; border: none;">
																P<fmt:formatNumber value="${colunas.numeroPeriodo}"
																	pattern="00" />
															</acronym>
														</f:verbatim>
													</rich:columns>
												</rich:columnGroup>
											</f:facet>

											<f:verbatim>
												<acronym
													title="PERÍODO ${colunas.numeroPeriodo}: DE ${colunas.diaMesInicioTexto} A ${colunas.diaMesFimTexto} (${colunas.chTotalPeriodo}h)"
													style="text-align: center; cursor: pointer; border: none;">
													P<fmt:formatNumber value="${colunas.numeroPeriodo}"
														pattern="00" />
												</acronym>
												<br />
											</f:verbatim>


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
													style="font-align:center;"
													onkeypress="return mascara(this,masknumeros);">
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
														var="colunas" index="c" rowspan="1" id="chSemana">
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
<!-- 					</div> -->

				</td>
			</tr>



			</tbody>
			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton value="Cadastrar"
							action="#{cronogramaExecucao.salvar}" /> <h:commandButton
							value="<< Voltar" action="#{cronogramaExecucao.voltarForm}" /> <h:commandButton
							value="Cancelar" action="#{cronogramaExecucao.cancelar}"
							onclick="#{confirm}" /></td>
				</tr>
			</tfoot>
		</table>

	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<table class="formulario" style="width: 100%">
	<c:if test="${ not empty relatorioAcaoExtensao.mbean.obj.andamento }">
		<c:set value="0" var="objetivoAtual" />
			<c:forEach items="#{relatorioAcaoExtensao.mbean.obj.andamento}" var="_andamento" varStatus="st1">
				
				<c:if test="${ objetivoAtual != _andamento.atividade.objetivo.id }">
					<tr>
						<td align="right" class="subFormulario" width="100%" colspan="7">
							<a href="javascript: void(0);" onclick="habilitarDetalhes(${_andamento.atividade.objetivo.id});" title="Visualizar Detalhes da Solicitação">
								<img src="${ctx}/img/cronograma/limpar.gif" />
								<h:graphicImage value="/img/indicator.gif" id="indicator_${_andamento.atividade.objetivo.id}" style="display: none;" /> 
								<h:outputText value="#{_andamento.atividade.objetivo.objetivo}" />
							</a>
						</td>
					</tr>
					<tr>
						<td colspan="3">
							<table style="width: 100%; display: none;" id="tbAtividades_${ _andamento.atividade.objetivo.id }">
					
				</c:if>

								<tr>
									<td colspan="2" class="subFormulario">									
										<b>Atividades Relacionadas:</b>
									</td>
									<td class="subFormulario" style="text-align: center;">								
										<b>Período Realização:</b>
									</td>
									<td class="subFormulario" style="text-align: center;">									
										<b>Carga Horária:</b>
									</td>
									<td class="subFormulario" style="text-align: center;">									
										<b>Andamento Objetivo:</b>
									</td>
									<td class="subFormulario" style="text-align: center;">									
										<b>Situação Objetivo:</b>
									</td>
									<td class="subFormulario" style="text-align: center;">									
									</td>
								</tr>

								<tr>
									<td colspan="2" width="25%;">
										${st1.index + 1}. <h:outputText value="#{_andamento.atividade.descricao}" />
									</td>
									<td width="20%;" align="center">	
										<h:outputText value="#{_andamento.atividade.dataInicio}" id="dataInicioAtividade"><f:convertDateTime pattern="dd/MM/yyyy"  /></h:outputText>
											<c:if test="${not empty _andamento.atividade.dataFim}">
												&nbsp; a &nbsp; 
											</c:if> 
										<h:outputText value="#{_andamento.atividade.dataFim}" id="dataFimAtividade"><f:convertDateTime pattern="dd/MM/yyyy"  /></h:outputText>						
									</td>
									<td style="text-align: center;" width="10%">
										<h:outputText value="#{ _andamento.atividade.cargaHoraria }" /> h
									</td>
									<td style="text-align: center;" width="10%">
								      	<rich:inputNumberSlider value="#{ _andamento.andamentoAtividade }" minValue="0" maxValue="100" step="1" showInput="false" 
								      		onfocus="setAba('objetivos')">
								      		<a4j:support event="onchange" reRender="outputPanelImg" onsubmit="true" />
								      	</rich:inputNumberSlider>
									</td>
									<td style="text-align: center;" width="10%">
										<h:selectOneMenu id="idSistema" value="#{ _andamento.statusAtividade }" onfocus="setAba('objetivos')">
											<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
											<f:selectItem itemValue="1" itemLabel="EM CURSO" />
											<f:selectItem itemValue="2" itemLabel="CONCLUÍDO" />
											<f:selectItem itemValue="3" itemLabel="CANCELADO" />
											<a4j:support event="onchange" reRender="outputPanelImg" onsubmit="true" />
										</h:selectOneMenu>
									</td>
									<td style="text-align: center;" width="3%">
										<a4j:outputPanel ajaxRendered="true" id="outputPanelImg">
											<h:graphicImage value="/img/extensao/andamento.png" rendered="#{ _andamento.andamento }" title="Em Andamento"/>
											<h:graphicImage value="/img/extensao/yellow.png" rendered="#{ _andamento.atrasada }" title="Atrasada"/>
											<h:graphicImage value="/img/extensao/green.png" rendered="#{ _andamento.concluida }" title="Concluída"/> 
											<h:graphicImage value="/img/extensao/red.png" rendered="#{ _andamento.cancelada }" title="Cancelada"/>
										</a4j:outputPanel>
									</td>
								</tr>

				
				<c:set value="#{ _andamento.atividade.objetivo.id }" var="objetivoAtual" />
				<c:set var="proximo" value="${relatorioAcaoExtensao.mbean.obj.andamento[st1.index + 1].atividade.objetivo.id}" />
				<c:if test="${ objetivoAtual != proximo }">
							
								<tr>
									<td colspan="7"><b> Digite um breve relato sobre a execução do objetivo</b>
										<h:inputTextarea rows="4" style="width:99%" value="#{ _andamento.atividade.objetivo.observacaoExecucao }"/>
									</td>	
								</tr>
							</table>
						</td>
					</tr>
				</c:if>											
							
			</c:forEach>
	</c:if>
</table>
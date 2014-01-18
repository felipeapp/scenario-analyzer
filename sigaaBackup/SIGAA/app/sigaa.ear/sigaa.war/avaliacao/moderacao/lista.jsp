<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> > Moderação das Observações da Avaliação Institucional</h2>

	<div class="descricaoOperacao">
	<p>Caro Usuário,</p>
	<p>Selecione um Departamento, informe um
	Ano/Período ou o nome de um docente para consultar para buscar por
	observações realizadas pelos discentes. <br />
	Selecione uma observação da lista para editá-la.<br /></p>
	</div>

	<h:form id="form">
		<a4j:keepAlive beanName="moderadorObservacaoBean"></a4j:keepAlive>	
		<table class="formulario" width="90%">
			<caption>Informe os Parâmetros da Busca</caption>
			<tbody>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox value="#{moderadorObservacaoBean.checkBuscaDepartamento}" id="checkBuscaDepartamento" styleClass="noborder"/>
					</td>
					<th><label for="checkBuscaDepartamento" onclick="$('form:checkBuscaDepartamento').checked = !$('form:checkBuscaDepartamento').checked;">Departamento:</label></th>
					<td>
						<h:selectOneMenu
							onfocus="javascript:$('form:checkBuscaDepartamento').checked = true;"
							id="departamento"
							value="#{moderadorObservacaoBean.idDepartamento}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{unidade.allDepartamentoUnidAcademicaCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td>
						<h:selectBooleanCheckbox value="#{moderadorObservacaoBean.checkBuscaAnoPeriodo}" id="checkBuscaAnoPeriodo" styleClass="noborder"/>
					</td>
					<th><label for="checkBuscaAnoPeriodo" onclick="$('form:checkBuscaAnoPeriodo').checked = !$('form:checkBuscaAnoPeriodo').checked;">Ano-Período:</label></th>
					<td>
						<h:selectOneMenu
							onfocus="javascript:$('form:checkBuscaAnoPeriodo').checked = true;"
							id="anoPeriodo"
							value="#{moderadorObservacaoBean.idParametroProcessamento}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{relatorioAvaliacaoMBean.anoPeriodoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<c:if test="${not moderadorObservacaoBean.moderarTrancamentos}">
					<tr>
						<td>
							<h:selectBooleanCheckbox value="#{moderadorObservacaoBean.checkBuscaNome}" id="checkBuscaNome" styleClass="noborder"/>
						</td>
						<th><label for="checkBuscaNome" onclick="$('form:checkBuscaNome').checked = !$('form:checkBuscaNome').checked;">Docente:</label></th>
						<td>
							<h:inputText value="#{moderadorObservacaoBean.docente.pessoa.nome}" id="nomeServidor" size="59" maxlength="256" onfocus="javascript:$('form:checkBuscaNome').checked = true;"/>
							<rich:suggestionbox for="nomeServidor" width="450" height="100" minChars="3" id="suggestionNomeServidor" 
								suggestionAction="#{servidorAutoCompleteMBean.autocompleteNomeServidor}" var="_servidor" 
								fetchValue="#{_servidor.pessoa.nome}">	
								<h:column>
									<h:outputText value="#{_servidor.pessoa.nome}" />
								</h:column>							        
							        <f:param name="apenasAtivos" value="true" />
							        <a4j:support event="onselect">
									<f:setPropertyActionListener value="#{_servidor.id}" target="#{moderadorObservacaoBean.docente.id}" />
								</a4j:support>
							</rich:suggestionbox>
						</td>
					</tr>
				</c:if>
				<c:if test="${moderadorObservacaoBean.moderarTrancamentos}">
					<tr>
						<td>
							<h:selectBooleanCheckbox value="#{moderadorObservacaoBean.checkBuscaComponente}" id="checkBuscaComponente" styleClass="noborder"/>
						</td>
						<th><label for="checkBuscaComponente" onclick="$('form:checkBuscaComponente).checked = !$('form:checkBuscaComponente').checked;">Componente Curricular:</label></th>
						<td>
							<h:inputText value="#{moderadorObservacaoBean.nomeComponente}" id="nomeComponente" size="60" maxlength="60" onfocus="javascript:$('form:checkBuscaComponente').checked = true;"/>
						</td>
					</tr>
				</c:if>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{ moderadorObservacaoBean.buscar }" id="buscar" /> 
						<h:commandButton value="Cancelar" action="#{ moderadorObservacaoBean.cancelar }" id="cancelar" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br/>
		<c:if test="${ not empty moderadorObservacaoBean.listaDocentes}">
			<div class="infoAltRem">
				<h:graphicImage value="/img/check.png" style="overflow: visible;" />: Moderação Finalizada
				<h:graphicImage value="/img/cross.png" style="overflow: visible;" />: Moderação Pendente
				<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Moderar Comentários
			</div>
			<%-- Tabela de resultados da busca por DocenteTurma --%>
			<c:if test="${not moderadorObservacaoBean.moderarTrancamentos}">
				<c:set var="departamentoAtual" value="0" />
				<c:set var="docenteAtual" value="0" />
				<table class="listagem" style="width: 70%">
					<caption>Resultados Encontrados (${fn:length(moderadorObservacaoBean.listaDocentes)})</caption>
					<thead>
						<tr>
							<th width="5%" style="text-align: center;"></th>
							<th>Turma</th>
							<th width="5%" style="text-align: right;">Qtd<br/>Observações</th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="#{moderadorObservacaoBean.listaDocentes}" var="linha" varStatus="status">
							<c:if test="${departamentoAtual != linha.id_unidade}">
								<tr>
									<td class="subFormulario" colspan="4">${linha.departamento}</td>
								</tr>
								<c:set var="departamentoAtual" value="${linha.id_unidade}" />
								<c:set var="docenteAtual" value="0" />
							</c:if>
							<c:if test="${docenteAtual != linha.id_servidor}">
								<c:if test="${index > 1 and index % 2 == 0}">
									<tr><td colspan="3"></td></tr>
								</c:if>
								<tr>
									<td class="subFormulario" colspan="4">
										Docente: <h:outputText value="#{linha.nome_docente}" />
									</td>
								</tr>
								<c:set var="docenteAtual" value="${linha.id_servidor}" />
								<c:set var="index" value="0" />
							</c:if>
							<tr class="${index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
								<td width="5%" style="text-align: right;">
									<c:if test="${linha.qtd_observacoes_docente_turma > 0}">
										<h:graphicImage value="/img/check.png" style="overflow: visible;" rendered="#{linha.docente_turma_finalizado}"/>
										<h:graphicImage value="/img/cross.png" style="overflow: visible;" rendered="#{not linha.docente_turma_finalizado}"/>
									</c:if>
								</td>
								<td>
									${linha.codigo_componente} - ${linha.nome_componente} - TURMA ${linha.codigo_turma} (${linha.ano}.${linha.periodo})
								</td>
								<td style="text-align: right;">
									${linha.qtd_observacoes_docente_turma}
									<h:outputText value="0" rendered="#{empty linha.qtd_observacoes_docente_turma}" />
								</td>
								<td width="5%" style="text-align: right;">
									<c:if test="${linha.qtd_observacoes_docente_turma > 0 and not linha.docente_turma_finalizado}">
										<h:commandLink action="#{moderadorObservacaoBean.selecionarDocenteTurma}" title="Moderar Comentários para Docentes da Turma" id="trancamentoLink">
											<h:graphicImage url="/img/seta.gif" />
											<f:param id="idDocenteTurma" name="idDocenteTurma" value="#{linha.id_docente_turma}"/>
										</h:commandLink>
									</c:if>
									<h:graphicImage url="/img/seta_cinza.gif" rendered="#{linha.qtd_observacoes_docente_turma == 0}" title="Esta turma não possui comentários para o docente."/>
									<h:graphicImage url="/img/seta_cinza.gif" rendered="#{linha.docente_turma_finalizado}" title="A moderação dos comentários para o docente desta turma foi finalizada."/>
								</td>
							</tr>
							<c:set var="index" value="${index + 1}" />
						</c:forEach>
					</tbody>
				</table>
			</c:if>
			<%-- Tabela de resultados da busca por Turma --%>
			<c:if test="${moderadorObservacaoBean.moderarTrancamentos}">
				<c:set var="departamentoAtual" value="0" />
				<table class="listagem" style="width: 70%">
					<caption>Resultados Encontrados (${fn:length(moderadorObservacaoBean.listaDocentes)})</caption>
					<thead>
						<tr>
							<th width="5%" style="text-align: center;"></th>
							<th>Turma</th>
							<th width="5%" style="text-align: right;">Qtd<br/>Observações</th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="#{moderadorObservacaoBean.listaDocentes}" var="linha" varStatus="status">
							<c:if test="${departamentoAtual != linha.id_unidade}">
								<tr>
									<td class="subFormulario" colspan="4">${linha.departamento}</td>
								</tr>
								<c:set var="departamentoAtual" value="${linha.id_unidade}" />
							</c:if>
							<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
								<td width="5%" style="text-align: right;">
									<c:if test="${linha.qtd_observacoes_trancamento > 0}">
										<h:graphicImage value="/img/check.png" style="overflow: visible;" rendered="#{linha.trancamento_finalizado}"/>
										<h:graphicImage value="/img/cross.png" style="overflow: visible;" rendered="#{not linha.trancamento_finalizado}"/>
									</c:if>
								</td>
								<td>
									${linha.codigo_componente} - ${linha.nome_componente} - TURMA ${linha.codigo_turma} (${linha.ano}.${linha.periodo})
								</td>
								<td style="text-align: right;">
									${linha.qtd_observacoes_trancamento}
								</td>
								<td width="5%" style="text-align: right;">
									<c:if test="${linha.qtd_observacoes_trancamento > 0 and not linha.trancamento_finalizado}">
										<h:commandLink action="#{moderadorObservacaoBean.selecionarTurma}" title="Moderar Comentários de Trancamentos da Turma" id="trancamentoLink">
											<h:graphicImage url="/img/seta.gif" />
											<f:param id="idTurma" name="idTurma" value="#{linha.id_turma}"/>
										</h:commandLink>
									</c:if>
									<h:graphicImage url="/img/seta_cinza.gif" rendered="#{linha.qtd_observacoes_trancamento == 0}" title="Esta turma não possui comentários."/>
									<h:graphicImage url="/img/seta_cinza.gif" rendered="#{linha.trancamento_finalizado}" title="A moderação dos comentários desta turma foi finalizada."/>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>
		</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
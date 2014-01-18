<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> > Resultado Analítico da Avaliação Institucional</h2>

	<a4j:keepAlive beanName="relatorioAvaliacaoMBean"></a4j:keepAlive>

	<h:form id="form">

		<table class="formulario" width="80%">
			<caption>Informe os Dados para Realizar a Busca do Docente</caption>
			<tbody>
				<tr>
					<td>
						<c:if test="${!acesso.chefeDepartamento}">
							<h:selectBooleanCheckbox value="#{relatorioAvaliacaoMBean.checkBuscaDepartamento}" id="checkBuscaDepartamento" styleClass="noborder" />
						</c:if>
					</td>
					<td>
						<c:if test="${!acesso.chefeDepartamento}">
							Departamento:
						</c:if>
						<c:if test="${acesso.chefeDepartamento}">
							<b>Departamento:</b>
						</c:if>
					</td>
					<td>
						<c:if test="${!acesso.chefeDepartamento}">
							<h:selectOneMenu onfocus="javascript:$('form:checkBuscaDepartamento').checked = true;"
								id="departamento"
								value="#{relatorioAvaliacaoMBean.idUnidade}">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{relatorioAvaliacaoMBean.unidadesCombo}" />
							</h:selectOneMenu>
						</c:if>
						<c:if test="${acesso.chefeDepartamento}">
							<h:inputHidden value="#{relatorioAvaliacaoMBean.idUnidade}"/>
							<h:outputText value="#{relatorioAvaliacaoMBean.unidade.nome}"/>
						</c:if>
					</td>
				</tr>
				<tr>
					<td width="2%">
						<h:selectBooleanCheckbox value="#{relatorioAvaliacaoMBean.checkBuscaNome}" id="checkBuscaNome" styleClass="noborder"/>
					</td>
					<td width="15%">Nome:</td>
					<td>
						<h:inputText value="#{relatorioAvaliacaoMBean.docente.pessoa.nome}" id="nomeServidor" size="59" maxlength="256" onfocus="javascript:$('form:checkBuscaNome').checked = true;"/>
						<rich:suggestionbox for="nomeServidor" width="450" height="100" minChars="3" id="suggestionNomeServidor" 
							suggestionAction="#{servidorAutoCompleteMBean.autocompleteNomeServidor}" var="_servidor" 
							fetchValue="#{_servidor.pessoa.nome}">	
							<h:column>
								<h:outputText value="#{_servidor.pessoa.nome}" />
							</h:column>							        
						        <f:param name="apenasAtivos" value="true" />
						        <a4j:support event="onselect">
								<f:setPropertyActionListener value="#{_servidor.id}" target="#{relatorioAvaliacaoMBean.docente.id}" />
							</a4j:support>
						</rich:suggestionbox>
					</td>
				</tr> 
				<tr>
					<td width="2%">
						<h:selectBooleanCheckbox value="#{relatorioAvaliacaoMBean.checkBuscaAnoPeriodo}" id="checkBuscaAnoPeriodo" styleClass="noborder"/>
					</td>
					<td>Ano-Período:</td>
					<td>
						<h:selectOneMenu
							onfocus="javascript:$('form:checkBuscaAnoPeriodo').checked = true;"
							id="anoPeriodo"
							value="#{relatorioAvaliacaoMBean.anoPeriodo}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{relatorioAvaliacaoMBean.anoPeriodoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr> 
			</tbody>
			<tfoot>
					<tr>
						<td colspan="3">
							<h:commandButton value="Buscar" action="#{ relatorioAvaliacaoMBean.buscarDocente }"
							id="Botaorelatorio" /> 
							<h:commandButton value="Cancelar" action="#{ relatorioAvaliacaoMBean.cancelar }" 
							id="BotaoCancelar" onclick="#{confirm}" />
						</td>
					</tr>
			</tfoot>
		</table>
		<br/>
		<c:if test="${ not empty relatorioAvaliacaoMBean.listaDocentes}">
		<div class="infoAltRem">
			<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Resultado do Docente 
		</div>
		<table class="listagem" style="width: 80%">
		<thead>
			<td>Docente</td>
			<td>Departamento</td>
			<td align="center" width="5%">Ano</td>
			<td align="center" width="5%">Período</td>
			<td align="center" width="5%">EaD</td>
			<td align="right" width="5%">Turmas</td>
			<td width="5%"></td>
		</thead>
		<caption>Resultados Encontrados (${fn:length(relatorioAvaliacaoMBean.listaDocentes)})</caption>
		<tbody>
			<c:forEach items="#{relatorioAvaliacaoMBean.listaDocentes}" var="linha" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td>
						<h:outputText value="#{linha.nome}" />
					</td>
					<td>
						<h:outputText value="#{linha.departamento}" />
					</td>
					<td align="center">
						<h:outputText value="#{linha.ano}" />
					</td>
					<td align="center">
						<h:outputText value="#{linha.periodo}" />
					</td>
					<td align="center">
						<ufrn:format type="simnao" valor="${linha.ead}" />
					</td>
					<td align="right">
						<h:outputText value="#{linha.qtd_turmas}" />
					</td>
					<td align="right" width="5%">
						<h:commandLink action="#{relatorioAvaliacaoMBean.viewResultadoDocente}" title="Visualizar Resultado do Docente" id="viewLink">
							<h:graphicImage url="/img/view.gif" />
							<f:param id="idDocente" name="idServidor" value="#{linha.id_pessoa}"/>
							<f:param id="ano" name="ano" value="#{linha.ano}"/>
							<f:param id="periodo" name="periodo" value="#{linha.periodo}"/>
							<f:param id="idFormulario" name="idFormulario" value="#{linha.id_formulario_avaliacao}"/>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
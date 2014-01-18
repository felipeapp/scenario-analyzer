<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> > Resultado Analítico da Avaliação Institucional</h2>

	<h:messages showDetail="true" />
	<h:outputText value="#{relatorioAvaliacaoMBean.create}" />

	<h:form id="form">

		<table class="visualizacao" width="80%">
			<caption>Resultados da Avaliação Institucional do Docente</caption>
			<tbody>
				<tr>
					<th width="25%">Nome:</th>
					<td>
						<h:outputText value="#{relatorioAvaliacaoMBean.docente.nome}" id="nomeDocente" />
					</td>
				</tr> 
				<tr>
					<th>Departamento:</th>
					<td>
						<h:outputText value="#{relatorioAvaliacaoMBean.docente.unidade.nome}" id="descricaoDepartamento" />
					</td>
				</tr>
			</tbody>
		</table>
		<br/>
		<div class="infoAltRem" style="width: 50%">
			<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Resultado do Docente 
		</div>
		<c:if test="${ not empty relatorioAvaliacaoMBean.listaDocentes}">
			<table class="formulario" width="50%">
				<caption>Resultados Disponíveis para Consulta</caption>
				<thead>
					<tr>
						<th align="left" width="5%" colspan="2">Ano-Período</th>
						<th width="5%"></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="#{relatorioAvaliacaoMBean.listaDocentes}" var="linha" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<td align="left" colspan="2">
								<h:outputText value="#{linha.ano}" />.<h:outputText value="#{linha.periodo}" />
							</td>
							<td align="right" width="5%">
								<h:commandLink action="#{relatorioAvaliacaoMBean.viewResultadoDocente}" title="Visualizar Resultado do Docente" id="viewLink">
									<h:graphicImage url="/img/view.gif" />
									<f:param id="idDocente" name="idServidor" value="#{linha.id_servidor}"/>
									<f:param id="ano" name="ano" value="#{linha.ano}"/>
									<f:param id="periodo" name="periodo" value="#{linha.periodo}"/>
									<f:param id="idFormulario" name="periodo" value="#{linha.id_formulario_avaliacao}"/>
								</h:commandLink>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if>
		<c:if test="${ empty relatorioAvaliacaoMBean.listaDocentes}">
			<div align="center">Não há Resultados de Avaliações Disponíveis</div>
		</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
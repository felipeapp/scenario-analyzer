<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema /> > Observações dos Docentes na Avaliação Institucional</h2>
<br/>
<h:form id="form">
		<table class="formulario" width="95%">
			<caption>Selecione um Ano-Período</caption>
			<tbody>
				<tr>
					<th class="required">Ano-Período:</th>
					<td>
						<h:selectOneMenu
							id="anoPeriodo"
							value="#{relatorioAvaliacaoMBean.anoPeriodo}" >
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{relatorioAvaliacaoMBean.anoPeriodoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Buscar Observações" action="#{ relatorioAvaliacaoMBean.observacoesTurmasDiscente }" id="consultar"/>
						<h:commandButton value="Cancelar" action="#{ relatorioAvaliacaoMBean.cancelar }" id="cancelar" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br/>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
		<br/>
		<c:if test="${ not empty relatorioAvaliacaoMBean.observacoesTurmaDiscente}">
			<table class="listagem" style="width: 95%">
				<caption>Observações Dadas pelos Docentes</caption>
				<tbody>
					<c:set var="turmaAnterior" value="" />
					<c:forEach items="#{relatorioAvaliacaoMBean.observacoesTurmaDiscente}" var="linha" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<th valign="top"><b>Turma:</b></th>
							<td>
								${linha.docenteTurma.turma}
							</td>
							<th valign="top"><b>Docente:</b></th>
							<td>
								${linha.docenteTurma.docenteNome}
							</td>
						</tr>
						<tr>
							<th valign="top"><b>Observação:</b></th>
							<td colspan="3">
								${linha.observacoesModeradas}
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if>
		<br/>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h2> <ufrn:subSistema /> &gt; Relatório de Disciplinas que mais Reprovam</h2>

<h:form id="form">
<h:inputHidden value="#{relatoriosPlanejamento.analitico}" />
<h:inputHidden value="#{relatoriosPlanejamento.validaPeriodoAno}" />
<table align="center" class="formulario" width="100%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<th  style="width: 30%;">Centro: </th>
		<td>
			<h:selectOneMenu id="centro" value="#{relatoriosPlanejamento.centro.id}">
				<f:selectItem itemLabel="-- TODOS --" itemValue="0"/>
				<f:selectItems value="#{unidade.allCentrosEscolasCombo}"/>
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<th class="obrigatorio">Ano Início: </th>
		<td>
			<h:inputText id="anoInicio" value="#{relatoriosPlanejamento.anoInicio}" size="4" maxlength="4" onkeyup="formatarInteiro(this)"/>
		</td>
	</tr>
	<tr>
		<th class="obrigatorio">Ano Fim: </th>
		<td>
			<h:inputText id="anoFim" value="#{relatoriosPlanejamento.anoFim}" size="4" maxlength="4" onkeyup="formatarInteiro(this)"/>
		</td>
	</tr>	
	<tr>
		<td colspan="2" align="center">
			<c:if test="${ not relatoriosPlanejamento.analitico}">
				<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatoriosPlanejamento.gerarRelatorioReprovacoesPlanejamento}"/>
			</c:if>
			<c:if test="${relatoriosPlanejamento.analitico}">
				<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatoriosPlanejamento.gerarRelatorioReprovacoesPlanejamentoAnalitico}"/>
			</c:if>
			<h:commandButton value="Cancelar" action="#{relatoriosPlanejamento.cancelar}" id="cancelar" onclick="#{confirm}"/>
		</td>
	</tr>

</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
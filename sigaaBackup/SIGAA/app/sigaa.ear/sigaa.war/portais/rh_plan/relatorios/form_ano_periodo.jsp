<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h:form id="form">
<h:outputText value="#{relatoriosPlanejamento.create}"></h:outputText>
<h2><ufrn:subSistema /> > Relatório Quantitativo de Alunos Matriculados</h2>

<h:inputHidden value="#{relatoriosPlanejamento.validaAnoPeriodo}" />
<table align="center" class="formulario" width="60%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<th width="20%">Tipo: </th>
		<td>
			<h:selectOneRadio id="tipo" value="#{relatoriosPlanejamento.tipo}" onclick="esconderAnoPeriodo(this)">
				<f:selectItem itemLabel="Matriculados" itemValue="1"/>
				<f:selectItem itemLabel="Ativos" itemValue="2"/>
			</h:selectOneRadio>
		</td>
	</tr>
	<tr id="anoPeriodo">
		<th>Ano-Período: </th>
		<td>
			<h:inputText id="ano" value="#{relatoriosPlanejamento.ano}" size="4" maxlength="4" onkeyup="formatarInteiro(this)"/> -
			<h:inputText id="periodo" value="#{relatoriosPlanejamento.periodo}" size="1" maxlength="1" onkeyup="formatarInteiro(this)" />
		</td>
	</tr>
	<tr>
		<td colspan="2" align="center">
						<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
						action="#{relatoriosPlanejamento.gerarRelatorioQuantitativoAlunosMatriculados}"/> <h:commandButton
						value="Cancelar" action="#{relatoriosPlanejamento.cancelar}" id="cancelar" /></td>
	</tr>

</table>
</h:form>
</f:view>

<script>
	function esconderAnoPeriodo(t) {

		if (t.value == 2) {
			$('anoPeriodo').hide();
		} else { 
			$('anoPeriodo').show();
		}
	}
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
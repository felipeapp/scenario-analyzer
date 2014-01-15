<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema /> > Relatório de quantitativo de solicitações, turmas, matrículas e vagas por componente curricular</h2>
<h:messages showDetail="true" showSummary="true"/>

<h:form id="form">

<table align="center" class="formulario" width="95%">
	<caption>Dados do Relatório</caption>

	<tr>
		<th align="right" width="45%"> Ano-período: </th>
		<td align="left">
			<h:inputText value="#{relatorioQuantitativoTurmasSolicitacoesBean.ano}" id="ano" size="4" maxlength="4" onkeyup="return formatarInteiro(this);"/> -
			<h:inputText value="#{relatorioQuantitativoTurmasSolicitacoesBean.periodo}" id="periodo" size="1" maxlength="1" onkeyup="return formatarInteiro(this);"/>
		</td>
	</tr>
	
	<tfoot>
	<tr>
		<td colspan="2" align="center">
			<h:commandButton value="Gerar Relatório" id="gerarRelatorio" action="#{relatorioQuantitativoTurmasSolicitacoesBean.gerarRelatorio}" />
			<h:commandButton value="Cancelar" id="cancelar" action="#{relatorioQuantitativoTurmasSolicitacoesBean.cancelar}" onclick="#{confirm}"/>
		</td>
	</tr>
</table>

</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{realtorioDocente.create}"></h:outputText>
<h2><ufrn:subSistema /> > Lista de Docentes por Departamento </h2>

<h:form id="form">
<table align="center" class="formulario" width="100%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<th>Departamento: </th>
		<td><h:selectOneMenu style="width: 380px;" value="#{relatorioDocente.departamento.id}"
					id="departamento">
					<f:selectItem itemValue="0" itemLabel="TODOS" />
					<f:selectItems value="#{unidade.allDepartamentoCombo}" />
				</h:selectOneMenu></td>
	</tr>
	<tr>
		<td colspan="2" align="center">
						<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
						action="#{relatorioDocente.gerarRelatorioQuantitativo}"/> <h:commandButton
						value="Cancelar" action="#{relatorioDocente.cancelar}" id="cancelar" /></td>
	</tr>

</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
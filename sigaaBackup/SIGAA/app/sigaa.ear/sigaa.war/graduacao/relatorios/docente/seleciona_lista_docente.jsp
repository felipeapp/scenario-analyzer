<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{realtorioDocente.create}"></h:outputText>
<h2><ufrn:subSistema /> > Lista de Docentes por Departamento </h2>

<h:form id="form">
<table align="center" class="formulario" width="100%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<th>Centro: </th>
		<td>
			<h:selectOneMenu id="centros" style="width: 300px"
				valueChangeListener="#{ unidade.changeCentro }" onchange="submit()"
				value="#{relatorioDocente.centro.id}" >
				<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
				<f:selectItems value="#{unidade.allCentroCombo}" />
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<th>Departamento: </th>
		<td>
		
		<h:selectOneMenu id="departamentos" style="width: 300px" value="#{relatorioDocente.departamento.id}">
			<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
			<f:selectItems value="#{unidade.unidades}" />
		</h:selectOneMenu>
		
		</td>
	</tr>
	<tr>
		<td colspan="2" align="center">
						<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
						action="#{relatorioDocente.gerarRelatorioLista}"/> <h:commandButton
						value="Cancelar" action="#{relatorioDocente.cancelar}" id="cancelar" /></td>
	</tr>

</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
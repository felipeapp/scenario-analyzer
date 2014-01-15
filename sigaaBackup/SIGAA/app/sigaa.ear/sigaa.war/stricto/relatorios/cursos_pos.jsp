<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:outputText value="#{relatorioCursosPos.create}" />

<h2> <ufrn:subSistema /> > ${relatorioCursosPos.titulo} </h2>

<h:form id="form">
<table class="formulario" style="width: 95%">
<caption> Informe os critérios para a emissão do relatório </caption>

	<tr>
		<th>Centro: </th>
		<td>
			<h:selectOneMenu id="centro" value="#{relatorioCursosPos.unidade.id}">
				<f:selectItem itemLabel=" Todos " itemValue="-1"/>
				<f:selectItems value="#{unidade.allCentroCombo}"/>
			</h:selectOneMenu>
		</td>
	</tr>

	<tr>
		<th class="required"> Formato do Relatório: </th>
		<td>
			<h:selectOneRadio value="#{relatorioCursosPos.formato}">
				<f:selectItem itemValue="pdf" itemLabel="PDF" />
				<f:selectItem itemValue="xls" itemLabel="XLS (Excel)" />
				<f:selectItem itemValue="html" itemLabel="HTML" />
			</h:selectOneRadio>
		</td>
	</tr>

	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton action="#{relatorioCursosPos.gerarRelatorio}" value="Emitir Relatório"/>
			<h:commandButton action="#{relatorioCursosPos.cancelar}" value="Cancelar"/>
		</td>
	</tr>
	</tfoot>
</h:form>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
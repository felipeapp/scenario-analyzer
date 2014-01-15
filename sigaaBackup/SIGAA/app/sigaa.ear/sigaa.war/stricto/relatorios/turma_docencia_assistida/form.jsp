<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:outputText value="#{relatorioTurmasDocenciaAssistida.create}" />
<h2> <ufrn:subSistema /> &gt; Relatório de Turmas de Docência Assistida </h2>

<h:form id="form">
<table class="formulario" style="width: 95%">
	<caption> Informe os Critérios Para a Emissão do Relatório </caption>

	<%--
	<tr>
		<th width="30%">Programa: </th>
		<td>
			<h:selectOneMenu id="departamento" value="#{relatorioTurmasDocenciaAssistida.idDepartamento}">
				<f:selectItem itemLabel=" Todos " itemValue="-1"/>
				<f:selectItems value="#{unidade.allDeptoCombo}"/>
			</h:selectOneMenu>
		</td>
	</tr>
	--%>
	
	<tr>
		<th>Ano.Período:</th>
		<td>
			<h:inputText value="#{relatorioTurmasDocenciaAssistida.ano}" id="txtAno" maxlength="4" size="5" onkeyup="return formatarInteiro(this);"/> . 
			<h:inputText value="#{relatorioTurmasDocenciaAssistida.periodo}" id="txtPeriodo" maxlength="1" size="2" onkeyup="return formatarInteiro(this);"/>
		</td>
	</tr>
	
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton action="#{relatorioTurmasDocenciaAssistida.gerarRelatorio}" value="Emitir Relatório" id="btnGerar"/>
			<h:commandButton action="#{relatorioTurmasDocenciaAssistida.cancelar}" onclick="#{confirm}" value="Cancelar" id="btnCancelar"/>
		</td>
	</tr>
	</tfoot>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
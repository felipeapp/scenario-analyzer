<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:outputText value="#{relatoriosPlanejamento.create}" />

<h:form id="form">
<h2> ${relatoriosPlanejamento.tituloRelatorio} </h2>
<h:inputHidden value="#{relatoriosPlanejamento.tituloRelatorio}"/>
<h:inputHidden value="#{relatoriosPlanejamento.nomeRelatorio}"/>
<h:inputHidden value="#{relatoriosPlanejamento.origemDados}"/>
<h:inputHidden value="#{relatoriosPlanejamento.validaAno}" />
<h:inputHidden value="#{relatoriosPlanejamento.validaMes}" />
<table class="formulario" style="width: 95%">
<caption> Informe os critérios para a emissão do relatório </caption>

	<tr>
		<th class="obrigatorio"> Mês: </th>
		<td>
			<h:selectOneMenu id="mes" value="#{relatoriosPlanejamento.mes}">
				<f:selectItems value="#{relatoriosPlanejamento.meses}"/>
			</h:selectOneMenu>
		</td>
	</tr>

	<tr>
		<th class="obrigatorio"> Ano: </th>
		<td>
			<h:inputText id="ano" value="#{relatoriosPlanejamento.ano}" size="4" maxlength="4" onkeyup="formatarInteiro(this)"/>
		</td>
	</tr>

	<tr>
		<th class="obrigatorio"> Formato do Relatório: </th>
		<td>
			<h:selectOneRadio value="#{relatoriosPlanejamento.formato}">
				<f:selectItem itemValue="pdf" itemLabel="PDF" />
				<f:selectItem itemValue="xls" itemLabel="XLS (Excel)" />
				<f:selectItem itemValue="html" itemLabel="HTML" />
			</h:selectOneRadio>
		</td>
	</tr>

	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton action="#{relatoriosPlanejamento.gerarRelatorio}" value="Emitir Relatório"/>
			<h:commandButton action="#{relatoriosPlanejamento.cancelar}" value="Cancelar" onclick="#{confirm}"/>
		</td>
	</tr>
	</tfoot>
</h:form>
</table>
<div class="obrigatorio" style="width: 50%">Campo de preenchimento obrigatório.</div>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
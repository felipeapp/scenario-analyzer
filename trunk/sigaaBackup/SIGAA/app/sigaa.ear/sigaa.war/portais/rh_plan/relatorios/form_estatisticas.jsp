<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:outputText value="#{relatoriosPlanejamento.create}" />

<h:form id="form">
<h2> <ufrn:subSistema /> > ${relatoriosPlanejamento.tituloRelatorio} </h2>
<h:inputHidden value="#{relatoriosPlanejamento.tituloRelatorio}"/>
<h:inputHidden value="#{relatoriosPlanejamento.nomeRelatorio}"/>
<h:inputHidden value="#{relatoriosPlanejamento.validaUnidade}" />
<h:inputHidden value="#{relatoriosPlanejamento.validaAno}" />
<table class="formulario" style="width: 95%">
<caption> Informe os critérios para a emissão do relatório </caption>

	<tr>
		<th class="obrigatorio"> Unidade: </th>
		<td>
			<h:selectOneMenu id="centro" value="#{relatoriosPlanejamento.unidade.id}">
				<f:selectItem itemValue="-1" itemLabel="UFRN"/>
				<f:selectItems value="#{unidade.allCentrosEscolasCombo}"/>
			</h:selectOneMenu>
		</td>
	</tr>

	<tr>
		<th class="obrigatorio"> Ano: </th>
		<td>
			<h:inputText id="ano" value="#{relatoriosPlanejamento.ano}" size="4" maxlength="4" onkeyup="formatarInteiro(this)" converter="#{ intConverter }"/>
		</td>
	</tr>

	<tr>
		<th class="obrigatorio"> Formato do Relatório: </th>
		<td>
			<h:selectOneRadio value="#{relatoriosPlanejamento.formato}" id="formato">
				<f:selectItem itemValue="pdf" itemLabel="PDF" />
				<f:selectItem itemValue="xls" itemLabel="XLS (Excel)" />
				<f:selectItem itemValue="html" itemLabel="HTML" />
			</h:selectOneRadio>
		</td>
	</tr>

	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton action="#{relatoriosPlanejamento.gerarRelatorio}" value="Emitir Relatório" id="gerar"/>
			<h:commandButton action="#{relatoriosPlanejamento.cancelar}" value="Cancelar" onclick="#{confirm}" id="cancelar"/>
		</td>
	</tr>
	</tfoot>
</table>
<div class="obrigatorio" style="width: 100%">Campo de preenchimento obrigatório.</div>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
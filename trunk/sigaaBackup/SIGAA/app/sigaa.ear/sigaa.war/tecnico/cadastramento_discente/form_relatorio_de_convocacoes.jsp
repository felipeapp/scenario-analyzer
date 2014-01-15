<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript">
function mudarTodos(status) {
	var re= new RegExp('selectCompareceu', 'g')
	var elements = document.getElementsByTagName('select');
	for (i = 0; i < elements.length; i++) {
		if (elements[i].id.match(re)) {
			elements[i].selectedIndex = status.selectedIndex;
		}
	}
}
</script>
<f:view>
<a4j:keepAlive beanName="cadastramentoDiscenteTecnico"></a4j:keepAlive>
<h2><ufrn:subSistema /> &gt; Relatório de Convocação</h2>
<div class="descricaoOperacao">
</div>

<h:form id="form">
<table class="formulario" width="80%">
	<caption>Filtros do Relatório</caption>
	<tr>
		<th class="obrigatorio" width="25%">Processo Seletivo:</th>
		<td>
			<h:selectOneMenu id="selectPsVestibular" value="#{cadastramentoDiscenteTecnico.processoSeletivo.id}">
				<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
				<f:selectItems id="itensPsVestibular" value="#{convocacaoProcessoSeletivoTecnico.processosCombo}" />
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<th width="25%">Convocação:</th>
		<td>
			<h:selectOneMenu id="opcao" value="#{cadastramentoDiscenteTecnico.idConvocacao}">
				<f:selectItem itemValue="0" itemLabel="-- TODAS --" />
				<f:selectItems value="#{cadastramentoDiscenteTecnico.convocacoesCombo}" />
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<th width="25%">Polo / Grupo:</th>
		<td>
			<h:selectOneMenu id="selectOpcao" value="#{cadastramentoDiscenteTecnico.opcao}">
				<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
				<f:selectItems value="#{convocacaoProcessoSeletivoTecnico.polosCombo}" />
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<th width="25%">Formato:</th>
		<td>
			<h:selectOneRadio id="formato" value="#{cadastramentoDiscenteTecnico.formatoRelatorio}">
				<f:selectItem itemValue="html" itemLabel="HTML" />
				<f:selectItem itemValue="pdf" itemLabel="PDF" />
				<f:selectItem itemValue="xls" itemLabel="EXCEL" />
			</h:selectOneRadio>
		</td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton id="btnBuscar" value="Buscar Discentes" action="#{cadastramentoDiscenteTecnico.gerarRelatorioDeConvocacao}"/>
				<h:commandButton value="Cancelar" action="#{ cadastramentoDiscenteTecnico.cancelar }" id="btnCancelar" onclick="#{confirm}" immediate="true"/>
			</td>
		</tr>
	</tfoot>
</table>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
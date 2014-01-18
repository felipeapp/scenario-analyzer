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
<h2><ufrn:subSistema /> &gt; Relatório Geral de Vagas e Convocados</h2>


<h:form prependId="false">
<table class="formulario" width="80%">
	<caption>Filtros do Relatório</caption>
	<tr>
		<th class="obrigatorio" width="25%">Processo Seletivo:</th>
		<td>
			<h:selectOneMenu id="Processo" value="#{cadastramentoDiscenteTecnico.processoSeletivo.id}">
				<f:selectItem itemValue="" itemLabel="-- SELECIONE --" />
				<f:selectItems id="itensPsVestibular" value="#{convocacaoProcessoSeletivoTecnico.processosCombo}" />
			</h:selectOneMenu>
		</td>
	</tr>
	
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton id="btnBuscar" value="Buscar Discentes" action="#{cadastramentoDiscenteTecnico.gerarRelatorioVagasConvocados}"/>
				<h:commandButton value="Cancelar" action="#{ cadastramentoDiscenteTecnico.cancelar }" id="btnCancelar" onclick="#{confirm}" immediate="true"/>
			</td>
		</tr>
	</tfoot>
</table>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
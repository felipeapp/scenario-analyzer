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
<a4j:keepAlive beanName="cadastramentoDiscente"></a4j:keepAlive>

<h2><ufrn:subSistema /> &gt; Encerramento do Cadastramento de Discentes</h2>
<div class="descricaoOperacao">
	<p>Caro Usu�rio,</p>
		<p>
			Esta opera��o processar� todas as convoca��es de candidatos com
			status <b>PR�-CADASTRADO</b> cancelando sua convoca��o e encerrando definitivamente o cadastramento de discentes.
		</p>
	</div>

<h:form id="form">

<table class="formulario" width="80%">
	<caption>Dados da Convoca��o</caption>
	<tr>
		<th class="obrigatorio">Processo Seletivo:</th>
		<td>
			<h:selectOneMenu id="selectPsVestibular" value="#{convocacaoVagasRemanescentesVestibularMBean.obj.processoSeletivo.id}" onchange="submit()"
				valueChangeListener="#{ convocacaoVagasRemanescentesVestibularMBean.processoSeletivoListener }">
				<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
				<f:selectItems id="itensPsVestibular" value="#{convocacaoVagasRemanescentesVestibularMBean.processoSeletivoVestibularCombo}" />
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<th class="rotulo"> Estrat�gia de convoca��o:
		</th>
		<td>
			<h:outputText value="#{convocacaoVagasRemanescentesVestibularMBean.obj.processoSeletivo.estrategiaConvocacao.class.simpleName}"/>
			<h:outputText value="N�o definido" rendered="#{ empty convocacaoVagasRemanescentesVestibularMBean.obj.processoSeletivo.estrategiaConvocacao.class.simpleName}"/>
		</td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton id="btnBuscar" value="Buscar Convocados" action="#{convocacaoVagasRemanescentesVestibularMBean.buscarEncerramento}"/>
				<h:commandButton value="Cancelar" action="#{ convocacaoVagasRemanescentesVestibularMBean.cancelar }" id="btnCancelar" onclick="#{confirm}" immediate="true"/>
			</td>
		</tr>
	</tfoot>
</table>
<br/>
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</h:form>


</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
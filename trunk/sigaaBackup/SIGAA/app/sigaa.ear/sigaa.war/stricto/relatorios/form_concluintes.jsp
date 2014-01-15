<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:outputText value="#{relatorioConcluintesPosBean.create}" />

<h2> <ufrn:subSistema></ufrn:subSistema> &gt; Relatório Quantitativo de Alunos Concluintes </h2>

<h:form id="form">
<table class="formulario" style="width: 95%">
<caption> Informe os critérios para a emissão do relatório </caption>

	<h:inputHidden value="#{relatorioConcluintesPosBean.nomeRelatorio}"/>

	<tr>
		<th class="obrigatorio">Ano: </th>
		<td>
		<h:inputText value="#{relatorioConcluintesPosBean.ano}" size="4" maxlength="4" onkeyup="formatarInteiro(this);"/>
		</td>
	</tr>
	
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton action="#{relatorioConcluintesPosBean.gerarRelatorio}" value="Emitir Relatório"/>
			<h:commandButton action="#{relatorioConcluintesPosBean.cancelar}" value="Cancelar" onclick="#{confirm}"/>
		</td>
	</tr>
	</tfoot>
</table>
</h:form>
<br />
<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
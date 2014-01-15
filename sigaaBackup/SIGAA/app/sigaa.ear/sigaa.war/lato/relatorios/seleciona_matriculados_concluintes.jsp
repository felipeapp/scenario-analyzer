<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{relatoriosLato.create}"></h:outputText>
<h2><ufrn:subSistema /> &gt; Quantitativo dos Alunos Matriculados e dos Alunos Concluídos por Centro</h2>

<h:form id="form">
<table align="center" class="formulario" width="50%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<th class="required">Ano-Período:</th>
		<td>
			<h:inputText id="ano" value="#{relatoriosLato.ano}" size="4" maxlength="4" onkeyup="formatarInteiro(this)"/> -
			<h:inputText id="periodo" value="#{relatoriosLato.periodo}" size="1" maxlength="1" onkeyup="formatarInteiro(this)"/>
		</td>
	</tr>
	<tfoot>
	<tr>
		<td colspan="2" align="center">
			<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
				action="#{relatoriosLato.gerarRelatorioMatriculadosEConcluintes}"/> <h:commandButton
				value="Cancelar" action="#{relatoriosLato.cancelar}" id="cancelar" /></td>
	</tr>
	</tfoot>
</table>
<br/>
<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{relatoriosTecnico.create}"></h:outputText>
<h2><ufrn:subSistema /> &gt; Lista de Alunos que Concluíram Programa</h2>

<h:form id="form">
<table align="center" class="formulario" width="75%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<th class="obrigatorio">Ano-Período de Saída: </th>

		<td>
			<h:inputText id="ano" value="#{relatoriosTecnico.ano}" size="4" maxlength="4" onkeyup="formatarInteiro(this);"/> -
			<h:inputText id="periodo" value="#{relatoriosTecnico.periodo}" size="1" maxlength="1" onkeyup="formatarInteiro(this);"/>
		</td>

	</tr>
	<tr>
		<th>Curso:</th>

		<td>
			<h:selectOneMenu id="curso" value="#{relatoriosTecnico.idCurso}">
				<f:selectItem itemLabel="-- TODOS --" itemValue="0"/>
				<f:selectItems value="#{relatoriosTecnico.cursosCombo}"/>
			</h:selectOneMenu>
		</td>

	</tr>
	<tfoot>
		<tr>
			<td colspan="2" align="center">
				<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatoriosTecnico.gerarRelatorioConcluidos}"/> 
				<h:commandButton value="Cancelar" action="#{relatoriosTecnico.cancelar}" id="cancelar" onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>

</table>
</h:form>
<br/>
<div class="obrigatorio">Campos de preenchimento obrigatório.</div>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
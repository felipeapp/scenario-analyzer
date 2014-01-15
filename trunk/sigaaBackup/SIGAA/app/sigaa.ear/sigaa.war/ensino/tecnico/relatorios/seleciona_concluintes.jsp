<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{relatoriosTecnico.create}"></h:outputText>
<h2><ufrn:subSistema /> &gt; Lista de Alunos Concluintes</h2>

<h:form id="form">
<table align="center" class="formulario" width="65%">
	<caption class="listagem">Dados do Relatório</caption>
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
			<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatoriosTecnico.gerarRelatorioConcluintes}"/> 
			<h:commandButton value="Cancelar" action="#{relatoriosTecnico.cancelar}" id="cancelar" onclick="#{confirm}"/>
		</td>
	</tr>
	</tfoot>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
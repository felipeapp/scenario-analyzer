<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h:outputText value="#{relatoriosTecnico.create}"></h:outputText>
<h2><ufrn:subSistema /> > Relatório por Orientador de Turma</h2>

<h:form id="form">
<table align="center" class="formulario" width="40%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<th width="53%">Ano-Período:</th>
		<td>
			<h:inputText id="ano" value="#{relatoriosTecnico.ano}" size="4" maxlength="4"/>
		</td>
	</tr>
<tfoot>
	<tr>
		<td colspan="2" align="center">
			<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatoriosTecnico.gerarRelatorioOrientadoresPorTurma}"/> 
			<h:commandButton value="Cancelar" action="#{relatoriosTecnico.cancelar}" id="cancelar" onclick="#{confirm}"/>
		</td>
	</tr>
</tfoot>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
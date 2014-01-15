<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h:outputText value="#{relatoriosLato.create}"></h:outputText>
<h2><ufrn:subSistema /> > Orientadores de Trabalho Final de Curso</h2>

<h:form id="form">
<table align="center" class="formulario" width="75%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<th>Curso:</th>

		<td>
			<h:selectOneMenu id="curso" value="#{relatoriosLato.idCurso}">
				<f:selectItem itemLabel="-- TODOS --" itemValue="-1"/>
				<f:selectItems value="#{relatoriosLato.cursosCombo}"/>
			</h:selectOneMenu>
		</td>

	</tr>
	<tr>
		<td colspan="2" align="center">
			<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
				action="#{relatoriosLato.gerarRelatorioOrientadoresTcc}"/> <h:commandButton
				value="Cancelar" action="#{relatoriosLato.cancelar}" id="cancelar" onclick="#{confirm}" /></td>
	</tr>

</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
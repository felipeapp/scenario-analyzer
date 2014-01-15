<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{realtorioDiscente.create}"></h:outputText>
<h2><ufrn:subSistema /> > Relatório Quantitativo de Alunos Reprovados e Desnivelados</h2>

<h:form id="form">
<table align="center" class="formulario" width="80%">
	<caption class="listagem">Dados do Relatório</caption>
	<tbody>
		<tr>
			<th>Unidade das Disciplinas:</th>
			<td><h:selectOneMenu value="#{relatorioDiscente.centro.id}" id="centroDisciplinas">
				<f:selectItem itemValue="0" itemLabel="--> TODOS <--" />
				<f:selectItems value="#{unidade.allCentrosUnidAcademicaGraduacaoTecnicoCombo}" />
			</h:selectOneMenu></td>
		</tr>
		<tr>
			<th>Unidade dos Alunos:</th>
			<td><h:selectOneMenu value="#{relatorioDiscente.departamento.id}" id="centroAluno">
				<f:selectItem itemValue="0" itemLabel="--> TODOS <--" />
				<f:selectItems value="#{unidade.allCentrosUnidAcademicaGraduacaoTecnicoCombo}" />
			</h:selectOneMenu></td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3" align="center">
				<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatorioDiscente.gerarRelatorioQuantitativoReprovadosDesnivelados}"/> 
				<h:commandButton value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" />
			</td>
		</tr>
	</tfoot>

</table>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
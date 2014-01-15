<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{realtorioDiscente.create}"></h:outputText>
<h2><ufrn:subSistema /> > Relatório dos Ingressantes sem Solicitação de Matrícula</h2>

<h:form id="form">
<table align="center" class="formulario" width="45%">
	<caption class="listagem">Dados do Relatório</caption>
	<tbody>
		<tr>
			<th>Ano-Período: </th>
			<td>
				<h:selectOneMenu value="#{relatorioDiscenteMatricula.ano}" id="ano">
					<f:selectItems value="#{relatorioDiscenteMatricula.anos}" />
				</h:selectOneMenu>-
				
				<h:selectOneMenu value="#{relatorioDiscenteMatricula.periodo}" id="periodo">
					<f:selectItems value="#{relatorioDiscenteMatricula.periodos}" />
				</h:selectOneMenu>
			</td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3" align="center">
				<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatorioDiscenteMatricula.alunosIngressantesSemMatricula}"/> 
				<h:commandButton value="Cancelar" action="#{relatorioDiscenteMatricula.cancelar}" id="cancelar" onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>
</table>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{realtorioDiscente.create}"></h:outputText>
<h2><ufrn:subSistema /> > Quantitativo de Discentes Regulares por Gênero e Curso</h2>

<h:form id="form">
<table align="center" class="formulario" width="45%">
	<caption class="listagem">Dados do Relatório</caption>
	<tbody>
		<tr>
			<th class="required">Campus:</th>
			<td><h:selectOneMenu value="#{relatorioDiscente.campus.id}" id="municipio">
				<f:selectItem itemValue="0" itemLabel="--> TODOS <--" />
				<f:selectItems value="#{campusIes.allCampusCombo}" />
			</h:selectOneMenu></td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3" align="center">
				<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatorioDiscente.totalAlunosRegularesPorCurso}"/> 
				<h:commandButton value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>
</table>

	<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
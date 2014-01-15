<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{relatorioTurma.create}"></h:outputText>
<h2><ufrn:subSistema /> > Relatório de Disciplinas de Estágio</h2>

<h:form id="form">
<table align="center" class="formulario" width="100%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<td><h:selectBooleanCheckbox styleClass="noborder"  onclick="$('form:ano').disabled = !this.checked; $('form:periodo').disabled = !this.checked;"
		       value="#{relatorioTurma.filtroAnoPeriodo}" id="checkAnoPeriodo" /></td>
		<td width="10%" nowrap="nowrap"><label for="form:checkAnoPeriodo">Ano-Período</label></td>
		<td><h:selectOneMenu value="#{relatorioTurma.ano}"
					id="ano" onfocus="$('form:checkAnoPeriodo').checked = true;">
					<f:selectItems value="#{relatorioTurma.anos}" />
				</h:selectOneMenu>-<h:selectOneMenu value="#{relatorioTurma.periodo}"
					id="periodo" onfocus="$('form:checkAnoPeriodo').checked = true;">
					<f:selectItems value="#{relatorioTurma.periodos}" />
				</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<td><h:selectBooleanCheckbox styleClass="noborder" value="#{relatorioTurma.filtroDepartamento}" id="checkDepartamento"/></td>
		<td><label for="form:checkDepartamento">Departamento</label></td>
		<td><h:selectOneMenu id="departamento" value="#{relatorioTurma.departamento.id}" onfocus="$('form:checkDepartamento').checked = true;">
				<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
				<f:selectItems value="#{unidade.allDepartamentoCombo}" />
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<td colspan="3" align="center">
						<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
						action="#{relatorioTurma.gerarRelatorioListaTurmasChEstagio}"/> <h:commandButton
						value="Cancelar" action="#{relatorioTurma.cancelar}" id="cancelar" /></td>
	</tr>

</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
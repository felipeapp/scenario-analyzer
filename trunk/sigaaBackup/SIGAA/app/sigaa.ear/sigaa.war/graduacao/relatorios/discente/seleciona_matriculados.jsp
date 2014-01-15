<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{relatorioDiscente.create}"></h:outputText>
<h2><ufrn:subSistema /> > Lista de alunos matriculados em um determinado período</h2>

<h:form id="form">
<table align="center" class="formulario" width="100%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<td></td>
		<td>Ano-Período</td>
		<td><h:selectOneMenu value="#{relatorioDiscente.ano}" id="ano">
				<f:selectItems value="#{relatorioDiscente.anos}" />
		</h:selectOneMenu>-<h:selectOneMenu value="#{relatorioDiscente.periodo}" id="periodo">
					<f:selectItems value="#{relatorioDiscente.periodos}" />
		</h:selectOneMenu></td>
	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{relatorioDiscente.filtroAtivo}" style="border: none;" id="checkAtivos"/></td>
		<td colspan="2" align="left"><label for="form:checkAtivos">Somente Ativos</label></td>
	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{relatorioDiscente.filtroCurso}" style="border: none;" id="checkCurso" /></td>
		<td><label for="form:checkCurso">Curso</label></td>
		<td><h:selectOneMenu value="#{relatorioDiscente.matrizCurricular.curso.id}" id="curso" onfocus="$('form:checkCurso').checked = true;"
				onchange="submit()" valueChangeListener="#{curriculo.carregarMatrizes}">
			<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
			<f:selectItems value="#{cursoGrad.allCombo}" />
		</h:selectOneMenu></td>
	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{relatorioDiscente.filtroMatriz}" style="border: none;" id="checkMatriz" /></td>
		<td><label for="form:checkMatriz">Matriz Curricular</label></td>
		<td><h:selectOneMenu id="matriz" value="#{relatorioDiscente.idMatrizCurricular }" onfocus="$('form:checkMatriz').checked = true;">
			<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
			<f:selectItems value="#{curriculo.possiveisMatrizes}" />
		</h:selectOneMenu></td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="3" align="center">
				<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatorioDiscente.gerarRelatorioListaAlunoMatriculados}"/> 
				<h:commandButton value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" onclick="#{confirm}" immediate="true"/>
			</td>
		</tr>
	</tfoot>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
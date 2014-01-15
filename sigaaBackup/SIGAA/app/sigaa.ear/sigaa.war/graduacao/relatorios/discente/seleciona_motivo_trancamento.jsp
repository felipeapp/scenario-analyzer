<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{realtorioDiscente.create}"></h:outputText>
<h2><ufrn:subSistema /> > Relatório de Alunos pelo seu tipo de saída, seja ela temporária ou não</h2>

<h:form id="form">
<table align="center" class="formulario" width="100%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<td><h:selectBooleanCheckbox value="#{relatorioDiscente.filtroAnoPeriodo}" id="checkAnoPeriodo"/></td>
		<td><label for="form:checkAnoPeriodo">Ano-Período:</label></td>
		<td><h:selectOneMenu value="#{relatorioDiscente.ano}" onfocus="$('form:checkAnoPeriodo').checked = true"
					id="ano">
					<f:selectItems value="#{relatorioDiscente.anos}" />
				</h:selectOneMenu>-<h:selectOneMenu value="#{relatorioDiscente.periodo}" onfocus="$('form:checkAnoPeriodo').checked = true"
					id="periodo">
					<f:selectItems value="#{relatorioDiscente.periodos}" />
				</h:selectOneMenu></td>

	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{relatorioDiscente.filtroMotivoTrancamento}" id="checkMotivoTrancamento"/></td>
		<td><label for="form:checkMotivoTrancamento">Motivo do Trancamento:</label></td>
		<td><h:selectOneMenu value="#{relatorioDiscente.motivoTrancamento.id}" onfocus="$('form:checkMotivoTrancamento').checked = true;"
					id="motivoTrancamento">
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
					<f:selectItems value="#{motivoTrancamento.allCombo}" />
				</h:selectOneMenu></td>
	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{relatorioDiscente.filtroCentro}" id="checkCentro"/></td>
		<td><label for="form:checkCentro">Unidade:</label></td>
		<td><h:selectOneMenu value="#{relatorioDiscente.matrizCurricular.curso.unidade.id}" id="centro"
				onchange=" $('form:checkCentro').checked = true; submit();" immediate="true" valueChangeListener="#{curriculo.carregarCursosCentro}">
			<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
			<f:selectItems value="#{unidade.allCentrosEscolasCombo}" />
		</h:selectOneMenu></td>
	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{relatorioDiscente.filtroCurso}" id="checkCurso"/></td>
		<td><label for="form:checkCurso">Curso:</label></td>
		<td><h:selectOneMenu value="#{relatorioDiscente.idCurso}" id="curso"
				onchange="$('form:checkCurso').checked = true; submit();" immediate="true" valueChangeListener="#{curriculo.carregarMatrizes}">
			<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
			<f:selectItems value="#{curriculo.possiveisCursos}" />
		</h:selectOneMenu></td>
	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{relatorioDiscente.filtroMatriz}" id="checkMatriz"/></td>
		<td><label for="form:checkMatriz">Matriz Curricular:</label></td>
		<td><h:selectOneMenu id="matriz" value="#{relatorioDiscente.idMatrizCurricular}" onfocus="$('form:checkMatriz').checked = true;">
			<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
			<f:selectItems value="#{curriculo.possiveisMatrizes}" />
		</h:selectOneMenu></td>
	</tr>
	<tr>
		<td colspan="3" align="center">
						<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
						action="#{relatorioDiscente.gerarRelatorioListaAlunoMotivoTrancamento}"/> <h:commandButton
						value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" /></td>
	</tr>
</table>
<script>

	if($('form:checkCentro').checked && $('form:checkCentro').value !=0 ){
		$('form:checkCurso').disabled = false;
		$('form:curso').disabled = false;
		if($('form:checkCurso').checked && $('form:checkCurso').value !=0){
			$('form:checkMatriz').disabled = false;
			$('form:matriz').disabled = false;
		} else {
			$('form:checkMatriz').disabled = true;
			$('form:matriz').disabled = true;
		}
	} else {
		$('form:checkCurso').disabled = true;
		$('form:checkMatriz').disabled = true;
		$('form:curso').disabled = true;
		$('form:matriz').disabled = true;
	}

</script>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{relatorioDiscente.create}"></h:outputText>
<h2><ufrn:subSistema /> > Relatório do Índice de trancamentos e cancelamentos de matrícula em componentes curriculares</h2>

<h:form id="form">
<table align="center" class="formulario" width="100%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<td></td>
		<td>Ano-Período Conclusão: </td>
		<td><h:selectOneMenu value="#{relatorioCurso.ano}"
					id="ano">
					<f:selectItems value="#{relatorioCurso.anos}" />
				</h:selectOneMenu>-<h:selectOneMenu value="#{relatorioCurso.periodo}"
					id="periodo">
					<f:selectItems value="#{relatorioCurso.periodos}" />
				</h:selectOneMenu> <h:selectBooleanCheckbox id="checkAnoPeriodo" value="#{relatorioCurso.filtroAnoPeriodo}" onclick="desabilitarAnoSemestre()" /> Todos Períodos</td>
	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{relatorioCurso.filtroUnidade}" id="checkCentro"/></td>
		<td><label for="form:checkCentro">Unidade</label></td>
		<td><h:selectOneMenu value="#{relatorioCurso.curso.unidade.id}" id="centro"
				onchange=" $('form:checkCentro').checked = true; submit();" immediate="true" valueChangeListener="#{curriculo.carregarCursosCentro}">
			<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
			<f:selectItems value="#{unidade.allCentrosUnidAcademicaGraduacaoTecnicoCombo}" />
		</h:selectOneMenu></td>
	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{relatorioCurso.filtroCurso}" id="checkCurso"/></td>
		<td><label for="form:checkCurso">Curso</label></td>
		<td><h:selectOneMenu value="#{relatorioCurso.idCurso}" id="curso"
				onchange="$('form:checkCurso').checked = true; submit();" immediate="true" valueChangeListener="#{curriculo.carregarMatrizes}">
			<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
			<f:selectItems value="#{curriculo.possiveisCursos}" />
		</h:selectOneMenu></td>
	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{relatorioCurso.filtroMatriz}" id="checkMatriz"/></td>
		<td><label for="form:checkMatriz">Matriz Curricular</label></td>
		<td><h:selectOneMenu id="matriz" value="#{relatorioCurso.idMatrizCurricular}" onfocus="$('form:checkMatriz').checked = true;">
			<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
			<f:selectItems value="#{curriculo.possiveisMatrizes}" />
		</h:selectOneMenu></td>
	</tr>	<tr>
		<td colspan="4" align="center">
						<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
						action="#{relatorioCurso.gerarRelatorioListaCursoIndiceTrancamento}"/> <h:commandButton
						value="Cancelar" action="#{relatorioCurso.cancelar}" id="cancelar" /></td>
	</tr>

</table>
</h:form>
<script language="javascript">

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

	function desabilitarAnoSemestre(){
		if($('form:todos').checked){
			$('form:ano').disabled = true;
			$('form:periodo').disabled= true;
		}else {
			$('form:ano').disabled  = false;
			$('form:periodo').disabled  = false;
		}
	}
</script>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%></h2>

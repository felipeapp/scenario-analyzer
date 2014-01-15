<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{realtorioDocente.create}"></h:outputText>
<h2><ufrn:subSistema /> > Lista de Discentes que ingressaram em um determinado ano semestre</h2>

<h:form id="form">
<table align="center" class="formulario" width="100%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<td></td>
		<td>Ano-Período: </td>
		<td><h:selectOneMenu value="#{relatorioDiscente.ano}"
					id="ano">
					<f:selectItems value="#{relatorioDiscente.anos}" />
				</h:selectOneMenu>-<h:selectOneMenu value="#{relatorioDiscente.periodo}"
					id="periodo">
					<f:selectItems value="#{relatorioDiscente.periodos}" />
				</h:selectOneMenu></td>

	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{relatorioDiscente.filtroCentro}" id="checkCentro"/></td>
		<td><label for="form:checkCentro">Unidade</label></td>
		<td><h:selectOneMenu value="#{relatorioDiscente.centro.id}" id="centro"
				onchange=" $('form:checkCentro').checked = true; submit();" immediate="true" valueChangeListener="#{curriculo.carregarCursosCentro}">
			<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
			<f:selectItems value="#{unidade.centrosEspecificasEscolas}" />
		</h:selectOneMenu></td>
	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{relatorioDiscente.filtroCurso}" id="checkCurso"/></td>
		<td><label for="form:checkCurso">Curso</label></td>
		<td><h:selectOneMenu value="#{relatorioDiscente.idCurso}" id="curso"
				onchange="$('form:checkCurso').checked = true; submit();" immediate="true" valueChangeListener="#{relatorioDiscente.carregarMatrizes}">
			<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
			<f:selectItems value="#{curriculo.possiveisCursos}" />
		</h:selectOneMenu></td>
	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{relatorioDiscente.filtroMatriz}" id="checkMatriz"/></td>
		<td><label for="form:checkMatriz">Matriz Curricular</label></td>
		<td><h:selectOneMenu id="matriz" value="#{relatorioDiscente.idMatrizCurricular}" immediate="true" onfocus="$('form:checkMatriz').checked = true;">
			<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
			<f:selectItems value="#{curriculo.possiveisMatrizes}" />
		</h:selectOneMenu></td>
	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{relatorioDiscente.filtroIngresso}" id="checkFiltroIngresso"/></td>
		<td>Forma de Ingresso: </td>
		<td><h:selectOneMenu value="#{relatorioDiscente.formaIngresso.id}" onfocus="$('form:checkIngresso').checked = true;"
					id="tipoEntrada">
					<f:selectItem itemLabel="TODOS" itemValue="0"/>
					<f:selectItems value="#{formaIngresso.allCombo}" />
				</h:selectOneMenu></td>
	</tr>
	<tr>
		<td></td>
		<td>Status: </td>
		<td> <h:selectOneMenu value="#{relatorioDiscente.status}"
					id="departamento">
					<f:selectItem itemValue="0" itemLabel="TODOS" />
					<f:selectItems value="#{relatorioDiscente.statusDiscenteListaIngressantes}" />
				</h:selectOneMenu></td>
	</tr>
	<tr>
		<td colspan="3" align="center">
						<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
						action="#{relatorioDiscente.gerarRelatorioListaIngressante}"/> <h:commandButton
						value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" /></td>
	</tr>

</table>
</h:form>
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
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
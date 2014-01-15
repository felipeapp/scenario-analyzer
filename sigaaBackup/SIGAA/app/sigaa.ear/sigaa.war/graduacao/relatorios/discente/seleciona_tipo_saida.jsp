<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<% CheckRoleUtil.checkRole(request, response, new int[] {SigaaPapeis.DAE,  SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO , SigaaPapeis.PORTAL_PLANEJAMENTO} ); %>


<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{relatorioDiscente.create}"></h:outputText>
<h2><ufrn:subSistema /> > Relatório de Alunos pelo seu tipo de saída</h2>

<h:form id="form">
<table align="center" class="formulario" width="100%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<td></td>
		<td>Ano-Período Saída:</td>
		<td><h:selectOneMenu value="#{relatorioDiscente.ano}"
					id="ano">
					<f:selectItems value="#{relatorioDiscente.anos}" />
				</h:selectOneMenu>-<h:selectOneMenu value="#{relatorioDiscente.periodo}"
					id="periodo">
					<f:selectItems value="#{relatorioDiscente.periodos}" />
				</h:selectOneMenu></td>

	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{relatorioDiscente.filtroStatus}" id="checkStatus"/></td>
		<td><label for="form:checkStatus">Status dos Discentes:</label></td>
		<td>	
			<h:selectOneMenu value="#{relatorioDiscente.status}" id="status">
				<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
				<f:selectItems value="#{statusDiscente.allCombo}" />
			</h:selectOneMenu>
		</td>

	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{relatorioDiscente.filtroIngresso}" id="checkIngresso"/></td>
		<td><label for="form:checkIngresso">Tipo de Entrada (Ingresso):</label></td>
		<td><h:selectOneMenu value="#{relatorioDiscente.formaIngresso.id}" onfocus="$('form:checkIngresso').checked = true;"
					id="tipoEntrada">
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
					<f:selectItems value="#{formaIngresso.allCombo}" />
				</h:selectOneMenu></td>
	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{relatorioDiscente.filtroEgresso}" id="checkEgresso"/></td>
		<td><label for="form:checkEgresso">Tipo de Saída (Egresso):</label></td>
		<td><h:selectOneMenu value="#{relatorioDiscente.tipoMovimentacaoAluno.id}" onfocus="$('form:checkEgresso').checked = true;"
					id="tipoSaida">
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
					<f:selectItems value="#{tipoMovimentacaoAluno.allAtivosCombo}" />
				</h:selectOneMenu></td>
	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{relatorioDiscente.filtroUnidade}" id="checkCentro"/></td>
		<td><label for="form:checkCentro">Unidade:</label></td>
		<td><h:selectOneMenu value="#{relatorioDiscente.matrizCurricular.curso.unidade.id}" id="centro"
				onchange=" $('form:checkCentro').checked = true; submit();" immediate="true" valueChangeListener="#{curriculo.carregarCursosCentro}">
			<f:selectItem itemLabel="-- SELECIONE --"  itemValue="0"/>
			<f:selectItems value="#{unidade.allCentrosUnidAcademicaGraduacaoTecnicoCombo}" />
		</h:selectOneMenu></td>
	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{relatorioDiscente.filtroCurso}" id="checkCurso"/></td>
		<td><label for="form:checkCurso">Curso:</label></td>
		<td><h:selectOneMenu value="#{relatorioDiscente.idCurso}" id="curso"
				onchange="$('form:checkCurso').checked = true; submit();" immediate="true" valueChangeListener="#{curriculo.carregarMatrizes}">
			<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
			<f:selectItems value="#{curriculo.possiveisCursos}" />
		</h:selectOneMenu></td>
	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{relatorioDiscente.filtroMatriz}" id="checkMatriz"/></td>
		<td><label for="form:checkMatriz">Matriz Curricular:</label></td>
		<td><h:selectOneMenu id="matriz" value="#{relatorioDiscente.idMatrizCurricular}" onfocus="$('form:checkMatriz').checked = true;">
			<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
			<f:selectItems value="#{curriculo.possiveisMatrizes}" />
		</h:selectOneMenu></td>
	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{relatorioDiscente.filtroAfastamentoPermanente}" id="checkAfastamentoPermanente"/></td>
		<td colspan="2"><label for="checkAfastamentoPermanente">Somente afastamentos permamentes</label></td>
	</tr>	
	<tfoot>
		<tr>
		<td colspan="3" align="center">
						<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
						action="#{relatorioDiscente.gerarRelatorioListaAlunoTipoSaida}"/> <h:commandButton
						value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" /></td>
		</tr>
	</tfoot>
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
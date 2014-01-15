<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaSubsistemas"%>

<f:view>
<h:messages showDetail="true"/>
<h:outputText value="#{relatorioDiscente.create}"/>
<h2><ufrn:subSistema /> > Relatório de Alunos para Eleição</h2>
<h:form id="form">

<table align="center" class="formulario" width="100%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<td width="3%"><h:selectBooleanCheckbox styleClass="noborder" value="#{relatorioDiscente.filtroMatriculado}" id="checkMatriculado"/></td>
		<td colspan="2"><label for="form:checkMatriculado">Somente Matriculados no Período Atual</label></td>
	</tr>
	<c:if test="${nivel == 'G'}"> 
	<tr>
		<td><h:selectBooleanCheckbox styleClass="noborder" value="#{relatorioDiscente.filtroAtivo}" id="checkAtivos"/></td>
		<td colspan="2"><label for="form:checkAtivos">Incluir Formandos</label></td>
	</tr>
	</c:if>
	<c:choose>
		
		<c:when test="${!acesso.coordenadorCursoGrad && !acesso.secretarioGraduacao}">
		
			<c:if test="${nivel == 'G'}"> 
			<tr>
				<td><h:selectBooleanCheckbox styleClass="noborder" value="#{relatorioDiscente.filtroUnidade}" id="checkCentro" onclick="clickCheckCentro(); submit();"/></td>
				<td width="15%" nowrap="nowrap"><label for="form:checkCentro">Unidade:</label></td>
				<td><h:selectOneMenu value="#{relatorioDiscente.curso.unidade.id}" id="centro"
						onchange=" $('form:checkCentro').checked = true; submit();" style="width: 80%;" 
						immediate="true" valueChangeListener="#{curriculo.carregarCursosCentro}">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{unidade.allCentrosEscolasCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox styleClass="noborder" value="#{relatorioDiscente.filtroCurso}" id="checkCursoo" onclick="clickCheckCurso();"/></td>
				<td><label for="form:checkCursoo">Curso:</label></td>
				<td><h:selectOneMenu value="#{relatorioDiscente.idCurso}" id="cursoo" style="width: 80%;" 
						onchange=" $('form:checkCursoo').checked = true; zerarMatriz(); submit();" immediate="true" valueChangeListener="#{curriculo.carregarAllMatrizes}">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{curriculo.possiveisCursos}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox styleClass="noborder" value="#{relatorioDiscente.filtroMatriz}" id="checkMatriz"/></td>
				<td><label for="form:checkMatriz">Matriz Curricular:</label></td>
				<td><h:selectOneMenu id="matriz" value="#{relatorioDiscente.idMatrizCurricular}" 
					style="width: 80%;"  onfocus="$('form:checkMatriz').checked = true; $('form:checkCursoo').checked = true;">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{curriculo.possiveisMatrizes}" />
				</h:selectOneMenu></td>
			</tr>
			<script>
				if($('form:checkCentro').checked && $('form:checkCentro').value !=0 ){
					$('form:checkCurso').disabled = false;
					$('form:cursoo').disabled = false;
					if($('form:checkCursoo').checked && $('form:checkCurso').value !=0){
						$('form:checkMatriz').disabled = false;
						$('form:matriz').disabled = false;
					} else {
						$('form:checkMatriz').disabled = true;
						$('form:matriz').disabled = true;
					}
				} else {
					$('form:checkCursoo').disabled = true;
					$('form:cursoo').disabled = true;
					$('form:checkMatriz').disabled = true;
					$('form:matriz').disabled = true;
				}
				
				function zerarMatriz() {
					$('form:matriz').value = 0;
				}
				
				function clickCheckCentro() {
					$('form:cursoo').value = 0;
					$('form:checkCursoo').checked = false;
					clickCheckCurso();
				}
				
				function clickCheckCurso() {
					zerarMatriz();
					$('form:checkMatriz').checked = false;
				}
			</script>
			</c:if>
			
		<c:if test="${nivel == 'S' and relatorioDiscente.portalCoordenadorStricto}">
			<tr>
				<td></td>
				<td>Programa: </td>
				<td><b><h:outputText value="#{relatorioDiscente.programaStricto.sigla}"/> - <h:outputText value="#{relatorioDiscente.programaStricto.nome}"/></b></td>
			</tr>
			<tr>
			<!-- <h:selectBooleanCheckbox styleClass="noborder" value="#{relatorioDiscente.filtroCurso}" id="checkCurso"/> -->
				<td></td>
				<td ><label for="form:checkCurso" class="required">Curso:</label></td>
				<td><h:selectOneMenu value="#{relatorioDiscente.idCurso}" id="curso" 
					onchange=" $('form:checkCurso').checked = true;" immediate="true">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{relatorioDiscente.possiveisCursosCoordStricto}" />
				</h:selectOneMenu></td>
			</tr>
		</c:if>		
		
		<c:if test="${nivel == 'S' and relatorioDiscente.portalPpg}">	
			<tr>
				<td><h:selectBooleanCheckbox styleClass="noborder" value="#{relatorioDiscente.filtroUnidade}" id="checkPrograma"/></td>
				<td width="10%" nowrap="nowrap"><label for="form:checkPrograma">Programa:</label></td>
				<td><h:selectOneMenu value="#{relatorioDiscente.curso.unidade.id}" id="centro"
						onchange=" $('form:checkPrograma').checked = true; submit();" immediate="true" valueChangeListener="#{curriculo.carregarCursosPrograma}">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{unidade.allProgramaPosCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox styleClass="noborder" value="#{relatorioDiscente.filtroCurso}" id="checkCurso"/></td>
				<td><label for="form:checkCurso">Curso:</label></td>
				<td><h:selectOneMenu value="#{relatorioDiscente.idCurso}" id="curso" 
					onchange=" $('form:checkCurso').checked = true;" immediate="true">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{curriculo.possiveisCursos}" />
				</h:selectOneMenu></td>
			</tr>
			<script>
				if($('form:checkPrograma').checked && $('form:checkPrograma').value !=0 ){
					$('form:checkCurso').disabled = false;
					$('form:curso').disabled = false;
				} else {
					$('form:checkCurso').disabled = true;
					$('form:curso').disabled = true;
				}
			</script>
		</c:if>
		<c:if test="${acesso.lato and nivel == 'L'}">	
			<tr>
				<td>
					<h:selectBooleanCheckbox styleClass="noborder" value="#{relatorioDiscente.filtroCurso}" id="checkCurso"/>
				</td>
				<td>
					<label for="form:checkCurso">Curso:</label>
				</td>
				<td>
					<h:selectOneMenu value="#{relatorioDiscente.idCurso}" id="curso" onchange=" $('form:checkCurso').checked = true;" immediate="true">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{curso.allCursoEspecializacaoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
		</c:if>
		
		</c:when>
		
		<c:when test="${acesso.coordenadorCursoStricto or acesso.secretariaPosGraduacao}">
		
		</c:when>
		
		<c:when test="${acesso.ppg}">
		
		</c:when>
		
		<c:otherwise>
			<tr >
				<td></td>
				<td>Curso </td>
				<td><b><h:outputText value="#{relatorioDiscente.cursoAtualCoordenacao.unidade.sigla}"/> - <h:outputText value="#{relatorioDiscente.cursoAtualCoordenacao.nome}"/></b></td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox styleClass="noborder" value="#{relatorioDiscente.filtroMatriz}" id="checkMatriz"/></td>
				<td><label for="form:checkMatriz">Matriz Curricular:</label></td>
				<td><h:selectOneMenu id="matriz" value="#{relatorioDiscente.idMatrizCurricular}" onfocus="$('form:checkMatriz').checked = true;">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{relatorioDiscente.possiveisMatrizesCoord}" />
				</h:selectOneMenu></td>
			</tr>
			<script>
				if($('form:checkCurso').checked && $('form:checkCurso').value !=0){
						$('form:checkMatriz').disabled = false;
						$('form:matriz').disabled = false;
				} else {
						$('form:checkMatriz').disabled = true;
						$('form:matriz').disabled = true;
				}
			</script>
		</c:otherwise>
	</c:choose>
	<tfoot>
	<tr>
		<td colspan="3" align="center">
			<h:commandButton id="gerarRelatorio" value="Gerar Relatório"	
				action="#{relatorioDiscente.gerarRelatorioListaEleicao}"/> 
			<h:commandButton value="Cancelar" action="#{relatorioDiscente.cancelar}"
				 id="cancelar" onclick="return confirm('Deseja realmente cancelar a operação?')"/>
		</td>
	</tr>
	</tfoot>
</table>
</h:form>
</f:view>
	
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
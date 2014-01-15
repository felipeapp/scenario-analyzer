<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2><ufrn:subSistema /> > Relatório de Turmas</h2>

<h:form id="form">
<a4j:keepAlive beanName="relatorioTurma"  />
<table class="formulario" id="dadosRelatorio">
	<caption class="listagem">Dados do Relatório</caption>
	<tbody>
	<tr>
		<td width="5%">
			<h:selectBooleanCheckbox styleClass="noborder"  value="#{relatorioTurma.filtroAnoPeriodo}" id="checkAnoPeriodo" disabled="#{  relatorioTurma.campos.anoPeriodo }" />
		</td>
		<td width="15%" nowrap="nowrap">
			<label for="form:checkAnoPeriodo">Ano-Período:</label>
		</td>
		<td>
			<h:selectOneMenu value="#{relatorioTurma.ano}" id="ano" onfocus="$('form:checkAnoPeriodo').checked = true;" disabled="#{   relatorioTurma.campos.anoPeriodo }" >
				<f:selectItems value="#{relatorioTurma.anos}" />
			</h:selectOneMenu>
			.
			<h:selectOneMenu value="#{relatorioTurma.periodo}" id="periodo" onfocus="$('form:checkAnoPeriodo').checked = true;" disabled="#{   relatorioTurma.campos.anoPeriodo }">
				<f:selectItems value="#{relatorioTurma.periodos}" />
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<td>
			<h:selectBooleanCheckbox styleClass="noborder" value="#{relatorioTurma.filtroCentro}" id="checkCentro" />
		</td>
		<td>
			<label for="form:checkCentro">Unidade(Centro):</label>
		</td>
		<td>
			<h:selectOneMenu id="centro" style="width: 80%" value="#{relatorioTurma.centro.id}"
				onfocus="$('form:checkCentro').checked = true; $('form:checkDepartamento').checked = false;"
				valueChangeListener="#{ unidade.changeCentro }" immediate="true">
				<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
				<f:selectItems value="#{unidade.allCentrosEscolasCombo}" />
				<a4j:support event="onchange" reRender="departamentoPanel"/>
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<td>
			<h:selectBooleanCheckbox styleClass="noborder" value="#{relatorioTurma.filtroDepartamento}" id="checkDepartamento"/>
		</td>
		<td>
			<label for="form:checkDepartamento">Unidade(Departamento):</label>
		</td>
		<td>
			<h:panelGroup id="departamentoPanel">
				<c:if test="${relatorioTurma.centro.id == 0}">
					<%-- Todos departamentos --%>
					<h:selectOneMenu id="todosDepartamentos" value="#{relatorioTurma.departamento.id}"
						 onfocus="$('form:checkDepartamento').checked = true; $('form:checkCentro').checked = false;" 
						 style="width: 80%" >
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{unidade.allDetentorasComponentesCombo}" />
					</h:selectOneMenu>
				</c:if>
				<c:if test="${relatorioTurma.centro.id != 0}">
					<h:selectOneMenu id="departamentoCentro" value="#{relatorioTurma.departamento.id}" 
						onfocus="$('form:checkDepartamento').checked = true; $('form:checkCentro').checked = false;" 
						style="width: 80%">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{unidade.unidades}" />
					</h:selectOneMenu>
				</c:if>
				<a4j:status>
					<f:facet name="start">
						<h:graphicImage value="/img/indicator.gif" />
					</f:facet>
				</a4j:status>
			</h:panelGroup>
		</td>
	</tr>
	<tr>
		<td>
			<h:selectBooleanCheckbox styleClass="noborder" value="#{relatorioTurma.filtroSituacaoTurma}" id="checkSituacaoTurma" disabled="#{   relatorioTurma.campos.situacaoTurma }"/>
		</td>
		<td  nowrap="nowrap">
			<label for="form:checkSituacaoTurma">Situação da Turma:</label>
		</td>
		<td>
			<h:selectOneMenu id="situacaoTurma" value="#{relatorioTurma.situacaoTurma.id}" onfocus="$('form:checkSituacaoTurma').checked = true;" disabled="#{   relatorioTurma.campos.situacaoTurma }">
				<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
				<f:selectItems value="#{situacaoTurma.allCombo}" />
 			</h:selectOneMenu>   	
		</td>
	</tr>
	<tr  nowrap="nowrap">
		<td colspan="3" class="subFormulario">Reserva de Curso</td>
	</tr>
	<tr>
		<td>
			<h:selectBooleanCheckbox styleClass="noborder" value="#{relatorioTurma.filtroReservaCurso}" id="checkReservaCurso" />
		</td>
		<td>
			<label for="form:checkReservaCurso">Curso:</label>
		</td>
		<td>
			<h:selectOneMenu id="curso" value="#{relatorioTurma.curso.id}" onchange="$('form:checkReservaCurso').checked = true;" style="width:99%;">
				<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
				<f:selectItems value="#{curso.allCursoGraduacaoCombo}" />
			</h:selectOneMenu>
		</td>
	</tr>
	</tbody>
	<tfoot>
	<tr>
		<td colspan="3">
			<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatorioTurma.gerarRelatorioListaTurma}" /> 
			<h:commandButton id="cancelar"       value="Cancelar"        action="#{relatorioTurma.cancelar}" onclick="#{confirm}"/>
		</td>
	</tr>
	</tfoot>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

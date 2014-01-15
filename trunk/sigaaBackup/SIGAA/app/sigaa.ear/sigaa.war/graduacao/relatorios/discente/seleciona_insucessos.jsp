<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{realtorioDiscente.create}"></h:outputText>
<h2><ufrn:subSistema /> > Relatório de Insucessos de Alunos</h2>

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
		<td></td>
		<td><label for="form:checkCurso">Curso</label></td>
		<td>
			<c:choose>
				<c:when test="${acesso.coordenadorCursoGrad || acesso.secretarioGraduacao || acesso.coordenacaoProbasica}">
					<c:if test="${not empty cursoAtual}">
						${cursoAtual}
					</c:if>
					<c:if test="${empty cursoAtual}">
						TODOS
					</c:if>
				</c:when>
				<c:when test="${acesso.dae || acesso.secretarioCentro}">
					<h:selectOneMenu value="#{relatorioDiscente.idCurso}" id="curso">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{relatorioPorCurso.cursosCombo}" />
					</h:selectOneMenu>
				</c:when>
			</c:choose>
		</td>
	</tr>
	<tr>
		<td colspan="3" align="center">
						<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
						action="#{relatorioDiscente.gerarRelatorioListaInsucessosAlunos}"/> <h:commandButton
						value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" /></td>
	</tr>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
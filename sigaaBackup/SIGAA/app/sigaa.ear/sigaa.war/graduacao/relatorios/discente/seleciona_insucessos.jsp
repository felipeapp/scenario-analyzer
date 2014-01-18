<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema /> > Relatório de Insucessos de Alunos</h2>

	<div class="descricaoOperacao">
		<p><b>Caro Usuário,</b></p>
		<p>
			Selecione um ano-período para gerar o Relatório de Insucessos de
			Alunos detalhando os discentes que não concluíram a turma. Caso
			marque a opção de <b>Relatório Sintético</b>, será exibido apenas os
			totais por componente curricular.
		</p>
	</div>
<h:form id="form">
<table  class="formulario" width="50%">
	<caption>Dados do Relatório</caption>
	<tbody>
	<tr>
		<th>Ano-Período:</th>
		<td>
			<h:selectOneMenu value="#{relatorioDiscente.ano}" id="ano">
				<f:selectItems value="#{relatorioDiscente.anos}" />
			</h:selectOneMenu>-<h:selectOneMenu value="#{relatorioDiscente.periodo}" id="periodo">
				<f:selectItems value="#{relatorioDiscente.periodos}" />
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<th class="${acesso.coordenadorCursoGrad || acesso.secretarioGraduacao || acesso.coordenacaoProbasica ? 'rotulo':''}">Curso:</th>
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
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{relatorioPorCurso.cursosCombo}" />
					</h:selectOneMenu>
				</c:when>
			</c:choose>
		</td>
	</tr>
	<tr>
		<th><h:selectBooleanCheckbox value="#{ relatorioDiscente.relatorioSintetico }" id="relatorioSintetico" /> </th>
		<td>
			Exibir apenas os totais por componente curricular.
		</td>
	</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2" align="center">
				<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatorioDiscente.gerarRelatorioListaInsucessosAlunos}"/>
				<h:commandButton value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" onclick="#{ confirm }"/>
			</td>
		</tr>
	</tfoot>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
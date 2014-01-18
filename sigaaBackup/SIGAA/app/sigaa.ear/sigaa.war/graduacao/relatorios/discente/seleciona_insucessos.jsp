<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema /> > Relat�rio de Insucessos de Alunos</h2>

	<div class="descricaoOperacao">
		<p><b>Caro Usu�rio,</b></p>
		<p>
			Selecione um ano-per�odo para gerar o Relat�rio de Insucessos de
			Alunos detalhando os discentes que n�o conclu�ram a turma. Caso
			marque a op��o de <b>Relat�rio Sint�tico</b>, ser� exibido apenas os
			totais por componente curricular.
		</p>
	</div>
<h:form id="form">
<table  class="formulario" width="50%">
	<caption>Dados do Relat�rio</caption>
	<tbody>
	<tr>
		<th>Ano-Per�odo:</th>
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
				<h:commandButton id="gerarRelatorio" value="Gerar Relat�rio" action="#{relatorioDiscente.gerarRelatorioListaInsucessosAlunos}"/>
				<h:commandButton value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" onclick="#{ confirm }"/>
			</td>
		</tr>
	</tfoot>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
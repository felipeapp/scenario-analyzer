<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<c:set value="escolha_restricoes" var="pagina"></c:set>
	<%@ include file="/graduacao/matricula/cabecalho_matricula.jsp"%>

	<h:form id="form">

	<c:set var="discente" value="#{matriculaGraduacao.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>

	<c:if test="${ matriculaGraduacao.compulsoria }">
		<table class="formulario" width="85%">
		<caption>Selecione as Restrições a Serem Verificadas</caption>
		<tr class="linhaImpar"><td>
		<h:selectBooleanCheckbox id="prerequisitos" value="#{ matriculaGraduacao.restricoes.preRequisitos }"/>
			<label for="form:prerequisitos">Verificar se o discente possui pré-requisitos para matricular-se nas turmas</label>
		</td></tr>
		<tr class="linhaPar"><td>
		<h:selectBooleanCheckbox id="corequisitos" value="#{ matriculaGraduacao.restricoes.coRequisitos }"/>
			<label for="form:corequisitos">Verificar se o discente possui co-requisitos para matricular-se nas turmas</label>
		</td></tr>
		<tr class="linhaImpar"><td>
		<h:selectBooleanCheckbox id="horarios" value="#{ matriculaGraduacao.restricoes.choqueHorarios }"/>
			<label for="form:horarios">Verificar choque de horários entre turmas a serem matriculadas</label>
		</td></tr>
		<tr class="linhaPar"><td>
		<h:selectBooleanCheckbox id="crdisciplina" value="#{ matriculaGraduacao.restricoes.mesmoComponente }"/>
			<label for="form:crdisciplina">Verificar se o discente possui aprovação ou aproveitamento nos
		 componentes (e equivalentes) das turmas a serem matriculadas</label>
		</td></tr>
		<tr class="linhaImpar"><td>
		<h:selectBooleanCheckbox id="crextra" value="#{ matriculaGraduacao.restricoes.limiteCreditosExtra }"/>
			<label for="form:crextra">Verificar limite de créditos eletivos</label>
		</td></tr>
		<tr class="linhaPar"><td>
		<h:selectBooleanCheckbox id="crsemestre" value="#{ matriculaGraduacao.restricoes.limiteMaxCreditosSemestre }"/>
			<label for="form:crsemestre">Verificar limite máximo de créditos por semestre do currículo do aluno</label>
		</td></tr>
		<tr class="linhaPar"><td>
		<h:selectBooleanCheckbox id="crminsemestre" value="#{ matriculaGraduacao.restricoes.limiteMinCreditosSemestre }"/>
			<label for="form:crminsemestre">Verificar limite mínimo de créditos por semestre do currículo do aluno</label>
		</td></tr>
		<tr class="linhaImpar"><td>
		<h:selectBooleanCheckbox id="alunoespecial" value="#{ matriculaGraduacao.restricoes.alunoEspecial }"/>
			<label for="form:alunoespecial">Verificar restrições de matrículas para alunos especiais</label>
		</td></tr>
		<tr class="linhaPar"><td>
		<h:selectBooleanCheckbox id="alunooutrocampus" value="#{ matriculaGraduacao.restricoes.alunoOutroCampus}"/>
			<label for="form:alunooutrocampus">Verificar restrições de matrículas para alunos de outro campus (mobilidade acadêmica)</label>
		</td></tr>
		<tr class="linhaImpar"><td>
		<h:selectBooleanCheckbox id="capacidadeTurma" value="#{ matriculaGraduacao.restricoes.capacidadeTurma}"/>
			<label for="form:capacidadeTurma">Verificar se as capacidades de alunos das turmas são respeitadas</label>
		</td></tr>
		</table>
	</c:if>

	<p>&nbsp;</p>
	<table class="formulario" width="50%">
	<caption>Status da Matrícula</caption>
	<td align="center">
		<h:selectOneMenu value="#{ matriculaGraduacao.situacao.id }" style="width: 100%; text-align: center;" id="situacao">
			<f:selectItem itemValue="0" itemLabel="-- SELECIONE -- "/>
			<f:selectItems value="#{ matriculaGraduacao.status}"/>
		</h:selectOneMenu>
	</td></tr>
	</table>
	<br>
	<p>&nbsp;</p>
	<table class="formulario" width="50%">
	<caption>Escolha o Ano-Semestre</caption>
	<td align="center">
		<h:selectOneMenu value="#{ matriculaGraduacao.anoPeriodo }" id="ano">
			<f:selectItems value="#{ matriculaGraduacao.calendariosPossiveis}"/>
		</h:selectOneMenu>
	</td></tr>
	</table>

	<br/>
	<p align="center">
	<h:commandButton action="#{ matriculaGraduacao.submeterRestricoes}" value="Confirmar" id="btnConfirmar"/>
	</p>

	</h:form>

</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
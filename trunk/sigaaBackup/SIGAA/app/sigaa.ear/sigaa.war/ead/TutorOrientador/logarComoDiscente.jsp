<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Logar como Discente</h2>

	<div class="descricaoOperacao">
		Digite os critérios de busca e clique em <strong>Buscar.</strong>
		Em seguida você poderá escolher qualquer <strong>Discente</strong> da listagem
		para se logar. 
	</div>

	<h:form id="formulario">
	<h:messages showDetail="true"/>
	<br/>

	<table class="formulario" width="80%">
	<caption>Informe os Critérios de Busca</caption>
	<tr>
		<td><h:selectBooleanCheckbox value="#{ logarComoDiscente.buscaMatricula }" styleClass="noborder" id="checkMatricula"/> </td>
		<td style="align: right;">Matrícula: </td>
		<td><h:inputText value="#{ logarComoDiscente.matricula }" id="matriculaDiscente" size="14" maxlength="12"
						onfocus="getEl('formulario:checkMatricula').dom.checked = true;" 
						onkeyup="return formatarInteiro(this);"/> </td>
	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{ logarComoDiscente.buscaNome }" styleClass="noborder" id="checkNome"/></td>
		<td style="align: right;" width="10%" nowrap="nowrap">Nome do Discente: </td>
		<td><h:inputText value="#{ logarComoDiscente.nome }" size="60" onfocus="getEl('formulario:checkNome').dom.checked = true;"/></td>
	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{ logarComoDiscente.buscaCurso }" styleClass="noborder" id="checkCurso"/></td>
		<td style="align: right;">Curso: </td>
		<td><h:inputText value="#{ logarComoDiscente.curso }" size="60" onfocus="getEl('formulario:checkCurso').dom.checked = true;" /></td>
	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{ logarComoDiscente.buscaTutor }" styleClass="noborder" id="checkTutor"/></td>
		<td style="align: right;">Tutor:</td>
		<td>
			<h:selectOneMenu value="#{ logarComoDiscente.usuario.id }"  onfocus="getEl('formulario:checkTutor').dom.checked = true;">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{ logarComo.tutores }"/>
			</h:selectOneMenu>
		</td>
	</tr>
	<tfoot>
	<tr>
		<td colspan="3">
			<h:commandButton value="Buscar" action="#{ logarComoDiscente.localizarDiscentesPorTutor }"/>
			<h:commandButton value="Cancelar" action="#{ logarComo.cancelar }" onclick="#{confirm}"/>
		</td>
	</tr>
	</tfoot>
	</table>

	<c:if test="${ not empty logarComoDiscente.alunosOrientados }">
	<br />
	<div class="infoAltRem">
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Logar Como Discente
	</div>
	
	<br />
	
	<table class="listagem">
		<caption>Alunos Encontrados(${logarComoDiscente.numResultados })</caption>
		<thead>
		<tr>
			<td style="align: center;">Matrícula</td>
			<th>Nome</th>
			<th>Curso</th>
			<th>Tutor</th>
			<th></th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="#{logarComoDiscente.alunosOrientados}" var="aluno" varStatus="status">
		<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
			<td width="50" style="align: center;">${ aluno.matricula }</td>
			<td>${ aluno.pessoa.nome }</td>
			<td>${ aluno.curso.descricao }</td>
			<td>
				<table>
				<c:forEach items="#{ aluno.tutores }" var="tutor">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td>${tutor.pessoa.nome}</td></tr>
				</c:forEach>	
				</table>	
			</td>
			<td width="10">
				<h:commandLink action="#{logarComoDiscente.logarDiscente}">
					<h:graphicImage value="/img/seta.gif" title="Logar como discente" alt="Logar Como Discente"/> 
					<f:param name="discente" value="#{ aluno.id }"/>				
				</h:commandLink>
			</td>
		</tr>
		</c:forEach>
		</tbody>
	</table>
	</c:if>

	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

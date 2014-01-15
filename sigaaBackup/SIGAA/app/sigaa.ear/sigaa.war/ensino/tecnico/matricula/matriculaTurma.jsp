<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/matricula.js"></script>

<script type="text/javascript">
function marcarTodos() {
	var elements = document.getElementsByTagName('input');
	for (i = 0; i < elements.length; i++) {
		if (elements[i].type == 'checkbox') {
			elements[i].checked = true;
		}
	}
}
</script>

<h2> <ufrn:subSistema></ufrn:subSistema> &gt; Matricular </h2>
<br/>

<f:view>

<h:form id="matricula">
<table class="formulario" width="80%">
<caption>Informe os Dados para a Matrícula</caption>
<tbody>
<tr>
	<th class="obrigatorio"> Curso: </th>
	<td> <h:selectOneMenu value="#{ matriculaTecnico.curso.id }" onchange="submit()">
			<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
			<f:selectItems value="#{ matriculaTecnico.cursos }"/>
		 </h:selectOneMenu> </td>
</tr>
<tr>
	<th class="obrigatorio">Turma de Entrada: </th>
	<td> <h:selectOneMenu value="#{ matriculaTecnico.turmaEntrada.id }" onchange="submit()">
			<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
			<f:selectItems value="#{ matriculaTecnico.turmasEntrada }"/>
		 </h:selectOneMenu> </td>
</tr>
<c:if test="${ matriculaTecnico.tipo == 'turmaTurma' }">
<tr>
	<th class="obrigatorio">Turma: </th>
	<td>
		<c:choose>
		<c:when test="${acesso.coordenadorCursoLato || acesso.secretarioLato}">	
			<h:selectOneMenu id="turma" value="#{ matriculaTecnico.turma.id }" style="width: 100%" onchange="submit()">
				<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
				<f:selectItems value="#{ lato.turmasMatricula }"/>
			</h:selectOneMenu>
		</c:when>
		<c:otherwise>

			<h:inputText value="#{ matriculaTecnico.turma.disciplina.nome }" id="nomeTurma" size="60"/>
			<rich:suggestionbox for="nomeTurma" width="450" height="100" minChars="3" id="suggestionTurma" 
				suggestionAction="#{turmaAutoCompleteMBean.autocompleteTurma}" var="_turma" 
				fetchValue="#{_turma.disciplina.nome}">
			 
				<h:column>
					<h:outputText value="#{_turma.nomeCompleto}" />
				</h:column>
			 
		        <a4j:support event="onselect" immediate="true">
	               <f:setPropertyActionListener value="#{_turma }" target="#{matriculaTecnico.turma}" />
			    </a4j:support>
			</rich:suggestionbox>
		
		</c:otherwise>
		</c:choose>
	</td>
</tr>
</c:if>
<c:if test="${ matriculaTecnico.tipo == 'turmaModulo' }">
<tr>
	<th>Módulo:</th>
	<td>
		<h:selectOneMenu value="#{ matriculaTecnico.modulo.id }">
			<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
			<f:selectItems value="#{ matriculaTecnico.modulos }"/>
		</h:selectOneMenu>
	</td>
</tr>
</c:if>
</tbody>

<tfoot>
<tr>
	<td colspan="2">
		<h:commandButton value="Buscar" immediate="true" action="#{ matriculaTecnico.buscar }" />
		<h:commandButton value="Cancelar" immediate="true" action="#{ matriculaTecnico.cancelar }" onclick="#{confirm}"/>
	</td>
</tfoot>

</table>

<br/>&nbsp;

<h:dataTable value="#{ matriculaTecnico.discentes }" var="_discente" width="100%" styleClass="listagem" rendered="#{ not empty matriculaTecnico.discentes }">

	<f:facet name="caption"><f:verbatim>Discentes Matriculados</f:verbatim></f:facet>
	
	<h:column>
		<f:facet name="header">
			<f:verbatim>
			<a href="#" onclick="marcarTodos();">Todos</a>
			</f:verbatim>
		</f:facet>
		<h:selectBooleanCheckbox value="#{ _discente.selecionado }" rendered="#{ not _discente.matricular }"/>
	</h:column>

	<h:column>
		<f:facet name="header"><f:verbatim>Matrícula</f:verbatim></f:facet>
		<h:outputText value="#{ _discente.matricula }"/>
	</h:column>

	<h:column>
		<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
		<h:outputText value="#{ _discente.pessoa.nome }"/>
	</h:column>

</h:dataTable>

<a4j:region rendered="#{ not empty matriculaTecnico.discentes }">
	<table class="formulario" width="100%">
		<tfoot>
			<tr>
				<td colspan="4">
					<h:commandButton value="Matricula Discentes" action="#{ matriculaTecnico.realizarMatricula }"/>		
				</td>
			</tr>
		</tfoot>
	</table>
</a4j:region>
	
</h:form>

	<center>	
		<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
	</center>
<br/>

</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
<script type="text/javascript">
Element.hide('turmas');
</script>
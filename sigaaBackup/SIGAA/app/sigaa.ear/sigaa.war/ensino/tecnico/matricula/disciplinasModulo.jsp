<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<script type="text/javascript" src="/shared/javascript/matricula.js"></script>

<h2> <ufrn:subSistema /> &gt; Matricular </h2>

<f:view>

<h:form id="matricula">
<table class="formulario" width="100%">
<caption>Turmas Abertas do Módulo</caption>
	<tr>
		<td align="center">
			<h:dataTable var="turma" value="#{ matriculaTecnico.turmasModulo }" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
				<h:column>
					<f:facet name="header"></f:facet>
					<h:selectBooleanCheckbox value="#{ turma.matricular }"/>
				</h:column>
			
				<h:column>
					<f:facet name="header"><f:verbatim>Código</f:verbatim></f:facet>
					<h:outputText value="#{ turma.disciplina.detalhes.codigo }"/>
				</h:column>
				
				<h:column>
					<f:facet name="header"><f:verbatim>Disciplina</f:verbatim></f:facet>
					<h:outputText value="#{ turma.nomeDisciplinaEspecializacao }"/>
				</h:column>
				
				<h:column>
					<f:facet name="header"><f:verbatim>Turma</f:verbatim></f:facet>
					<h:outputText value="#{ turma.codigo }"/>
				</h:column>
			</h:dataTable>
		</td>
	</tr>
<tfoot>
	<tr>
		<td align="center">
			<c:if test="${ matriculaTecnico.tipo == 'alunoTurma' || matriculaTecnico.tipo == 'alunoModulo' }">
				<h:commandButton value="<< Voltar" action="#{ matriculaTecnico.matriculaAluno }"/>
			</c:if>
			<c:if test="${ matriculaTecnico.tipo == 'turmaTurma' || matriculaTecnico.tipo == 'turmaModulo' }">
				<h:commandButton value="<< Voltar" action="#{ matriculaTecnico.matriculaTurma }"/>
			</c:if>
			<h:commandButton value="Cancelar" action="#{ matriculaTecnico.cancelar }"/>
			<h:commandButton value="Avançar >>" action="#{ matriculaTecnico.realizarMatricula }"/>
		</td>
	</tr>
</tfoot>
</table>

</h:form>

</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>

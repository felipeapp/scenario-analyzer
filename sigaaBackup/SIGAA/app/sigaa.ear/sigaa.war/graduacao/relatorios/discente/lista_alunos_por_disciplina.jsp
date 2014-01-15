<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>


<center><h3>Relatório de Alunos Por Disciplina</h3></center>

<h5> Alunos encontrados: ${fn:length(relatorioAlunosEmDisciplinas.matriculas)} </h5>

<f:view>
	<h:form id="lista">
		<h:dataTable value="#{relatorioAlunosEmDisciplinas.matriculas}" var="m" width="100%" styleClass="tabelaRelatorio">
			<f:facet name="caption">
				<h:outputText value="<strong>#{relatorioAlunosEmDisciplinas.tituloRelatorio}</strong>" escape="false" />
			</f:facet>

			<h:column>
				<f:facet name="header">
					<h:outputText value="Nome" />
				</f:facet>
				<h:outputText value="#{m.discente.pessoa.nome}" />
			</h:column>
			
			<h:column>
				<f:facet name="header">
					<h:outputText value="Matrícula" />
				</f:facet>
				<h:outputText value="#{m.discente.matricula}" />				
			</h:column>
			
			<h:column>
				<f:facet name="header">
					<h:outputText value="Cód. da Turma" />
				</f:facet>
				<h:outputText value="Turma #{m.turma.codigo}" />				
			</h:column>			
			
		</h:dataTable>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
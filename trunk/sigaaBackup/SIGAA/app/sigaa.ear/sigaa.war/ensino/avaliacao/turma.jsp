<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>Cadastro de Avaliação > Informar Notas</h2>

<f:view>

<h:form>

<t:dataTable var="matricula" value="#{ cadastrarAvaliacao.matriculas }" styleClass="listagem" rowClasses="linhaPar,linhaImpar" width="100%">

	<t:column styleClass="rightAlign" width="10%">
		<f:facet name="header">
		<f:verbatim>Matrícula</f:verbatim>
		</f:facet>
		<h:outputText value="#{ matricula.discente.matricula }"/>
	</t:column>

	<t:column>
		<f:facet name="header">
		<f:verbatim>Aluno</f:verbatim>
		</f:facet>
		<h:outputText value="#{ matricula.discente.pessoa.nome }" />
	</t:column>

	<t:column styleClass="centerAlign">
		<f:facet name="header">
		<f:verbatim>Nota</f:verbatim>
		</f:facet>

		
	</t:column>

</t:dataTable>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

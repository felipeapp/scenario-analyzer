<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form>

<fieldset>
<legend>Importação de Dados de Turmas Anteriores</legend>

<div class="descricaoOperacao">
	Selecione uma das turmas abaixo para importar os seus dados para esta Turma Virtual.
</div>

	<h:dataTable var="t" value="#{ importacaoDadosTurma.turmasAnteriores }" styleClass="listing" rowClasses="even, odd" columnClasses="first, width90, width90, icon">
	
		<h:column>
			<f:facet name="header"><f:verbatim><p align="left"><h:outputText value="Componente Curricular"/></p></f:verbatim></f:facet>
			<h:outputText value="#{ t.disciplina.codigo } - #{ t.disciplina.nome }"/>
		</h:column>
		
		<h:column>
			<f:facet name="header"><f:verbatim><p align="right"><h:outputText value="Turma"/></p></f:verbatim></f:facet>
			<p align="right"><h:outputText value="#{ t.codigo }"/></p>
		</h:column>
	
		<h:column>
			<f:facet name="header"><h:outputText value="Ano-Período"/></f:facet>
			<h:outputText value="#{ t.anoPeriodo }"/>
		</h:column>
	
		<h:column>
			<h:commandLink action="#{ importacaoDadosTurma.opcoesImportacao }" title="Selecionar Turma">
				<h:graphicImage value="/img/seta.gif"/>
				<f:param name="id" value="#{ t.id }"/>
				<f:param name="planoCurso" value="#{ false }"/>
			</h:commandLink>
		</h:column>
	
	</h:dataTable>

</fieldset>

</h:form>
</f:view>
<%@include file="/ava/rodape.jsp"%>

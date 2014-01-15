<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema /> > Selecione um curso</h2>

<f:view>
	
	<h:form>
	<t:dataTable var="cursoLoop" value="#{ opCoordenadorGeralEad.cursos }" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
	
		<f:facet name="caption"><h:outputText value="Cursos de Ensino a Distância"/></f:facet>
	
		<t:column>
			<f:facet name="header"><h:outputText value="Nome"/></f:facet>
			<h:outputText value="#{ cursoLoop.descricao }"/>
		</t:column>
		
		<t:column>
			<h:commandButton action="#{ opCoordenadorGeralEad.realizarOperacao }" image="/img/seta.gif"/>
		</t:column>
	
	</t:dataTable>
	
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

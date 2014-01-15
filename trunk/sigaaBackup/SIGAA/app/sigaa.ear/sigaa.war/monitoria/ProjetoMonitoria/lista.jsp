<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	
	<h:messages/>
	<h2><ufrn:subSistema /> > Projetos de Ensino</h2>
	<br>

	<h:outputText value="#{projetoMonitoria.create}"/>
	
	<div class="infoAltRem">
	    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Projeto de Ensino
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Projeto de Ensino<br/>
	</div>

	<h:form>
	<table class="listagem">
			<caption class="listagem">Lista de Projetos de Ensino </caption>

	<c:forEach items="#{projetoMonitoria.all}" var="projeto">
		<tr>
			<td> ${projeto.titulo} </td>
			<td> ${projeto.descricao} </td>
			<td width="2%">
					<h:commandLink  title="Alterar" action="#{projetoMonitoria.atualizar}" style="border: 0;">
				    	<f:param name="id" value="#{projeto.id}"/>				    	
						<h:graphicImage url="/img/alterar.gif"/>
					</h:commandLink>
			</td>		
			<td width="2%">	
					<h:commandLink  title="Remover" action="#{projetoMonitoria.preRemover}" style="border: 0;">
				    	<f:param name="id" value="#{projeto.id}"/>				    	
						<h:graphicImage url="/img/delete.gif"/>
					</h:commandLink>
				</td>
	</c:forEach>
	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
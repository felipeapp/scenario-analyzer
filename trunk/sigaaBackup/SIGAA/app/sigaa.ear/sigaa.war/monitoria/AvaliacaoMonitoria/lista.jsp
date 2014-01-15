<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	<h:messages/>
	<h2><ufrn:subSistema /> > Projeto de Ensino não avaliados</h2>
	<br>

	<h:outputText value="#{avalProjetoMonitoria.create}"/>
	<h:outputText value="#{projetoMonitoria.create}"/>	

	<div class="infoAltRem">
	    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Projeto<br/>
	    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Avaliação
	</div>

	<h:form>
	<table class="listagem">
	<caption>Projeto de Ensino</caption>

	<c:forEach items="#{avalProjetoMonitoria.allProjetosAvaliacao}" var="avaliacao" varStatus="status">
		<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">		
			<td> ${avaliacao.projetoEnsino.anoTitulo} </td>
			<td> ${avaliacao.tipoProjetoEnsino.descricao}</td>
			<td> ${avaliacao.statusAvaliacao.descricao}</td>

			<td width="2%">				
				<h:commandLink action="#{projetoMonitoria.view}" style="border: 0;" title="Visualizar Projeto">
			       	 <f:param name="id" value="#{avaliacao.projetoEnsino.id}"/>
			    	 <h:graphicImage url="/img/view.gif" />
				</h:commandLink>
			</td>

			<td width="2%">
				<h:commandLink action="#{avalProjetoMonitoria.alterarAvaliacao}" style="border: 0;" title="Alterar Avaliação">
				      <f:param name="idAvaliacao" value="#{avaliacao.id}"/>
				      <h:graphicImage url="/img/alterar.gif" />
				</h:commandLink>
			</td>		
	</c:forEach>
	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
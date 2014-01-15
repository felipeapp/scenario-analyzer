<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipo de Participação</h2>

	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/> 
			 <h:commandLink action="#{tipoParticipacao.preCadastrar}" value="Cadastrar"/>
		</div>
	</h:form>
	<h:outputText value="#{tipoParticipacao.create}" />
<c:set var="lista" value="${tipoParticipacao.allAtivos}" />
<c:if test="${empty lista}">
	<br />
	<center>
	<span style="color:red;">Nenhum Tipo Participação Encontrado.</span>
	</center>
</c:if>
<c:if test="${not empty lista}">	

	<table class="listagem">
		<caption class="listagem">Lista de Tipos de Participação</caption>
		<thead>
			<tr>	
				<td>Tipo Produção</td>
				<td>Descrição</td>
			</tr>
		</thead>
		<c:forEach items="${tipoParticipacao.allPaginado}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td style="width: 50%;">${item.tipoProducao.descricao}</td>
				<td>${item.descricao}</td>
			</tr>
		</c:forEach>
	</table>
	
<center>
	<h:form>
		<h:commandButton image="/img/voltar.gif" actionListener="#{paginacao.previousPage}" rendered="#{paginacao.paginaAtual > 0 }"/>
 		<h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{paginacao.changePage}" onchange="submit()" immediate="true">
			<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
		</h:selectOneMenu>
 		<h:commandButton image="/img/avancar.gif" actionListener="#{paginacao.nextPage}"  rendered="#{paginacao.paginaAtual < (paginacao.totalPaginas - 1)}"/>
		<br/><br/>
 		<em><h:outputText value="#{paginacao.totalRegistros }"/> Registro(s) Encontrado(s)</em>
	</h:form>
</center>	
	
</c:if>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
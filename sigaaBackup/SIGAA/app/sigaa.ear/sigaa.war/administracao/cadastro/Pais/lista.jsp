<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > País</h2>
	<h:outputText value="#{pais.create}" />
	
	<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
					<h:commandLink action="#{pais.preCadastrar}" value="Cadastrar"/>
				</h:form>
			</div>
	</center>
		

	<table class=listagem>
	  <caption class="listagem">Lista de Países</caption>
		<thead>
			<tr>
				<td>Nome</td>
				<td>Nacionalidade</td>
			</tr>
		</thead>
		<c:forEach items="${pais.allPaginado}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.nome}</td>
				<td>${item.nacionalidade}</td>
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

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
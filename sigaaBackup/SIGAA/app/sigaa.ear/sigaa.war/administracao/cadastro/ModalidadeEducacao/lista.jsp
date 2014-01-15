<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:outputText value="#{modalidadeEducacao.create }"></h:outputText>
	<h2><ufrn:subSistema /> > Forma de Participação do Aluno</h2>
	<h:outputText value="#{modalidadeEducacao.create}" />
	<ufrn:subSistema teste="not graduacao, consulta">
	<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
					<h:commandLink action="#{modalidadeEducacao.preCadastrar}" value="Cadastrar"/>
				</h:form>
			</div>
	</center>
	</ufrn:subSistema>

	<table class=formulario width="50%">
		<caption class="listagem">Lista de Modalidades de Educação</caption>
		<thead>
		<tr>
			<td>Descrição</td>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${modalidadeEducacao.allPaginado}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricao}</td>
			</tr>
		</c:forEach>
		</tbody>
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
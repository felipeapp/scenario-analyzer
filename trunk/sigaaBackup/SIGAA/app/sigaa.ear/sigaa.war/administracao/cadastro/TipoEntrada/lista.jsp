<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<c:if test="${sessionScope.acesso.administracao}">

</c:if>
	<h2><ufrn:subSistema /> > Tipo de Entrada</h2>
	<h:outputText value="#{tipoEntrada.create}" />

	<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
					<h:commandLink action="#{tipoEntrada.preCadastrar}" value="Cadastrar"/>
					<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
			        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
				</h:form>
			</div>
	</center>

	<table class=listagem>
		<tr>
			<caption class="listagem">Lista de Tipos de Entrada</caption>
			<thead>
				<td>Descrição</td>
				<td></td>
				<td></td>
			</thead>
		</tr>
		<c:forEach items="${tipoEntrada.allPaginado}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricao}</td>
				<td width=20>
					<h:form><input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/alterar.gif" value="Alterar"
						action="#{tipoEntrada.atualizar}" /></h:form>
				</td>
				<td width=25>
					<h:form><input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/delete.gif" alt="Remover"
						action="#{tipoEntrada.remover}" onclick="#{confirmDelete}"/></h:form>
				</td>
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
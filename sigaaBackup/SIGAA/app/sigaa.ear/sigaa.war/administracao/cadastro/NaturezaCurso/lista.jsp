<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Natureza do Curso</h2>
	<h:outputText value="#{naturezaCurso.create}" />
	
	<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
					<h:commandLink action="#{naturezaCurso.preCadastrar}" value="Cadastrar"/>
					<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
			        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
				</h:form>
			</div>
	</center>

	<table class=listagem>
		<tr>
			<caption class="listagem">Lista de Natureza dos Cursos</caption>
			<thead>
				<td>Descri��o</td>
				<td></td>
				<td></td>
			</thead>
		</tr>
		<c:forEach items="${naturezaCurso.allPaginado}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricao}</td>
				<td width=20>
					<h:form><input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/alterar.gif" value="Alterar"
						action="#{naturezaCurso.atualizar}" /></h:form>
				</td>
				<td width=25>
					<h:form><input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/delete.gif" alt="Remover"
						action="#{naturezaCurso.remover}" onclick="#{confirmDelete}"/></h:form>
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

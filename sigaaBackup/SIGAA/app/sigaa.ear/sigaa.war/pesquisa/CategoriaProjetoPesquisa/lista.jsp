<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Categoria de Projeto de Pesquisa</h2>
	<h:outputText value="#{categoriaProjetoPesquisaBean.create}" />
	
	<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
					<h:commandLink action="#{categoriaProjetoPesquisaBean.preCadastrar}" value="Cadastrar"/>
					<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
			        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
				</h:form>
			</div>
	</center>
		
	<table class="listagem">
		<caption class="listagem">Lista de Categorias de Projeto de Pesquisa</caption>
		<thead>
		<tr>
			<td>Denominação</td>
			<td>Ordem</td>
			<td>Ativo</td>
			<td></td>
			<td></td>
		</tr>
		</thead>
		<h:form>
		<c:forEach items="#{categoriaProjetoPesquisaBean.allPaginado}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.denominacao}</td>
				<td>${item.ordem}</td>
				<td>${item.ativo?'Sim':'Não'}</td>
				<td width="20">
					<h:commandLink title="Alterar" action="#{categoriaProjetoPesquisaBean.atualizar}">
						<f:param name="id" value="#{item.id}"/>
						<h:graphicImage url="/img/alterar.gif"/>
					</h:commandLink>
				</td>
				<td width="25">
					<h:commandLink title="Remover" action="#{categoriaProjetoPesquisaBean.remover}" onclick="#{confirmDelete}">
						<f:param name="id" value="#{item.id}"/>
						<h:graphicImage url="/img/delete.gif"/>
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>
		</h:form>
	</table>
	
	<center>
		<h:form id="formPaginacao">
			<h:graphicImage value="/img/voltar_des.gif" rendered="#{paginacao.primeiraPagina}"/>
			<h:commandButton image="/img/voltar.gif" actionListener="#{paginacao.previousPage}" rendered="#{!paginacao.primeiraPagina}"/>
			<h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{paginacao.changePage}" onchange="submit()" immediate="true">
				<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
			</h:selectOneMenu>
			<h:commandButton image="/img/avancar.gif" actionListener="#{paginacao.nextPage}" rendered="#{!paginacao.ultimaPagina}"/>
			<h:graphicImage value="/img/avancar_des.gif" rendered="#{paginacao.ultimaPagina}"/>
		</h:form>
	</center>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

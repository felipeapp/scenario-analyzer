<%--
	Exibe a paginação para uma listagem.
	Precisa da variável "itensPaginacao" com a lista de itens paginados, "beanPaginacao" com o mbean da listagem e das variáveis "labelCrescente" e "labelDecrescente" com as labels para a ordenação.
--%>

<div class="navegacao">
	<div class="resultados"> 
		Exibindo <strong><h:outputText value="#{ paginacao.paginaAtual * paginacao.tamanhoPagina + 1 }" /></strong>
		- <strong><h:outputText value="#{ paginacao.paginaAtual * paginacao.tamanhoPagina + fn:length(itensPaginacao) }" /></strong>
		de <strong><h:outputText value="#{ paginacao.totalRegistros }" /></strong>
	</div>
	
	<div class="setas">
		<div>
			<h:commandButton image="/img/primeira.gif" actionListener="#{beanPaginacao.primeiraPagina}" rendered="#{paginacao.paginaAtual > 0 }"/>
			<h:graphicImage value="/img/primeira_des.gif" rendered="#{paginacao.paginaAtual <= 0 }" />
			
			<h:commandButton image="/img/voltar.gif" actionListener="#{beanPaginacao.previousPage}" rendered="#{paginacao.paginaAtual > 0 }"/>
			<h:graphicImage value="/img/voltar_des.gif" rendered="#{paginacao.paginaAtual <= 0 }" />&nbsp;
		</div>
		
		<div>
			<h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{beanPaginacao.changePage}" onchange="submit()" immediate="true">
				<f:selectItems value="#{paginacao.listaPaginas}"/>
			</h:selectOneMenu>
		</div>
		
		<div>&nbsp;
			<h:commandButton image="/img/avancar.gif" actionListener="#{beanPaginacao.nextPage}"  rendered="#{paginacao.paginaAtual < (paginacao.totalPaginas - 1)}"/>
			<h:graphicImage value="/img/avancar_des.gif" rendered="#{paginacao.paginaAtual >= (paginacao.totalPaginas - 1)}" />
			
			<h:commandButton image="/img/ultima.gif" actionListener="#{beanPaginacao.ultimaPagina}"  rendered="#{paginacao.paginaAtual < (paginacao.totalPaginas - 1)}"/>
			<h:graphicImage value="/img/ultima_des.gif" rendered="#{paginacao.paginaAtual >= (paginacao.totalPaginas - 1)}" />
		</div>
	</div>
	
	<div class="opcoes">
		<span>Ordem:</span>
		<h:commandLink action="#{ beanPaginacao.inverterOrdem }" rendered="#{ not beanPaginacao.crescente }">${ labelDecrescente }</h:commandLink>
		<h:commandLink action="#{ beanPaginacao.inverterOrdem }" rendered="#{ beanPaginacao.crescente }">${ labelCrescente }</h:commandLink>
		
		<span>Registros por página:</span> 
		<h:selectOneMenu value="#{beanPaginacao.tamanhoPagina}" valueChangeListener="#{beanPaginacao.mudarRegistrosPorPagina}" onchange="submit()" immediate="true">
			<f:selectItem itemValue="10" itemLabel="10" />
			<f:selectItem itemValue="20" itemLabel="20" />
			<f:selectItem itemValue="50" itemLabel="50" />
			<f:selectItem itemValue="100" itemLabel="100" />
		</h:selectOneMenu>
	</div>
</div>
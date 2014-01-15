<center>
	<h:form id="formPaginacao">
		<h:messages showDetail="true"/>
		<h:commandButton image="/img/primeira_des.gif" action="#{atividadeExtensao.firstPage}" rendered="#{paginacao.paginaAtual <= 1}" id="btPrimaDes"/>
		<h:commandButton image="/img/primeira.gif" action="#{atividadeExtensao.firstPage}" rendered="#{paginacao.paginaAtual > 1}" id="btPrima"/>

		<h:commandButton image="/img/voltar_des.gif" action="#{atividadeExtensao.previousPage}" rendered="#{paginacao.paginaAtual <= 1}" id="btBackDes"/>
		<h:commandButton image="/img/voltar.gif" action="#{atividadeExtensao.previousPage}" rendered="#{paginacao.paginaAtual > 1}" id="btBack"/>

		<h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{atividadeExtensao.changePage}" onchange="submit()" immediate="true" id="selectPgAtual">
			<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
		</h:selectOneMenu>

		<h:commandButton image="/img/avancar.gif" action="#{atividadeExtensao.nextPage}" rendered="#{paginacao.paginaAtual < paginacao.totalPaginas}" id="btProx"/>
		<h:commandButton image="/img/avancar_des.gif" action="#{atividadeExtensao.nextPage}" rendered="#{paginacao.paginaAtual >= paginacao.totalPaginas}" id="btProxDes"/>

		<h:commandButton image="/img/ultima.gif" action="#{atividadeExtensao.lastPage}" rendered="#{paginacao.paginaAtual < paginacao.totalPaginas}" id="btUlti"/>
		<h:commandButton image="/img/ultima_des.gif" action="#{atividadeExtensao.lastPage}" rendered="#{paginacao.paginaAtual >= paginacao.totalPaginas}" id="btUltiDes"/>
	</h:form>
</center>
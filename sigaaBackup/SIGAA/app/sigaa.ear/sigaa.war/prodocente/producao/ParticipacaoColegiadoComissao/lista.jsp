<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/painel_detalhar_pd.js"></script>
<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<%@include file="../../include_painel.jsp"%>
	<c:set var="dirBase" value="/prodocente/producao/" scope="session"/>
	<h2>
		 <a href="${ctx}/prodocente/listar_producao.jsf">
 		<h:graphicImage title="Voltar para Tela de Listar Produções Intelectuais" value="/img/prodocente/voltarproducoes.gif" style="overflow: visible;"/>
 		</a>
		Participação em Comissão de Colegiado
	</h2>
	<h:form>
	<div class="infoAltRem">
		 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/><h:commandLink action="#{participacaoColegiadoComissao.preCadastrar}" value="Cadastrar Participação em Comissão de Colegiado"/><h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
	   <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
	</div>
	</h:form>

	<h:outputText value="#{participacaoColegiadoComissao.create}" />
	
<c:set var="lista" value="${participacaoColegiadoComissao.all}" />
<c:if test="${empty lista}">
	<br />
	<center>
	<span style="color:red;">Nenhuma Participação em Colegiado e Comissão Encontrada.</span>
	</center>
</c:if>
<c:if test="${not empty lista}">


	<table class=listagem style="width:100%" border="1">
		<caption class="listagem">Lista de Participação em Comissão de Colegiado</caption>
		<thead>
			<tr>
			<td></td>
			<td>Comissão</td>
			<td width="13%">Instituição</td>
			<td width="15%">Departamento</td>
			<td style="text-align: center;">Ano de Referência</td>
			<td width="18%">Situação</td>
			<td></td>
			<td></td>
			</tr>
		</thead>
		<h:form>
		<c:forEach items="#{participacaoColegiadoComissao.all}" var="item"varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td align="center" width="2%">
					<c:if test="${item.sequenciaProducao !=null}">
						<ufrn:help img="/img/prodocente/lattes.gif">Origem da Produção: <b>Lattes</b></ufrn:help>
					</c:if>
					<c:if test="${item.sequenciaProducao ==null}">
						<ufrn:help img="/img/icones/producao_menu.gif">Origem da Produção: <b>Prodocente</b></ufrn:help>
					</c:if>
				</td>
				<td>
               		<a4j:commandLink id="show" action="#{carregarDadosProducoesMBean.carregar}"
               		oncomplete="Richfaces.showModalPanel('view')" immediate="true" reRender="view">  
               			${item.titulo}
               			<f:param name="id" value="#{item.id}"/>
				    </a4j:commandLink>  
				</td>
				<td>
					${item.instituicao.sigla}
				</td>
				<td>${item.departamento.sigla}</td>
				<td style="text-align: center;">${item.anoReferencia}</td>
				<td>${item.situacaoDesc}</td>

				<td width=20>
					<h:commandLink action="#{participacaoColegiadoComissao.atualizar}" >
						<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Alterar"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
				</td>						
				<td width=25>
					<h:commandLink action="#{participacaoColegiadoComissao.remover}" onclick="#{confirmDelete}">
						<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
				</td>

			</tr>
		</c:forEach>
		</h:form>
	</table>
	
	<br />
	<h:form id="paginacaoForm">

		<center>

		<h:commandButton image="/img/voltar.gif" actionListener="#{paginacao.previousPage}" rendered="#{paginacao.paginaAtual > 0 }"/>

		<h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{paginacao.changePage}" onchange="submit()" immediate="true">

		<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>

		</h:selectOneMenu>

		<h:commandButton image="/img/avancar.gif" actionListener="#{paginacao.nextPage}" rendered="#{paginacao.paginaAtual < (paginacao.totalPaginas - 1)}"/>

		<br/>

		<i><h:outputText value="#{paginacao.totalRegistros }"/> Registro(s) Encontrado(s)</i>

		</center>

	</h:form>

</c:if>
<h:outputText value="#{participacaoColegiadoComissao.dropList}" />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
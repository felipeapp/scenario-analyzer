<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/painel_detalhar_pd.js"></script>

<style>

	a:link {
		color: #003395;
		font-size: inherit;
		font-weight: none;
		text-decoration: none;
	}
	
	a:visited {
		color: #003390;
		text-decoration: none;
		font-weight: none;
	}
	
	a:active {
		color: #444444;
		text-decoration: none;
		font-weight: none;
	}
	
	a:hover {
		color: #333;
		font-weight: none;
		text-decoration: none;
	}
	
</style>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<%@include file="../../include_painel.jsp"%>
	<c:set var="dirBase" value="/prodocente/producao/" scope="session"/>

	<h2>
	<a href="${ctx}/prodocente/listar_producao.jsf">
 		<h:graphicImage title="Voltar para Tela de Listar Produções Intelectuais" value="/img/prodocente/voltarproducoes.gif" style="overflow: visible;"/>
 	</a>
	Produção Intectual - Artigos, Periódicos, Jornais e Similares
	</h2>

	<h:form>

		<div class="infoAltRem" style="width: 100%">
			<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/><h:commandLink action="#{artigo.preCadastrar}" value="Cadastrar Novo Artigo"/><h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Artigo
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Artigo
		    <h:graphicImage value="/img/link.gif" style="overflow: visible;"/>: Enviar Arquivo
		    <img src="/shared/img/icones/download.png" style="overflow: visible;"/>: Baixar Arquivo
		</div>

	</h:form>

	<h:outputText value="#{artigo.create}" />


<c:set var="lista" value="${artigo.all}" />
<c:if test="${empty lista}">
	<br />
	<center>
	<span style="color:red;">Nenhum Artigo Encontrado.</span>
	</center>
</c:if>
<c:if test="${not empty lista}">


	<table class="listagem" style="width:100%" border="1">
		<caption class="listagem">Artigos, Periódicos, Jornais e Similares</caption>

		<thead>
			<td></td>
			<td>Titulo</td>
			<td></td>
			<td>Tipo de Participação</td>
			<td>Ano</td>
			<td>Situação</td>

			<td></td>
			<td></td>
			<td></td>
		</thead>
		
		<h:form>
		<c:forEach items="#{artigo.all}" var="item" varStatus="status">
			
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td align="center">
					<c:if test="${item.sequenciaProducao !=null}">
						<ufrn:help img="/img/prodocente/lattes.gif">Origem da Produção: <b>Lattes</b></ufrn:help>
					</c:if>
					<c:if test="${item.sequenciaProducao ==null}">
						<ufrn:help img="/img/icones/producao_menu.gif">Origem da Produção: <b>Prodocente</b></ufrn:help>
					</c:if>
				</td>
				<td>
               		<a4j:commandLink id="showItem" action="#{carregarDadosProducoesMBean.carregar}"
               		oncomplete="Richfaces.showModalPanel('view')" immediate="true" reRender="view">  
               			${item.titulo}
               			<f:param name="id" value="#{item.id}"/>
				    </a4j:commandLink>  
				</td>
				<td align="center">
				<c:if test="${item.idArquivo != null}">
					<a href="/sigaa/verProducao?idProducao=${item.idArquivo}&key=${ sf:generateArquivoKey(item.idArquivo) }" target="_blank">
						  	<img src="/shared/img/icones/download.png" border="0" alt="Baixar Arquivo da Produção" title="Baixar Arquivo da Produção" />
					</a>
				</c:if>
				</td>
				<td>${item.tipoParticipacao.descricao}</td>
				<td>${item.anoReferencia }</td>
				<td>${item.situacaoDesc}</td>

				<td width=20>

					<h:commandLink 	title="Alterar" action="#{artigo.atualizar}">
						<h:graphicImage url="/img/alterar.gif"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
				</td>
				<td width=25>

					<h:commandLink 	title="Remover" action="#{artigo.remover}" onclick="#{confirmDelete}">
						<h:graphicImage url="/img/delete.gif"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>

				</td>
				<td width=25>

				 	<h:commandLink
						title="Enviar Arquivo"
						action="#{producao.preEnviarArquivo}">
						<h:graphicImage url="/img/link.gif"/>
						<f:param name="idProducao" value="#{item.id}"/>
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
<h:outputText value="#{artigo.dropList}" />
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
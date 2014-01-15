<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/painel_detalhar_pd.js"></script>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<%@include file="../../include_painel.jsp"%>
	<c:set var="dirBase" value="/prodocente/producao/" scope="session" />
	
	<h2><a href="${ctx}/prodocente/listar_producao.jsf">
 		<h:graphicImage title="Voltar para Tela de Listar Produções Intelectuais" 
 				value="/img/prodocente/voltarproducoes.gif" style="overflow: visible;" />
 		</a>Participação em Evento
	</h2>
	<h:form>
		<div class="infoAltRem">
			<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;" /><h:commandLink action="#{publicacaoEvento.preCadastrar}" value="Cadastrar" id="cadastrar" /><h:graphicImage value="/img/alterar.gif"style="overflow: visible;" />: Alterar
	    	<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover
	    	<h:graphicImage value="/img/link.gif" style="overflow: visible;" />: Enviar Arquivo
	    	<img src="/shared/img/icones/download.png" style="overflow: visible;" />: Baixar Arquivo da Produção
		</div>
	</h:form>

	<h:outputText value="#{publicacaoEvento.create}" />
	
	<c:set var="lista" value="#{publicacaoEvento.all}" />
	
	<c:choose>
	<c:when test="${empty lista}">
		<br />
		<center><span style="color:red;">Nenhuma Participação em Evento Encontrada.</span></center>
	</c:when>

	<c:otherwise>
	<table class="listagem" style="width:100%" border="1">
		<caption class="listagem">Lista de Participação em Eventos</caption>
		<thead>
			<tr>
				<td></td>
				<td>Título</td>
				<td></td>
				<td>Tipo de Participação</td>
				<td>Nome do Evento</td>
				<td>Ano</td>
				<td>Situação</td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
		</thead>
		<tbody>
		<% int i = 10; %>
		<h:form>
		<c:forEach items="#{lista}" var="item" varStatus="status" end="40">

			<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
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
						<img src="/shared/img/icones/download.png" border="0" alt="Baixar Arquivo da Produção" 
								title="Baixar Arquivo da Produção" /></a>
				</c:if>
				</td>
				<td>${item.tipoParticipacao.descricao}</td>
				<td>${item.nomeEvento}</td>
				<td>${item.anoReferencia}</td>
				<td>${item.situacaoDesc}</td>

				<td width=20>
					<h:commandLink action="#{publicacaoEvento.atualizar}" >
						<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Alterar"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
				</td>						
				<td width=25>
					<h:commandLink action="#{publicacaoEvento.remover}"  onclick="#{confirmDelete}">
						<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
				</td>
				<td width=25>
					<h:commandLink action="#{producao.preEnviarArquivo}" >
						<h:graphicImage value="/img/link.gif" style="overflow: visible;" title="Enviar Arquivo"/>
						<f:param name="idProducao" value="#{item.id}"/>
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>
		</h:form>
		</tbody>
	</table>
	
	<br /><br />
	<h:form id="paginacaoForm">
		<center>
			<h:commandButton image="/img/voltar.gif" actionListener="#{paginacao.previousPage}" 
					rendered="#{paginacao.paginaAtual > 0 }" id="voltar" />
			<h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{paginacao.changePage}" 
					onchange="submit()" immediate="true">
				<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}" />
			</h:selectOneMenu>
			<h:commandButton image="/img/avancar.gif" actionListener="#{paginacao.nextPage}" id="avancar"
					rendered="#{paginacao.paginaAtual < (paginacao.totalPaginas - 1)}" />
			<br />
			<i><h:outputText value="#{paginacao.totalRegistros }" /> Registro(s) Encontrado(s)</i>
		</center>
	</h:form>

	</c:otherwise>
	</c:choose>
	<h:outputText value="#{publicacaoEvento.dropList}" />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

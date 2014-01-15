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
		Texto Didático ou Discussão
	</h2>

	<h:form>
	<div class="infoAltRem">
		 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/><h:commandLink action="#{textoDidatico.preCadastrar}" value="Cadastrar Textos Didáticos ou Discussão"/><h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
	    <h:graphicImage value="/img/link.gif" style="overflow: visible;"/>: Enviar Arquivo
	    <img src="/shared/img/icones/download.png" style="overflow: visible;"/>: Baixar Arquivo
	</div>
	</h:form>
	<h:outputText value="#{textoDidatico.create}" />
	
<c:set var="lista" value="${textoDidatico.all}" />
<c:if test="${empty lista}">
	<br />
	<center>
	<span style="color:red;">Nenhum Texto Didático ou Discussão Encontrado.</span>
	</center>
</c:if>
<c:if test="${not empty lista}">


	<table class=listagem style="width:100%;" border="1">
		<caption class="listagem">Lista de Textos Didáticos ou Discussão</caption>
		<thead>
			<tr>
				<td></td>
				<td style="text-align: left;">Título</td>
				<td></td>
				<td width="10%" style="text-align: left;">Tipo de Texto</td>
				<td style="text-align: left;">Tipo de Participação</td>
				<td style="text-align: left;" width="5%">Informação</td>
				<td style="text-align: left;">Autores</td>
				<td style="text-align: center;">Ano de Referência</td>
				<td style="text-align: left;">Situação</td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
		</thead>
		<h:form>
		<c:forEach items="#{textoDidatico.all}" var="item" varStatus="status">
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
				<td style="text-align: left;">
				  <c:if test="${item.textoDiscussao}">
				    <img style="vertical-align: middle;" src="/sigaa/img/prodocente/textodiscussao_pequeno.gif" alt="Texto Discussão" title="Texto Discussão" />
				    <font style="font-size:9px; color:#656565; vertical-align: middle;" >Discussão</font>
				  </c:if>
				  <c:if test="${not item.textoDiscussao}">
				    <img style="vertical-align: middle;" src="/sigaa/img/prodocente/textodidatico_pequeno.gif" alt="Texto Didático" title="Texto Didádico" />
				    <font style="font-size:9px; color:#656565; vertical-align: middle;" >Didádico</font>
				  </c:if>
				</td>
				<td style="text-align: left;">${item.tipoParticipacao.descricao}</td>
				<td style="text-align: left;"><ufrn:format type="texto" lineWrap="20" valor="${item.informacao}" /></td>
				<td style="text-align: left;">${item.autores}</td>
				<td style="text-align: center;">${item.anoReferencia}</td>
				<td style="text-align: left;">${item.situacaoDesc}</td>

				<td width=20>
					<h:commandLink action="#{textoDidatico.atualizar}" >
						<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Alterar"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
				</td>						
				<td width=25>
					<h:commandLink action="#{textoDidatico.remover}"  onclick="#{confirmDelete}">
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
<h:outputText value="#{textoDidatico.dropList}" />
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
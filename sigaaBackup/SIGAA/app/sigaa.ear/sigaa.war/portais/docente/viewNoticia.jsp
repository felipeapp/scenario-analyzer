<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	.noticia { background: #FFF; height: 98%; padding: 0 10px; overflow: auto; }
	.noticia h1 { text-align: left; margin-bottom: 5px; padding-top: 5px;}
	.noticia p { text-align: left; }
	.noticia p.data { text-align: right; color: #333; font-size: 0.95em; font-style: italic; border-top: 1px solid #AAA; margin-top: 15px; width: 40%; float: right; padding: 2px;}
	.noticia p.arquivo { text-align: left; font-weight: bold; margin: 10px 0 0;}
</style>

<f:view>
	<c:set var="noticia" value="#{ noticiaPortal.noticiaRequest }"/>
	<div class="noticia">
		<h1>  <h:outputText value="#{ noticia.titulo }" converter="convertTexto" /></h1>
		

		<br/>
		<p> <h:outputText value="#{noticia.descricao}" converter="convertTexto" /> </p>

		<c:if test="${ not empty noticia.idArquivo }">
			<p class="arquivo">
			Arquivo anexado: <a href="/sigaa/verFoto?idArquivo=${ noticia.idArquivo }&key=${ sf:generateArquivoKey(noticia.idArquivo) }">Baixar Arquivo</a>
			</p>
		</c:if>
		
		<p class="data"> Notícia cadastrada em <fmt:formatDate value="${ noticia.criadoEm }" pattern="dd/MM/yyyy HH:mm"/></p>
	</div>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
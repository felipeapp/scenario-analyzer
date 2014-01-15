	<%@ include file="./include/cabecalho.jsp" %>
	<f:view  locale="#{portalPublicoDepartamento.lc}">
	<a4j:keepAlive beanName="portalPublicoDepartamento"></a4j:keepAlive>
	<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
	<c:set var="noticiaDpto" value="#{portalPublicoDepartamento.noticiaSiteDetalhes}" />
	<div id="colEsq">
		<%@ include file="./include/menu.jsp" %>
	</div>
	<div id="colDir">
		<%@ include file="./include/departamento.jsp" %>
		<div id="colDirCorpo">
		<!--  INÍCIO CONTEÚDO -->
			
			<h1><h:outputText value="#{idioma.noticias}"/></h1>
			<p class="tit-noticia">
				${noticiaDpto.titulo}
				<br clear="all">
				<b><ufrn:format type="dataHora" valor="${noticiaDpto.dataCadastro}"></ufrn:format></b>
			</p>
			<br clear="all">
			<div class="desc-noticia">
				<c:if test="${not empty noticiaDpto.idFoto}">
					<img src="${ctx}/verFoto?idFoto=${noticiaDpto.idFoto}
					&key=${ sf:generateArquivoKey(noticiaDpto.idFoto) }" 
					align="left" style="padding:10px" >
				</c:if>
				${noticiaDpto.descricao}
			</div>
			<c:if test="${not empty noticiaDpto.idArquivo}">
			<br clear="all">
			<a class="linkArquivo"  href="/sigaa/verProducao?idProducao=${noticiaDpto.idArquivo}&key=${sf:generateArquivoKey(noticiaDpto.idArquivo)}" target="_blank" title="Baixar o arquivo anexo à notícia.">	Baixar arquivo
			</a>
			</c:if>
		<!--  FIM CONTEÚDO  -->	
		</div>
	</div>
	</f:view>
	<%@ include file="../include/rodape.jsp" %>	
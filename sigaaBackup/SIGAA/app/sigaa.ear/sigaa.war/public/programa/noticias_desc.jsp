<%-- Cabeçalho --%>
<%@ include file="include/cabecalho.jsp" %>

<f:view locale="#{portalPublicoPrograma.lc}">
<a4j:keepAlive beanName="portalPublicoPrograma"/>
<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
<c:set var="noticiaPrograma" value="${portalPublicoPrograma.noticiaSiteDetalhes}" />
<%-- topo --%>
<%@ include file="include/programa.jsp" %>
<%-- menu flutuante --%>
<%@ include file="include/menu.jsp" %>
							
<%-- conteudo --%>
<div id="conteudo">
	<div class="titulo">
		<h:outputText value="#{idioma.noticias}"/>
	</div>

	<div class="texto">
		<h2>${noticiaPrograma.titulo}</h2>

		<c:if test="${not empty noticiaPrograma.idFoto}">
			<img class="foto" src="${ctx}/verFoto?idFoto=${noticiaPrograma.idFoto}
			&key=${ sf:generateArquivoKey(noticiaPrograma.idFoto) }">
		</c:if>

		${noticiaPrograma.descricao}
	</div>
	
	<div class="data_cadastrada">
		<b>Notícia cadastrada em:</b> <ufrn:format type="dataHora" valor="${noticiaPrograma.dataCadastro}"/>
	</div>

	<c:if test="${not empty noticiaPrograma.idArquivo}">
		<div class="arquivo_baixar">
			<a href="/sigaa/verProducao?idProducao=${noticiaPrograma.idArquivo}
			&key=${sf:generateArquivoKey(noticiaPrograma.idArquivo)}" 
			target="_blank" title="Baixar o arquivo anexo à notícia.">	
				<img src="img/ico_download.png" align="absmiddle" /> <h:outputText value="#{idioma.downloadArquivo}"/>
			</a>
		</div>
	</c:if>
</div>

<%--  FIM CONTEÚDO  --%>

</f:view>	
<%@ include file="./include/rodape.jsp" %>	
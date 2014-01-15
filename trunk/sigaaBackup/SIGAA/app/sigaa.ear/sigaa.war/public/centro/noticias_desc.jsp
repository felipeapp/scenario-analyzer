	<%@ include file="./include/cabecalho.jsp" %>
	<f:view locale="#{portalPublicoCentro.lc}">
		<a4j:keepAlive beanName="portalPublicoCentro"></a4j:keepAlive>
		<c:set var="noticiaCentro" value="${portalPublicoCentro.noticiaSiteDetalhes}" />
		<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
	<div id="colEsq">
		<%@ include file="./include/menu.jsp" %>
	</div>
	<div id="colDir">
		<%@ include file="./include/centro.jsp" %>
		<div id="colDirCorpo">
		<!--  INÍCIO CONTEÚDO -->
			
			<h1><h:outputText value="#{idioma.noticias}"/></h1>
			<h4>
				${noticiaCentro.titulo}
			</h4> (<ufrn:format type="dataHora" valor="${noticiaCentro.dataCadastro}" ></ufrn:format>)	
			<br/>
			<br>
				<c:if test="${not empty noticiaCentro.idFoto}">
					<img src="${ctx}/verFoto?idFoto=${noticiaCentro.idFoto}
					&key=${ sf:generateArquivoKey(noticiaCentro.idFoto) }" 
					align="left" style="padding:10px" >
				</c:if>
				${noticiaCentro.descricao}
			
			<c:if test="${not empty noticiaCentro.idArquivo}">
				<br clear="all">
				<a class="linkArquivo"  href="/sigaa/verProducao?idProducao=${noticiaCentro.idArquivo}&key=${sf:generateArquivoKey(noticiaCentro.idArquivo)}" target="_blank" title="Baixar o arquivo anexo à notícia.">	Baixar arquivo
				</a>
			</c:if>
		<!--  FIM CONTEÚDO  -->	
		</div>
	</div>
	</f:view>	
	<%@ include file="../include/rodape.jsp" %>	
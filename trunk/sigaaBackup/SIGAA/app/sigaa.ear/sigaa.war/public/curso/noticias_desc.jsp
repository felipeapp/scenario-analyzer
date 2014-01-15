<%-- Cabeçalho --%>
<%@ include file="include/cabecalho.jsp" %>

<f:view locale="#{portalPublicoCurso.lc}">
<a4j:keepAlive beanName="portalPublicoCurso"/>
<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
<c:set var="noticiaCurso" value="${portalPublicoCurso.noticiaSiteDetalhes}" />
<%-- topo --%>
<%@ include file="include/curso.jsp" %>
<%-- menu flutuante --%>
<%@ include file="include/menu.jsp" %>
	 
<%-- conteudo --%>
<div id="conteudo">
	
	<div class="titulo"><h:outputText value="#{idioma.noticias}"/> > ${noticiaCurso.titulo}</div>
				
				
				<br clear="all">
				
				
				<div class="desc-noticia">
					<c:if test="${not empty noticiaCurso.idFoto}">
						<img src="${ctx}/verFoto?idFoto=${noticiaCurso.idFoto}
						&key=${ sf:generateArquivoKey(noticiaCurso.idFoto) }" align="left">
					</c:if>
					${noticiaCurso.descricao}
					<br clear="all">
				</div>
				
				<div class="rodape-noticia">
					Notícia cadastrada em   
					<ufrn:format type="dataHora" valor="${noticiaCurso.dataCadastro}"/>
					&nbsp;
				</div>
				
				<c:if test="${not empty noticiaCurso.idArquivo}">
					<br clear="all">
					<a class="linkArquivo"  href="/sigaa/verProducao?idProducao=${noticiaCurso.idArquivo}
					&key=${sf:generateArquivoKey(noticiaCurso.idArquivo)}" 
					target="_blank" title="Baixar o arquivo anexo à notícia.">	
					Baixar arquivo
					</a>
				</c:if>
				
			<!--  FIM CONTEÚDO  -->	
		</div>
	</div>
	</f:view>
	</div>		
	<%@ include file="./include/rodape.jsp" %>	
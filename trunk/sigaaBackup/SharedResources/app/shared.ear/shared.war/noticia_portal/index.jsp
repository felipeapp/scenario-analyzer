<%@include file="/include/cabecalho_popup.jsp"%>


<style>

 #corpo_noticia {
	font-size:9pt;
	margin-top:15px;
	padding:0;
 }
 
 #corpo_noticia .cabecalho_noticia {
 
 }
 
 #corpo_noticia .titulo {
 	font-size:16pt;
	font-weight:normal;
	letter-spacing:-2px;
 }  

 #corpo_noticia .info {
	color:#747170;
	font-size:8pt;
	
	padding:0;
 }  
  
 #corpo_noticia .conteudo_noticia {
 	font-size:10pt;
 	margin:8px 0;
 }
  
  
  .linha_separador {
	border-color:#CCCCCC -moz-use-text-color -moz-use-text-color;
	border-style:dashed none none;
	border-width:1px 0 0;
	height:1px;
	margin:0;
	padding:0;  
  }
</style>

<f:view>

${ noticiasComum.init }


<h2> ${ noticiasComum.portalNoticia.descricao } > Listar Noticias </h2>


	<h:form>
		<c:forEach var="noticia" items="#{noticiasComum.noticias}">
			<div id="corpo_noticia">
				<div class="cabecalho_noticia">
					<span class="titulo">${ noticia.titulo }</span>
					<p class="info">
						<fmt:formatDate pattern="EEEE, dd MMM yy" value="${ noticia.criadoEm }" />
						<c:if test="${noticia.idArquivo > 0}">
							<h:graphicImage url="/img/attach.png" /> Anexo:
							<a href="/shared/verArquivo?idArquivo=${noticia.idArquivo}&key=${ sf:generateArquivoKey(noticia.idArquivo) }" target="_blank">Download</a>
						</c:if>
						<c:if test="${noticia.publicada}">
							Noticia publicada no portal.
						</c:if>
						<c:if test="${noticia.publicada}">
							Noticia foi despublicada.
						</c:if>						
					</p>
				</div>
				
				<div class="conteudo_noticia">
					<p>
						${ sf:escapeHtml(noticia.descricao) }
					</p>
				</div>
				
				<div class="rodape_noticia">
				
					<hr class="linha_separador" />
				</div>
			</div>
		</c:forEach>
	</h:form>

	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
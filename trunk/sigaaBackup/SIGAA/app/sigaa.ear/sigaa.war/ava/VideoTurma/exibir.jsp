<%@include file="/WEB-INF/jsp/include/cabecalho_popup.jsp"%>
<%@ taglib uri="/tags/a4j" prefix="a4j" %>
<jwr:script src="/javascript/jquery-ui/js/jquery-1.4.2.min.js" />
<script>var J = jQuery.noConflict();</script>
<script type="text/javascript" src="${ctx}/avaliacao/flowplayer/flowplayer-3.2.2.min.js"></script>
<a4j:keepAlive beanName="videoTurma" />

<f:view>
<h2>Vídeo</h2>

<h1 style="font-weight:bold;font-size:14pt;margin-top:20px;">${ videoTurma.video.titulo }</h1>

<h:form id="formAva">

<center>
	<%-- Se for um vídeo externo --%>
	<c:if test="${ videoTurma.video.linkVideo != null }">
		<object width="497" height="280">
			<param name="movie" value="${ videoTurma.video.linkVideo }">
			<param name="allowFullScreen" value="true">
			<param name="allowscriptaccess" value="always">
			<param name="wmode" value="transparent">
			<embed src="${ videoTurma.video.linkVideo }" type="application/x-shockwave-flash" wmode="transparent" allowscriptaccess="always" allowfullscreen="true" width="497" height="280">
		</object>
		<script>
			function verVideo () {
			 	var idRegistro = ".registrarVideo";
				J(idRegistro).trigger("click");				
			}	
		</script>
	</c:if>
	
	<%-- Se for um vídeo enviado pelo docente. --%>
	<c:if test="${ videoTurma.video.link == '' || videoTurma.video.link == null }">
		<c:if test="${!videoTurma.video.converter || videoTurma.video.idArquivoConvertido != null}">
			<h:outputText value="<div class='video' id='player_#{videoTurma.video.id}' style='display:block;width:#{ videoTurma.video.largura }px;height:#{ videoTurma.video.altura }px;'></div>" escape="false" />
			
			<c:set var="idArquivoVideo" value='#{videoTurma.video.idArquivoConvertido != null ? videoTurma.video.idArquivoConvertido : videoTurma.video.idArquivo }' />
			
			<script>				
				$f("player_<h:outputText value='#{videoTurma.video.id}'/>", "/sigaa/avaliacao/flowplayer/flowplayer-3.2.2.swf", {
					clip: {
						url: "/sigaa/verFoto?idFoto=<h:outputText value='#{idArquivoVideo}' />"
							+ escape("&key=<h:outputText value='#{ sf:generateArquivoKey(idArquivoVideo) }' />")
							+ escape("&salvar=false")
							,
					    onStart: function(clip){
					        var idRegistro = ".registrarVideo";
							J(idRegistro).trigger("click");
						},
						autoPlay: false
					},									
					plugins: {
						controls: {
							volume:false,
							playlist:false,
							time:false
						}
					}
									
				});
			</script>
		</c:if>
		<c:if test="${videoTurma.video.converter && videoTurma.video.idArquivoConvertido == null && !videoTurma.video.erro}">
			<div style="margin:10px;border:1px dashed #CCC;padding:10px;">O vídeo está em processo de conversão. Por favor, aguarde alguns minutos e acesse esta página novamente.</div>
		</c:if>
		<c:if test="${videoTurma.video.converter && videoTurma.video.idArquivoConvertido == null && videoTurma.video.erro}">
			<div style="margin:10px;border:1px dashed #CCC;padding:10px;color:#CC0000;">Ocorreu um problema com a conversão do vídeo. Provavelmente o formato enviado não é suportado. Por favor, converta o vídeo para outro formato e tente novamente.</div>
		</c:if>
	</c:if>
	<a4j:commandLink styleClass="registrarVideo" style="display:none;" action="#{videoTurma.verVideoPortalPrincipal}">
         		<f:param name="id" value="#{ videoTurma.video.id }"/>
	</a4j:commandLink>
	
	
</center>

<div>${ videoTurma.video.descricao }</div>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_popup.jsp"%>
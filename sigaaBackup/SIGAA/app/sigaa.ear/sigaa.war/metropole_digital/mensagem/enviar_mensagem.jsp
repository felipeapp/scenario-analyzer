<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>

<script type="text/javascript">
  tinyMCE.init({
        mode : "textareas",
        theme : "simple",
        width : "650",
        height : "200"
 });
</script>

<f:view>

<a4j:keepAlive beanName="notificacoesIMD"/>

${ notificacoesIMD.iniciarFormulario }

<c:if test="${!empty notificacoesIMD.titulo }">
	<h2><ufrn:subSistema /> > ${notificacoesIMD.titulo} </h2>
</c:if>

<c:if test="${empty notificacoesIMD.titulo }">
	<h2><ufrn:subSistema /> > Enviar mensagem </h2>
</c:if>

<c:if test="${!empty notificacoesIMD.descricao }">
	<div id="ajuda" class="descricaoOperacao">    
		${notificacoesIMD.descricao}
	</div>
</c:if>

<%@include file="/metropole_digital/mensagem/formulario.jsp"%>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>



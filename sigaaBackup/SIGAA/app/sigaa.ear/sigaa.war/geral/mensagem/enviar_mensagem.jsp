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

<a4j:keepAlive beanName="notificacoes"/>

${ notificacoes.iniciarFormulario }

<c:if test="${!empty notificacoes.titulo }">
	<h2><ufrn:subSistema /> > ${notificacoes.titulo} </h2>
</c:if>

<c:if test="${empty notificacoes.titulo }">
	<h2><ufrn:subSistema /> > Enviar mensagem </h2>
</c:if>

<c:if test="${!empty notificacoes.descricao }">
	<div id="ajuda" class="descricaoOperacao">    
		${notificacoes.descricao}
	</div>
</c:if>

<%@include file="/geral/mensagem/formulario.jsp"%>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
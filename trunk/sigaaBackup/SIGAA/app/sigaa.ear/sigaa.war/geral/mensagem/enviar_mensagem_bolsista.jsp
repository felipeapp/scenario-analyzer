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

<a4j:keepAlive beanName="notificacoes"/>
<a4j:keepAlive beanName="enviarMsgBolsista"/>
<f:view>

${ notificacoes.iniciarFormulario }

<c:if test="${!empty notificacoes.titulo }">
	<h2><ufrn:subSistema /> > ${notificacoes.titulo} </h2>
</c:if>

<c:if test="${empty notificacoes.titulo }">
	<h2><ufrn:subSistema /> > Enviar mensagem ao bolsista </h2>
</c:if>

<c:if test="${!empty notificacoes.descricao }">
	<div id="ajuda" class="descricaoOperacao">    
		${notificacoes.descricao}
	</div>
</c:if>

<h:form id="form">
	<table class="formulario" width="80%">
		<caption>Enviar Mensagem</caption>
		
		<tr>
			<td>
				<%@include file="/geral/mensagem/formulario.jsp"%>	
			</td>
		</tr>	
	</table>
</h:form>

	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
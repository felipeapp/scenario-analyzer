<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@page import="br.ufrn.arq.negocio.validacao.ListaMensagens"%><c:if test="${!empty mensagensAviso}">
	<div id="painel-erros" style="position: relative; padding-bottom: 10px;">
		<c:if test="${ mensagensAviso.infoPresent }">
		<ul class="info">
	    	<c:forEach items="${ mensagensAviso.infoMessages }" var="msg">
				<li>${ msg.mensagem }</li>
			</c:forEach>
		</ul>
		</c:if>
	
		<c:if test="${ mensagensAviso.warningPresent }">
		<ul class="warning">
			<c:forEach items="${ mensagensAviso.warningMessages }" var="msg">
				<li>${ msg.mensagem }</li>
			</c:forEach>
		</ul>
		</c:if>
	
		<c:if test="${ mensagensAviso.errorPresent }">
		<ul class="erros">
			<c:forEach items="${ mensagensAviso.errorMessages }" var="msg">
				<li>${ msg.mensagem }</li>
			</c:forEach>
		</ul>
		</c:if>
	
		<div id="fechar-painel-erros" style="position: absolute; bottom: 2px; right: 2px;" >
			<a href="javascript://nop/" onclick="$('painel-erros').hide();" style="color: #AAA; font-size: 0.9em;">
				(x) fechar mensagens
			</a>
		</div>
	</div>
	
	<script type="text/javascript">
		function toUpScoll() {
			window.scrollTo(0,0);
		}

		window.onload = toUpScoll();
	</script>
	
	<% request.getSession().setAttribute(ListaMensagens.LISTA_TEMPORARIA_SESSION, request.getSession().getAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION)); %>
	<% request.getSession().removeAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION); %>
</c:if>
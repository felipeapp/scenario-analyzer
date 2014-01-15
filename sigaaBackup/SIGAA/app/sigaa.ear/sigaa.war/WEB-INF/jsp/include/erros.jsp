<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import="br.ufrn.arq.negocio.validacao.ListaMensagens"%>

<script src="${ctx}/javascript/encoding.js" type="text/javascript" ></script>

<c:if test="${not empty mensagensAviso.mensagens}">
	<div id="painel-erros" style="position: relative; padding-bottom: 10px;">
		<c:if test="${ mensagensAviso.errorPresent }">
			<c:choose>
				<c:when test="${acesso.acessibilidade}">
					<c:forEach items="${ mensagensAviso.errorMessages }" var="msg">
						<script type="text/javascript" language="javascript">
							var txt = '${ msg.mensagem }';
							txt = txt.mudarHexa(txt);	
							alert(txt);	
						</script>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<ul class="erros">
						<c:forEach items="${ mensagensAviso.errorMessages }" var="msg">
							<li>${ msg.mensagem }</li>
						</c:forEach>
					</ul>
				</c:otherwise>
			</c:choose>
		</c:if>
	
		<c:if test="${ mensagensAviso.warningPresent }">
			<c:choose>
				<c:when test="${acesso.acessibilidade}">
				  <c:forEach items="${ mensagensAviso.warningMessages }" var="msg">
					<script language="javascript" type="text/javascript">
						alert('${ msg.mensagem }');
					</script>
				  </c:forEach>
				</c:when>
				<c:otherwise>
					<ul class="warning">
						<c:forEach items="${ mensagensAviso.warningMessages }" var="msg">
							<li>${ msg.mensagem }</li>
						</c:forEach>
					</ul>
				</c:otherwise>
			</c:choose>
		</c:if>
	
		<c:if test="${ mensagensAviso.infoPresent }">
			<c:choose>
				<c:when test="${acesso.acessibilidade}">
			    	<c:forEach items="${ mensagensAviso.infoMessages }" var="msg">
						<script language="javascript" type="text/javascript">
							alert('${ msg.mensagem }');
						</script>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<ul class="info">
				    	<c:forEach items="${ mensagensAviso.infoMessages }" var="msg">
							<li>${ msg.mensagem }</li>
						</c:forEach>
					</ul>
				</c:otherwise>
			</c:choose>
		</c:if>
	
		<c:if test="${!acesso.acessibilidade}">
			<div id="fechar-painel-erros" style="position: absolute; bottom: 2px; right: 2px;" >
				<a href="javascript://nop/" onclick="$('painel-erros').hide();" style="color: #AAA; font-size: 0.9em;">
					(x) fechar mensagens
				</a>
			</div>
		</c:if>
	</div>

<% request.getSession().setAttribute(ListaMensagens.LISTA_TEMPORARIA_SESSION, request.getSession().getAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION)); %>
<% request.getSession().removeAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION); %>

</c:if>
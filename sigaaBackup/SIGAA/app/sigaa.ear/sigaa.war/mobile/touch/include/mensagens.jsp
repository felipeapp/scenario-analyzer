<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="br.ufrn.arq.negocio.validacao.ListaMensagens"%>
	
	<p align="center">
	<small>
		<c:if test="${!empty mensagensAviso}">
			<c:if test="${ mensagensAviso.infoPresent}">
			<div style="color: green; margin-right: auto; margin-left: auto; text-align: center">
	    		<c:forEach items="${ mensagensAviso.infoMessages }" var="msg">
					${ msg.mensagem } <br/>
				</c:forEach>
			</div>
			</c:if>
			
			<c:if test="${ mensagensAviso.warningPresent }">
			<div style="color: blue; margin-right: auto; margin-left: auto; text-align: center">
				<c:forEach items="${ mensagensAviso.warningMessages }" var="msg">
					${ msg.mensagem } <br/>
				</c:forEach>
			</div>
			</c:if>
			
			<c:if test="${ mensagensAviso.errorPresent}">
			<div style="color: red; margin-right: auto; margin-left: auto; text-align: center">
				<c:forEach items="${ mensagensAviso.errorMessages}" var="msg">
					${ msg.mensagem } <br/>
				</c:forEach>
			</div>
			</c:if>
		
			<% request.getSession().setAttribute(ListaMensagens.LISTA_TEMPORARIA_SESSION, request.getSession().getAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION)); %>
			<% request.getSession().removeAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION); %>
		</c:if>
		
		<div style="color: green; margin-right: auto; margin-left: auto; text-align: center">
			<c:forEach items="${requestScope.mensagensMobileInformations}" var="msg">
				<c:out value="${msg}" /><br/>
			</c:forEach>
		</div>
		
		<div style="color: blue; margin-right: auto; margin-left: auto; text-align: center">
			<c:forEach items="${requestScope.mensagensMobileWarning}" var="msg">
				<c:out value="${msg}" /><br/>
			</c:forEach>
		</div>
		
		<div style="color: red; margin-right: auto; margin-left: auto; text-align: center">
			<c:forEach items="${requestScope.mensagensMobileErro}" var="msg">
				<c:out value="${msg}" /><br/>
			</c:forEach>
		</div>
				
		<%--<h:messages id="listaMensagens" styleClass="msgErro"/> --%>
				
	</small>
	</p>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<f:view>
	<%@include file="/public/docente/cabecalho.jsp" %>
	<c:set var="msgPermVerTurma" value="#{turmaVirtual.verificaAcessoExterno}"/>
	<h:form id="formListaTurmasDocente">
		<c:choose>
			<c:when test="${empty msgPermVerTurma}">
				<%@include file="/ava/aulas.jsp" %>
			</c:when>
			<c:otherwise>
				<div id="id-docente">
					<h3>${fn:toLowerCase(docente.nome)}</h3>
					<p class="departamento">${docente.unidade.siglaAcademica} - ${docente.unidade.nome}</p>
				</div>
				<center>
					<br clear="all"/>
					<h:outputText value="#{msgPermVerTurma}" styleClass="erro" />
					<br/><br/>
					<a href="${ctx}/public/docente/disciplinas.jsf?siape=${docente.siape}">Voltar</a>		
				</center>
			</c:otherwise>
		</c:choose>
		
	</h:form>
</f:view>
<%@include file="/public/include/rodape.jsp" %>

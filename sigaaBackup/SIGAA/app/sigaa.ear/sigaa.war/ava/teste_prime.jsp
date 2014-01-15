<%@include file="/ava/cabecalho.jsp" %>
<f:view>
	<a4j:keepAlive beanName="configuracoesAva" />

	<%@include file="/ava/menu.jsp" %>
	<h:form id="formAva">
		<%@include file="/ava/aulas.jsp" %>
	<%
		String uagent = request.getHeader("User-Agent").toLowerCase();
		String modo = request.getParameter("modo");
		if (modo == "mobile" || (uagent != null && AmbienteUtils.isMobileUserAgent(uagent))) { %>		
			<hr/>
			<br/>
			<p align="center">
				<h:commandLink value="Modo Mobile" action="#{ turmaVirtual.alterarParaModoMobile }" id="lnkDefinirModoMobile" /> |
				<h:outputText value="Modo Clássico" id="txtDefinirModoClassico" />
			</p>
	<%}%>		
	</h:form>
</f:view>
<%@include file="/ava/rodape.jsp" %>
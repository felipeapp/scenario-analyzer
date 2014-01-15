<%@include file="../include/cabecalho.jsp"%>

<f:view>
	<div data-role="page" id="ultimosEmprestimos" data-theme="b">
		<h:form id="formUltimosEmprestimos">
			<div data-role="header" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<c:if test="${not empty acesso.usuario.servidor }">
						<li><h:commandLink id="lnkVoltarDocente" value="Voltar" action="#{ portalDocenteTouch.forwardBiblioteca}" /></li>
						<li><h:commandLink value="Início" action="#{ portalDocenteTouch.acessarPortal }" id="lnkInicioDocente"/></li>
					</c:if>
					<c:if test="${not empty acesso.usuario.discente }">
						<li><h:commandLink id="lnkVoltarDiscente" value="Voltar" action="#{ portalDiscenteTouch.forwardBiblioteca}" /></li>
						<li><h:commandLink value="Início" action="#{ portalDiscenteTouch.acessarPortal }" id="lnkInicioDiscente"/></li>
					</c:if>
					<li><h:commandLink value="Sair" action="#{ loginMobileTouch.logoff }" id="lnkSair" onclick="#{logout }" /></li>
				</ul>
			</div>
		
			<div data-role="content">
				<p align="center"><strong>Biblioteca</strong></p>
				<p align="center"><strong>Últimos Empréstimos</strong></p>
				
				<%@include file="/mobile/touch/include/mensagens.jsp"%>
		
				<h4><h:outputText value="#{fn:length(operacoesBibliotecaMobileMBean.emprestimos)} Empréstimo(s)." /></h4>
	
			    <div data-role="collapsible-set" data-content-theme="b">
				    <a4j:repeat var="emprestimo" value="#{ operacoesBibliotecaMobileMBean.emprestimos }" rowKeyVar="n">
						<div data-role="collapsible" data-theme="b" data-collapsed="true">
				    		<h3><h:outputText style="white-space: normal;" value="#{operacoesBibliotecaMobileMBean.titulosResumidos[n].titulo}" /></h3>
				    		<b>Autor:</b> <h:outputText value="#{operacoesBibliotecaMobileMBean.titulosResumidos[n].autor}" /><br/>
				    		<b>Data:</b> <h:outputText value="#{emprestimo.dataEmprestimo}" converter="convertData"><f:convertDateTime pattern="dd/MM/yyyy HH:mm"/></h:outputText><br/>
				    		<b>Renovação:</b> <h:outputText value="#{emprestimo.dataRenovacao}" converter="convertData"><f:convertDateTime pattern="dd/MM/yyyy HH:mm"/></h:outputText><br/>
				    		<b>Prazo:</b> <h:outputText value="#{emprestimo.prazo}" converter="convertData"><f:convertDateTime pattern="dd/MM/yyyy HH:mm"/></h:outputText><br/>
				    		<b>Código de Barras:</b> <h:outputText value="#{emprestimo.material.codigoBarras}" /><br/>
				    	</div>	
			    	</a4j:repeat>
				</div>
				
				<c:if test="${not empty acesso.usuario.discente }">
					<script>
						$("#formUltimosEmprestimos\\:lnkVoltarDiscente").attr("data-icon", "back");
						$("#formUltimosEmprestimos\\:lnkInicioDiscente").attr("data-icon", "home");
						$("#formUltimosEmprestimos\\:lnkSair").attr("data-icon", "sair");
					</script>
				</c:if>
			
				<c:if test="${not empty acesso.usuario.servidor }">
					<script>
						$("#formUltimosEmprestimos\\:lnkVoltarDocente").attr("data-icon", "back");
						$("#formUltimosEmprestimos\\:lnkInicioDocente").attr("data-icon", "home");
						$("#formUltimosEmprestimos\\:lnkSair").attr("data-icon", "sair");
					</script>
				</c:if>
			</div>
			
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
		</h:form>
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>
	</div>
</f:view>

<%@include file="../include/rodape.jsp" %>
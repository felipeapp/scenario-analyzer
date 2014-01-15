<%@include file="../include/cabecalho.jsp"%>

<f:view>
	<div data-role="page" id="renovarEmprestimos" data-theme="b">
		<h:form id="formRenovarEmprestimos">
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
				<p align="center"><strong>Empréstimos Renováveis</strong></p>
				
				<%@include file="/mobile/touch/include/mensagens.jsp"%>				

				<h:outputText rendered="#{ not empty operacoesBibliotecaMobileMBean.emprestimosAtivosRenovaveis }"> 
					Selecione os empréstimos que deseja renovar e selecione a opção 'Renovar'.
					<br/><br/>
				</h:outputText>
							
				<div data-role="collapsible-set" data-content-theme="b">
					<a4j:repeat var="emprestimo" value="#{operacoesBibliotecaMobileMBean.emprestimosAtivosRenovaveis }" rowKeyVar="indice" rendered="#{fn:length(operacoesBibliotecaMobileMBean.emprestimosAtivosRenovaveis) > 0}">
						<div data-role="collapsible" data-theme="b" data-collapsed="true" style="padding-left:0px;">
							<h3><h:outputText style="white-space: normal;" value="#{operacoesBibliotecaMobileMBean.titulosResumidos[indice].titulo}" /></h3>
							<b>Autor:</b> <h:outputText value="#{operacoesBibliotecaMobileMBean.titulosResumidos[indice].autor}"/><br/>
							<b>Cód. Barras:</b> <h:outputText value="#{emprestimo.material.codigoBarras}" /><br/>
							<b>Prazo:</b> <h:outputText value="#{emprestimo.prazo}" converter="convertData"><f:convertDateTime pattern="dd/MM/yy HH:mm"/></h:outputText><br/>
							<br/>
							<input type="checkbox" data-theme="b" class="checkbox" id="emprestimo<h:outputText value="#{indice}"/>" name="emprestimosSelecionados" value='<h:outputText value="#{indice}"/>' />
							<label for="emprestimo<h:outputText value="#{indice}"/>">Selecionar</label>
						</div>
					</a4j:repeat>
				</div>
				
				<h4><h:outputText value="Não existem empréstimos ativos renováveis." rendered="#{ empty operacoesBibliotecaMobileMBean.emprestimosAtivosRenovaveis }" /></h4>								
				
				<a4j:commandButton value="Renovar" action="#{operacoesBibliotecaMobileMBean.renovarEmprestimos}" rendered="#{ not empty operacoesBibliotecaMobileMBean.emprestimosAtivosRenovaveis }"/>
				<a4j:commandButton value="Renovar Todos" action="#{operacoesBibliotecaMobileMBean.renovarTodos}" rendered="#{ not empty operacoesBibliotecaMobileMBean.emprestimosAtivosRenovaveis }"/>
				
				<c:if test="${not empty acesso.usuario.discente }">
					<script>
						$("#formRenovarEmprestimos\\:lnkVoltarDiscente").attr("data-icon", "back");
						$("#formRenovarEmprestimos\\:lnkInicioDiscente").attr("data-icon", "home");
						$("#formRenovarEmprestimos\\:lnkSair").attr("data-icon", "sair");
					</script>
				</c:if>
			
				<c:if test="${not empty acesso.usuario.servidor }">
					<script>
						$("#formRenovarEmprestimos\\:lnkVoltarDocente").attr("data-icon", "back");
						$("#formRenovarEmprestimos\\:lnkInicioDocente").attr("data-icon", "home");
						$("#formRenovarEmprestimos\\:lnkSair").attr("data-icon", "sair");
					</script>
				</c:if>
			</div>
			
			<script>
				$(".checkbox").checkboxradio();
			</script>
			
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
		</h:form>
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>	
	</div>
</f:view>
<%@include file="../include/rodape.jsp" %>
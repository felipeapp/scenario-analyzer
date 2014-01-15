<%@include file="../include/cabecalho.jsp"%>

<f:view>
	<div data-role="page" id="visualizarEmprestimos" data-theme="b">
		<h:form id="formVisualizarMateriais">
			<div data-role="header" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<li><h:commandLink id="lnkVoltar" value="Voltar" action="#{operacoesBibliotecaMobileMBean.consultarTitulos}" /></li>
					<c:if test="${not empty acesso.usuario.servidor }">
						<li><h:commandLink value="Início" action="#{ portalDocenteTouch.acessarPortal }" id="lnkInicioDocente"/></li>
					</c:if>
					<c:if test="${not empty acesso.usuario.discente }">
						<li><h:commandLink value="Início" action="#{ portalDiscenteTouch.acessarPortal }" id="lnkInicioDiscente"/></li>
					</c:if>
					<li><h:commandLink value="Sair" action="#{ loginMobileTouch.logoff }" id="lnkSair" onclick="#{logout }" /></li>
				</ul>
			</div>
		
			<div data-role="content">
				<p align="center"><strong>Biblioteca</strong></p>
				<p align="center"><strong>Visualizar Materiais</strong></p>
				
				<%@include file="/mobile/touch/include/mensagens.jsp"%>
		    	
				<a4j:outputPanel rendered="#{! operacoesBibliotecaMobileMBean.periodico}">
					<strong>Existe(m) <h:outputText value="#{fn:length( operacoesBibliotecaMobileMBean.exemplares)}" /> exemplar(es) para esse Título.</strong>
				    <div data-role="collapsible-set" data-content-theme="b">
						<a4j:repeat var="exemplar" value="#{operacoesBibliotecaMobileMBean.exemplares}" rowKeyVar="status">
							<div data-role="collapsible" data-theme="b" data-collapsed="true">
								<h3>
									<h:outputText style="white-space: normal;" value="#{exemplar.codigoBarras}" /> - 
									<h:outputText value="#{exemplar.situacao.descricao}" style="color:green; white-space: normal;" rendered="#{exemplar.disponivel}" />
									<h:outputText value="#{exemplar.situacao.descricao}" style="color:#000; white-space: normal;" rendered="#{!exemplar.disponivel && !exemplar.emprestado}" />
									<h:outputText value="#{exemplar.situacao.descricao}" style="color:red; white-space: normal;" rendered="#{exemplar.emprestado}" />
								</h3>
								
								<b>Tipo de Material:</b> <h:outputText value="#{exemplar.tipoMaterial.descricao}" /><br/>
								<b>Biblioteca:</b> <h:outputText value="#{exemplar.biblioteca.descricao}" /><br/>
								<b>Localização:</b> <h:outputText style="color:#D99C00" value="#{exemplar.numeroChamada}" /><br/>
							</div>
						</a4j:repeat>
					</div>
				</a4j:outputPanel>
			
				<a4j:outputPanel rendered="#{operacoesBibliotecaMobileMBean.periodico}">
					<strong>Existe(m) <h:outputText value="#{fn:length( operacoesBibliotecaMobileMBean.fasciculos)}" /> fascículo(s) para esse Título.</strong>
					<h:outputText value="Assinatura: #{operacoesBibliotecaMobileMBean.assinatura.titulo}" rendered="#{operacoesBibliotecaMobileMBean.assinatura != null}" />
					<div data-role="collapsible-set" data-content-theme="b">
						<a4j:repeat var="fasciculo" value="#{operacoesBibliotecaMobileMBean.fasciculos}" rowKeyVar="status">
							<div data-role="collapsible" data-theme="b" data-collapsed="true">
								<h3>
									<h:outputText style="white-space: normal;" value="#{fasciculo.codigoBarras}" /> - 
									<h:outputText value="#{fasciculo.situacao.descricao}" style="color:green; white-space: normal;" rendered="#{fasciculo.disponivel}" />
									<h:outputText value="#{fasciculo.situacao.descricao}" style="color:#000; white-space: normal;" rendered="#{!fasciculo.disponivel && !fasciculo.emprestado}" />
									<h:outputText value="#{fasciculo.situacao.descricao}" style="color:red; white-space: normal;" rendered="#{fasciculo.emprestado}" />
								</h3>
								
								<b>Tipo de Material:</b> <h:outputText value="#{fasciculo.tipoMaterial.descricao}" /><br/>
								<b>Ano Cron.:</b> <h:outputText value="#{fasciculo.anoCronologico}" /><br/>
								<b>Ano:</b> <h:outputText value="#{fasciculo.ano}" /><br/>
								<b>Volume:</b> <h:outputText value="#{fasciculo.volume}" /><br/>
								<b>Número:</b> <h:outputText value="#{fasciculo.numero}" /><br/>
								<b>Edição:</b> <h:outputText value="#{fasciculo.edicao}" /><br/>
								<b>Biblioteca:</b> <h:outputText value="#{fasciculo.biblioteca.descricao}" /><br/>
								<b>Localização:</b> <h:outputText style="color:#D99C00" value="#{fasciculo.numeroChamada}" /><br/>
							</div>
						</a4j:repeat>
					</div>
				</a4j:outputPanel>
				
				<c:if test="${not empty acesso.usuario.servidor }">
					<script>
						$("#formVisualizarMateriais\\:lnkInicioDocente").attr("data-icon", "home");
					</script>
				</c:if>
				<c:if test="${not empty acesso.usuario.discente }">
					<script>
						$("#formVisualizarMateriais\\:lnkInicioDiscente").attr("data-icon", "home");
					</script>
				</c:if>
				
				<script>
					$("#formVisualizarMateriais\\:lnkVoltar").attr("data-icon", "back");
					$("#formVisualizarMateriais\\:lnkSair").attr("data-icon", "sair");
				</script>
			</div>
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
		</h:form>
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>	
	</div>
</f:view>

<%@include file="../include/rodape.jsp" %>
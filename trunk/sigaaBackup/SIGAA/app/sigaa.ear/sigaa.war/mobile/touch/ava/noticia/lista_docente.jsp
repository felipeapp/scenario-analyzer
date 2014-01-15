<%@include file="/mobile/touch/include/cabecalho.jsp"%>

<f:view>
	<div data-role="page" id="page-lista-noticias-docente" data-theme="b">
		<h:form id="form-lista-noticias-docente">
				<style>
					.ui-icon-nova-noticia {
						background: url("/sigaa/mobile/touch/img/09-chat-2.png") no-repeat;
						background-size: 17px 15px;
					}
				</style>
	
				<div data-role="header" data-position="inline" data-theme="b">
					<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
				</div>
				
				<div data-role="navbar" data-theme="b" data-iconpos="left">
					<ul>
						<li><h:commandLink value="Voltar" action="#{ portalDocenteTouch.exibirTopico }" id="lnkVoltar" /></li>
						<li><h:commandLink value="Início" action="#{ portalDocenteTouch.acessarPortal }" id="lnkInicio"/></li>
						<li><h:commandLink value="Sair" action="#{ loginMobileTouch.logoff }" id="lnkSair" onclick="#{logout }" /></li>
					</ul>
				</div>
			
				<div data-role="content">
					
					<p align="center">
						<strong>
							<small>
								<h:outputText value="#{ portalDocenteTouch.turma.disciplina.codigoNome} (#{portalDocenteTouch.turma.anoPeriodo})" escape="false"/>
							</small>
							<br/><br/>
							Últimas Notícias
						</strong>
					</p>
					
					<%@include file="/mobile/touch/include/mensagens.jsp"%>
					
					<div data-role="collapsible-set" data-content-theme="b">
					    <a4j:repeat var="noticia" value="#{ noticiaTurmaTouch.noticias }" rowKeyVar="n">
							<div data-role="collapsible" data-theme="b" data-collapsed="<h:outputText value="false" rendered="#{ n == 0 }"/><h:outputText value="true" rendered="#{ n > 0 }"/>" style="padding-left:0px;">
					    		<h3><h:outputText style="white-space: normal;" value="#{ noticia.descricao }"  escape="false"/></h3>					    		
					    		<p>
									<strong><h:outputText value="#{ noticia.data }"/></strong>
									<h:outputText value="<p>#{ noticia.noticia }</p>"  escape="false" />
								</p>
					    	</div>	
				    	</a4j:repeat>
					</div>
					
					<c:if test="${empty noticiaTurmaTouch.noticias}">
				    	<p align="center">Não existem notícias cadastradas.</p>
				    </c:if>
				    
				    <c:if test="${not empty noticiaTurmaTouch.noticias}">
				    	<br/>
				    </c:if>
				    
					<h:commandLink value="Cadastrar Notícia" action="#{ noticiaTurmaTouch.iniciarCadastroNoticia }" id="lnkIniciarCadastroNoticia" />
				</div>
				
				<script>
					$("#form-lista-noticias-docente\\:lnkSair").attr("data-icon", "sair");
					$("#form-lista-noticias-docente\\:lnkInicio").attr("data-icon", "home");
					$("#form-lista-noticias-docente\\:lnkVoltar").attr("data-icon", "back");
					
					$("#form-lista-noticias-docente\\:lnkIniciarCadastroNoticia").attr("data-role", "button");
					$("#form-lista-noticias-docente\\:lnkIniciarCadastroNoticia").attr("data-icon", "nova-noticia");
				</script>
				
				<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>	
		  	</h:form>
			<%@include file="/mobile/touch/include/modo_classico.jsp"%>
		</div>
	</f:view>
<%@include file="/mobile/touch/include/rodape.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@page contentType="text/html; charset=ISO-8859-1"%>

<%-- Tags --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/sigaaFunctions" prefix="sf"%>
<%@ taglib uri="/tags/ufrn" prefix="ufrn"%>
<%@ taglib uri="/tags/ajax" prefix="ajax"%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk"%>

<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<%@taglib uri="/tags/jawr" prefix="jwr"%>

<%@taglib uri="/tags/primefaces-p" prefix="p"%>

<%-- Variáveis globais --%>
<jsp:useBean id="dataAtual" class="java.util.Date" scope="page" />

<c:set var="contexto" value="${pageContext.request.contextPath}" scope="application" />
<c:set var="ctx" value="<%= request.getContextPath() %>" />
<c:set var="ajaxScripts" value="false" />

<%@page import="br.ufrn.rh.dominio.Categoria"%>
<%@page import="br.ufrn.sigaa.dominio.Usuario"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<%@page import="br.ufrn.comum.dominio.Sistema"%>

<%-- Início do Código HTML --%>
<html xmlns:p="http://primefaces.prime.com.tr/ui">
	<head>
		<meta HTTP-EQUIV="Pragma" CONTENT="no-cache">
		<fmt:setBundle basename="Mensagens" scope="application" />
		
		<title>${ configSistema['siglaSigaa'] } - ${configSistema['nomeSigaa'] }</title>
		
		<link rel="shortcut icon" href="<html:rewrite page='/img/sigaa.ico'/>" />
		
		<script type="text/javascript" src="/shared/jsBundles/jawr_loader.js"></script>
		<script type="text/javascript">
			JAWR.loader.style('/bundles/css/sigaa_base.css', 'all');
			JAWR.loader.style('/css/ufrn_print.css', 'print');
			
			JAWR.loader.script('/bundles/js/sigaa_base.js');
        </script>
        
		<jwr:style src="/bundles/css/sigaa.css" media="all" />
		
		<c:if test="${ res1024 != null }">
			<style>
				<!--
				html.background {
					background: url(/shared/img/bg_pagina_1024.jpg) repeat-y center;
				}
				
				#container,#cabecalho,#rodape { /* Flexibilidade */
					width: 990px;
				}
				
				#info-sistema {
					width: 982px;
				}
				-->
			</style>
		</c:if>
		
		<script type="text/javascript">
			var mensagem;
			var initMensagem = function() {
				mensagem = new Mensagem();
			}
			YAHOO.util.Event.addListener(window,'load', initMensagem);
		</script>
		<%-- Fim dos imports para abrir chamado --%>
		
		<%-- Evita problemas com o javascript do Primefaces --%>
		<script>var eventYahoo = YAHOO.util.Event; YAHOO.util.Event = null;</script>
	</head>

	<body class="background">
	
		<c:if test="${sessionScope.avisoBrowser != null}">
			<script type="text/javascript">
					window.open('/sigaa/public/navegador.jsp?ajaxRequest=true','','width=670,height=230, top=100, left=100, scrollbars' );
				</script>
			<%
				session.removeAttribute("avisoBrowser");
			%>
		</c:if>
	
	
			<div id="container"><%-- Importa o cabecalho do Ministério da Educação 
			<c:import context="/shared" url="/include/ministerio_educacao.jsp"></c:import>
			--%>
			<div id="cabecalho">
				<div id="info-sistema">
				<h1><span>${ configSistema['siglaInstituicao'] } - ${
				configSistema['siglaSigaa'] } -</span></h1>
				<h3>${ configSistema['nomeSigaa'] }</h3>
			
				<c:if test="${not empty sessionScope.usuario}">
					<div id="tempoSessao"></div>
					<span class="sair-sistema"> <html:link
						action="logar?dispatch=logOff"> SAIR </html:link> </span>
				</c:if></div>
				<c:set var="confirm"
					value="return confirm('Deseja cancelar a operação? Todos os dados digitados não salvos serão perdidos!');"
					scope="application" /> <c:set var="confirmDelete"
					value="return confirm('Confirma a remoção desta informação?');"
					scope="application" />
			
				<div id="painel-usuario" <c:if test="${empty sessionScope.usuario}"> style="height: 20px;" </c:if>>
					<c:if test="${not empty sessionScope.usuario}">
						<div id="menu-usuario">
						<ul>
							<li class="modulos"><c:if
								test="${ not sessionScope.usuario.somenteConsultor }">
								<span id="modulos"> <a href="#" id="show-modulos">
								Módulos </a> </span>
							</c:if></li>
							<c:if test="${sessionScope.acessarCaixaPostal && sessionScope.usuario.vinculoAtivo.numero != 0}">
								<li class="caixa-postal"><a
									href=<%="/sigaa/abrirCaixaPostal.jsf?sistema="+String.valueOf(Sistema.SIGAA)%>> Caixa
								Postal </a></li>
							</c:if>
							<li class="chamado"><c:choose>
								<c:when
									test="${usuario.vinculoAtivo.vinculoDiscente and acesso.totalSistemas == 1}">
									<a href="javascript://nop/"
										onclick="alert('Caro aluno, em caso de dúvidas em relação ao uso do ${ configSistema['siglaSigaa'] }, recorra à coordenação do seu curso.\nEm caso de erro, envie uma mensagem para ${configSistema['emailSuporte']} (não esquecendo de informar seu número de matrícula).');">
									Abrir Chamado </a>
								</c:when>
								<c:otherwise>
									<c:if test="${configSistema['caminhoAberturaChamado']==null}">
										
										<c:if test="${sistemaBean.IProjectAtivo}">
											<a href="#" onclick="window.open('/sigaa/abrirChamado.jsf?tipo=3&sistema=2&idUsuario=${configSistema['idUsuarioChamado']}', 'chamado', 'scrollbars=1,width=830,height=600')">Abrir Chamado</a>
										</c:if>
						
										<c:if test="${not sistemaBean.IProjectAtivo}">
											<a href="#" accesskey="a" onclick="window.open('/sigaa/novoChamadoAdmin.jsf?sistema=2', 'chamado', 'scrollbars=1,width=830,height=600')">Abrir Chamado</a>
										</c:if>
										
									</c:if>
									<c:if test="${configSistema['caminhoAberturaChamado']!=null}">
										<a href="#" onclick="window.open('${configSistema['caminhoAberturaChamado']}', 'chamado', 'scrollbars=1,width=830,height=600')">Abrir Chamado</a>
									</c:if>
								</c:otherwise>
							</c:choose></li>
							<c:if test="${!acesso.administracao}">
								<li class="menus"><c:set var="categoriaDocente"
									value="<%= String.valueOf(Categoria.DOCENTE) %>" /> <c:if
									test="${sessionScope.usuario.vinculoAtivo != null && sessionScope.usuario.vinculoAtivo.vinculoServidor && usuario.servidor.categoria.id == categoriaDocente }">
									<ufrn:link action="verPortalDocente">Menu Docente</ufrn:link>
								</c:if> <c:if test="${sessionScope.usuario.discenteAtivo != null}">
									<ufrn:link action="verPortalDiscente">Menu Discente</ufrn:link>
								</c:if> <c:if test="${ sessionScope.usuario.somenteConsultor }">
									<ufrn:link action="verPortalConsultor">Portal Consultor</ufrn:link>
								</c:if></li>
							</c:if>
							<c:if test="${acesso.administracao}">
								<li class="admin"><ufrn:link action="verMenuAdministracao">Área Admin.</ufrn:link>
								</li>
							</c:if>
							<c:if test="${ not sessionScope.usuario.somenteConsultor }">
								<li class="dados-pessoais"><a href="#"
									onclick="window.open('/sigaa/alterar_dados.jsf','','width=670,height=430, top=100, left=100, scrollbars' )">Alterar
								senha</a></li>
							</c:if>
							<li class="ajuda"><%-- <a href="#" id="show-ajuda" rel="documento.getElementById('urlHelp')"> Ajuda </a>--%>
							<a href="${ configSistema['linkManualSigaa'] }" target="_blank">
							Ajuda </a></li>
						</ul>
						</div>
				
						<div id="info-usuario">
				
							<p class="periodo-atual">
								<c:if test="${paramGestora != null}">
									Semestre atual: <strong>${calendarioAcademico.anoPeriodo}</strong>
								</c:if>
							</p>
					
							<p class="usuario">
								<c:if test="${not empty sessionScope.usuario}">
									<span title="${sessionScope.usuario.pessoa.nome }">
										<ufrn:format type="texto" length="50" valor="${sessionScope.usuario.pessoa.nomeAbreviado }"/>
									</span>
									<c:if test="${sessionScope.usuarioAnterior != null}">
										<ufrn:checkRole usuario="${ sessionScope.usuarioAnterior }" papel="<%= SigaaPapeis.SEDIS %>">
											<a href="${ctx}/logar.do?dispatch=retornarUsuarioSedis">(Deslogar)</a>
										</ufrn:checkRole>
										<ufrn:checkNotRole usuario="${ sessionScope.usuarioAnterior }" papel="<%= SigaaPapeis.SEDIS %>">
											<a href="${ctx}/logar.do?dispatch=retornarUsuario">(Deslogar)</a>
										</ufrn:checkNotRole>
									</c:if>
									<c:if test="${ acesso.multiplosVinculos }">
										<a href="${ctx}/escolhaVinculo.do?dispatch=listar">
											<img src="/shared/img/group_go.png" alt="Alterar vínculo" title="Alterar vínculo" />
										</a>
									</c:if>
								</c:if>
							</p>
					
							<p class="unidade">
								<ufrn:format type="texto" length="60" valor="${sessionScope.usuario.unidade.nome}" /> 
								<c:if test="${not empty sessionScope.usuario.unidade}">
									(${sessionScope.usuario.unidade.codigoFormatado})
								</c:if> 
								<c:if test="${ sessionScope.usuario.somenteConsultor }">
									Área de Conhecimento: <em>${sessionScope.usuario.consultor.areaConhecimentoCnpq.nome }</em>
								</c:if>
							</p>
				
						</div>
					</c:if>
					<div id="menu-principal"></div>
				</div>
			</div>
			<%-- Fim do div 'cabecalho' --%>
		
			<div id="conteudo">
				<%@include file="/WEB-INF/jsp/include/erros.jsp"%>
				
				<% if (request.isSecure()) { %>
					<c:set var="protocolo" value="https" />
				<% } else { %>
					<c:set var="protocolo" value="http" />
				<% } %>
				
				<c:set var="menuDocente" value="http://${header['Host']}/sigaa/portais/docente/menu_docente_externo.jsf" />
				<c:set var="menuDocenteExterno" value="http://${header['Host']}/sigaa/portais/docente/menu_docente_externo.jsf" />
				<c:set var="menuDiscente" value="http://${header['Host']}/sigaa/portais/discente/menu_discente_externo.jsf" />

				<%-- Conteúdo aqui --%>
				
				<f:view>
					<p:resources />

					<style>
						.lineup {
						}
						
						.squad {
							vertical-align: top;
						}
						
						.slot {
							background:#333333; 
							width:90px; 
							height:110px;
							display:block;
						}
						
						.slotHover {
							background:#666666; 
						}
						
						.slotActive {
							background: #FFCC00;
						}
						
						.pf-panel {
							margin: 10px;
						}
					
						.pf-panel-hd,  .pf-panel-bd,  .pf-panel-ft {
							border-color:#000000;
						}
						
						 .pf-panel-hd {
							 background: url(../images/dialoghd.gif);
							 color: #FFFFFF;
						}
						
						 .pf-panel-bd {
							background: #333333;
							color:#CCCCCC;
						}
	
					</style>
							
							<script>
								function handleDrop () { }
							</script>

					<h:form>
						<h:panelGrid columns="2" columnClasses="lineup,squad">
						
							<p:panel header="Squad">
								<h:panelGrid columns="4">
								
									<p:graphicImage id="messi" value="http://www.primefaces.org/showcase/ui/barca/messi_thumb.jpg">
										<p:draggable revert="true" scope="forward"/>
									</p:graphicImage>
									
								</h:panelGrid>
							</p:panel>
							
							<h:panelGroup> 
								<p:outputPanel id="LF" styleClass="slot">
									<p:droppable dropListener="#{grupoDiscentes.gerenciar}" tolerance="fit" activeStyleClass="slotActive" onDropUpdate="messages" scope="forward" onDrop="handleDrop"/>
								</p:outputPanel>
							</h:panelGroup> 
						</h:panelGrid>
						
					</h:form>
			
				</f:view>
				
				<%-- Se a tela não contém componentes primefaces, deve voltar a variável de eventos do yahoo. --%>
				<script>if (YAHOO.util.Event == null) YAHOO.util.Event = eventYahoo;</script>
				
				<div class="clear"></div>
			</div>
			
			<div id="rodape">
				<p>${ configSistema['siglaSigaa']} | Copyright &copy; <%= br.ufrn.arq.util.UFRNUtils.getCopyright(2006) %> - ${configSistema['nomeResponsavelInformatica']} - ${configSistema['siglaInstituicao']} - ${ configSistema['telefoneHelpDesk'] } - <%= br.ufrn.arq.util.AmbienteUtils.getNomeServidorComInstancia() %></p>
			</div>
					
		</div>  <%-- Fim do div 'container' --%>
		
		<c:if test="${ sessionScope.alertErro != null }">
			<script type="text/javascript">
				alert('${ sessionScope.alertErro }');
			</script>
			<c:remove var="alertErro" scope="session"/>
		</c:if>
		<script language="javascript">
		Relogio.init(<%= session.getMaxInactiveInterval() / 60 %>);
		</script>
		
	</body>

</html>
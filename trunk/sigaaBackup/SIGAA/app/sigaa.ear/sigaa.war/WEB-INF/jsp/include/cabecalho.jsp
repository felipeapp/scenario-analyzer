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


<%-- Variáveis globais --%>
<jsp:useBean id="dataAtual" class="java.util.Date" scope="page" />

<c:set var="contexto" value="${pageContext.request.contextPath}"
	scope="application" />
<c:set var="ctx" value="<%= request.getContextPath() %>" />
<c:set var="ajaxScripts" value="false" />

<%@page import="br.ufrn.rh.dominio.Categoria"%>
<%@page import="br.ufrn.sigaa.dominio.Usuario"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<%@page import="br.ufrn.comum.dominio.Sistema"%><html class="background">
<head>
<meta HTTP-EQUIV="Pragma" CONTENT="no-cache">
<fmt:setBundle basename="Mensagens" scope="application" />

<title>${ configSistema['siglaSigaa'] } - ${
configSistema['nomeSigaa'] }</title>
<link rel="shortcut icon" href="<html:rewrite page='/img/sigaa.ico'/>" />

<script type="text/javascript" src="/shared/jsBundles/jawr_loader.js"></script>
<script type="text/javascript">
                JAWR.loader.style('/bundles/css/sigaa_base.css', 'all');
        		JAWR.loader.style('/css/ufrn_print.css', 'print');

        		JAWR.loader.script('/bundles/js/sigaa_base.js');
</script>
<jwr:style src="/bundles/css/sigaa.css" media="all" />

<link rel="alternate stylesheet" type="text/css" media="screen" title="mono" href="/sigaa/css/mono.css" />
<link rel="alternate stylesheet" type="text/css" media="screen" title="contraste" href="/sigaa/css/contraste.css" />
<script src="${ctx}/javascript/styleswitch.js" type="text/javascript" ></script>

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
			
			function isIE(){
			<% 
				String ua = request.getHeader("User-Agent");
					if( ua.contains("MSIE") ) { %> 
						return true;
				<%  } else {%>
						return false;
				<%  }  %>
			}
		</script>
<!-- Fim dos imports para abrir chamado -->
</head>

<body>

<c:if test="${sessionScope.avisoBrowser != null}">
	<script type="text/javascript">
			window.open('/sigaa/public/navegador.jsp?ajaxRequest=true','','width=670,height=230, top=100, left=100, scrollbars' );
		</script>
	<%
		session.removeAttribute("avisoBrowser");
	%>
</c:if>


<c:if
	test="${param.ajaxRequest == null and param.dialog == null and sessionScope.ajaxRequest == null}">
	<div id="container"><%-- Importa o cabecalho do Ministério da Educação 
	<c:import context="/shared" url="/include/ministerio_educacao.jsp"></c:import>
	--%>
	<div id="cabecalho">
	<div id="info-sistema">
	<h1><span>${ configSistema['siglaInstituicao'] } - ${
	configSistema['siglaSigaa'] } -</span></h1>
	<h3>${ configSistema['nomeSigaa'] }</h3>
	<div class="dir">
	<c:if test="${not empty sessionScope.usuario}">
		<% 
			String test = request.getHeader("User-Agent");
			if( test.contains("MSIE") ) { %> 
				<%@include file="/WEB-INF/jsp/include/_mono_contraste.jsp"%>
		<%  } %>
		<%@include file="/WEB-INF/jsp/include/_acessibilidade.jsp"%>
		<span id="tempoSessao"></span>
		<span class="sair-sistema"> <html:link
			action="logar?dispatch=logOff"> SAIR  </html:link> </span>
	</c:if>
	</div>
	</div>
	<c:set var="confirm"
		value="return confirm('Deseja cancelar a operação? Todos os dados digitados não salvos serão perdidos!');"
		scope="application" /> <c:set var="confirmDelete"
		value="return confirm('Confirma a remoção desta informação?');"
		scope="application" />

	<div id="painel-usuario"
		<c:if test="${empty sessionScope.usuario}"> style="height: 20px;" </c:if>>
	<c:if test="${not empty sessionScope.usuario}">
		<div id="menu-usuario">
		<ul>
			<li class="modulos"><c:if
				test="${ not sessionScope.usuario.somenteConsultor }">
				<span id="modulos"> <a href="#" id="show-modulos">
				Módulos </a> </span>
			</c:if></li>
			<c:if test="${sessionScope.usuario.vinculoAtivo.numero != 0}">
				
				<li class="caixa-postal">
				<a href=<%="/sigaa/abrirCaixaPostal.jsf?sistema="+String.valueOf(Sistema.SIGAA)%>> 
					<c:if test="${ empty sessionScope.qtdMsgsNaoLidasCxPostal || sessionScope.qtdMsgsNaoLidasCxPostal <= 0 }">
					Caixa Postal					
					</c:if>
					<c:if test="${ not empty sessionScope.qtdMsgsNaoLidasCxPostal && sessionScope.qtdMsgsNaoLidasCxPostal > 0 }">
						<c:if test="${ sessionScope.qtdMsgsNaoLidasCxPostal > 99 }">
							Cx. Postal <font color="red" style="font-size:0.7em;">(99+)</font>
						</c:if>
						<c:if test="${ sessionScope.qtdMsgsNaoLidasCxPostal <= 99 }">
							Cx. Postal <font color="red" style="font-size:0.7em;">(${ sessionScope.qtdMsgsNaoLidasCxPostal })</font>
						</c:if>
					</c:if> 
				</a>
				</li>
				
			</c:if>
			<li class="chamado">
			<c:choose>
				<c:when test="${!acesso.abrirChamado}">
					<a href="javascript://nop/" onclick="alert('Caro aluno, em caso de dúvidas em relação ao uso do ${ configSistema['siglaSigaa'] }, recorra à coordenação do seu curso.\nEm caso de erro, envie uma mensagem para ${configSistema['emailSuporte']} (não esquecendo de informar seu número de matrícula).');">Abrir Chamado</a>
				</c:when>
				<c:otherwise>
					<c:if test="${configSistema['caminhoAberturaChamado']==null }">
					
						<% if (Sistema.isIprojectAtivo() ) { %>
							<a href="#" onclick="window.open('/sigaa/abrirChamado.jsf?tipo=3&sistema=2&idUsuario=${configSistema['idUsuarioChamado']}', 'chamado', 'scrollbars=1,width=830,height=600')">Abrir Chamado</a>
						<% } else { %>
							<a href="#" onclick="window.open('/sigaa/novoChamadoAdmin.jsf?sistema=2', 'chamado', 'scrollbars=1,width=830,height=600')">Abrir Chamado</a>
						<% } %>						
					</c:if>
					<c:if test="${configSistema['caminhoAberturaChamado']!=null}">
						<a href="#" onclick="window.open('${configSistema['caminhoAberturaChamado']}', 'chamado', 'scrollbars=1,width=700,height=600')">Abrir Chamado</a>
					</c:if>
				</c:otherwise>
			</c:choose>
			</li>
			<c:if test="${!acesso.administracao}">
				<c:if test="${sessionScope.usuario.vinculoAtivo.notNull && usuario.vinculoAtivo.vinculoServidor && usuario.vinculoAtivo.servidor.docente }">
					<li class="menus">
						<ufrn:link action="verPortalDocente">Menu Docente</ufrn:link>
					</li>
				</c:if> <c:if test="${sessionScope.usuario.discenteAtivo != null}">
					<li class="menus">
						<ufrn:link action="verPortalDiscente">Menu Discente</ufrn:link>
					</li>
				</c:if> <c:if test="${ sessionScope.usuario.somenteConsultor }">
					<li class="menus">
						<ufrn:link action="verPortalConsultor">Portal Consultor</ufrn:link>
					</li>
				</c:if>
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

		<p class="periodo-atual"><c:if test="${paramGestora != null}">
					${calendarioAcademico.descricaoCalendario}
		</c:if></p>

		<p class="usuario"><c:if test="${not empty sessionScope.usuario}">
			<span title="${sessionScope.usuario.pessoa.nomeAbreviado }"><ufrn:format type="texto" length="50"
				valor="${sessionScope.usuario.pessoa.nomeAbreviado }"/></span>
			<c:if test="${sessionScope.usuarioAnterior != null}">
				<ufrn:checkRole usuario="${ sessionScope.usuarioAnterior }"
					papel="<%= SigaaPapeis.SEDIS %>">
					<a href="${ctx}/logar.do?dispatch=retornarUsuarioSedis">(Deslogar)</a>
				</ufrn:checkRole>
				<ufrn:checkNotRole usuario="${ sessionScope.usuarioAnterior }"
					papel="<%= SigaaPapeis.SEDIS %>">
					<a href="${ctx}/logar.do?dispatch=retornarUsuario">(Deslogar)</a>
				</ufrn:checkNotRole>
			</c:if>
			<c:if test="${ acesso.multiplosVinculos }">
				<i>
					<small>
						<a href="${ctx}/escolhaVinculo.do?dispatch=listar">Alterar vínculo</a>
					</small>
				</i>
			</c:if>
		</c:if></p>

		<p class="unidade">
			<ufrn:format type="texto" length="60" valor="${sessionScope.usuario.vinculoAtivo.unidade.nome}" />
			<c:if test="${not empty sessionScope.usuario.vinculoAtivo.unidade}"> (${sessionScope.usuario.vinculoAtivo.unidade.codigoFormatado})</c:if>
			<c:if test="${ sessionScope.usuario.somenteConsultor }"> Área de Conhecimento: <em>${sessionScope.usuario.consultor.areaConhecimentoCnpq.nome }</em></c:if>
		</p>

		</div>
	</c:if>
	<div id="menu-principal"></div>
	</div>
	</div>
	<%-- Fim do div 'cabecalho' --%>

	<div id="conteudo">
</c:if>
<%@include file="/WEB-INF/jsp/include/erros.jsp"%>

<%
	if (request.isSecure()) {
%>
<c:set var="protocolo" value="https" />
<%
	} else {
%>
<c:set var="protocolo" value="http" />
<%
	}
%>


<c:set var="menuDocente"
	value="http://${header['Host']}/sigaa/portais/docente/menu_docente_externo.jsf" />
<c:set var="menuDocenteExterno"
	value="http://${header['Host']}/sigaa/portais/docente/menu_docente_externo.jsf" />
<c:set var="menuDiscente"
	value="http://${header['Host']}/sigaa/portais/discente/menu_discente_externo.jsf" />
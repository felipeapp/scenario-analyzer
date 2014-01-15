<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"  "http://www.w3.org/TR/html4/loose.dtd">

<%@page contentType="text/html; charset=ISO-8859-1" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="/tags/sigaaFunctions" prefix="sf"  %>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
<%@ taglib uri="/tags/ajax" prefix="ajax" %>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>

<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<%@taglib uri="/tags/jawr" prefix="jwr"%>

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%
	SimpleDateFormat dia = new SimpleDateFormat("dd");
	SimpleDateFormat mes = new SimpleDateFormat("MMMM");
	SimpleDateFormat ano = new SimpleDateFormat("yyyy");
	Date dataHoje = new Date();
%>

<c:set var="ctx" value="<%= request.getContextPath() %>"/>
<c:set var="confirm" value="return confirm('Deseja cancelar a operação? Todos os dados digitados serão perdidos!');" scope="application"/>

<html>

<head>
	<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
	<title>${ configSistema['siglaSigaa'] } - ${ configSistema['nomeSigaa'] }</title>
	<link rel="shortcut icon" href="${ctx}/img/sigaa.ico">
	
	<script type="text/javascript" src="/shared/jsBundles/jawr_loader.js" ></script>
	<script type="text/javascript">
		JAWR.loader.style('/javascript/ext-2.0.a.1/resources/css/ext-all.css', 'all');
		JAWR.loader.style('/css/public.css','all');
	 	JAWR.loader.script('/bundles/js/sigaa_base.js');
	 	JAWR.loader.script('/bundles/js/ext2_all.js');
	</script>
	<jwr:style src="/public/css/public.css" media="all"/>

	<link href="../css/geral.css" rel="stylesheet" type="text/css" />
	<link href="../css/index.css" rel="stylesheet" type="text/css" />
	<link href="../css/docente.css" rel="stylesheet" type="text/css" />
	<link rel="stylesheet" media="all" href="/shared/css/ufrn.css" type="text/css" />
	<link rel="stylesheet" media="print" href="/shared/css/ufrn_print.css"/>
	
	<style>
		#cabecalho { width: 950px; }
		#acesso { display: none;}
	</style>

</head>

<body>
	<div id="container">
	<%-- Importa o cabecalho do Ministério da Educação 
	<c:import context="/shared" url="/include/ministerio_educacao.jsp"></c:import>--%>

	<div id="container-inner">

	<div id="cabecalho">
		<div id="identificacao">
			<span class="ufrn">
				${ configSistema['siglaInstituicao'] } &rsaquo; 
				<a href="${ctx}/public" title="Página Inicial do ${ configSistema['siglaSigaa'] }">
				${ configSistema['siglaSigaa'] } - ${ configSistema['nomeSigaa'] }
				</a>
			</span>
			<span class="data">
				 ${ configSistema['cidadeInstituicao'] }, 
				 <%= dia.format(dataHoje) %> de <%= mes.format(dataHoje) %> de 
				 <%= ano.format(dataHoje) %> 
			</span>
		</div>
	</div>


	<div id="corpo">
	<%@include file="/WEB-INF/jsp/include/erros.jsp"%>
	${portalPublicoDocente.iniciar}
	<c:set var="portal" value="#{portalPublicoDocente}" />
	<c:set var="docente" value="#{portalPublicoDocente.docente}" />

	<div id="left" class="barra_professor">
		<div class="foto_professor">
			<c:if test="${portalPublicoDocente.usuario.idFoto != null}">
				<img src="${ctx}/verFoto?idFoto=${portalPublicoDocente.usuario.idFoto}&key=${ sf:generateArquivoKey(portalPublicoDocente.usuario.idFoto) }" height="120"/>
			</c:if>
			<c:if test="${portalPublicoDocente.usuario.idFoto == null}">
				<img src="${ctx}/img/no_picture.png" height="120"/>
			</c:if>
		</div>
		<h3>${docente.nome}</h3>
		<h3 class="departamento"> ${docente.unidade.nome} </h3>
		<h3 class="situacao">
		</h3>
		<ul class="menu_professor">
			<li class="perfil_pessoal"><a href="${ctx}/public/docente/portal.jsf?siape=${docente.siape}">Perfil Pessoal</a></li>
			<li class="publicacoes"><a href="${ctx}/public/docente/producao.jsf?siape=${docente.siape}">Produção Intelectual</a></li>
			<li class="disciplinas_ministradas"><a href="${ctx}/public/docente/disciplinas.jsf?siape=${docente.siape}">Disciplinas Ministradas</a></li>
			<%-- <li class="listagem_pid"><a href="${ctx}/public/docente/listagem_pids.jsf?siape=${docente.siape}">Plano Individual Docente</a></li> --%>
			<li class="projetos_pesquisa"><a href="${ctx}/public/docente/pesquisa.jsf?siape=${docente.siape}">Projetos de Pesquisa</a></li>
			<li class="projetos_extensao"><a href="${ctx}/public/docente/extensao.jsf?siape=${docente.siape}">Atividades de Extens&atilde;o</a></li>
			<li class="projetos_monitoria"><a href="${ctx}/public/docente/monitoria.jsf?siape=${docente.siape}">Projetos de Monitoria</a></li>
		</ul>

		<a class="home-link" href="${ctx}/public/" alt="Página inicial" title="Página inicial"> Ir ao Menu Principal </a>
	</div>

	<div id="center">

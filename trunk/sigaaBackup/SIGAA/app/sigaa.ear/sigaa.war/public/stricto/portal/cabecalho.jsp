<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@page contentType="text/html; charset=ISO-8859-1" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="/tags/sigaaFunctions" prefix="sf"  %>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
<%@ taglib uri="/tags/ajax" prefix="ajax" %>
<%@ taglib uri="/tags/jawr" prefix="jwr" %>

<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%
	SimpleDateFormat dia = new SimpleDateFormat("dd");
	SimpleDateFormat mes = new SimpleDateFormat("MMMM");
	SimpleDateFormat ano = new SimpleDateFormat("yyyy");
	Date dataHoje = new Date();
%>

<c:set var="ctx" value="<%= request.getContextPath() %>"/>

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
</head>

<body>

	<div id="container">
	<div id="container-inner">

	<div id="cabecalho">
		<div id="info">
			<span class="ufrn"> ${ configSistema['siglaInstituicao'] } </span>
			<span class="data"> ${ configSistema['cidadeInstituicao'] }, <%= dia.format(dataHoje) %> de <%= mes.format(dataHoje) %> de <%= ano.format(dataHoje) %> </span>
		</div>
		<div id="identificacao">
			<a id="home" href="${ctx}/public/" alt="Página inicial" title="Página inicial"> </a>
			<div id="acesso">
				<a href="/sigaa/" alt="Clique aqui para logar-se no SIGAA" title="Clique aqui para logar-se no SIGAA">
					Clique aqui para entrar no sistema
				</a>
			</div>
		</div>
	</div>


	<div id="corpo">
	<%@include file="/WEB-INF/jsp/include/erros.jsp"%>
	

<style>
	#cabecalho { width: 950px; }
	#acesso { display: none;}
</style>



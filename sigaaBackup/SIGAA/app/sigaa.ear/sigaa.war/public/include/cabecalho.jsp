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
	
	
</head>

<body>
	<div id="container">
	<%-- Importa o cabecalho do Ministério da Educação 
	<c:import context="/shared" url="/include/ministerio_educacao.jsp"></c:import>--%>

	<div id="container-inner">

	<div id="cabecalho">
		<div id="logo">
				<a href="${ctx}/public/"> <img src="${ configSistema['brasaoInstituicao'] }" height="85%"/> </a>
		</div>
		<div id="identificacao">
			<span class="ufrn"> ${ configSistema['nomeInstituicao'] } </span>
			<span class="data">
				 ${ configSistema['cidadeInstituicao'] }, 
				 <%= dia.format(dataHoje) %> de <%= mes.format(dataHoje) %> de 
				 <%= ano.format(dataHoje) %> 
			</span>
			<a id="home" href="${ctx}/public/" title="Página inicial" style="width: 450px;"> </a>
			<div style="margin-left: 55%; margin-top: 30px;">
				<ul>
					<li>
						<a href="${ configSistema['linkSigaa'] }/sigaa/verTelaLogin.do?acessibilidade=true" alt="Acessível para Deficientes visuais" style="font-size: 10px; 
						font-style: italic;">
							<img src="${ctx}/img/modoAcessibilidade.png" width="30px;" height="30px;" align="left" style="margin-right: 5px;" />
							Acessível para <br /> Deficientes visuais 
						</a>
					</li>
				</ul>
			</div>
			<div id="acesso">
				<ul>
					<c:if test="${!escondeEntrarSistema}">
						<li class="acesso_esq_bg"></li>
						<li class="acesso_centro_bg">
							<a href="/sigaa/" alt="Clique aqui para logar-se no ${ configSistema['siglaSigaa'] }" title="Clique aqui para logar-se no ${ configSistema['siglaSigaa'] }">
								Entrar no sistema
							</a>
						</li>
						<li class="acesso_dir_bg"></li>
					</c:if>
				</ul>
			</div>
		</div>
		<br clear="all"/>
	</div>


	<div id="corpo">
	<%@include file="/WEB-INF/jsp/include/erros.jsp"%>
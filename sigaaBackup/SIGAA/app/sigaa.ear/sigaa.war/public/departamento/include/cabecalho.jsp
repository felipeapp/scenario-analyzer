<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"  "http://www.w3.org/TR/html4/loose.dtd">
<%@page contentType="text/html; charset=utf-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="/tags/sigaaFunctions" prefix="sf"  %>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
<%@ taglib uri="/tags/ajax" prefix="ajax" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<%@taglib uri="/tags/jawr" prefix="jwr"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>


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
	<script type="text/javascript" src="/shared/jsBundles/jawr_loader.js" ></script>
	<script type="text/javascript">
		JAWR.loader.style('/javascript/ext-2.0.a.1/resources/css/ext-all.css', 'all');
		JAWR.loader.style('/css/public.css','all');
	 	JAWR.loader.script('/bundles/js/sigaa_base.js');
	 	JAWR.loader.script('/bundles/js/ext2_all.js');
	</script>
	<jwr:style src="/public/css/public.css" media="all"/>
	<jwr:style src="/public/css/departamento.css" media="all"/>

</head>
${portalPublicoDepartamento.iniciar}
<body>
		<div id="container">
		
			<%-- Importa o cabecalho do Ministério da Educação 
			<c:import context="/shared" url="/include/ministerio_educacao.jsp"></c:import>	--%>
			
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
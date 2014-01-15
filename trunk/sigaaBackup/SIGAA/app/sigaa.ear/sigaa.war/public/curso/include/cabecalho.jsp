<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"  "http://www.w3.org/TR/html4/loose.dtd">
<%@page contentType="text/html; charset=utf-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="/tags/sigaaFunctions" prefix="sf"  %>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
<%@ taglib uri="/tags/ajax" prefix="ajax" %>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<%@ taglib uri="/tags/jawr" prefix="jwr"%>
<%@ taglib uri="/tags/rich" prefix="rich" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%
	SimpleDateFormat dia = new SimpleDateFormat("dd");
	SimpleDateFormat mes = new SimpleDateFormat("MMMM");
	SimpleDateFormat ano = new SimpleDateFormat("yyyy");
	Date dataHoje = new Date();
%>
${portalPublicoCurso.iniciar}
<c:set var="ctx" value="<%= request.getContextPath() %>"/>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>${ configSistema['siglaSigaa'] } - ${ configSistema['nomeSigaa'] }</title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
    <meta name="author" content="Andressa Kroeff Pires - SINFO">
    <link rev=made href="mailto:andressa@info.ufrn.br">
    <script type="text/javascript" src="/shared/jsBundles/jawr_loader.js"></script>
	<script type="text/javascript">
	     		JAWR.loader.script('/bundles/js/sigaa_base.js');
	</script>
    <link rel="stylesheet" href="${ctx}/public/curso/css/template.css" />
    <%@ include file="/public/curso/css/curso.css.jsp" %>
</head>
<body>
	<div id="bg">
    	<div id="bg_rodape">
        	<div id="estrutura">
				<%-- bloqueio de tela para a visualizacao apos alteracao de cores do estilo --%>
			    <%@ include file="bloqueio_tela.jsp" %>
            	

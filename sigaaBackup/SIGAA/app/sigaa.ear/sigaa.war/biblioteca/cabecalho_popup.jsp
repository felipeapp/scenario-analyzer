<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>    <%--   fechado no rodape_popup.jsp  --%>

	<head>  

		<%@page contentType="text/html; charset=ISO-8859-1" %>
		
		<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
		<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
		<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
		
		<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
		<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
		<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>
		
		<%@taglib uri="/tags/rich" prefix="rich"%>
		<%@taglib uri="/tags/a4j" prefix="a4j"%>
		
		<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
		
		<%@taglib uri="/tags/jawr" prefix="jwr"%>
		
		<c:set var="ctx" value="<%= request.getContextPath() %>"/>
		
		<script type="text/javascript" src="/shared/jsBundles/jawr_loader.js" ></script>
		<script type="text/javascript">
		        JAWR.loader.style('/bundles/css/sigaa_base.css');
				JAWR.loader.style('/css/ufrn_print.css', 'print');
		
				JAWR.loader.script('/bundles/js/sigaa_base.js');
		</script>
		
		<jwr:style src="/bundles/css/sigaa.css"/>
		
		<style type="text/css">
			#container-popup{
				width: 95%;
			}
		</style>
		
	</head>

<body <%--   fechado no rodape_popup.jsp  --%>


<%@include file="/WEB-INF/jsp/include/erros.jsp"%>
		
<div id="container-popup" >       <%--   fechado no rodape_popup.jsp  --%>
	<div id="conteudo-popup" >    <%--   fechado no rodape_popup.jsp  --%>
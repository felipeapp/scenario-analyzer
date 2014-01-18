<%@ page contentType="text/html; charset=ISO-8859-1" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib uri="/tags/jawr" prefix="jwr"%>
<%@ taglib uri="/tags/ufrnFunctions" prefix="sf" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html>
<head>
	<title>Diretoria de Sistemas</title>
	<script type="text/javascript" src="/shared/jsBundles/jawr_loader.js" ></script>
	<script type="text/javascript">
		JAWR.loader.style('/bundles/css/comum_base.css','all');
		JAWR.loader.style('/css/ufrn_print.css', 'print');

		JAWR.loader.script('/bundles/js/comum_base.js');
	</script>
</head>

<body>

<div id="container" style="width: 670px;">

	<div id="conteudo">
		<%@include file="/include/erros.jsp"%>

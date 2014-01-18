<%@ page contentType="text/html; charset=ISO-8859-1" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>

<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@taglib uri="/tags/struts-logic" prefix="logic"%>

<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
<%@ taglib uri="/tags/ufrnFunctions" prefix="sf" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>

<%@ taglib uri="/tags/rich" prefix="rich"%>
<%@ taglib uri="/tags/a4j" prefix="a4j"%>

<%@taglib uri="/tags/jawr" prefix="jwr"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html>
<head>
	<title>Diretoria de Sistemas</title>

	<script type="text/javascript" src="/shared/jsBundles/jawr_loader.js" ></script>
    <script type="text/javascript">
        JAWR.loader.style('/bundles/css/comum_base.css');
  		JAWR.loader.script('/bundles/js/comum_base.js');
    </script>

	<style>
		#operacoes-subsistema.reduzido .aba { height: 200px; }	
	</style>
	
	<c:set var="confirm" value="return confirm('Deseja cancelar a Operação? Todos os dados digitados serão perdidos!');" scope="application"/>
	<c:set var="confirmDelete" value="return confirm('Confirma a remoção desta informação?');" scope="application"/>
</head>

<body>

<div id="container">

	<!-- Cabeçalho -->
	<div id="cabecalho">

	</div>
	<!-- Fim: Cabeçalho -->

	<div id="conteudo">
		<%@include file="/include/erros.jsp"%>

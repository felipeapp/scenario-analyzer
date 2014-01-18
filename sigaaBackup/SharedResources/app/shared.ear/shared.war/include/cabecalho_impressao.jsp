<%@ page contentType="text/html; charset=ISO-8859-1" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>

<html class="background">

<head>
	<title>Diretoria de Sistemas</title>
</head>

<link href="/shared/css/ufrn.css" rel="stylesheet" type="text/css"/>
<link href="/shared/css/ufrn_relatorio.css" rel="stylesheet" type="text/css"/>
<link rel="stylesheet" media="print" href="/shared/css/ufrn_print.css"/>

<body>

<div id="relatorio-container">

		<div id="relatorio-cabecalho">

			<div id="ufrn" ><img src="/shared/img/ufrn.gif"/>
				<div style="font-size: 0.9em;"  class="naoImprimir"><sipac:linkSubsistema nomeAbreviado="true"/></div>
			</div>
			<div id="sinfo"><img src="/shared/img/sinfo.gif"/>  </div>
			<div id="texto">
				${ configSistema['ministerio'] } <br/>
				${ configSistema['nomeInstituicao'] } <br>
				
				<br/><br/>
			</div>

		</div>
		<div id="relatorio">

		<br/><br/>

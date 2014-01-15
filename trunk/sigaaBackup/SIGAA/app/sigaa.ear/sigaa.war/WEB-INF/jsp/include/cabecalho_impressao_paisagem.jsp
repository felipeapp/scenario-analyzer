<%@page isELIgnored ="false" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="/tags/struts-logic" prefix="logic"  %>
<%@ taglib uri="/tags/struts-bean" prefix="bean"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/tags/sigaaFunctions" prefix="sf"  %>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
<%@ taglib uri="/tags/ajax" prefix="ajax" %>

<%@taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@page contentType="text/html; charset=ISO-8859-1" %>

<jsp:useBean id="dataAtual" class="java.util.Date" scope="page" />

<html>

<head>
	<title>${ configSistema['nomeSigaa'] }</title>
	<script type="text/javascript" src="/shared/javascript/sigaa.js"> </script>
</head>

<link href="/shared/css/ufrn_relatorio.css" rel="stylesheet" type="text/css"/>
<link href="/shared/css/ufrn.css" rel="stylesheet" type="text/css"/>
<link rel="stylesheet" media="print" href="/shared/css/ufrn_print.css"/>
<body>

<div id="relatorio-paisagem-container">

		<div id="relatorio-cabecalho">
			<div id="ufrn"><img src="/shared/img/ufrn.gif"/><br/><ufrn:subSistema/> </div>
			<div id="sinfo"><img src="/shared/img/sinfo.gif"/>  </div>
			<div id="texto">
				${ configSistema['nomeInstituicao'] }<br>
				${ configSistema['nomeSigaa'] }<br>

				<c:if test="${not empty complemento}">
					<c:forEach items="${complemento}" var="complem" varStatus="loop">
						${complem }
					</c:forEach>
				</c:if>
				<br />
				<span class="dataAtual"> Emitido em <ufrn:format type="dataHora" name="dataAtual" /> </span>
			</div>
			<div class="clear"> </div>
		</div>
		<div id="relatorio">

		<br/>

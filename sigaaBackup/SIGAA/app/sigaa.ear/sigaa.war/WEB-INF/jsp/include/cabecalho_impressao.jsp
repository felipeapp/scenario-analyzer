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
<%@taglib uri="/tags/jawr" prefix="jwr"%>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@page contentType="text/html; charset=ISO-8859-1" %>

<jsp:useBean id="dataAtual" class="java.util.Date" scope="request" />
<c:set var="ctx" value="<%= request.getContextPath() %>"/>

<html class="background">

<head>
	<title>${ configSistema['nomeSigaa'] }</title>
	
	<script type="text/javascript" src="/shared/jsBundles/jawr_loader.js" ></script>
	<script type="text/javascript">
		JAWR.loader.style('/bundles/css/sigaa_base.css','all');
		JAWR.loader.style('/css/ufrn_relatorio.css','all');
		JAWR.loader.style('/css/ufrn_print.css', 'print');

		JAWR.loader.script('/bundles/js/sigaa_base.js');
	</script>
	<jwr:style src="/css/geral.css" media="all" />
	<link rel="stylesheet" media="all" href="/shared/css/ufrn.css"/>
</head>

<body>

<div id="relatorio-container">

		<div id="relatorio-cabecalho">
			<div id="ufrn"><img src="/shared/img/ufrn.gif" height="40"/> <br /> <ufrn:subSistema/></div>
			<div id="sinfo"><img src="/shared/img/sinfo.gif" height="50"/>  </div>
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

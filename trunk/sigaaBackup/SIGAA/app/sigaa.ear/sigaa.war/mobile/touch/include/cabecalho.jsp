<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib uri="/tags/ufrn" prefix="ufrn" %>
<%@taglib uri="/tags/sigaa" prefix="sigaa" %>
<%@taglib uri="/tags/sigaaFunctions" prefix="sf"%>


<style type="text/css">
	@import url("/sigaa/mobile/touch/css/touch.css");
</style>


<html> 
  <head> 
  	<link rel="stylesheet" href="/shared/javascript/jquerymobile/css/sigTheme.min.css" />
  	<link rel="stylesheet" href="/shared/javascript/jquerymobile/css/jquery.mobile.structure-1.1.1.min.css" />
  	<script src="/shared/javascript/jquerymobile/javascript/jquery-1.7.1.min.js"></script>
  	<script src="/shared/javascript/jquerymobile/javascript/jquery.mobile-1.1.1.min.js"></script>
  	
  	<%--<link rel="stylesheet" href="/shared/javascript/jquerymobile/css/sigTheme.min.css" />
	<link rel="stylesheet" href="/shared/javascript/jquerymobile/css/jquery.mobile.structure-1.1.0.min.css" />
	<script src="/shared/javascript/jquerymobile/javascript/jquery-1.7.1.min.js"></script>
	<script src="/shared/javascript/jquerymobile/javascript/jquery.mobile-1.1.0-rc.1.min.js"></script>--%>
	
	<c:set var="logout"
		value="if (!confirm('Tem certeza de que deseja sair do sistema?')) return false;"
		scope="application" />
  	
  	<%--<meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1"> --%>
  	<meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1, maximum-scale=1">
  
  	<title>${ configSistema['siglaSigaa']} Mobile</title>
  	
  	<style>
		.ui-icon-sair {
			background: url("/sigaa/mobile/touch/img/sair.PNG") no-repeat;
			background-size: 18px 18px;
		}
	</style>
  </head>
  <body>
  	<c:if test="${modo == 'classico' }">
  	<% 
		session.setAttribute("modo", "mobile");
  	%>	
  	</c:if>
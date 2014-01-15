<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"  "http://www.w3.org/TR/html4/loose.dtd">
<%@page contentType="text/html; charset=iso-8859-1" %>

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
<c:set var="ctx" value="<%= request.getContextPath() %>"/>
<html xmlns="http://www.w3.org/1999/xhtml">

	<head>
		<title>${ configSistema['siglaSigaa'] } - ${ configSistema['nomeSigaa'] }</title>
   	 <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
		<script type="text/javascript" src="/shared/jsBundles/jawr_loader.js" ></script>
		<script type="text/javascript">
			JAWR.loader.style('/javascript/ext-2.0.a.1/resources/css/ext-all.css', 'all');
			JAWR.loader.style('/css/public.css','all');
		 	JAWR.loader.script('/bundles/js/sigaa_base.js');
		 	JAWR.loader.script('/bundles/js/ext2_all.js');
		</script>
		<jwr:style src="/public/css/public.css" media="all"/>
		<jwr:style src="/public/css/curso_aberto.css" media="all"/>
		
	</head>

	<body>
		<div id="container">
		
			<%-- Importa o cabecalho do Ministério da Educação 
			<c:import context="/shared" url="/include/ministerio_educacao.jsp"></c:import>	--%>
			
			<div id="container-inner">
			
				<div id="cabecalho">
					<div id="identificacao">
						<span class="ufrn">
							${ configSistema['siglaInstituicao'] } &rsaquo; 
							<a href="${ctx}/" title="Página Inicial do ${ configSistema['siglaSigaa'] }">
							${ configSistema['siglaSigaa'] } - ${ configSistema['nomeSigaa'] }
							</a>
						</span>
						<span class="data">
							 ${ configSistema['cidadeInstituicao'] }, 
							 <%= dia.format(dataHoje) %> de <%= mes.format(dataHoje) %> de 
							 <%= ano.format(dataHoje) %> 
				
						</span>
					</div>
					<br clear="all"/>
					<a id="logoCursoAberto" href="./cursosabertos.jsf"></a></span>
					<span id="tituloCursoAberto"></span>
				</div>
				
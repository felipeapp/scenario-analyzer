<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page contentType="text/html; charset=ISO-8859-1" %>

<%-- Tags --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>

<%--@ taglib uri="/tags/ajax" prefix="ajax" --%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>

<%@ taglib uri="/tags/sigaaFunctions" prefix="sf" %>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
<%@ taglib uri="/tags/rich" prefix="rich" %>
<%@ taglib uri="/tags/a4j" prefix="a4j" %>
<%@taglib uri="/tags/jawr" prefix="jwr"%>

<%@ taglib uri="/tags/primefaces-p" prefix="p"%>

<%-- Variáveis globais --%>
<jsp:useBean id="dataAtual" class="java.util.Date" scope="page" />

<c:set var="contexto" value="${pageContext.request.contextPath}" scope="application"/>
<c:set var="ctx" value="<%= request.getContextPath() %>"/>
<c:set var="noticias" value="${ turmaVirtual.noticias }"/>

<%@page import="br.ufrn.rh.dominio.Categoria"%>
<%@page import="br.ufrn.sigaa.dominio.Usuario"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<jwr:style src="/css/ensino/pid.css" media="all"/>
<jwr:style src="/css/ensino/pid_impressao.css" media="all"/>

<f:view>
	
	<c:set var="_pidBean" value="#{consultaPidMBean}" />
	<%@include file="/pid/busca_pid/_view.jsp"%>

</f:view>
<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
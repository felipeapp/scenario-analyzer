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
	<title>Sistema Integrado de Gestão de Atividades Acadêmicas</title>
	
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
			<div id="ufrn"><img src="/shared/img/ufrn.gif"/><br/><div class="naoImprimir"><ufrn:subSistema/></div></div>
			<div id="sinfo"><img src="/shared/img/sinfo.gif"/>  </div>
			<div id="texto">
				${ configSistema['nomeInstituicao'] }<br>
				Sistema Integrado de Gestão de Atividades Acadêmicas<br>

				<c:if test="${not empty complemento}">
					<c:forEach items="${complemento}" var="complem" varStatus="loop">
						-${complem }
					</c:forEach>
				</c:if>
				<br />
				<span class="dataAtual"> Emitido em <ufrn:format type="dataHora" name="dataAtual" /> </span>
			</div>
			<div class="clear"> </div>
		</div>
		<div id="relatorio">

		<br/>

<style>
.tituloDeclaracao {
	margin-top: 1cm;
	font-family: Verdana, sans-serif;
	font-size: 16px;
	font-weight: bold;
	text-align: center;
}

.tituloMembros {
	margin-top: 1cm;
	font-family: Verdana, sans-serif;
	font-size: 14px;
	font-weight: bold;
	text-align: center;
}

.paragrafo {
	font-family: Verdana, sans-serif;
	font-size: 12px;
	text-indent: 2cm;
	text-align: justify;
}

.membro {
	font-size: 12px;
	text-indent: 2cm;
	text-align: justify;
}
.titulo{
	text-align: center;
}
.assinatura {
	margin-top: 1cm;
	text-align: center;
	padding: 2px;
	border-top: 1px solid #000;
	width: 70%;
}
</style>
<f:view>
<br/><br/>
<div class="tituloDeclaracao">DECLARAÇÃO</div>
<br><br>
<div class="paragrafo">
Declaramos que ${declaracaoParticipacaoBanca.obj.sexo ? " o Prof. Dr. " : " a Profa. Dra. " }
    ${declaracaoParticipacaoBanca.obj.participante}, 
    ${declaracaoParticipacaoBanca.obj.cpfPassaporte}, participou como
	${declaracaoParticipacaoBanca.obj.tipoParticipacao} da Comissão Examinadora 
	${declaracaoParticipacaoBanca.obj.tipoBanca} de
	${declaracaoParticipacaoBanca.obj.nivelBanca} do(a) graduando(a)
	${declaracaoParticipacaoBanca.obj.discente}, intitulada:</div>
	<br/>
	<div class="titulo">
	${declaracaoParticipacaoBanca.obj.titulo}</div>
	<br/>
	<div class="paragrafo">no	${declaracaoParticipacaoBanca.obj.curso} do
	${declaracaoParticipacaoBanca.obj.unidade} da ${ configSistema['nomeInstituicao'] }, em sessão pública realizada no dia
	${declaracaoParticipacaoBanca.obj.dataBanca}.</div>
<br/>
<div class="tituloMembros">Membros da Banca</div>
<br>
<c:forEach items="#{declaracaoParticipacaoBanca.obj.membros}" var="item">
	<div class="membro">${item.descricaoCertificado }</div>
</c:forEach>
<br><br>
<center><div class="assinatura">${declaracaoParticipacaoBanca.assinatura}</div></center><br><br>
<br><br>


</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
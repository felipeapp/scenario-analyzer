<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"  "http://www.w3.org/TR/html4/loose.dtd">
	
<%@page import="br.ufrn.comum.dominio.Sistema"%>
<%@page import="br.ufrn.arq.util.ValidatorUtil"%>

<%@page import="br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais"%><html class="background">

<%

Integer sistema = (Integer) request.getAttribute("sistema");
if (sistema == null) sistema = 0;
String sigla = "";
String nome = "";
String url = "";

if (sistema == Sistema.SIGAA) {
	sigla = RepositorioDadosInstitucionais.get("siglaSigaa");
	nome = RepositorioDadosInstitucionais.get("nomeSigaa");
} else if (sistema == Sistema.SIPAC) {
	sigla = RepositorioDadosInstitucionais.get("siglaSipac");
	nome = RepositorioDadosInstitucionais.get("nomeSipac");
} else if (sistema == Sistema.SIGRH) {
	sigla = RepositorioDadosInstitucionais.get("siglaSigrh");
	nome = RepositorioDadosInstitucionais.get("nomeSigrh");
} else if (sistema == Sistema.SIGPP) {
	sigla = RepositorioDadosInstitucionais.get("siglaSigpp");
	nome = RepositorioDadosInstitucionais.get("nomeSigpp");
}
%>

    <head>
    	<meta HTTP-EQUIV="Pragma" CONTENT="no-cache">
	    <title><%= sigla %> - <%= nome %>
		</title>
		<link rel="shortcut icon" href="/sigaa/img/sigaa.ico" />
		<link rel="stylesheet" media="all" href="/shared/css/ufrn.css" type="text/css" />
		<style>	@import url("/shared/css/msg.css");</style>

		<!-- Fim dos imports para abrir chamado -->
    </head>
	<body>
	<div id="container">
	<%-- Importa o cabecalho do Minist�rio da Educa��o --%>
	<%@include file="/include/ministerio_educacao.jsp"%>
	<div id="cabecalho">
		<div id="info-sistema">
			<h1> <span>${ configSistema['siglaInstituicao'] } - <%= sigla %> </span> - </h1>

			<h3> <%= nome %> </h3>
		</div>
	</div> 
	<div id="conteudo">
		<div class="principal">
		<div class="colEsq">
			<p class="bgEsqTop naoecontrado"></p>
			<p class="icon"><img src="/shared/img/icon_404.jpg" align="center"/></p>

		</div>
	
		<div class="colDivisor"></div>
	
		<div class="colDir">
			<p></p>
			<p class="titDir">Desculpe, p�gina n�o encontrada!</p>
			<p class="bgDirTop"></p>
			<p class="bgDir">
				Caro Usu�rio,<br/><br/> 
				Desculpe, mas a p�gina que voc� solicitou n�o foi encontrada. 
				Entre em contato com a nossa equipe de suporte atrav�s da op��o 
				"Abrir Chamado".<br/><br/>

				 Siga uma das op��es abaixo para continuar navegando 
				em nosso sistema:
		      <div class="btn inicial">
				<a href="<%= url %>"><span>P�gina Inicial</span></a>
			  </div>
			  <div class="btn volta">
				<a href="javascript:history.go(-1);"><span>Voltar</span></a>
			  </div> 
	      </p>
		</div>

	</div>
	<div id="rodape">
		<p>	Copyright 2006 - ${configSistema['nomeResponsavelInformatica'] } - ${ configSistema['siglaInstituicao'] } - <%= br.ufrn.arq.util.AmbienteUtils.getNomeServidorComInstancia() %></p>
	</div>
</div>  
<!-- MYFACES JAVASCRIPT -->
</body>
</html>
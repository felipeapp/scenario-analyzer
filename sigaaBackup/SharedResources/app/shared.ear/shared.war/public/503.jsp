<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"  "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais"%>
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
	
<%@page import="br.ufrn.comum.dominio.Sistema"%>
<%@page import="br.ufrn.arq.util.ValidatorUtil"%><html class="background">
    <head>
    	<meta HTTP-EQUIV="Pragma" CONTENT="no-cache">
	   <title><%= sigla %> - <%= nome %></title>
		<link rel="stylesheet" media="all" href="/shared/css/ufrn.css" type="text/css" />
		<style>	@import url("/shared/css/msg.css");</style>

		<!-- Fim dos imports para abrir chamado -->
    </head>
	<body>
	<div id="container">
	<%-- Importa o cabecalho do Ministério da Educação --%>
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
			<p class="bgEsqTop atualizacao"></p>
			<p class="icon"><img src="/shared/img/icon_503.jpg" align="center"/></p>
	
		</div>
		
		<div class="colDivisor"></div>
		
		<div class="colDir">
			<p></p>
			<p class="titDir">Desculpe, sistema em atualização!</p>
			<p class="bgDirTop"></p>
			<p class="bgDir">
				Caro Usuário,<br/><br/> 
		      Desculpe, o sistema está em atualização e encontra-se momentaneamente 
		      indisponível. Tente acessá-lo novamente dentro de instantes.
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
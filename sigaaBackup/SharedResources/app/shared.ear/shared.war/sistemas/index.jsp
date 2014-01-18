<SCRIPT LANGUAGE="JavaScript" src="/shared/javascript/ocultarMostrar.js"> </SCRIPT>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="br.ufrn.arq.util.CalendarUtils"%>

<html class="background">
<head>
<title>Diretoria de Sistemas</title>
<link rel="stylesheet" media="all" href="/shared/css/ufrn.css" type="text/css" />
<link rel="stylesheet" media="print" href="/shared/css/ufrn_print.css"/>
<link rel="stylesheet" media="all" href="/sistemas/img_css/inicial.css"/>
</head>

<body>
<div id="container">
	<!-- Cabeçalho -->
	<div id="cabecalho">
		<h1 style="background: transparent url('https://www.sistemas.ufrn.br/public/img_css/topo_dir_sistemas.png') no-repeat center center; height: 100px; width: 750px;"></h1>
	</div><!-- Fim: Cabeçalho -->
	<div id="conteudo">
		<div align="center" style="width: 95%; margin: 0px 5px 5px 5px; background: #EFF3FA; padding: 10px;">
			<p style="font-weight: bold; color: #404e82;">Bem Vindo(a) aos Sitemas Institucionais da ${ configSistema['siglaInstituicao'] } </p>
			<p style="width: 75%;">	A partir daqui é possível acessar os sistemas mantidos pela Diretoria de Sistemas da ${configSistema['nomeResponsavelInformatica'] }. Escolha abaixo o sistema que deseja utilizar.</p>
		</div>
		<br>
		<table width="100%" style="font-size: 12px">
			<tr>
				<td align="center" width="20%" valign="top">
					<img src="img_css/sigaa.png"/> <br>
					<a href="${ configSistema['linkSigaa'] }">SIGAA</a><br>
					<small>
					<i>Sistema Integrado de Gestão de Atividades Acadêmicas </i>
					</small>
				</td>
				<td align="center" width="20%">
					<img src="img_css/sipac.png"/> <br>
					<a href="${ configSistema['linkSipac'] }">SIPAC</a>
				</td>
				<td align="center" width="20%">
					<img src="img_css/sigrh.png"/> <br>
					<a href="${ configSistema['linkSigrh'] }">SIGRH</a>
				</td>
				<td align="center" width="20%">
					<img src="img_css/ecomunicacao.png"/> <br>
					<a href="http://www.sistemas.ufrn.br/shared/login_comunicacao.jsf">
						e-Comunicação
					</a>
				</td>
				<td align="center" width="20%">
					<img src="img_css/iproject.png"/> <br>
					iProject
				</td>
			</tr>
		</table>
		<br>
	</div>
	<div id="rodape">
		<p>	Copyright <%= CalendarUtils.getAnoAtual() %> - ${configSistema['nomeResponsavelInformatica'] } - ${ configSistema['siglaInstituicao'] }</p>
	</div>
</div>
</body>
</html>

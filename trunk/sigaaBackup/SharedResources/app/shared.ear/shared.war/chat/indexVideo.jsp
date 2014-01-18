<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>

<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<%@page import="br.ufrn.arq.chat.ChatEngine"%>
<%@page import="java.util.List"%>
<%@page import="br.ufrn.comum.dominio.UsuarioGeral"%>
<%@page import="java.util.Iterator"%>
<%@page import="br.ufrn.arq.util.NetworkUtils"%>
<%@ taglib uri="/tags/ufrnFunctions" prefix="sf" %>

<jsp:useBean id="dataAtual" class="java.util.Date" scope="page" />

<html>
<body onunload="javascript: executaServletAoFecharJanela(); ">

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>${ configSistema['siglaInstituicao'] } - Sala de Discussão Interativa - Chat On-line</title>
		
		<!-- 
		
		<link rel="stylesheet" href="/shared/chat/css/public.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" href="/shared/chat/css/chat.css" rel="stylesheet" type="text/css" />

		 -->
		
		<script type="text/javascript" src="/shared/javascript/jquery/jquery.js"></script>
		<script type="text/javascript" src="/shared/javascript/jquery/fbborderlayout.js"></script>
		
		<script type="text/javascript" charset="UTF-8">
		
			var docente = false;
			var nomeDocente = "";
			var nomeUsuario = "${ param['nomeUsuario'] }";
			var podeMinistrar = ${ param['podeMinistrar'] ? "true" : "false" };
			var videoInicializado = false;
		
			function executaServletAoFecharJanela() {
				$.ajax({ url : "/shared/SairChat", type : "GET", data : { idchat : "${ param['idchat'] }" }});
				
				var origem = "${ param['origem'] }";
		      	if (origem == 'portalDiscente')
		      		window.opener.location.href='/sigaa/portais/discente/discente.jsf';
			}
		
			function ln2br (dataStr) {
		        return dataStr.replace(/(\r\n|[\r\n])/g, "<br />");
		    }
			
			function receberChat (){
				$.ajax({
					url : "/shared/ReceiveChat",
					type : "GET",
					data : { idchat : "${ param['idchat'] }" },
					success : function (r){

						if (String.prototype.trim != "function") {
					  		String.prototype.trim = function () {
							   return this.replace(/^\s+|\s+$/g, '');
							};
						}
						
						if (r.trim() != ""){
							var aux = r.split("\n");
							
							for (var i = 0; i < aux.length; i++){
								var mensagem = aux[i].replace("<li>", "");
								if (mensagem.trim() != ""){
									if (mensagem.indexOf(">"+nomeDocente+"<") != -1)
										$('#painelMsgsDocente').append(mensagem);
									else
										$('#painelMsgs').append(mensagem);
								}
							}
						}
						
						$('#txtMsg').focus();
						
						scrollConversacao();
					}
				});
			}
			
			function receberUsuarios () {
				$.ajax({
					url : "/shared/ListMembers",
					contentType : "text/html; charset=ISO-8859-1",
					type : "GET",
					data : { videoChat : "true", idchat : "${ param['idchat'] }" },
					success : function (r){
						var us = r.split("</li>");
						var usuarios = "";
						var _nomeDocente = "";
						for (var u in us){
							u = us[u].trim();
							if (u.indexOf("[m]") == 0){
								u = u.substring(3, u.length);
								var r = /<h4>(.*?)</g;
								var a = r.exec(u);
								_nomeDocente = a[1];
							}
							usuarios += u + "</li>";
						}
						
						configuraDocente (_nomeDocente);
						
						$('#listaContatos').html(usuarios);
					}
				});
			}
			
			function configuraDocente (_nomeDocente) {
				nomeDocente = _nomeDocente;
				if (nomeUsuario == nomeDocente){
					if (docente == false){
						docente = true;
						$("#video").html('<object width="320" height="240" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0"><param name="movie" value="${ ctx }/sigaa/ava/streampublisher.swf"><param name="FlashVars" value="sl=chat${ param['idchat'] }&s=${ param['servidor'] }"><param name="quality" value="high"><embed src="${ ctx }/sigaa/ava/streampublisher.swf" flashvars="sl=chat${ param['idchat'] }&s=${ param['servidor'] }" width="320" height="240" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash"></embed></object>');
					}
				} else if (docente == true || !videoInicializado){
					docente = false;
					$("#video").html('<object width="320" height="240" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0"><param name="movie" value="${ ctx }/sigaa/ava/streamplayer.swf"><param name="FlashVars" value="sl=chat${ param['idchat'] }&s=${ param['servidor'] }"><param name="quality" value="high"><embed src="${ ctx }/sigaa/ava/streamplayer.swf" flashvars="sl=chat${ param['idchat'] }&s=${ param['servidor'] }" width="320" height="240" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash"></embed></object>');
					videoInicializado = true;
				}
			}
			
			setInterval('receberChat()', 3000);
			setInterval('receberUsuarios()', 3000);
		
			function submitFn () {
				var single = false;
				
				$.ajax({
					url : "/shared/PostChat",
					type : "POST",
					data : {
							msg : $("#txtMsg").val().replace("</li>", "").replace(/[\u00A0-\u2666]/g, function(c) {return '&#'+c.charCodeAt(0)+';';}),
							idchat : $("#idChat").val()
						},
					success : function () {
						if (!single) {
							receberChat();
							$(docente ? '#painelMsgsDocente' : '#painelMsgs').append ("<h3>${usuarioChat.nome}<span class='diz'> diz:</span></h3> <p class='texto'>" + ln2br($('#txtMsg').val()) + "</p>");
							scrollConversacao();
								
							$('#txtMsg').val("");
							single = true;
						}
					}
				});
			};
			
			$(document).ready (function () {
				$('#txtMsg').bind('keypress', function(e) {
					if (e.which == 13 && !e.shiftKey )
						submitFn();
				});
				
				if (!podeMinistrar)
					$("#bMinistrar").hide();
			
				$('#btnEnviar').click(function () { submitFn(); return false; });
				
				$("#barra-docente").FBBorderLayout({
					spacing: 5,
					north_collapsable: false
				});
				
				$("#conversacaoDocente").FBBorderLayout({
					spacing: 0,
					north_collapsable: false
				});
				
				$("#conversacao").FBBorderLayout({
					spacing: 0,
					north_collapsable: false
				});
				
				$("#barra-contatos").FBBorderLayout({
					spacing: 0,
					north_collapsable: false,
					south_collapsable: false
				});
				
				$("body").FBBorderLayout({
					spacing: 5,
					north_collapsable: false,
					west_collapsable: false,
					east_collapsable: false,
					south_collapsable: false,
					west_width: 325
				});
				
				$("#rodape").FBBorderLayout({
					spacing: 0,
					east_collapsable: false,
					east_width: 60
				});
			});
			
			setInterval('$("#barra-contatos").attr("width", 50);', 1000);
			
			function scrollConversacao () {
				var p = $("p:last", $("#divMsgs"));
				if (p.length > 0)
					setTimeout('$("#divMsgs").stop();$("#divMsgs").animate({ scrollTop: '+p.position().top*1000+' }, 0);', 100);
				
				p = $("p:last", $("#divMsgsDocente"));
				if (p.length > 0)
					setTimeout('$("#divMsgsDocente").stop();$("#divMsgsDocente").animate({ scrollTop: '+p.position().top*1000+' }, 0);', 100);
			}
		</script>
		
		<style>
		
			body {
				font-size: 70%;
				line-height: 1.25em;
				font-family: Verdana, sans-serif;
				color: #333;
				margin: 0;
				padding: 0;
				background:url("/sigaa/ava/img/chat/bg.png");
			}
			
			#cabecalho {
				background:url("/sigaa/ava/img/chat/bgheader.png");
				color: #F9F9F9;
				border-bottom: 1px solid #ddd;
				height:85px;
			}
			
			#cabecalho #info {
				padding: 5px 10px 3px;
			}
			
			#cabecalho #info #instituicao {
				float:left;
				font-weight: bold;
				font-size: 1.3em;
				letter-spacing: 2px;
				font-family: Arial, sans-serif;
			}
			
			#cabecalho #info #data {
				float:right;
			}
			
			#cabecalho #icone {
				background:url("/sigaa/ava/img/chat/icone.png");
				width:54px;
				height:50px;
				position:absolute;
				left:10px;
				top:25px;
			}
			
			#cabecalho #tituloPagina {
				background:url("/sigaa/ava/img/chat/titulopagina.png");
				width:411px;
				height:23px;
				position:absolute;
				left:74px;
				top:25px;
			}
			
			#cabecalho #tituloChat {
				color:#FFF;
				font-weight:bold;
				font-size:16pt;
				position:absolute;
				left:74px;
				top:53px;
			}
			
			#barra-docente #video {
				text-align:center;
			}
			
			#baseConversacao, #baseConversacaoDocente, #baseContatos {
				border:1px solid #C9C9C7;
				background:#FFF;
			}
			
			#conversacaoHeader, #conversacaoDocenteHeader {
				background:url("/sigaa/ava/img/chat/chat.png") no-repeat 5px 5px #E7E7E1;
				height:22px;
				text-indent:40px;
				color:#1375CC;
				font-size:12pt;
				padding-top:8px;
			}
			
			#contatosHeader {
				background:url("/sigaa/ava/img/chat/participantes.png") no-repeat 5px 5px #E7E7E1;
				height:22px;
				text-indent:40px;
				color:#1375CC;
				font-size:12pt;
				padding-top:8px;
			}
			
			ul {
				list-style:none;
			} 
			
			#rodape {
				height:55px;
				border-top:5px solid #B36B01;
				padding-top:3px;
			}
			
			#rodape #btnEnviar {
				display:block;
				background:url("/sigaa/ava/img/chat/benviar.png") no-repeat;
				width:50px;
				height:50px;
				cursor:pointer;
			}
			
			#listaContatos {
				margin:0px;
				padding:0px;
			}
			
			.membro {
				display:block;
				padding:5px;
				height:50px;
			}
			
			.membro img {
				float:left;
				margin-right:5px;
				width:40px;
				height:50px;
			}
			
			.membro h4 {
				margin:0px;
				padding:0px;
				font-size:7pt!important;
			}
			
			.membro p {
				font-size:8pt;
				margin:0px;
				padding:0px;
				color:#1375CC;
			}
			
			.fbbl_east_collapser {
				background:#CCC;
			}
			
			#divMsgs, #divMsgsDocente {
				padding-left:10px;
			}
		</style>
	</head>
	<body>
	
		<div id="cabecalho" class="fbbl_north">
			<div id="info">
				<span id="instituicao">${ configSistema['siglaInstituicao'] }</span>
				<span id="data"><small><%= br.ufrn.arq.util.AmbienteUtils.getNomeServidorComInstancia() %> </small> &nbsp;&nbsp; <fmt:formatDate value="${ dataAtual }" dateStyle="full"/></span>
				<div style="clear: both"></div>
				<div id="icone"></div>
				<div id="tituloPagina"></div>
				<div id="tituloChat">${ param['chatName'] }</div>
			</div>
		</div>
		
		<div id="barra-docente" class="fbbl_west">
			<div id="video" class="fbbl_north" style="height:240px;background:#FFF;"></div>
			
			<div class="fbbl_center" id="baseConversacaoDocente">
				<div id="conversacaoDocente">
					<div id="conversacaoDocenteHeader" class="fbbl_north">
						Ministrante
						
						<script>
							function ministrar () {
								if (podeMinistrar)
									$.ajax({
										url : "/shared/EntrarChat?chatagendado=true&idchat=${ param['idchat'] }&idusuario=${ param['idusuario'] }&passkey=${ param['passkey'] }&video=true&chatName=${ param['chatName'] }&origem=turmaVirtual&nomeUsuario=${ param['nomeUsuario'] }&podeMinistrar=true&ministrante=true",
										contentType : "text/html; charset=ISO-8859-1",
										type : "GET"
									});
							}
						</script>
						
						<a href="#" id="bMinistrar" onclick="ministrar();return false;" style='position:absolute;top:3px;right:3px;' title='Ministrar aula'><img src="/sigaa/ava/img/chat/bMinistrar.png" /></a>
					</div>
					<div class="fbbl_center" id="divMsgsDocente">
						<div class="conversas" id="painelMsgsDocente"></div>
					</div>
				</div>
			</div>
		</div>
		
		<div class="fbbl_center" id="baseConversacao">
			<div id="conversacao">
				<div id="conversacaoHeader" class="fbbl_north">Chat da turma</div>
				<div class="fbbl_center" id="divMsgs">
					<div class="conversas" id="painelMsgs"></div>
				</div>
			</div>
		</div>
		
		<div id="baseContatos" class="fbbl_east">
			<div id="barra-contatos">
				<div id="contatosHeader" class="fbbl_north">Membros On-line</div>
				<div class="fbbl_center"><ul id="listaContatos"></ul></div>
				<div class="fbbl_south">
					<ul class="opcoes">
						<li><a href="/shared/SairChat?idchat=${ param['idchat'] }" title="Sair do Chat" class="sair">Sair do chat <img src="/sigaa/ava/img/chat/bsair.png" /></a></li>
					</ul>
				</div>
			</div>
		</div>
		
		<div id="rodape" class="fbbl_south">
			<div class="fbbl_center">
				<textarea name="msg" id="txtMsg" style="width:98%;height:80%;"></textarea>
				<input id="idChat" type="hidden" name="idchat" value="${param['idchat']}"/>
			</div>
			
			<div class="fbbl_east">
				<a href="#" id="btnEnviar" class="enviar"><div></div></a>
			</div>
		</div>
	</body>
</html>
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
		
		<link rel="stylesheet" href="/shared/chat/css/public.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" href="/shared/chat/css/chat.css" rel="stylesheet" type="text/css" />

		<link rel="stylesheet" type="text/css" href="/shared/javascript/ext-2.0.a.1/resources/css/ext-all.css" />
		
		<script type="text/javascript" src="/shared/javascript/jquery/jquery.js"></script>
		<script type="text/javascript" src="/shared/javascript/jquery/fbborderlayout.js"></script>
		
		<script>
		
		window.onbeforeunload = function() {
			return "O chat será fechado.";
		};
		
		var ajax = null;
		function executaServletAoFecharJanela() {
										
					if (window.ActiveXObject) ajax = new ActiveXObject('Microsoft.XMLHTTP');
					    else if (window.XMLHttpRequest) ajax = new XMLHttpRequest();
				    if(ajax != null){
				      ajax.open('GET', "/shared/SairChat?idchat=${ param['idchat'] }", false);
				      //ajax.onreadystatechange = statusAjax;
				      ajax.send(null);
					      
				      	var origem = "${ param['origem'] }";
				      	var teste = "${ param['teste'] }";
				      	if (origem == 'portalDiscente') {
				      		window.opener.location.href='/sigaa/portais/discente/discente.jsf';
				      	}
				      	
				      	if (origem == 'portalDocente') {
				      		window.opener.location.href='/sigaa/portais/docente/docente.jsf';
				      	}
				    }
				   window.onbeforeunload = null;

		}
		</script>
		
		
		<script type="text/javascript">
		$(function() {
			$('body').FBBorderLayout({
				spacing: 0,
				north_resizable: false,
				north_collapsable: false,
				south_resizable: false,
				south_collapsable: false,
				east_resizable: false,
				east_collapsable: false,
				east_width: 200,
				east_min_width:200,
				east_max_width:400,
				west_resizable: false,
				west_collapsable: false,
				west_width: 200,
				west_min_width:200,
				west_max_width:400
			})
		});

		// Exibe o relógio da turma virtual.
        function exibirHora () {
			var hora = "${ dataAtual }";
			var dataFracoes = hora.split(" ");
			var horaFracoes = dataFracoes[3].split(":");
			contadorRelogio(horaFracoes[0],horaFracoes[1],horaFracoes[2]);
        }

        function contadorRelogio (h,m,s) {

        	if ( s == 59 ){
				s = 0;
				m++;
        	} else
            	s++;
        	if ( m == 59 ){
				m = 0;
				if ( h == 23 )					
					h = 0;
				else
					h++;
        	} 
			if (s.toString().length == 1) { s = '0'+s; }
       	 	if (m.toString().length == 1) { m = '0'+m; }
       	 	document.getElementById("relogioChat").innerHTML = h + ":" + m + ":" + s;
       	 	setTimeout("contadorRelogio(" + h + "," + m + "," + s + ")",1000);	         	
        }
		</script>
	</head>
	<body onload="exibirHora();">
	
		<div class="fbbl_north" id="cabecalho">
			<div id="info">
				<span class="ufrn" style="float: left"> UFRN </span>
				<span class="data"> 
					<small><%= br.ufrn.arq.util.AmbienteUtils.getNomeServidorComInstancia() %> 
					</small> &nbsp;&nbsp; <fmt:formatDate value="${ dataAtual }" dateStyle="full"/> 
					<span id="relogioChat" style="padding-left:2px;border-left:1px solid #D99C44;"></span>
				</span>
				<div style="clear: both"></div>
			</div>
			<div id="identificacao">
				<a id="home" href="#" alt="P&#225;gina inicial" title="P&#225;gina inicial"></a>
			</div>
		</div>
		
		<div class="fbbl_east" id="barra-contatos">
			<h2>Membros On-line</h2>
			<ul class="lista-contatos" id="listaContatos">
					
			</ul>
			<ul class="opcoes">
				<li><a href="/shared/SairChat?idchat=${ param['idchat'] }" alt="Sair do Chat" class="sair">Sair do chat</a></li>
			</ul>
		</div>
		
		<div class="fbbl_south" id="rodape">
			<div class="campo-escrita">
				<span id="dummy"></span>
				<form action="/shared/PostChat" id="enviaMsg" method="post">
					<textarea name="msg" id="txtMsg"></textarea>
					<input type="hidden" name="idchat" value="${param['idchat']}"/>
					<a href="#" id="btnEnviar" class="enviar">Enviar</a>
				</form>
			</div>
		</div>
		
		<div class="fbbl_center" id="corpo">
			<h1>SIGAA Chat - <small>${ param['chatName'] }</small></h1>
			<div class="conversacao-bg"></div>
			<div class="conversacao" id="divMsgs">
				<ul class="conversas" id="painelMsgs"></ul>
			</div>
		</div>
		
	</body>
</html>
<script src="/shared/javascript/ext-2.0.a.1/ext-base.js"></script>
<script src="/shared/javascript/ext-2.0.a.1/ext-all.js"></script>

<script type="text/javascript">

	function return2br(dataStr) {
        return dataStr.replace(/(\r\n|[\r\n])/g, "<br />");
    }

	var task = {
		run: function(){
		    Ext.Ajax.request({
				url: "/shared/ReceiveChat?idchat=${ param['idchat'] }",
				success: function(res) {
					if (res.responseText != '') {
						Ext.get('painelMsgs').dom.innerHTML += res.responseText;
						setTimeout("$('#corpo').animate({scrollTop: 1000000});", 300);
						setTimeout("document.getElementById('txtMsg').focus();", 300);
					}
				}
		    });
		},
		interval: 3000 //3 seconds
	}
	
	var runner = new Ext.util.TaskRunner();
	runner.start(task);

	var taskMembers = {
			run: function(){
			    Ext.Ajax.request({
					url: "/shared/ListMembers?idchat=${ param['idchat'] }",
					success: function(res) {
						Ext.get('listaContatos').dom.innerHTML = res.responseText;
					}
			    });
			},
			interval: 3000 //3 seconds
		}
	var runnerMembers = new Ext.util.TaskRunner();
	runnerMembers.start(taskMembers);
		
	var submitFn = function() {
		var single = false;
		var updater = Ext.get('dummy').getUpdater();
		updater.formUpdate('enviaMsg');
		updater.on('update', function() {
			if (!single) {
				Ext.Ajax.request({
					url: "/shared/ReceiveChat?idchat=${ param['idchat'] }",
					success: function(res) {
						Ext.get('painelMsgs').dom.innerHTML += res.responseText;
						setTimeout("$('#corpo').animate({scrollTop: 1000000});", 300);
						setTimeout("document.getElementById('txtMsg').focus();", 300);
					}
			    });
				Ext.get('painelMsgs').dom.innerHTML += '<b>${usuarioChat.nome} diz:</b> <br> ' + return2br(Ext.get('txtMsg').dom.value) + '<br>';
				Ext.get('txtMsg').dom.value = '';
				single=true;
			}
		});
	};
	
	Ext.get('txtMsg').on('keypress', function(e) {
		if (e.getKey() == 13 && !e.shiftKey ) {
			submitFn();
		}
	});

	Ext.get('btnEnviar').on('click', submitFn);

</script>


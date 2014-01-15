<%@page import="br.ufrn.sigaa.caixa_postal.dominio.Mensagem"%>
<%@page import="br.ufrn.comum.dominio.Sistema"%>
<%@page import="br.ufrn.rh.dominio.Categoria"%>
<html>
	<head>
		<meta HTTP-EQUIV="Pragma" CONTENT="no-cache" />
		
		<title>${ configSistema['siglaSigaa'] } - ${configSistema['nomeSigaa'] }</title>
		<link rel="shortcut icon" href="<html:rewrite page='/img/sigaa.ico'/>" />

 		<script type="text/javascript" src="/shared/jsBundles/jawr_loader.js" ></script>
 		
		<script type="text/javascript">
			JAWR.loader.style('/bundles/css/sigaa_base.css', 'all');
			JAWR.loader.script('/bundles/js/turma_virtual_base.js');
		</script>
		
		<script>
			JAWR.loader.style('/javascript/ext-2.0.a.1/resources/css/ext-all.css', 'all');
		</script>

		<script src="/shared/javascript/ext-2.0.a.1/ext-base.js"></script>
		<script src="/shared/javascript/ext-2.0.a.1/ext-all.js"></script>
		
		<script src="${ ctx }/ava/javascript/turma.jsp"></script>
		
		<jwr:style src="/bundles/css/sigaa.css" media="all"/>
		<jwr:style src="/ava/css/turma.css" media="all"/>
		<jwr:style src="/css/jquery-ui/css/jquery-ui-1.8.4.custom.css" media="all"/>
		
 		<jwr:script src="/javascript/jquery-ui/js/jquery-1.4.2.min.js" />
	    
		<script>var J = jQuery.noConflict();</script>
		
		<jwr:script src="/javascript/jquery-ui/js/jquery-ui-1.8.4.custom.min.js"/>
		<jwr:script src="/javascript/jquery-layout.js"/>
		
		<jwr:script src="/javascript/jquery-wtooltip.js"/>
        
        <%-- Resources do Primefaces --%>
		
		<%-- <p:resources /> --%>
		<link rel="stylesheet" type="text/css" href="/sigaa/primefaces_resource/1.1/skins/sam/skin.css" /><link rel="stylesheet" type="text/css" href="/sigaa/primefaces_resource/1.1/jquery/plugins/ui/jquery.ui.dialog.css" />
		<script type="text/javascript" src="/sigaa/primefaces_resource/1.1/primefaces/core/core.js"></script>
		<script type="text/javascript" src="/sigaa/primefaces_resource/1.1/primefaces/dialog/dialog.js"></script>
		<script type="text/javascript" src="/sigaa/primefaces_resource/1.1/primefaces/button/button.js"></script>
        
		<link rel="stylesheet" type="text/css" href="/sigaa/ava/primefaces/redmond/skin.css" />
		<link rel="stylesheet" media="print" href="/shared/css/ufrn_print.css"/>
		
		<jwr:style src="/ava/css/ava_print.css" media="print"/>
						
		<script type="text/javascript">

			// String contendo o nome da turma.
			var auxNomeTurma = null;
	
			/* Configura o layout com JQuery-UI. */
			var myLayout;
			
			J("document").ready(function () {
				//setTimeout('montaLayout()', 20);
				montaLayout();
	
				Relogio.init(<%= session.getMaxInactiveInterval() / 60 %>);
	
				esconderLoading();
			});
			
			function esconderLoading () {
				J("#dialog").css("display","none");
				J("#mascara").css("display","none");
			}
	
			function exibirLoading () {
				J("#dialog").css("display","block");
				J("#mascara").css("display","block");
			}
			
			var urlVideoChat = "";
			var idVideoChat = ""
			function exibirJanelaVideoChat (idChat, idUsuario, passKey, nomeChat, nomeUsuario, podeMinistrar, servidor){
				urlVideoChat = '/shared/EntrarChat?chatagendado=true&idchat=' + idChat
							+ '&idusuario=' + idUsuario + '&passkey=' + passKey
							+ '&video=true&chatName=' + nomeChat + '&origem=turmaVirtual&nomeUsuario=' + nomeUsuario
							+ '&podeMinistrar=' + podeMinistrar + '&servidor=' + servidor;
				
				idVideoChat = 'chat_'+idChat;
				
				if (podeMinistrar)
					J("#dialogMinistrante").dialog("open");
				else
					abrirJanelaVideoChat(false);
				
				return false;
			}
			
			function abrirJanelaVideoChat (ministrante){
				window.open(urlVideoChat + (ministrante ? "&ministrante=true" : ""), idVideoChat, 'height=600,width=800,location=0,resizable=1');
			}

			function montaLayout(){

				J('#baseLayout').css('height', ( document.documentElement.clientHeight + 13 ) + "px");
				
				myLayout = J("#baseLayout").layout({
					name : "asd",
					minWidth: 500,
					defaults: {
						size: "auto",
						spacing_open: 5,
						togglerLength_open: 0,
						resizable: false,
						slidable: false
					}, north: {
						size:
							<h:outputText value="48" rendered="#{turmaVirtual.config == null || turmaVirtual.config.template == 1}" />
							<h:outputText value="78" rendered="#{turmaVirtual.config.template == 2}" />
					}, south: {
						size: 25
					}, west: {
						size: 190
					}, east: {
						size: 225,
						togglerLength_closed: -1,
						spacing_closed: 30,
						togglerTip_closed: "Abrir barra lateral",
						fxName: "drop",
						fxSpeed: "fast",
						togglerClass: "botaoDireita"
					}, center: {
					}
				});
				
				myLayout.addToggleBtn( "#toggleDireita", "east" );

				J("#conteudo").css("z-index", "1");
				J("#cabecalho").css("z-index", "999");

				corrigeNomeTurma ();
				
				window.onresize = function () {
					J('#baseLayout').height(document.documentElement.clientHeight);
					corrigeNomeTurma ();
				};
				
				J("#dialogMinistrante").dialog({
					autoOpen: false,
					resizable: false,
					height:200,
					modal: true,
					buttons: {
						"Ministrante" : function() {
							abrirJanelaVideoChat(true);
							J( this ).dialog( "close" );
						},
						"Ouvinte" : function() {
							abrirJanelaVideoChat(false);
							J( this ).dialog( "close" );
						}
					}
				});
			}

			function corrigeNomeTurma () {
				var linkNomeTurma = J("#linkNomeTurma");
				
				if (linkNomeTurma != null){

					if (auxNomeTurma == null)
						auxNomeTurma = linkNomeTurma.html();

					var arrayNomeTurma = auxNomeTurma.split(" ");
					
					var espacoRestante =
						J("body").attr("clientWidth") - (
							J("#menuTurmaVirtual").attr("clientWidth") +
							J("#painelDadosUsuario").attr("clientWidth") +
							J("#linkCodigoTurma").attr("clientWidth") +
							J("#linkPeriodoTurma").attr("clientWidth")
							+ 30 // Margem
						);
				
					linkNomeTurma.html(arrayNomeTurma.join(" "));

					var adicionarRtc = false;
					
					if (espacoRestante > 0 && espacoRestante < linkNomeTurma.attr("clientWidth"))
						adicionarRtc = true;
					
					while (arrayNomeTurma.length > 0 && espacoRestante > 0 && espacoRestante < linkNomeTurma.attr("clientWidth")){
						arrayNomeTurma.pop();
						linkNomeTurma.html(arrayNomeTurma.join(" ") + (adicionarRtc ? " ..." : ""));
					}
				}
			}

			var subMenuEscondido = true;

			var podeAlternarSubMenu = true;
			
			function toggleSubMenuTurma(){
				if (podeAlternarSubMenu){ 
					if (subMenuEscondido)
						exibeSubMenu ();
					else
						escondeSubMenu ();
				}

				podeAlternarSubMenu = true;
			}
			
			function exibeSubMenu (){
				jQuery("#painel-usuario").show("fast");
				subMenuEscondido = false;
				document.getElementById("auxEsconderSubMenu").focus();
				document.getElementById("formAcoesTurma:botaoExibirOpcoes").style.background = "#FFF";
			}

			function escondeSubMenu (){
				jQuery("#painel-usuario").hide("fast");
				subMenuEscondido = true;
				document.getElementById("formAcoesTurma:botaoExibirOpcoes").style.background = 'url("images/ui-bg_glass_85_e8eef3_1x400.png") repeat-x scroll 50% 50% #E8EEF3';
				setTimeout(function () {podeAlternarSubMenu = true;}, 100)
			}

			function exibirMenuModulos () {
				dialogModulos.show();
				return false;
			}

			// Exibe o relógio da turma virtual.
            function exibirHora () {
				hora = "${turmaVirtual.hora}";
				fracoes = hora.split(":");
				contadorRelogio(fracoes[0],fracoes[1],fracoes[2]);
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
           	 	document.getElementById("relogioAva").innerHTML = h + ":" + m + ":" + s;
           	 	setTimeout("contadorRelogio(" + h + "," + m + "," + s + ")",1000);	         	
            }
		</script>
    </head>

	<body onload="exibirHora()">
		<div id="baseLayout" style="width:100%;position:relative;">
		
		<div id="dialogMinistrante" title="Acessar como ministrante?">
			<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>Com que perfil deseja acessar este chat? Caso opte por ser o ministrante e se já houver um ministrante no chat, este passará a ser um ouvinte.</p>
		</div>
		
		<div id="dialog">
			<br/><br/>
			<p>Por favor, aguarde enquanto a página é gerada ...</p><br/><br/>
			<h:graphicImage value="/img/loading.gif" />
		</div>
		<div id="mascara" style="width:2000px;height:2000px;"></div>
		
		<c:if test="${sessionScope.avisoBrowser != null}">
			<script type="text/javascript">
				window.open('/sigaa/public/navegador.jsp?ajaxRequest=true','','width=670,height=230, top=100, left=100, scrollbars' );
			</script>
			<% session.removeAttribute("avisoBrowser"); %>
		</c:if>
	
		<% if ( request.isSecure() ) { %>
			<c:set var="protocolo" value="https"/>
		<% } else { %>
			<c:set var="protocolo" value="http"/>
		<% }  %>
	
		<c:if test="${ !usuario.vinculoAtivo.vinculoDiscente }">
			<c:set var="turmasAbertas" value="#{ portalDocente.turmasAbertas }"/>
		</c:if>
		<c:if test="${ usuario.vinculoAtivo.vinculoDiscente }">
			<c:set var="turmasAbertas" value="#{ portalDiscente.turmasAbertas }"/>
		</c:if>
		
		<%-- Painel que exibe a listagem das turmas para facilitar a navegação --%>
		<p:dialog header="Trocar de Turma" widgetVar="dialogTurmas" modal="true" width="900" height="300">
			<c:if test="${ not empty turmasAbertas }">
				<h:outputText value="Selecione uma das turmas abaixo para acessá-la:" style="font-weight:bold;text-align:center;margin-top:10px;margin-bottom:10px;font-size:10pt;display:block;" styleClass="linkTurma"/>
				<h:form id="formTurma">
					<c:forEach items="#{turmasAbertas}" var="t" varStatus="status">
						<h:commandLink action="#{turmaVirtual.entrar}" styleClass="linkTurma" onclick="return permitirEscolhaTurma();">
							<f:param value="#{t.id}" name="idTurma" />
							<span style="display:inline-block;width:97%;height:16px;overflow:hidden;vertical-align:middle;color:#003390;font-size:9pt;background:url('/sigaa/img/avancar.gif') ${status.index % 2 == 0 ? '#F0F0F0' : ''} no-repeat right;padding:5px;">
								<h:outputText value="#{t.descricaoSemDocente}"/>
							</span>
						</h:commandLink>
					</c:forEach>
					<center>
						<div id="MensagemCarregamento" >
							<br/><br/>
							<p>Por favor, aguarde enquanto carregamos a página ...</p><br/><br/>
							<h:graphicImage value="/img/loading.gif" />
						</div>
					</center>	
				</h:form>
			</c:if>
			<c:if test="${ empty turmasAbertas }">
				<i>Nenhuma turma aberta encontrada.</i>
			</c:if>
		</p:dialog>
		
	
		<%-- Painel que exibe o perfil do discente selecionado --%>
		<%@include file="/ava/PerfilUsuarioAva/exibir.jsp" %>
		
		<%-- Painel que exibe o módulo --%>
		<%@include file="/ava/painel_modulos.jsp" %>
		
		<%-- Painel que exibe o parecer a respeito da necessidade educacional especial do discente selecionado --%>
		<%@include file="/ava/nee/panel_parecer_nee.jsp"%>
		
		
		<%-- Menu Vertical da turma virtual --%>
		<%@include file="/ava/menu_vertical.jsp" %>
		
		<%-- Coluna direita da turma virtual --%>
		<%@include file="/ava/barra_direita.jsp" %>
		
		
		<%-- Cabeçalho da turma virtual --%>
		<div id="cabecalho" class="ui-layout-north" style="overflow:visible;">
			<div id="info-sistema">
				<h1> <span>${ configSistema['siglaInstituicao'] } - ${ configSistema['siglaSigaa'] } -</span></h1>
				<h3>${ configSistema['nomeSigaa'] }</h3>
				<div class="dir">
					<span id="relogioAva" class="relogioAva"></span> 
					<c:if test="${not empty sessionScope.usuario}">
						
						<div id="tempoSessao"></div>
						
						<span id="sair-sistema" class="sair-sistema">
							<html:link action="logar?dispatch=logOff">SAIR</html:link>
						</span>
					</c:if>
				</div>
			</div>
			
			
			<c:set var="confirm" value="return confirm('Deseja cancelar a operação? Todos os dados digitados serão perdidos!');" scope="application"/>
			<c:set var="confirmDelete" value="return confirm('Confirma a remoção desta informação?');" scope="application"/>
	
			<div style="position:relative;">
				<c:if test="${not empty sessionScope.usuario}">
					<div id="painel-usuario" style="height: 20px;position:absolute;z-index:888;top:23px;right:5px;border:1px solid #99BBE8;display:none;">
						<div style="position:absolute;top:-4px;right:-1px;height:4px;background:#FFF;width:24px;border-right:1px solid #99BBE8;"></div>
						<div id="menu-usuario" style="background:#FFF;">
							<ul>
								<li class="modulos">
								<c:if test="${ not sessionScope.usuario.somenteConsultor }">
									<span id="modulos">
										<a href="#" onclick="return exibirMenuModulos ();"> Módulos </a>
									</span>
								</c:if>
								</li>
								<li class="caixa-postal"><a href="/sigaa/abrirCaixaPostal.jsf?sistema=<%=String.valueOf(Sistema.SIGAA)%>" accesskey="C">Caixa Postal</a></li>
								<li class="chamado">
								<c:choose>
									<c:when test="${!acesso.abrirChamado}">
										<a href="javascript://nop/" onclick="alert('Caro aluno, em caso de dúvidas em relação ao uso do ${ configSistema['siglaSigaa'] }, recorra à coordenação do seu curso.\nEm caso de erro, envie uma mensagem para ${configSistema['emailSuporte']} (não esquecendo de informar seu número de matrícula).');">Abrir Chamado</a>
									</c:when>
									<c:otherwise>
										<c:if test="${configSistema['caminhoAberturaChamado']==null }">
										
											<c:if test="${sistemaBean.IProjectAtivo}">
												<a href="#" onclick="window.open('/sigaa/abrirChamado.jsf?tipo=3&sistema=2&idUsuario=${configSistema['idUsuarioChamado']}', 'chamado', 'scrollbars=1,width=830,height=600')">Abrir Chamado</a>
											</c:if>
						
											<c:if test="${not sistemaBean.IProjectAtivo}">
												<a href="#" accesskey="a" onclick="window.open('/sigaa/novoChamadoAdmin.jsf?sistema=2', 'chamado', 'scrollbars=1,width=830,height=600')">Abrir Chamado</a>
											</c:if>
											
										</c:if>
										<c:if test="${configSistema['caminhoAberturaChamado']!=null}">
											<a href="#" onclick="window.open('${configSistema['caminhoAberturaChamado']}', 'chamado', 'scrollbars=1,width=830,height=600')">Abrir Chamado</a>
										</c:if>
									</c:otherwise>
								</c:choose>
								</li>
								<c:if test="${!acesso.administracao}">
									<li class="menus">
										<c:set var="categoriaDocente" value="<%= String.valueOf(Categoria.DOCENTE) %>"/>
										<c:if test="${sessionScope.usuario.vinculoAtivo != null && ((sessionScope.usuario.vinculoAtivo.vinculoServidor && usuario.servidor.categoria.id == categoriaDocente) || sessionScope.usuario.vinculoAtivo.vinculoDocenteExterno) }">
											<ufrn:link action="verPortalDocente">Menu Docente</ufrn:link>
										</c:if>
										<c:if test="${sessionScope.usuario.discenteAtivo != null}">
											<ufrn:link action="verPortalDiscente">Menu Discente</ufrn:link>
										</c:if>
										<c:if test="${ sessionScope.usuario.somenteConsultor }">
											<ufrn:link action="verPortalConsultor">Portal Consultor</ufrn:link>
										</c:if>
									</li>
								</c:if>
								<c:if test="${acesso.administracao}">
									<li class="admin"><ufrn:link action="verMenuAdministracao">Área Admin.</ufrn:link> </li>
								</c:if>
								<c:if test="${ not sessionScope.usuario.somenteConsultor }">
									<li class="dados-pessoais">
										<a href="#" onclick="window.open('/sigaa/alterar_dados.jsf','','width=670,height=430, top=100, left=100, scrollbars' )">Alterar senha</a>
									</li>
								</c:if>
								<li class="ajuda">
									<a href="/sigaa/manuais/index.jsp" id="show-ajuda" > Ajuda </a>
								</li>
							</ul>
						</div>
						
						<div style="position:absolute;bottom:-6px;height:5px;width:100%;background:url('/sigaa/ava/img/sombrasubmenu.png');"></div>
					</div>
				
					<div id="painelDadosUsuario" class="dados-menu" style="position:relative;margin-left:3px;height:23px;vertical-align:middle;padding-right:3px;border-right:1px solid #666666;margin-top:2px;margin-right:3px;">
						<p style="font-weight:bold;font-size:7pt;">
							<c:if test="${not empty sessionScope.usuario}">
								<ufrn:format type="texto" length="50" valor="${sessionScope.usuario.pessoa.nome }"/>
								<c:if test="${sessionScope.usuarioAnterior != null}">
						 			<ufrn:checkRole usuario="${ sessionScope.usuarioAnterior }" papel="<%= SigaaPapeis.SEDIS %>">
					 				 	<a href="${ctx}/logar.do?dispatch=retornarUsuarioSedis">(Deslogar)</a>
						 			</ufrn:checkRole>
						 			<ufrn:checkNotRole usuario="${ sessionScope.usuarioAnterior }" papel="<%= SigaaPapeis.SEDIS %>">
					 					<a href="${ctx}/logar.do?dispatch=retornarUsuario">(Deslogar)</a>
						 			</ufrn:checkNotRole>
				 				</c:if>
					 			<c:if test="${ acesso.multiplosVinculos }">
					 				<a href="${ctx}/escolhaVinculo.do?dispatch=listar" style="font-size:6pt;">
					 				<img src="/shared/img/group_go.png" alt="Alterar vínculo" title="Alterar vínculo" style="width:10px;" />
					 				Alterar Vínculo
					 				</a>
					 			</c:if>
							</c:if>
						</p>
						
						<div style="font-size:6pt;">
							<ufrn:format type="texto" length="60" valor="${sessionScope.usuario.vinculoAtivo.unidade.nome}" />
							<c:if test="${not empty sessionScope.usuario.vinculoAtivo.unidade}"> (${sessionScope.usuario.vinculoAtivo.unidade.codigoFormatado})</c:if>
							<c:if test="${ sessionScope.usuario.somenteConsultor }"> Área de Conhecimento: <em>${sessionScope.usuario.consultor.areaConhecimentoCnpq.nome }</em></c:if>
							
							<c:if test="${paramGestora != null}">
								- Semestre atual: <strong style="font-weight:bold;">${calendarioAcademico.anoPeriodo}</strong>
							</c:if>
						</div>
					</div>
				</c:if>
				
				<%-- Exibe o nome da turma com o código, ano e período --%>
				<div id="nomeTurma" class="dados-menu" style="position:relative;vertical-align:middle;padding-top:3px;">
					<p id="linkCodigoTurma" style="display:inline;font-weight:bold;" title="${turmaVirtual.turma.disciplina.nome}">${turmaVirtual.turma.disciplina.codigo} - </p>
					<p id="linkNomeTurma" style="display:inline;font-weight:bold;" title="${turmaVirtual.turma.disciplina.nome}">${turmaVirtual.turma.disciplina.nome}</p>
					<p id="linkPeriodoTurma" style="display:inline;font-weight:bold;" title="${turmaVirtual.turma.disciplina.nome}"> (${turmaVirtual.turma.ano}
						<c:if test="${turmaVirtual.turma.periodo != 0}">
							.${turmaVirtual.turma.periodo}
						</c:if>
					 	-
					 	<c:if test="${!turmaVirtual.turma.infantil}"> 
					 		T${turmaVirtual.turma.codigo})
					 	</c:if>
					 	<c:if test="${turmaVirtual.turma.infantil}"> 
					 		<c:if test="${turmaVirtual.turma.descricaoHorario == 'M'}">Manhã</c:if>
					 		<c:if test="${turmaVirtual.turma.descricaoHorario == 'T'}">Tarde</c:if>
					 		-
					 		${turmaVirtual.turma.codigo})
					 	</c:if>
					 </p>
				</div>
				
				<div id="menuTurmaVirtual" class="naoImprimir" style="position:absolute;right:5px;top:4px;">
				
				
					<c:set var="categoriaDocente" value="<%= String.valueOf(Categoria.DOCENTE) %>"/>
						
					<form id="formAcessarMenu" action="" method="get" style="display:none;">
						<input id="botaoAcessarMenu" type="submit" style="display:none;" />
					</form>
					
					<script>
						function clicarLinkMenu (endereco){
							J("#formAcessarMenu").attr("action", "/sigaa/"+endereco+".do");
							J("#botaoAcessarMenu").click();
							return false;
						}

						function exibirPaginados () {
							J("#formAcoesTurma\\:linkExibirPaginados").click();
							return false;
						}

						function exibirEmLista () {
							J("#formAcoesTurma\\:linkExibirEmLista").click();
							return false;
						}
					</script>
								
					<h:form id="formAcoesTurma" style="text-align:right;">
					
						<p:commandButton id="botaoAtivarEdicao" 
							value="#{ turmaVirtual.edicaoAtiva ? 'Desativar Edição' : 'Ativar Edição'}" 
							ajax="false"
							action="#{ turmaVirtual.ativarEdicao }"  
							update="botaoAtivarEdicao, formAva"
							style="height:20px;margin:0px;border-radius:5px 5px 5px 5px;margin-right:5px;" 
							image="botaoAtivarEdicao" title="Ativar/Desativar Edição" 
							rendered="#{ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }"/>
						
						<h:commandLink id="linkExibirPaginados" action="#{ turmaVirtual.exibirTopico }" style="display:none;">
							<f:param name="paginados" value="true" />
						</h:commandLink>
						<h:commandLink id="linkExibirEmLista" action="#{ turmaVirtual.exibirTopico }" style="display:none;" />
						
						
						<%-- Botões de ir para o portal do usuário, imprimir e exibir tópicos paginados/em lista --%>
						<p:commandButton rendered="#{ sessionScope.usuario.vinculoAtivo != null && ((sessionScope.usuario.vinculoAtivo.vinculoServidor && usuario.servidor.categoria.id == 1) || sessionScope.usuario.vinculoAtivo.vinculoDocenteExterno) }" onclick="return clicarLinkMenu('verPortalDocente');" id="botaoPortalDocente" style="height:20px;margin:0px;border-radius:5px 0px 0px 5px;padding-left:0px;width:25px;border-right:0px;" image="botaoPortal" title="Menu Docente" ajax="false"/>
						<p:commandButton rendered="#{ sessionScope.usuario.discenteAtivo != null }" onclick="return clicarLinkMenu('verPortalDiscente');" id="botaoPortalDiscente" style="height:20px;margin:0px;border-radius:5px 0px 0px 5px;padding-left:0px;width:25px;border-right:0px;" image="botaoPortal" title="Menu Discente"  ajax="false"/>
						<p:commandButton rendered="#{ sessionScope.usuario.somenteConsultor }" onclick="return clicarLinkMenu('verPortalConsultor');" id="botaoPortalConsultor" style="height:20px;margin:0px;border-radius:5px 0px 0px 5px;padding-left:0px;width:25px;border-right:0px;" image="botaoPortal" title="Portal Consultor"  ajax="false"/>
						 
												 
						<p:commandButton id="botaoImprimir" style="height:20px;margin:0px;border-radius:0px;padding-left:0px;width:25px;border-right:0px;" image="botaoImprimir" onclick="imprimir();" title="Imprimir página"/>						
						
						<p:commandButton id="visualizarAulasEmLista" onclick="return exibirEmLista();" style="height:20px;margin:0px;margin-right:5px;border-radius:0px 5px 5px 0px;padding-left:0px;width:25px;" image="botaoAulasEmLista" title="Visualizar aulas em lista" rendered="#{ turmaVirtual.estiloVisualizacaoTopicos == 2}"  ajax="false"/>
						<p:commandButton id="visualizarPaginadas" onclick="return exibirPaginados();" style="height:20px;margin:0px;margin-right:5px;border-radius:0px 5px 5px 0px;padding-left:0px;width:25px;" image="botaoAulasPaginadas" title="Visualizar aulas paginadas" rendered="#{ turmaVirtual.estiloVisualizacaoTopicos == 1}"  ajax="false"/>
					
						<%-- Botões de Trocar de Turma e exibir submenu --%>
			        	<p:commandButton id="botaoTrocarTurma" value="Trocar de Turma" style="height:20px;margin:0px;border-radius:5px 0px 0px 5px;border-right:0px;" onclick="dialogTurmas.show();" image="botaoTrocarTurma" title="Trocar de turma" />
			        	<p:commandButton id="botaoExibirOpcoes" style="height:20px;margin:0px;border-radius:0px 5px 5px 0px;padding-left:0px;width:26px;" onclick="toggleSubMenuTurma();return false;" image="botaoOpcoesTurma" title="Opções" />
					</h:form>
					
					<input id="auxEsconderSubMenu" style="width:1px;height:1px;border:none;background:none;color:none;" onblur="setTimeout(function () {podeAlternarSubMenu=false;escondeSubMenu();}, 100);" />
				</div>
			</div>
			
			<%-- Menu Horizontal da turma virtual --%>
			<%@include file="/ava/menu_horizontal.jsp" %>
		</div>
		
<script type="text/javascript">

function imprimir() {

	var conteudo = jQuery("#conteudo").html();
	J('#baseLayout').css('display', 'none');
	J('body').css("background", "#FFF");
	
	var cabecalhoImpressao = "<div style='background-color: #404E82;border-bottom: 3px solid #E1B26E;color: white;padding: 2px 4px 0 4px;'><span>${ configSistema['siglaInstituicao'] } - ${ configSistema['siglaSigaa'] } -</span> ${ configSistema['nomeSigaa'] }</div>";
	var nomeTurma = "<div style='padding:10px;margin-bottom:10px;border-bottom:1px solid #99BBE8;font-weight:bold;font-size:10pt;background:#DFE8F6;'>Turma: ${turmaVirtual.turma.disciplina.codigo} - ${turmaVirtual.turma.disciplina.nome} (${turmaVirtual.turma.ano}<c:if test='${turmaVirtual.turma.periodo != 0}'>.${turmaVirtual.turma.periodo}</c:if> - T${turmaVirtual.turma.codigo})</div>";

	var botaoCancelar = "<div class='naoImprimir' style='width:210px;margin: 0 auto;margin-bottom:30px;'>"+
							"<label>"+
								"<span class='botao-pequeno'style='text-align:center' onclick='cancelarPrint();'>"+
									"<a style='margin-left:-12px;'>Voltar para Turma<a/>"+
								"</span>"+
							"</label></div>";						
	
	J('body').append("<div id='impressao' style='clear:both;background:#FFF;margin:0px;padding:0px;height:100%;width:100%;'></div>");
	J('#impressao').css('display', 'block');
	J('#impressao').html(cabecalhoImpressao + nomeTurma + botaoCancelar + conteudo);
	window.print();
	return false;
} 

function cancelarPrint() {
	document.location.href = '/sigaa/ava/index.jsf'
}


var aviso = J("#MensagemCarregamento");
aviso.hide();

function permitirEscolhaTurma() {
	
	var turmas = J(".linkTurma");
	
	turmas.each(function() {
		J(this).hide();
	} );

	aviso.show();
}

</script>	
		<div id="conteudo" class="ui-layout-center">
			<div id="scroll-wrapper">		
			<c:if test="${sessionScope.avisoBrowser != null}">
				<script type="text/javascript">
					window.open('/sigaa/public/navegador.jsp?ajaxRequest=true','','width=670,height=230, top=100, left=100, scrollbars' );
				</script>
				<% session.removeAttribute("avisoBrowser"); %>
			</c:if>
			
			<%@include file="/WEB-INF/jsp/include/erros.jsp"%>
					
			<% if ( request.isSecure() ) { %>
				<c:set var="protocolo" value="https"/>
			<% } else { %>
				<c:set var="protocolo" value="http"/>
			<% }  %>
		
			<%--<c:set var="menuDocente" value="http://${header['Host']}/sigaa/portais/docente/menu_docente_externo.jsf"/>
			<c:set var="menuDocenteExterno" value="http://${header['Host']}/sigaa/portais/docente/menu_docente_externo.jsf"/>
			<c:set var="menuDiscente" value="http://${header['Host']}/sigaa/portais/discente/menu_discente_externo.jsf"/>
			--%>
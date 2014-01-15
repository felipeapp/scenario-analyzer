<%@page import="br.ufrn.rh.dominio.Categoria"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>

<%@ taglib uri="/tags/sigaaFunctions" prefix="sf" %>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
<%@ taglib uri="/tags/rich" prefix="rich" %>
<%@ taglib uri="/tags/a4j" prefix="a4j" %>

<c:set var="ctx" value="${ pageContext.request.contextPath }"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="br.ufrn.sigaa.caixa_postal.dominio.Mensagem"%>

<%@page import="br.ufrn.comum.dominio.Sistema"%><html>
<head>
	<title>${ configSistema['siglaSigaa'] } - ${ configSistema['nomeSigaa'] }</title>
	<link rel="shortcut icon" href="${ ctx }/img/sigaa.ico"/>

	<link rel="stylesheet" media="all" href="/shared/css/menus.css"/>
	<link rel="stylesheet" type="text/css" media="all" href="/sigaa/css/menu_sigaa.css"/>
	<link rel="stylesheet" type="text/css" href="/shared/javascript/ext-2.0.a.1/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="${ ctx }/ava/css/turma.css" />
	
	<script src="/shared/javascript/ext-2.0.a.1/ext-base.js"></script>
	<script src="/shared/javascript/ext-2.0.a.1/ext-all.js"></script>
		
	<script type="text/javascript" src="/shared/jsBundles/jawr_loader.js" ></script>
	<script type="text/javascript">
     	JAWR.loader.script('/bundles/js/sigaa_base.js');
     </script>
	
</head>

<body style="background: url(/shared/img/bg_pagina_1024.jpg) repeat-y center;">

<div id="cabecalho" style="width: 990px; margin: 0 auto;">
	<div id="info-sistema">
		<h1> <span>${ configSistema['siglaInstituicao'] } - ${ configSistema['siglaSigaa'] } </span> - </h1>
		<h3> ${ configSistema['nomeSigaa'] } </h3>
			<div class="dir" style="float:right;width: 300px;margin 0 auto;position:relative;font-size:1.1em;">
			<c:if test="${not empty sessionScope.usuario}">
				<%@include file="/WEB-INF/jsp/include/_acessibilidade.jsp"%>
				<span id="tempoSessao"></span>
				<span class="sair-sistema"> <html:link
					action="logar?dispatch=logOff"> SAIR  </html:link> </span>
			</c:if>
			</div>
	</div>

	<div id="painel-usuario" >
		<div id="menu-usuario">
			<ul>
			<li class="modulos"><span id="modulos"><a href="#" id="show-modulos-ava"> M&#243;dulos </a></span></li>
			
			<li class="caixa-postal"><a	href="<%="/sigaa/abrirCaixaPostal.jsf?sistema="+String.valueOf(Sistema.SIGAA)%>" > 
			<c:if test="${ empty sessionScope.qtdMsgsNaoLidasCxPostal || sessionScope.qtdMsgsNaoLidasCxPostal <= 0 }">
					Caixa Postal					
					</c:if>
					<c:if test="${ not empty sessionScope.qtdMsgsNaoLidasCxPostal && sessionScope.qtdMsgsNaoLidasCxPostal > 0 }">
						<c:if test="${ sessionScope.qtdMsgsNaoLidasCxPostal > 999 }">
						Cx. Postal (999+)
						</c:if>
						<c:if test="${ sessionScope.qtdMsgsNaoLidasCxPostal <= 999 }">
						Cx. Postal (${ sessionScope.qtdMsgsNaoLidasCxPostal })
						</c:if>
					</c:if>
			</a></li>
			
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
					<c:if test="${sessionScope.usuario.servidor != null && sessionScope.usuario.servidor.categoria.id == categoriaDocente}">
						<a href="${ctx}/verPortalDocente.do">Menu Docente</a>
					</c:if>
					<c:if test="${sessionScope.usuario.discente != null && (sessionScope.usuario.servidor == null || (sessionScope.usuario.servidor.categoria.id != categoriaDocente))}">
						<a href="${ctx}/verPortalDiscente.do">Menu Discente</a>
					</c:if>
					<c:if test="${ sessionScope.usuario.somenteConsultor }">
						<a href="${ctx}/verPortalConsultor.do">Portal Consultor</a>
					</c:if>
				</li>
			</c:if>
			<c:if test="${acesso.administracao}">
				<li class="admin"><a href="${ctx}/verMenuAdministracao.do">Área Admin.</a> </li>
			</c:if>
			<li class="dados-pessoais"><a href="/sigaa/dados_pessoais/dados.jsf">Dados Pessoais</a> </li>
			<li class="ajuda"><a href="#" id="show-ajuda-ava" rel="documento.getElementById('urlHelp')"> Ajuda </a></li>
    		</ul>
		</div>
    
    	<div id="info-usuario" align="left">
			<p class="usuario">				
			 	<ufrn:format  type="texto" length="50" valor="${sessionScope.usuario.pessoa.nome }"/>
				<c:if test="${sessionScope.usuarioAnterior != null}">
		 			<a href="${ctx}/administracao/usuario/logar_como.jsf">(Deslogar)</a>
		 		</c:if>
			 	<c:if test="${ acesso.multiplosVinculos }">
			 		<a href="${ctx}/escolhaVinculo.do?dispatch=listar"><img src="/shared/img/group_go.png" alt="Alterar vínculo" title="Alterar vínculo"/></a>
			 	</c:if>
		 	</p>

			<p class="unidade">
				<ufrn:format type="texto" length="50" valor="${sessionScope.usuario.unidade.nome}"/>
				<c:if test="${not empty sessionScope.usuario.unidade}">
					(${sessionScope.usuario.unidade.codigoFormatado})
				</c:if>
				<c:if test="${ sessionScope.usuario.somenteConsultor }">
					Área de Conhecimento: <em>${ sessionScope.usuario.consultor.areaConhecimentoCnpq.nome }</em>
				</c:if>
			</p>
			<p class="periodo-atual">
				<c:if test="${paramGestora != null}">
				Semestre atual: <strong>${calendarioAcademico.anoPeriodo}</strong>
				</c:if>
			</p>
		</div>
	</div>
	
	<f:view>
	<c:if test="${ !usuario.vinculoAtivo.vinculoDiscente }">
		<c:set var="turmasAbertas" value="#{ portalDocente.turmasAbertas }"/>
	</c:if>
	<c:if test="${ usuario.vinculoAtivo.vinculoDiscente }">
		<c:set var="turmasAbertas" value="#{ portalDiscente.turmasAbertas }"/>
	</c:if>
	</f:view>

    <div id="menu-cabecalho">
		<h2><ufrn:subSistema /> > Porta-Arquivos</h2>
	</div>

</div>

<div id="conteudo" style="width: 990px; margin: 0 auto;">

<%@include file="/ava/erros.jsp"%>